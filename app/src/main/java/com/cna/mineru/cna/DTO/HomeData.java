package com.cna.mineru.cna.DTO;

import android.graphics.Bitmap;

public class HomeData {
    public String title_text;
    public int id;
    public int tag;
    public byte[] image;
    public HomeData(int id, String title_text,int tag,byte[] image){
        this.title_text = title_text;
        this.id = id;
        this.tag = tag;
        this.image = image;
    }

}
