package com.hupisan.database.entity

import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.PrimaryKey

open class PaymentModel() : RealmObject {
    @PrimaryKey
    var uid: Int = 0
    var from_uid: Int = 0
    var diUpdate: String = ""
    var jumlahPembayaran: Int = 0
    var keterangan: String = ""
    var diTambahAtaudiKurangiAtauLunas: Int = 0
    var typeJenis: Boolean = false

    constructor(
        uid: Int,
        from_uid: Int,
        diUpdate: String,
        jumlahPembayaran: Int,
        keterangan: String,
        diTambahAtaudiKurangiAtauLunas: Int,
        typeJenis: Boolean
    ) : this() {
        this.uid = uid
        this.from_uid = from_uid
        this.diUpdate = diUpdate
        this.jumlahPembayaran = jumlahPembayaran
        this.keterangan = keterangan
        this.diTambahAtaudiKurangiAtauLunas = diTambahAtaudiKurangiAtauLunas
        this.typeJenis = typeJenis
    }
}
