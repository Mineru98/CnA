package com.cna.mineru.cna.DTO;

public class HomeData {
    public String title_text;
    public int id;
    public int Tag;
    public int Subtag;
    public HomeData(int id, String title_text, int Tag, int Subtag){
        this.title_text = title_text;
        this.id = id;
        this.Tag = Tag;
        this.Subtag = Subtag;
    }

}
