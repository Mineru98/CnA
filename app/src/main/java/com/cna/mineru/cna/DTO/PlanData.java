package com.cna.mineru.cna.DTO;

import java.sql.Date;

public class PlanData {
    public int id;
    public String title;
    public int form;
    public int gold;
    public int progress;
    public Date deadline;

    public PlanData(int id, String title, int form, int gold, String deadline,int progress){
        this.id = id;
        this.title = title;
        this.form = form;
        this.gold = gold;
        this.progress = progress;
        Date d = Date.valueOf(deadline);
        this.deadline = d;
    }

}
