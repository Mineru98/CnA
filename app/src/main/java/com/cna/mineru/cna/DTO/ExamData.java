package com.cna.mineru.cna.DTO;

public class ExamData {
    public int Id;
    public int NoteId;
    public String Title;
    public int isSolved;
    public int RoomId;
    public String ExamTitle;
    public long TTS;

    public ExamData(int NoteId, String Title){
        this.NoteId = NoteId;
        this.Title = Title;
    }

    public ExamData(String ExamTitle, int RoomId){
        this.ExamTitle = ExamTitle;
        this.RoomId = RoomId;
    }

    public ExamData(int Id, int NoteId, String Title, long TTS, int isSolved){
        this.Id = Id;
        this.NoteId = NoteId;
        this.Title = Title;
        this.TTS = TTS;
        this.isSolved = isSolved;
    }
}
