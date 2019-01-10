package com.cna.mineru.cna.DTO;

public class HomeData {
    public String title_text;
    public int id;
    public int tag;
    public byte[] Image;
    public HomeData(int id, String title_text,int tag){
        this.title_text = title_text;
        this.id = id;
        this.tag = tag;
    }

}
