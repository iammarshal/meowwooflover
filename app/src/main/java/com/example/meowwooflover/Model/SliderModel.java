package com.example.meowwooflover.Model;

public class SliderModel {
    private String url;

    public SliderModel() {
        this.url = "";
    }

    public SliderModel(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}