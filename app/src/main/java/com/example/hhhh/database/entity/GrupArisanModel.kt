package com.example.hhhh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_arisan_model")
data class GrupArisanModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Int,
    @ColumnInfo(name = "grup_name")
    var grup_name: String,
    @ColumnInfo(name = "dibuat")
    var dibuat: String,
    @ColumnInfo(name = "diupdate")
    var diupdate: String,
    @ColumnInfo(name = "anggaran_bayar_peruser")//anggaran kesepakatan pembayaran
    var anggaran_kesepakatan_peruser: Int,
    @ColumnInfo(name = "anggaran_terkumpul")//anggaran terkumpul dari tiap user yang sudah bayar
    var anggaran_terkumpul: Int,
    @ColumnInfo(name = "total_prize_poll")//total prie yang di dapat
    var total_prize_poll: Int,
    @ColumnInfo(name = "jumlah_member")//jumlah member yang ikut dalam event
    var jumlah_member: Int,
    @ColumnInfo(name = "status_dimulai")//ini boolean status telah di mulai atau belum
    var status_dimulai: Boolean
)