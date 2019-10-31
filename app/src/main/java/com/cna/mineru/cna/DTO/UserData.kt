package com.cna.mineru.cna.DTO

class UserData(
    var User_Id: Int,
    var Name: String,
    var ClassId: Int,
    var isGoogle: Int,
    var isPremium: Int,
    var isClassChecked: Int,
    var isCounpon: Int,
    var CounponCode: String,
    var CouponTag: Int,
    var CouponDate: String
) {
    var isFirst: Int = 0
    var isWifiSync: Int = 0

    init {
        this.isFirst = 1
        this.isWifiSync = 0

    }
}
