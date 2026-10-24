package my.notes.HHHH.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_pembayaran_model")
data class PaymentModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Int,
    @ColumnInfo(name = "from_uid")
    var from_uid: Int,
    @ColumnInfo(name = "diUpdate")
    var diUpdate: String,
    @ColumnInfo(name = "jumlah_pembayaran")
    var jumlahPembayaran: Int,
    @ColumnInfo(name = "keterangan")
    var keterangan: String,
    @ColumnInfo(name = "ditambah_atau_dikurangi")
    var diTambahAtaudiKurangiAtauLunas: Int,
    @ColumnInfo(name = "type_jenis")
    var typeJenis: Boolean
)
