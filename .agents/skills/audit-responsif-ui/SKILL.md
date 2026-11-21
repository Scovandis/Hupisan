---
name: audit-responsif-ui
description: Audit dan perbaikan responsivitas UI aplikasi Android (XML Layouts & Jetpack Compose). Menangani pengujian layar kecil (HP 320dp/360dp), font scaling ekstrem (130%-200%), keyboard overlap, rotasi landscape, dan adaptasi tablet. Menyediakan analisis statis pola berisiko, pengujian langsung di perangkat fisik/emulator via ADB (wm size, wm density, font_scale, uiautomator dump), resep perbaikan teruji, serta format laporan matematis terstandarisasi. Gunakan saat user mengetik "/audit-responsif-ui", meminta audit responsivitas layar, atau menguji UI pada berbagai ukuran layar.
---

# SKILL: AUDIT RESPONSIF UI — Playbook Audit & Perbaikan Tampilan Android

Skill ini adalah standar operasional dan panduan eksekusi untuk menganalisis, menguji, dan memperbaiki ketahanan responsivitas antarmuka (UI/UX) pada aplikasi Android baik yang berbasis **XML Layouts** maupun **Jetpack Compose**.

Tujuan utama skill ini adalah menjamin UI **tidak pernah pecah, tidak terpotong (*clipped*), tidak saling tumpang tindih (*overlap*), dan tidak terkubur oleh keyboard**, terutama pada kondisi perangkat ekstrem (layar sempit 320dp, font lansia 150–200%, orientasi landscape, dan tablet).

---

## 1. Parameter Eksekusi

Saat skill ini diaktifkan (misalnya via `/audit-responsif-ui [parameter]`), tentukan atau konfirmasikan 4 dimensi parameter berikut:

| Parameter | Pilihan | Default | Deskripsi |
| :--- | :--- | :--- | :--- |
| **Mode** | `analisa` \| `perbaiki` | `analisa` | `analisa`: Hanya memetakan masalah, menghitung margin kegagalan, dan membuat laporan.<br>`perbaiki`: Langsung mengedit kode XML/Kotlin, kompilasi, dan retest. |
| **Cakupan Layar** | `all` \| `<NamaActivity/Fragment>` | `all` | Menentukan layar target (misal: `MainActivity`, `HutangActivity`, `LunasActivity`, dsb). |
| **Ukuran Target** | `320dp` \| `360dp` \| `font_large` \| `keyboard` \| `landscape` \| `tablet` | `320dp, font_large, keyboard` | Kondisi constraint spesifik yang ingin diuji. |
| **Jenis Uji** | `statis` \| `perangkat` | `statis` | `statis`: Menganalisis kode XML/Compose & manifest.<br>`perangkat`: Menjalankan simulasi langsung di device/emulator via ADB. |

---

## 2. Langkah Analisis (Audit Methodology)

### A. Peta Proyek (Project Mapping)
1. **Identifikasi UI Engine**: Periksa apakah proyek menggunakan XML Layouts (`ViewBinding`/`DataBinding`), Jetpack Compose, atau hybrid.
2. **Folder Layout Spesifik**: Cek keberadaan folder responsif alternatif:
   - `res/layout-sw600dp/` atau `res/layout-sw720dp/` (Layout khusus tablet).
   - `res/layout-land/` (Layout khusus landscape).
   - `res/values-sw600dp/dimens.xml` atau `res/values/dimens.xml` (Penskalaan margin/padding).
3. **Manifest Inspection**:
   - Periksa `android:windowSoftInputMode` pada setiap Activity (`adjustResize` vs `adjustPan` vs default `adjustNothing`).
   - Periksa `android:screenOrientation` (apakah dikunci `portrait` atau mendukung rotasi).
4. **Edge-to-Edge & Insets**:
   - Cek implementasi `ViewCompat.setOnApplyWindowInsetsListener` atau Compose `WindowInsets.safeDrawing`. Pastikan status bar dan gesture bar tidak menutupi header atau floating action button (FAB).

---

### B. Daftar Pola Grep Berisiko (Static Code Inspection)

Gunakan perintah pencarian (grep/regex) untuk menemukan potensi cacat tampilan:

| Pola Regex / Kode | Lokasi Target | Alasan Risiko Tinggi |
| :--- | :--- | :--- |
| `layout_width="[0-9]+dp"` | `TextView`, `Button` | Lebar teks dikunci permanen. Jika teks diterjemahkan ke bahasa lain atau font scale dinaikkan, teks langsung terpotong. |
| `layout_height="[0-9]+dp"` | `LinearLayout`, `CardView`, item list | Tinggi kontainer statis. Konten internal yang membesar akan terpotong secara vertikal (*vertical clipping*). |
| `android:lines="1"` / `maxLines="1"` tanpa `android:ellipsize="end"` | `TextView` | Teks panjang terpotong kasar di ujung tanpa tanda elipsis (`...`), membingungkan pengguna. |
| `layout_width="wrap_content"` di dalam horizontal `LinearLayout` berbobot | `LinearLayout` (horizontal) | Menyebabkan perhitungan ganda (*double measurement pass*), dan rawan mendorong sibling view keluar dari batas layar. |
| `ScrollView` / `NestedScrollView` tanpa `android:fillViewport="true"` | Root ScrollView | Konten yang lebih pendek dari layar tidak akan meregang mengisi viewport, dan child dengan `match_parent` tidak berfungsi semestinya. |
| `MaterialCardView` bertumpuk dengan margin/padding negatif | Layout XML | Menyebabkan layout collision dan pemotongan drop shadow (elevation clipping). |
| Penggunaan `sp` untuk padding/margin, atau `dp` untuk `textSize` | Dimens / Layout | Melanggar standar Android: `sp` untuk ukuran tipografi (skala aksesibilitas), `dp` untuk dimensi ruang tata letak. |

---

### C. Pola Kritis yang Wajib Dibaca Manual (*Human-Eye Verification*)

Pola-pola ini paling sering lolos dari compiler tetapi menjadi **bug nyata yang fatal di perangkat**:

1. **Nama + Badge Sebaris (Horizontal Peer Collision)**:
   - *Kasus*: Nama pelanggan berdampingan dengan badge nominal atau status di baris yang sama.
   - *Cacat*: Jika nama panjang (misal *"Koperasi Simpan Pinjam Sejahtera Abadi"*), badge nominal terdorong hingga hilang dari layar kanan.
   - *Solusi*: Teks nama **wajib** memakai `layout_width="0dp"` + `layout_weight="1"` + `android:ellipsize="end"`, sedangkan badge memakai `layout_width="wrap_content"`.
2. **Header Judul + 2 Tombol Aksi**:
   - *Kasus*: Tombol Back di kiri, Judul di tengah, tombol Filter + Menu di kanan.
   - *Cacat*: Pada layar 320dp, judul menimpa ikon aksi atau judul terpotong menjadi 3 huruf.
3. **Kartu Berjajar dengan Tinggi Tetap (Grid / Row Inequality)**:
   - *Kasus*: Dua kartu berdampingan (misal Ringkasan Piutang & Ringkasan Hutang) diberi tinggi statis `layout_height="120dp"`.
   - *Cacat*: Pada font scale 150%, teks nominal dan subtitle bertumpuk atau angka miliaran terpotong.
4. **Bottom Bar Subtotal / Checkout CTA**:
   - *Kasus*: Subtotal harga diletakkan di sebelah kiri tombol "Bayar Sekarang" di bar bawah.
   - *Cacat*: Di layar 320dp, tombol tertekan sehingga teks tombol terpotong (*"BAY..."*).
5. **Dialog Form Input + ADJUST_NOTHING**:
   - *Kasus*: Dialog pop-up berisi `EditText` dengan tombol konfirmasi di bawah tanpa `adjustResize` dan tanpa `ScrollView`.
   - *Cacat*: Begitu keyboard lunak (IME) terbuka, tombol konfirmasi dan input bagian bawah tertutup 100% oleh keyboard sehingga pengguna tidak bisa menekan tombol aksi.

---

## 3. Format Laporan Standar

Setiap hasil audit wajib dilaporkan dengan struktur berikut:

```markdown
# 📋 LAPORAN AUDIT RESPONSIFITAS UI: [Nama Layar / Seluruh Aplikasi]

## 1. Kesimpulan Eksekutif
[Ringkasan 2–3 kalimat mengenai kelayakan tampilan, skor adaptabilitas layar, dan risiko fatal yang ditemukan.]

## 2. Hal yang Sudah Baik
- [Poin apresiasi komponen yang sudah elastis, implementasi scrolling yang tepat, atau insets yang rapi.]

## 3. Temuan Masalah (Diurutkan dari Dampak Terbesar)

### [STATUS] [SEVERITY: P1/P2/P3] [Judul Temuan]
- **Status Validasi**: `[BERISIKO]` (jika dari analisis statis) ATAU `[TERBUKTI]` (jika sudah terbukti lewat tes perangkat/screenshot).
- **Lokasi Kode**: [NamaFile.xml#L10-L25](file:///path/to/file#L10-L25)
- **Kondisi Terpicu**: Layar 320dp / Font 150% / Keyboard Terbuka / Landscape.
- **Hitungan Lebar Kasar (Math Proof)**:
  $$\text{Lebar Layar (320dp)} - \text{Padding Horizontal (32dp)} - \text{Badge (110dp)} = \text{Sisa Ruang (178dp)}$$
  Teks nama butuh ~230dp saat font 130% $\implies$ **OVERFLOW / TERPOTONG 52dp**.
- **Bukti Layar (Bila Uji Perangkat)**: [screenshot_name.png](file:///path/to/screenshot)
- **Rekomendasi Solusi**: [Solusi teknis ringkas dan konkret].
```

---

## 4. Resep Perbaikan Teruji (Battle-Tested Recipes)

Gunakan **satu resep pasti** per kategori masalah yang sudah terbukti berhasil di lapangan:

### Resep 1: Kartu Berjajar dengan Tinggi Seragam (Equal Height Cards)
- **Masalah**: Dua kartu berdampingan dalam baris memiliki panjang teks berbeda, menyebabkan tinggi kartu tidak seimbang atau terpotong jika dipaksa fixed height.
- **Solusi**:
  1. Hapus atribut `android:layout_height` fixed dari `MaterialCardView`.
  2. Set `android:layout_height="match_parent"` pada masing-masing CardView di dalam horizontal container.
  3. Pasang `android:minHeight="96dp"` (atau nilai wajar) **pada layout internal di dalam kartu** (bukan pada MaterialCardView terluar).
  4. Berikan bobot sama (`android:layout_width="0dp"`, `android:layout_weight="1"`).

### Resep 2: Pola Sebaris Teks Elastis + Elemen Kanan Statis
- **Masalah**: Teks menabrak elemen kanan saat nama panjang atau font besar.
- **Solusi**:
  ```xml
  <!-- Teks Utama Elastis -->
  <TextView
      android:layout_width="0dp"
      android:layout_height="wrap_content"
      android:layout_weight="1"
      android:maxLines="1"
      android:ellipsize="end"
      tools:text="Nama Debitur Panjang Sekali" />

  <!-- Elemen Kanan Statis / Nominal -->
  <TextView
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_marginStart="8dp"
      tools:text="Rp 1.000.000" />
  ```

### Resep 3: Dialog Anti-Terkubur Keyboard
- **Masalah**: Form input pada Dialog / BottomSheet tertutup keyboard.
- **Solusi**:
  1. Bungkus konten form dalam `NestedScrollView` atau `ScrollView` dengan atribut `android:fillViewport="true"` dan `android:isScrollContainer="true"`.
  2. Pastikan Activity induk di `AndroidManifest.xml` memiliki:
     ```xml
     android:windowSoftInputMode="adjustResize"
     ```
  3. Berikan bottom padding pada ScrollView: `android:paddingBottom="16dp"` dan `android:clipToPadding="false"`.

### Resep 4: Eliminasi CardView Shadow Clipping & Collisions
- **Masalah**: Sudut membulat card terpotong border atau drop shadow hilang saat bersentuhan dengan container.
- **Solusi**:
  1. Tambahkan `app:cardUseCompatPadding="true"` pada `MaterialCardView`.
  2. Berikan margin antar card minimal `4dp` s.d. `8dp`.
  3. Pasang `android:clipToPadding="false"` dan `android:clipChildren="false"` pada parent ViewGroup.

---

## 5. Playbook Pengujian Perangkat Nyata (ADB Device Testing)

Jalankan perintah ADB ini secara sistematis untuk memvalidasi UI langsung di layar HP atau Tablet:

### A. Simulasi Ukuran HP pada Layar Tablet
Jika menguji menggunakan Tablet fisik atau Emulator besar, simulasikan viewport HP kecil (320dp atau 360dp) tanpa perlu mengganti perangkat:
> [!IMPORTANT]
> **Catatan Orientasi Natural Tablet**: Tablet umumnya memiliki resolusi natural *landscape* (lebar > tinggi). Saat melakukan override `wm size`, masukkan koordinat dalam format **lebar x tinggi HP portrait** (misal `1080x2400` atau `720x1600`).

```bash
# 1. Catat resolusi & density asli
adb shell wm size
adb shell wm density

# 2. Simulasikan HP Standar 360dp (1080x2400 pada 420 dpi = 365dp width)
adb shell wm size 1080x2400
adb shell wm density 420

# 3. Simulasikan HP Sempit 320dp (720x1560 pada 320 dpi = 320dp width)
adb shell wm size 720x1560
adb shell wm density 320

# 4. RESET KEMBALI KE DEFAULT (Wajib dijalankan setelah selesai tes)
adb shell wm size reset
adb shell wm density reset
```

---

### B. Uji Skala Font Aksesibilitas (Font Scaling)
Uji apakah teks bertumpuk atau keluar batas saat pengguna mengaktifkan font besar di pengaturan sistem Android:

```bash
# Set Font Scale ke 130% (Besar)
adb shell settings put system font_scale 1.3

# Set Font Scale ke 150% (Sangat Besar - Aksesibilitas Lansia)
adb shell settings put system font_scale 1.5

# Set Font Scale ke 200% (Maksimal Android)
adb shell settings put system font_scale 2.0

# RESET KEMBALI NORMAL (100%)
adb shell settings put system font_scale 1.0
```

---

### C. Prosedur Install pada Perangkat Xiaomi / HyperOS / MIUI
Pada HP Xiaomi/Redmi, menjalankan `adb install` standar sering gagal dengan error `INSTALL_FAILED_USER_RESTRICTED` karena fitur keamanan MIUI. Gunakan jalur `push` + `pm install` lokal:

```bash
# 1. Push APK ke folder lokal tmp
adb push app/build/outputs/apk/debug/app-debug.apk /data/local/tmp/app-debug.apk

# 2. Install langsung lewat Package Manager internal
adb shell pm install -r -d /data/local/tmp/app-debug.apk

# 3. Bersihkan file installer sementara
adb shell rm /data/local/tmp/app-debug.apk
```

---

### D. Inspeksi Layout & Tangkapan Layar Otomatis
Lakukan dump struktur XML visual untuk memeriksa apakah koordinat elemen saling bertabrakan atau terlempar ke luar batas layar:

```bash
# 1. Dump UI Hierarchy ke file XML
adb shell uiautomator dump /sdcard/ui_inspect.xml
adb pull /sdcard/ui_inspect.xml ./ui_inspect.xml

# 2. Ambil bukti tangkapan layar (screenshot)
adb shell screencap -p /sdcard/screen_audit.png
adb pull /sdcard/screen_audit.png ./screen_audit.png

# 3. Monitor crash & fatal layout exception di logcat
adb logcat -d -s AndroidRuntime:E FATAL:E ViewRootImpl:E
```
