package com.example.hhhh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "data_model")
data class DataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "model_id")
    var id: Int = 0,
    @ColumnInfo(name = "username")
    var username: String,
    @ColumnInfo(name = "deskripsi")
    var deskripsi: String,
    @ColumnInfo(name = "jumlah")// nominal data yang di masukkan
    var jumlah: Int,
    @ColumnInfo(name = "diBuat")// tanggal di buat sebuah data
    var diBuat: String,
    @ColumnInfo(name = "jatuhTempo")
    var jatuhTempo: String,
    @ColumnInfo(name = "notif")//ini di buat ketika true maka notif muncul
    var notif: Boolean,
    @ColumnInfo(name = "gender")//ini untuk perubahan data bagian foto ketika gender lelaki ataupun perempuan
    var gender: String,//gender user hutang / piutang
    @ColumnInfo(name = "jenis")
    var jenis: String,//hutang / piutang
    @ColumnInfo(name = "statusData")
    var statusData: String, //ini datanya antara lunas atau belum lunas jika jumlah belum 0 maka belum lunas
    @ColumnInfo(name = "jam_jatuh_tempo")// tanggal untuk notifiki
    var jam_jatuh_tempo: String,
    @ColumnInfo(name = "tanggal_lunas")//tanggal selesai sebuah data
    var tanggal_lunas: String,
    @ColumnInfo(name = "dibayar")//ini data untuk statistic buat nampilin dibayar
    var jumlah_dibayar: Int
)