package com.hupisan.database.entity

import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.PrimaryKey

open class ArisanModel() : RealmObject {
    @PrimaryKey
    var uid: Int = 0
    var grup_id: Int = 0
    var username: String = ""
    var jumlah_bayar: Int = 0
    var tanggal_menang: String = ""
    var status_bayar: Boolean = false
    var status_menang: Boolean = false
    var gender: Int = 0

    constructor(
        uid: Int,
        grup_id: Int,
        username: String,
        jumlah_bayar: Int,
        tanggal_menang: String,
        status_bayar: Boolean,
        status_menang: Boolean,
        gender: Int
    ) : this() {
        this.uid = uid
        this.grup_id = grup_id
        this.username = username
        this.jumlah_bayar = jumlah_bayar
        this.tanggal_menang = tanggal_menang
        this.status_bayar = status_bayar
        this.status_menang = status_menang
        this.gender = gender
    }
}
