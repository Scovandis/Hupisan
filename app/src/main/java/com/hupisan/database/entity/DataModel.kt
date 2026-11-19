package com.hupisan.database.entity

import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.PrimaryKey

open class DataModel() : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var username: String = ""
    var deskripsi: String = ""
    var jumlah: Int = 0
    var diBuat: String = ""
    var jatuhTempo: String = ""
    var notif: Boolean = false
    var genderUser: Int = 0
    var jenisPinjaman: Boolean = false
    var statusDataSelesai: Boolean = false
    var jam_jatuh_tempo: String = ""
    var tanggal_lunas: String = ""
    var jumlah_dibayar: Int = 0

    constructor(
        id: Int,
        username: String,
        deskripsi: String,
        jumlah: Int,
        diBuat: String,
        jatuhTempo: String,
        notif: Boolean,
        genderUser: Int,
        jenisPinjaman: Boolean,
        statusDataSelesai: Boolean,
        jam_jatuh_tempo: String,
        tanggal_lunas: String,
        jumlah_dibayar: Int
    ) : this() {
        this.id = id
        this.username = username
        this.deskripsi = deskripsi
        this.jumlah = jumlah
        this.diBuat = diBuat
        this.jatuhTempo = jatuhTempo
        this.notif = notif
        this.genderUser = genderUser
        this.jenisPinjaman = jenisPinjaman
        this.statusDataSelesai = statusDataSelesai
        this.jam_jatuh_tempo = jam_jatuh_tempo
        this.tanggal_lunas = tanggal_lunas
        this.jumlah_dibayar = jumlah_dibayar
    }
}
