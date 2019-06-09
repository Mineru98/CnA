package com.cna.mineru.cna.DTO;

public class ClassData {
    public int ClassId;
    public int Term;
    public String Title;
    public int Tag;
    public int SubTag;

    public ClassData(int ClassId, int Term, String Title, int Tag, int SubTag){
        this.ClassId = ClassId;
        this.Term = Term;
        this.Title = Title;
        this.SubTag = SubTag;
        this.Tag = Tag;
    }

    public ClassData(int Tag, String Title){
        this.Title = Title;
        this.Tag = Tag;
    }
}
