package com.example.meowwooflover.Model;

public class CategoryModel {
    private String title;
    private int id;
    private String picUrl;

    public CategoryModel() {
        this("", 0, "");
    }

    public CategoryModel(String title, int id, String picUrl) {
        this.title = title;
        this.id = id;
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}