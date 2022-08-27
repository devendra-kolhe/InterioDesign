package com.example.interiodesign;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class showAR extends AppCompatActivity {
    private ArFragment arFragment;

    private int clickNo = 0;
    private ImageButton videoRecordingButton;
    private Node currentNode = null;
    private Product productObject;
    private VideoRecorder videoRecorder;


    private FirebaseData firebaseData;
    private RecyclerView bottomSheetRecyclerView;
    private Map<Node, Product> nodeProductMap = new HashMap<>();
    private Map<Node, ArrayList<Material>> nodeMaterialMap = new HashMap<>();

    private BottomSheetBehavior bottomSheetBehavior;
    private GestureDetector gestureDetector;
    private ViewRenderable viewRenderable;
    private InterioDesignAdapter interioDesignAdapter;

    private Node forViewRenderable;
    private TextView title,price,materialText;
    private ImageButton cancelButton,deleteModelButton;
    private RecyclerView materialListRecyclerView;
    private ProgressDialog progressDialog;

    private Boolean permissionBool = false;
    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted)
            permissionBool = true;
    });


    public showAR(){ }

    private void changeTextureInNodeProductMap(Integer pos,String texture){
        ArrayList<String> textureList = nodeProductMap.get(currentNode).getTexture();
        textureList.set(pos,texture);
        nodeProductMap.get(currentNode).setTexture(textureList);
    }

    public void changeTexture(View view){
        if(materialText.getText().toString().equals("#"))
            return;
        Integer position = Integer.parseInt(materialText.getText().toString())-1;
        String textureString = view.getTag().toString();
        Log.e("manualll",this. getClass(). getSimpleName()+" "+textureString+" "+Integer.toString(position));
        if(currentNode != null && textureString.equals("default_texture")){
            Log.e("manual",this.getClass().getSimpleName()+" "+"it is default" );
            Material defaultMaterial = nodeMaterialMap.get(currentNode).get(position);
            Renderable currentNodeCopy = currentNode.getRenderable().makeCopy();
            currentNodeCopy.setMaterial(position,(Material) defaultMaterial);
            currentNode.setRenderable(currentNodeCopy);
            changeTextureInNodeProductMap(position,textureString);
        }
        else if(currentNode!=null){
            CompletableFuture<Texture> textureCompletableFuture =
                    Texture.builder().setSource(this, getResources().getIdentifier(textureString,"drawable",getPackageName())).build();

            ((CompletableFuture<?>) textureCompletableFuture).thenAccept(texture -> {
                Renderable currentNodeCopy = currentNode.getRenderable().makeCopy();
                currentNodeCopy.getMaterial(position).setTexture("baseColor",(Texture) texture);
                currentNode.setRenderable(currentNodeCopy);
                changeTextureInNodeProductMap(position,textureString);
            });
        }
        else{
            Toast.makeText(getBaseContext(), "please load a model", Toast.LENGTH_SHORT).show();
        }
    }
    private String getNewName(ArrayList<String> textureList){
        String newName = "";
        for(int i=0;i< textureList.size();i++){
            if(!textureList.get(i).equals("default"))
                newName += Integer.toString(i) + textureList.get(i);
        }
        return newName;
    }

    public void addAllToCart(View view){
        Log.e("manual","inside addAllToCart "+nodeProductMap.values().toString());
        nodeProductMap.forEach((k,v)->{
            Product product = new Product(v);
            String newName = product.getKey()+getNewName(product.getTexture());
            product.setKey(newName);
            firebaseData.addToCartList(FirebaseAuth.getInstance().getUid(),product);
            Toast.makeText(getBaseContext(), "Items added to Cart.", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            Log.e("manual",ex.toString());
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) +  "/InterioDesign/Images/" + date + "_screenshot.jpg";
    }


    public void takePhoto(View view1) {
        if(!checkPermission())
            return;
        final String filename = generateFilename();
        ArSceneView view = arFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                } catch (IOException e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getBaseContext(), "Photo Saved.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG).show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }


//    public void changeColor(View view){
//        Log.e("manual",this. getClass(). getSimpleName()+" "+(String)view.getTag());
//
//        if(currentNode != null && view.getTag().equals("defaultColor")){
//            Material defaultMaterial = nodeMaterialMap.get(currentNode).get(0);
//            Renderable currentNodeCopy = currentNode.getRenderable().makeCopy();
//            currentNodeCopy.setMaterial((Material) defaultMaterial);
//            currentNode.setRenderable(currentNodeCopy);
//
//        }
//        else if(currentNode != null){
//            int color = Integer.decode((String)view.getTag());
//            CompletableFuture<Material> materialCompletableFuture =
//                    MaterialFactory.makeOpaqueWithColor(this, new Color((Integer) color));
//
//            ((CompletableFuture<?>) materialCompletableFuture).thenAccept(material -> {
//                Renderable currentNodeCopy = currentNode.getRenderable().makeCopy();
//                currentNodeCopy.setMaterial((Material) material);
//                currentNode.setRenderable(currentNodeCopy);
//            });
//        }
//        else{
//            Toast.makeText(getBaseContext(), "please load a model", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void removeModel(){
        if(currentNode != null){
            arFragment.getArSceneView().getScene().removeChild(currentNode);
            nodeProductMap.remove(currentNode);
            nodeMaterialMap.remove(currentNode);
            currentNode.setParent(null);
            currentNode = null;
        }
        else{
            Toast.makeText(getBaseContext(), "please load a model", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ar);

        firebaseData = new FirebaseData();
        firebaseData.getAllProducts();

        Intent i = getIntent();
        Product productObjectReference = (Product) i.getParcelableExtra("productObject");

        progressDialog = new ProgressDialog(showAR.this);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arCameraArea);
        videoRecordingButton = findViewById(R.id.video_recording_button);
        bottomSheetRecyclerView = findViewById(R.id.bottomSheetRecyclerView);
        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);


        bottomSheetRecyclerView.setHasFixedSize(true);
        bottomSheetRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);





        firebaseData.getProductMutableLiveList().observe(arFragment.getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                Log.e("manual",products.get(0).getPrice().toString());
                interioDesignAdapter = new InterioDesignAdapter(R.layout.collection_row,products,true,bottomSheetLayout);
                bottomSheetRecyclerView.setAdapter(interioDesignAdapter);
            }
        });
        firebaseData.getErrorMutableLive().observe(arFragment.getViewLifecycleOwner(), new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        gestureDetector = new GestureDetector(getBaseContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                return true;
            }
        });




        bottomSheetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("manualllllllllllllllllllllll", this.getClass().getSimpleName()+" "+ interioDesignAdapter.getProductObject());
                if(interioDesignAdapter.getProductObject() != null){
                    productObject = new Product(interioDesignAdapter.getProductObject());
                    Log.e("manual","when fetched from bottomsheet"+productObject.toString());
                    clickNo = 0;
                    procedure( productObject.getKey());
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        findViewById(R.id.addButton).setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED));

        findViewById(R.id.closeButton).setOnClickListener(view -> { finish(); });

        findViewById(R.id.closeBottomSheet).setOnClickListener(view->{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });


        videoRecordingButton.setOnClickListener(e->{
            if(!checkPermission())
                return;
            if(videoRecorder==null) {
                videoRecorder = new VideoRecorder();
                videoRecorder.setSceneView(arFragment.getArSceneView());
                int orientation = getResources().getConfiguration().orientation;
                videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_HIGH, orientation);
            }
            Log.e("manual","entered here");
            boolean isRecording = videoRecorder.onToggleRecord();
            if(isRecording) {
                Toast.makeText(this, "Started Recording", Toast.LENGTH_SHORT).show();
                videoRecordingButton.setImageResource(R.drawable.outline_videocam_24);
            }
            else{
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
                videoRecordingButton.setImageResource(R.drawable.outline_videocam_off_24);
            }
        });


        if(productObjectReference != null){
            productObject = new Product(productObjectReference);
            String modelName = productObject.getKey();
            Log.e("manual",this. getClass(). getSimpleName()+" "+"not null");
            clickNo=0;
            procedure(modelName);
        }

        buildViewRenderable();
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("manual","already permitted for write storage");
            permissionBool = true;
        } else {
            Log.e("manual","to launce request for write external storage ");
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        Log.e("manual","inside checkpermission to return permission bool"+permissionBool.toString());
        return permissionBool;
    }


    private boolean handleTouch(HitTestResult hitTestResult, MotionEvent motionEvent){
        if (hitTestResult.getNode() != null) {
            currentNode = hitTestResult.getNode();
            if(gestureDetector.onTouchEvent(motionEvent)){
                Log.e("manual","tapped double");

                Product product = nodeProductMap.get(currentNode);
                title.setText(product.getTitle());
                price.setText(product.getPrice().toString()+" Rs");
                materialText.setText("#");

                ArrayList<Integer> list = getMaterialCountList(currentNode.getRenderable().getSubmeshCount());
                Log.e("manual",list.toString());
                ListAdapter listAdapter = new ListAdapter(list,materialText);
                materialListRecyclerView.setAdapter(listAdapter);

                if(forViewRenderable.isEnabled() == true){
                    forViewRenderable.setEnabled(false);
                }
                forViewRenderable.setParent(currentNode);
                forViewRenderable.setLocalPosition(new Vector3(0.0f,currentNode.getLocalPosition().y+1.2f, 0.0f));
                forViewRenderable.setEnabled(true);
            }
        }
        return true;
    }




    private void setDefault(){
        productObject.setQuantity(1L);
        ArrayList<String> textureList = new ArrayList<>();
        for(int i=0;i<currentNode.getRenderable().getSubmeshCount();i++)
            textureList.add("default_texture");
        productObject.setTexture(textureList);
    }

    private ArrayList<Material> getMaterialList(Node currentNode){
        ArrayList<Material> materialArrayList = new ArrayList<>();
        for(int i=0;i<currentNode.getRenderable().getSubmeshCount();i++){
            materialArrayList.add(currentNode.getRenderable().getMaterial(i));
        }
        return materialArrayList;
    }

    private void addToRecent(){
        Long time = Calendar.getInstance().getTimeInMillis();
        Product product = new Product(productObject);
        product.setTime(time);
        firebaseData.addToRecentlyViewedList(FirebaseAuth.getInstance().getUid(), product);
    }


    private void addModel(Anchor anchor, ModelRenderable modelRenderable) {
        progressDialog.setMessage("Adding model..");
        Log.e("manual",this. getClass(). getSimpleName()+" "+"inside admodel");

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());


        TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
        model.setParent(anchorNode);
        model.setRenderable(modelRenderable);
        model.select();
        model.setOnTouchListener(this::handleTouch);
        currentNode = model;

        addToRecent();
        nodeMaterialMap.put(currentNode,getMaterialList(currentNode));
        setDefault();
        nodeProductMap.put(currentNode,productObject);
        progressDialog.dismiss();
    }


    private ArrayList<Integer> getMaterialCountList(int count){
        ArrayList<Integer> materialCountList = new ArrayList<>();
        for(int i=1;i<=count;i++){
            materialCountList.add(i);
        }
        return materialCountList;
    }



    private void buildModel(HitResult hitResult,File file){
        RenderableSource renderableSource = RenderableSource
                .builder()
                .setSource(this, Uri.parse(file.getPath()), RenderableSource.SourceType.GLB)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build();

        Anchor anchor = hitResult.createAnchor();
        ModelRenderable.builder()
                .setSource(this, renderableSource)
                .build()
                .thenAccept(modelRenderable -> addModel( anchor, modelRenderable))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Something is not right" + throwable.getMessage()).show();
                    return null;
                });
    }

    private File getFile(String modelName){
        File file = new File(this.getCacheDir(),modelName+".glb");

        if(file.exists()){
            Log.e("manual",this. getClass(). getSimpleName()+" "+"exists");
        }
        else {
            progressDialog.setMessage("Downloading model...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            progressDialog.setCancelable(false);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference model = storage.getReference().child(modelName+".glb");
            Log.e("manual",this. getClass(). getSimpleName()+" "+model.toString());

            Log.e("manual",this. getClass(). getSimpleName()+" "+"not exists");
            Log.e("manual",this. getClass(). getSimpleName()+" "+this.getCacheDir().toString());
            Toast.makeText(showAR.this, "Downloading selected model!", Toast.LENGTH_SHORT).show();

            model.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(showAR.this,"Model is downloaded.",Toast.LENGTH_SHORT).show();
                            Log.e("manual",this.getClass().getSimpleName()+" "+"now the file is downloaded and stored from storage");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(showAR.this,"Download Failed!!: "+e.toString(),Toast.LENGTH_SHORT).show();
                            Log.e("manual",this.getClass().getSimpleName()+" download failed "+e.toString());
                        }
                    });
            Log.e("manual",this. getClass(). getSimpleName()+" "+file.getName());
        }
        return file;
    }


    private void procedure(String modelName){
        File file = getFile(modelName);
        Log.e("manual",this. getClass(). getSimpleName()+" "+"inside procedure after getfile");

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            clickNo++;
            if (clickNo == 1) {
                Log.e("manual",this. getClass(). getSimpleName()+" "+"yes click==1");
                progressDialog.setMessage("Building model...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);

                buildModel(hitResult,file);
            }
        });
    }





    private void configureViewRenderable(ViewRenderable renderable){
        title = renderable.getView().findViewById(R.id.title);
        price = renderable.getView().findViewById(R.id.price);
        materialText = renderable.getView().findViewById(R.id.materialText);

        cancelButton = renderable.getView().findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> {
            forViewRenderable.setEnabled(false);
        });

        deleteModelButton = renderable.getView().findViewById(R.id.deleteModelButton);
        deleteModelButton.setOnClickListener(view ->{
            this.removeModel();
        });

        materialListRecyclerView = viewRenderable.getView().findViewById(R.id.materialListRecyclerView);
        materialListRecyclerView.setHasFixedSize(true);
        materialListRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        forViewRenderable = new Node();
        forViewRenderable.setRenderable(viewRenderable);
    }




    private void buildViewRenderable(){
        ViewRenderable.builder()
                .setView(this,R.layout.view_renderable)
                .build()
                .thenAccept(renderable->{
                    viewRenderable = renderable;
                    configureViewRenderable(renderable);

                    renderable.setShadowCaster(false);
                    renderable.setShadowReceiver(false);
                    //renderable.setSizer(new FixedHeightViewSizer(0.5f));
                    //renderable.setSizer(new FixedWidthViewSizer(0.5f));
                })
                .exceptionally(
                        (throwable) -> {
                            throw new AssertionError("Could not load plane card view.", throwable);
                        });
    }



}


