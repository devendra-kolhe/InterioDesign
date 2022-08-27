package com.example.interiodesign;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Product implements Parcelable {
    private String img;
    private Long price;
    private String title;
    private String key;
    private Long quantity;
    private Long time;
    private ArrayList<String> textureList;


    public Product(String img, Long price, String title, String key){
        this.img = img;
        this.price = price;
        this.title = title;
        this.key = key;
    }

    public Product(String img, Long price, String title, String key,Long quantity,ArrayList<String> textureList){
        this.img = img;
        this.price = price;
        this.title = title;
        this.key = key;
        this.quantity = quantity;
        this.textureList = textureList;
    }
    public Product(Product productCopy){
        this.img = productCopy.getImg();
        this.price = productCopy.getPrice();
        this.title = productCopy.getTitle();
        this.key = productCopy.getKey();
        this.quantity = productCopy.getQuantity();
        this.textureList = productCopy.getTexture();
    }


    protected Product(Parcel in) {
        img = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readLong();
        }
        title = in.readString();
        key = in.readString();
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readLong();
        }
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        textureList = in.createStringArrayList();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getQuantity() { return quantity; }

    public void setQuantity(Long quantity) { this.quantity = quantity; }

    public ArrayList<String> getTexture() { return textureList; }

    public void setTexture(ArrayList<String> textureList) {
        this.textureList = textureList;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(img);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(price);
        }
        parcel.writeString(title);
        parcel.writeString(key);
        if (quantity == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(quantity);
        }
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(time);
        }
        parcel.writeStringList(textureList);
    }
}
