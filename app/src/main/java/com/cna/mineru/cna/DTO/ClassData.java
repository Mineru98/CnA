package com.cna.mineru.cna.DTO;

public class ClassData {
    public int ClassId;
    public int Term;
    public String Title;
    public int Tag;

    public ClassData(int ClassId, int Term, String Title, int Tag){
        this.ClassId = ClassId;
        this.Term = Term;
        this.Title = Title;
        this.Tag = Tag;
    }
}
