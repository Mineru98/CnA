package com.cna.mineru.cna.DTO

class ExamData {
    var Id: Int = 0
    var NoteId: Int = 0
    var Title: String? = null
    var isSolved: Int = 0
    var RoomId: Int = 0
    var ExamTitle: String? = null
    var TTS: Long = 0

    constructor(TTS: Long, isSolved: Int) {
        this.TTS = TTS
        this.isSolved = isSolved
    }

    constructor(NoteId: Int, Title: String) {
        this.NoteId = NoteId
        this.Title = Title
    }

    constructor(ExamTitle: String, RoomId: Int) {
        this.ExamTitle = ExamTitle
        this.RoomId = RoomId
    }

    constructor(Id: Int, NoteId: Int, Title: String, TTS: Long, isSolved: Int) {
        this.Id = Id
        this.NoteId = NoteId
        this.Title = Title
        this.TTS = TTS
        this.isSolved = isSolved
    }
}
