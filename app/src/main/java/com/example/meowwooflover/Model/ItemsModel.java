package com.example.meowwooflover.Model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class ItemsModel implements Parcelable {
    private String title;
    private String description;
    private ArrayList<String> picUrl;
    private ArrayList<String> size;
    private double price;
    private double rating;
    private int numberInCart;
    private String sellerName;
    private int sellerTell;
    private String sellerPic;

    public ItemsModel() {
        this.title = "";
        this.description = "";
        this.picUrl = new ArrayList<>();
        this.size = new ArrayList<>();
        this.price = 0.0;
        this.rating = 0.0;
        this.numberInCart = 0;
        this.sellerName = "";
        this.sellerTell = 0;
        this.sellerPic = "";
    }

    protected ItemsModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        picUrl = in.createStringArrayList();
        size = in.createStringArrayList();
        price = in.readDouble();
        rating = in.readDouble();
        numberInCart = in.readInt();
        sellerName = in.readString();
        sellerTell = in.readInt();
        sellerPic = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeStringList(picUrl);
        dest.writeStringList(size);
        dest.writeDouble(price);
        dest.writeDouble(rating);
        dest.writeInt(numberInCart);
        dest.writeString(sellerName);
        dest.writeInt(sellerTell);
        dest.writeString(sellerPic);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemsModel> CREATOR = new Creator<ItemsModel>() {
        @Override
        public ItemsModel createFromParcel(Parcel in) {
            return new ItemsModel(in);
        }

        @Override
        public ItemsModel[] newArray(int size) {
            return new ItemsModel[size];
        }
    };

    // Getters and setters for all fields
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public ArrayList<String> getSize() {
        return size;
    }

    public void setSize(ArrayList<String> size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public int getSellerTell() {
        return sellerTell;
    }

    public void setSellerTell(int sellerTell) {
        this.sellerTell = sellerTell;
    }

    public String getSellerPic() {
        return sellerPic;
    }

    public void setSellerPic(String sellerPic) {
        this.sellerPic = sellerPic;
    }
}