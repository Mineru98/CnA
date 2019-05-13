package com.cna.mineru.cna.DTO;

public class NotiData {
    public int Id;
    public int NotiTag;
    public String Title;
    public String SubTitle;

    public NotiData(int Id, int NotiTag, String Title, String SubTitle){
        this.Id = Id;
        this.NotiTag = NotiTag;
        this.Title = Title;
        this.SubTitle = SubTitle;
    }
}
