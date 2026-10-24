package my.notes.HHHH.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "arisan_user_model")
data class ArisanModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Int,
    @ColumnInfo(name = "from_uid_group")
    var grup_id: Int,
    @ColumnInfo(name = "name_user")
    var username: String,
    @ColumnInfo(name = "jumlah_bayar")//ini dia bayar uang arisan dengan sejumlah berapa ya
    var jumlah_bayar: Int,
    @ColumnInfo(name = "tanggal_menang")//ini ketika bulan ini di kocok trus si user mendapatkan prize pollnya
    var tanggal_menang: String,
    @ColumnInfo(name = "status_bayar")//ini dia suadah bayar atau belum, jika sudah bayar maka status akan di ganti menjadi ready jika belum maka false
    var status_bayar: Boolean,
    @ColumnInfo(name = "status_menang")//ini usernya udah meanng atau belum
    var status_menang: Boolean,
    @ColumnInfo(name = "gender")
    var gender: Int //ini gender pake integer biar tiap ganti bahasa datanya tetap sama jadi statusnya kalo 0 kosong, 1 laki-laki, 2 perempuan, 3 lainnya
)
