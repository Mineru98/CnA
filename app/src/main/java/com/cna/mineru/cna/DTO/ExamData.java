package com.cna.mineru.cna.DTO;

public class ExamData {
    public int NoteId;
    public String Title;
    public int isSolved;
    public int RoomId;
    public String ExamTitle;
    public long TTS;

    public ExamData(String ExamTitle, int RoomId){
        this.ExamTitle = ExamTitle;
        this.RoomId = RoomId;
    }

    public ExamData(int NoteId, String Title, long TTS, int isSolved){
        this.NoteId = NoteId;
        this.Title = Title;
        this.TTS = TTS;
        this.isSolved = isSolved;
    }
}
