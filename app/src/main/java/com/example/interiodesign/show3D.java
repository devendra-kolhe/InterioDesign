package com.example.interiodesign;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Calendar;

public class show3D extends AppCompatActivity {

    private SceneView sceneView;
    private TransformationSystem transformationSystem;
    private Renderable renderable;
    private ImageButton closeButton,instructionPopup;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show3_d);

        Intent i = getIntent();
        String modelName = i.getStringExtra("modelName");
        Log.e("manual",this. getClass(). getSimpleName()+" "+"3d"+modelName);


        sceneView = findViewById(R.id.scene_View);
        closeButton=findViewById(R.id.closeButton);
        progressDialog = new ProgressDialog(show3D.this);
        //instructionPopup=findViewById(R.id.instructionButton);

        transformationSystem=new TransformationSystem(getResources().getDisplayMetrics(),new FootprintSelectionVisualizer());
        sceneView.getScene().addOnUpdateListener(this::onFrameUpdate);
        sceneView.getRenderer().setClearColor(new com.google.ar.sceneform.rendering.Color(Color.LTGRAY));
        sceneView.getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                transformationSystem.onTouch(hitTestResult,motionEvent);
            }
        });
        sceneView.getScene().getCamera().setLocalPosition(new Vector3(0,0.2f,0));


        procedure(modelName);

        closeButton.setOnClickListener(view -> {
            finish();
        });
//        instructionPopup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InstructionPopup instructionPopup = new InstructionPopup();
//                instructionPopup.showPopupWindow(view);
//            }
//        });


    }

    private void onFrameUpdate(FrameTime frameTime){
        if(!modelplaced){
            addModel(renderable);
        }
    }


    private void buildModel(File file) {
        RenderableSource renderableSource = RenderableSource
                .builder()
                .setSource(this, Uri.parse(file.getPath()), RenderableSource.SourceType.GLB)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .setScale(0.3f)
                .build();


        ModelRenderable.builder()
                .setSource(this,renderableSource)
                .build()
                .thenAccept(renderable -> addModel(renderable))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Something is not right" + throwable.getMessage()).show();
                    return null;
                });
        renderable = renderable;

    }


    private boolean modelplaced=false;

    private void addModel(Renderable renderable) {
        progressDialog.setMessage("Adding Model..");
        TransformableNode transformableNode=new TransformableNode(transformationSystem);
        transformableNode.getScaleController().setEnabled(true);
        transformableNode.getTranslationController().setEnabled(false);
        transformableNode.getRotationController().setEnabled(true);

//        transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0f, 0f, 1f), 90f));
        transformableNode.setRenderable(renderable);
        sceneView.getScene().addChild(transformableNode);
        transformableNode.setLocalPosition(new Vector3(0f,-0.05f,-0.6f));

        transformableNode.select();
        modelplaced=true;
        progressDialog.dismiss();
    }

    private void procedure(String modelName){
        File file = new File(this.getCacheDir(),modelName+".glb");

        if(file.exists()){
            Log.e("manual",this. getClass(). getSimpleName()+" "+"exists");

            findViewById(R.id.show3D).post(new Runnable() {
                public void run() {
                    progressDialog.setMessage("Building model..."); // Setting Message
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                }
            });

            buildModel(file);
        }
        else {

            findViewById(R.id.show3D).post(new Runnable() {
                public void run() {
                    progressDialog.setMessage("Downloading model..."); // Setting Message
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                }
            });

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference model = storage.getReference().child(modelName+".glb");
            Log.e("manual",this. getClass(). getSimpleName()+" "+model.toString());

            Log.e("manual",this. getClass(). getSimpleName()+" "+"not exists");
            Log.e("manual",this. getClass(). getSimpleName()+" "+this.getCacheDir().toString());

            model.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"Model is Downloaded.",Toast.LENGTH_SHORT);
                            Log.e("manual",this.getClass().getSimpleName()+" "+"now the file is downloaded and stored from storage");

                            progressDialog.setMessage("Building model..."); // Setting Message
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            progressDialog.show(); // Display Progress Dialog
                            progressDialog.setCancelable(false);


                            buildModel(file);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"Download Failed!!: "+e.toString(),Toast.LENGTH_SHORT);
                            Log.e("manual",this.getClass().getSimpleName()+" download failed "+e.toString());
                        }
                    });
            Log.e("manual",this. getClass(). getSimpleName()+" "+file.getName());
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            sceneView.resume();
        }
        catch (CameraNotAvailableException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        sceneView.pause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sceneView.destroy();
    }



}