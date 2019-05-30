package com.cna.mineru.cna.DTO;

public class ClassData {
    public int ClassId;
    public int Term;
    public String Title;
    public int Tag;
    public int isSubsection;

    public ClassData(int ClassId, int Term, String Title, int Tag, int isSubsection){
        this.ClassId = ClassId;
        this.Term = Term;
        this.Title = Title;
        this.Tag = Tag;
        this.isSubsection = isSubsection;
    }

    public ClassData(int Tag, String Title){
        this.Title = Title;
        this.Tag = Tag;
    }
}
