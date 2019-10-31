package com.cna.mineru.cna.DTO

class ClassData {
    var ClassId: Int = 0
    var Term: Int = 0
    var Title: String
    var Tag: Int = 0
    var SubTag: Int = 0

    constructor(ClassId: Int, Term: Int, Title: String, Tag: Int, SubTag: Int) {
        this.ClassId = ClassId
        this.Term = Term
        this.Title = Title
        this.SubTag = SubTag
        this.Tag = Tag
    }

    constructor(Tag: Int, Title: String) {
        this.Title = Title
        this.Tag = Tag
    }
}
