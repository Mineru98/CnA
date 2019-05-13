package com.cna.mineru.cna.DTO;

public class GraphData {
    public int id;
    public int note_type;
//    public int ClassId;

    public GraphData(int id, int note_type){
        this.note_type = note_type;
        this.id = id;
//        this.ClassId = ClassId;
    }

}
