package com.hupisan.database.entity

import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.PrimaryKey

open class GrupArisanModel() : RealmObject {
    @PrimaryKey
    var uid: Int = 0
    var grup_name: String = ""
    var dibuat: String = ""
    var diupdate: String = ""
    var anggaran_kesepakatan_peruser: Int = 0
    var anggaran_terkumpul: Int = 0
    var total_prize_poll: Int = 0
    var jumlah_member: Int = 0
    var status_dimulai: Boolean = false

    constructor(
        uid: Int,
        grup_name: String,
        dibuat: String,
        diupdate: String,
        anggaran_kesepakatan_peruser: Int,
        anggaran_terkumpul: Int,
        total_prize_poll: Int,
        jumlah_member: Int,
        status_dimulai: Boolean
    ) : this() {
        this.uid = uid
        this.grup_name = grup_name
        this.dibuat = dibuat
        this.diupdate = diupdate
        this.anggaran_kesepakatan_peruser = anggaran_kesepakatan_peruser
        this.anggaran_terkumpul = anggaran_terkumpul
        this.total_prize_poll = total_prize_poll
        this.jumlah_member = jumlah_member
        this.status_dimulai = status_dimulai
    }
}
