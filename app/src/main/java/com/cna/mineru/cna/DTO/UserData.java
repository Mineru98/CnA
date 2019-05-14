package com.cna.mineru.cna.DTO;

public class UserData {
    public int User_Id;
    public String Name;
    public int ClassId;
    public int isFirst;
    public int isGoogle;
    public int isWifiSync;
    public int isPremium;
    public int isClassChecked;
    public int isCounpon;
    public String CounponCode;
    public int CouponTag;
    public String CouponDate;

    public UserData(int User_Id, String Name, int ClassId, int isGoogle, int isPremium, int isClassChecked, int isCounpon, String CounponCode, int CouponTag, String CouponDate){
        this.User_Id = User_Id;
        this.Name = Name;
        this.ClassId = ClassId;
        this.isFirst = 1;
        this.isGoogle = isGoogle;
        this.isWifiSync = 0;
        this.isPremium = isPremium;
        this.isClassChecked = isClassChecked;
        this.isCounpon = isCounpon;
        this.CounponCode = CounponCode;
        this.CouponTag = CouponTag;
        this.CouponDate = CouponDate;

    }
}
