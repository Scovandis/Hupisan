---
name: qa-playbook
description: Standar Operasional Prosedur (SOP) QA Engineering Playbook v1.0 & Eksekusi Testing Langsung di Tablet/Perangkat Android. Mengatur analisis issue, klasifikasi bug, investigasi akar penyebab (Symptom, Suspected, Confirmed), severity & priority (S1-S4, P1-P4), kriteria eskalasi, evaluasi UI/UX & responsivitas tablet, functional testing (happy path, negative, boundary), serta workflow otomatisasi pengujian langsung ke tablet/device Android via ADB (buka aplikasi, inspeksi layout, tap, swipe, input text, capture screenshot bukti, monitor logcat/crash).
---

# QA ENGINEERING PLAYBOOK
## Standard Operating Procedure — Quality Assurance

**Versi:** 1.0  
**Status:** Standar internal QA  
**Berlaku untuk:** QA Engineer, Software Tester, QA Analyst, Developer, UI/UX Designer, Product Manager, Project Manager, dan stakeholder terkait.

---

# BAB 1 — TUJUAN DAN TANGGUNG JAWAB QA

## 1.1. Tujuan QA

QA bertanggung jawab memastikan produk:

1. Berjalan sesuai requirement.
2. Memenuhi aturan bisnis.
3. Memiliki desain yang konsisten dan mudah digunakan.
4. Tidak memiliki defect yang tidak dapat diterima berdasarkan risiko.
5. Tetap berfungsi setelah perubahan kode atau desain.
6. Menangani kondisi gagal dengan benar.
7. Memiliki bukti pengujian yang dapat ditelusuri.
8. Siap digunakan berdasarkan kriteria penerimaan dan risiko yang diketahui.

## 1.2. Perbedaan QA, QC, dan Testing

- **Quality Assurance (QA):** memastikan proses dan kontrol kualitas diterapkan sepanjang siklus pengembangan.
- **Quality Control (QC):** memeriksa apakah hasil produk memenuhi persyaratan kualitas.
- **Software Testing:** menjalankan pemeriksaan untuk menemukan defect dan memverifikasi perilaku sistem.

Dalam startup atau tim agile, satu orang bisa menjalankan beberapa fungsi tersebut. Namun, tanggung jawabnya tetap perlu dipahami agar QA tidak hanya berfokus pada pengujian di akhir proyek.

## 1.3. Aturan Utama QA

QA WAJIB:

- Memahami requirement sebelum menyimpulkan hasil pengujian.
- Menguji berdasarkan risiko, bukan hanya mengikuti alur normal.
- Menggunakan bukti untuk setiap temuan.
- Memisahkan fakta, dugaan, dan akar penyebab yang telah dikonfirmasi.
- Menilai dampak terhadap pengguna dan operasional.
- Memeriksa konsistensi UI/UX terhadap desain dan kebutuhan penggunaan.
- Menguji skenario gagal, batas nilai, serta kondisi tidak normal yang relevan.
- Melakukan retest setelah perbaikan.
- Melakukan regression testing pada area yang berpotensi terdampak.
- Mendokumentasikan hasil dan risiko yang belum terselesaikan.
- Memberikan rekomendasi berdasarkan bukti, bukan tekanan deadline.
- Mengeskalasi masalah kritis dengan segera.

> [!CAUTION]
> **QA tidak boleh menganggap suatu fitur berkualitas hanya karena berhasil dijalankan satu kali tanpa error.**

---

# BAB 2 — SOP ANALISIS ISSUE SEBELUM TESTING

Sebelum mulai menguji, QA harus memahami apa yang sedang diperiksa, mengapa issue muncul, dan hasil seperti apa yang seharusnya terjadi.

## 2.1. Analisis Konteks Issue

Setiap issue harus dianalisis melalui 10 pertanyaan berikut:

1. Apa masalah yang dilaporkan?
2. Siapa yang mengalami masalah?
3. Pada fitur atau proses apa masalah terjadi?
4. Apa hasil yang diharapkan (*expected result*)?
5. Apa hasil aktual (*actual result*)?
6. Apakah masalah dapat direproduksi?
7. Sejak kapan masalah terjadi?
8. Apakah masalah muncul setelah perubahan tertentu?
9. Apakah masalah terjadi pada semua pengguna atau hanya kondisi tertentu?
10. Apa dampaknya terhadap data, transaksi, tampilan, dan operasional?

QA tidak boleh langsung menyimpulkan bahwa laporan adalah bug sebelum memeriksa perilaku yang diharapkan.

## 2.2. Klasifikasi Temuan

Setiap temuan harus dikategorikan secara tepat:

| Kategori | Definisi | Contoh |
|---|---|---|
| **Bug / Defect** | Perilaku produk menyimpang dari requirement atau perilaku yang disepakati | Total saldo catatan salah hitung |
| **UI Defect** | Tampilan tidak sesuai desain atau mengganggu penggunaan | Tombol aksi terpotong, margin berantakan |
| **UX Issue** | Alur penggunaan membingungkan atau meningkatkan risiko kesalahan pengguna | Tombol hapus tidak memiliki dialog konfirmasi |
| **Requirement Gap** | Requirement tidak lengkap atau ambigu | Tidak ada penanganan saat koneksi internet offline |
| **Enhancement** | Usulan peningkatan di luar perilaku yang diwajibkan saat ini | Menambahkan filter pencarian baru |
| **Environment Issue** | Masalah disebabkan konfigurasi atau lingkungan pengujian | Database staging tidak sinkron atau error server |
| **Data Issue** | Data pengujian tidak valid atau tidak sesuai kondisi riil | ID arisan kosong atau relasi data corrupt |
| **Integration Issue** | Komunikasi antarsistem/layanan eksternal tidak sesuai kontrak | Respon payment gateway gagal diparsing |
| **Performance Issue** | Performa tidak memenuhi kebutuhan yang ditetapkan | List lambat dibuka saat ada ribuan data |
| **Security Issue** | Dugaan atau temuan kelemahan keamanan | Data sensitif tampil di log atau bisa diakses tanpa hak |

Jika klasifikasi belum pasti, QA harus menggunakan kategori sementara dan memperbaruinya setelah investigasi.

## 2.3. Validasi Sebelum Membuat Laporan Bug

Sebelum melaporkan bug, QA wajib memeriksa:

- [ ] Apakah requirement atau *expected behavior* tersedia?
- [ ] Apakah langkah reproduksi sudah benar dan terperinci?
- [ ] Apakah data pengujian valid?
- [ ] Apakah environment/build aplikasi sesuai?
- [ ] Apakah masalah dapat direproduksi ulang?
- [ ] Apakah terdapat log atau bukti pendukung (screenshot/logcat/screen recording)?
- [ ] Apakah masalah berasal dari aplikasi, konfigurasi, jaringan, perangkat, atau layanan eksternal?
- [ ] Apakah ada issue lain yang sudah melaporkan masalah yang sama (*duplicate check*)?

> [!NOTE]
> Jika masalah hanya terjadi satu kali (*intermittent*), jangan langsung menganggapnya tidak valid. Catat kejadian, bukti, kondisi sistem, memori, status jaringan, dan kemungkinan penyebabnya. Issue intermittent tetap wajib diinvestigasi.

---

# BAB 3 — SOP REPRODUKSI DAN INVESTIGASI BUG

## 3.1. Langkah Reproduksi

QA harus menyusun langkah yang dapat diikuti orang lain tanpa menebak:

Contoh format langkah reproduksi:
1. Buka aplikasi versi yang diuji (misal: build `my.notes.HHHH` v1.0).
2. Masuk ke halaman utama / menu transaksi atau catatan.
3. Pilih salah satu item data.
4. Lakukan pengeditan nominal atau status.
5. Simpan perubahan saat kondisi tertentu (misal: saat orientasi layar diputar atau koneksi offline).
6. Amati respon aplikasi dan data yang tersimpan.
7. Muat ulang halaman dan periksa konsistensi data di database lokal/server.

Langkah harus spesifik terhadap issue yang benar-benar diamati. Jangan menambahkan langkah yang tidak pernah dilakukan seolah-olah sudah terverifikasi.

## 3.2. Expected Result dan Actual Result

- **Expected result:** menjelaskan perilaku yang seharusnya terjadi berdasarkan requirement, desain, kontrak API, atau aturan bisnis yang disepakati.
- **Actual result:** menjelaskan apa yang benar-benar terjadi saat pengujian.

Contoh:
- *Expected:* Aplikasi menampilkan notifikasi peringatan dan tidak memproses penyimpanan jika nominal bernilai 0 atau negatif.
- *Actual:* Aplikasi menerima nominal negatif dan menyebabkan saldo total menjadi minus serta crash pada halaman analitik.

Actual result harus mencerminkan bukti yang ditemukan. Jangan menyatakan terjadi corrupt data tanpa bukti database atau log pendukung.

## 3.3. Bukti Pengujian

Bukti yang dapat dan wajib digunakan:
- Screenshot layar (dengan resolusi penuh).
- Screen recording video (untuk alur transaksi atau animasi bermasalah).
- Log aplikasi / Android Logcat (khususnya FATAL EXCEPTION, stack trace, error tags).
- Log server atau API request & response yang telah disanitasi.
- ID transaksi atau correlation ID.
- Versi aplikasi, build number, dan commit hash.
- Informasi perangkat: Model tablet, OS version, resolusi layar, dan DPI.
- Dump database atau query hasil inspeksi.

> [!WARNING]
> **Larangan Data Sensitif:** QA dilarang keras menyebarkan password, PIN, token otentikasi aktif, nomor kartu pembayaran asli, data identitas pribadi (PII), atau kredensial rahasia lainnya dalam screenshot atau laporan publik. Selalu sanitasi data sebelum dilampirkan.

## 3.4. Analisis Kemungkinan Akar Penyebab

QA harus membedakan tiga tingkat kesimpulan:
1. **Symptom:** Gejala yang terlihat di permukaan (contoh: dialog tertutup tiba-tiba atau layar freeze).
2. **Suspected cause:** Dugaan penyebab berdasarkan observasi awal (contoh: duplikasi lifecycle event atau NullPointerException saat binding).
3. **Confirmed root cause:** Akar penyebab yang telah dikonfirmasi melalui investigasi kode, breakpoint, atau trace log (contoh: query database berjalan di UI thread atau unhandled null value dari intent parameter).

> [!IMPORTANT]
> QA tidak boleh menyatakan dugaan sebagai akar penyebab final tanpa bukti teknis yang cukup.

## 3.5. Metode Analisis

Gunakan metode analisis yang sesuai:
- **5 Whys:** Menelusuri rantai sebab-akibat hingga ke sumber masalah.
- **Fishbone Diagram:** Mengelompokkan faktor penyebab (Environment, Code, Data, Device, Network).
- **Comparison Testing:** Membandingkan versi aplikasi yang berfungsi normal vs versi yang bermasalah.
- **Boundary Testing:** Menguji nilai batas ekstrem (min, max, batas memori, batas karakter).
- **Negative Testing:** Memberikan input dan kondisi yang sengaja dirancang salah/abnormal.
- **State Transition Testing:** Memeriksa perubahan status entitas (misal: draft -> aktif -> lunas -> diarsipkan).
- **Log Correlation:** Menghubungkan timestamp kejadian di UI perangkat dengan logcat dan server logs.

---

# BAB 4 — SOP PENILAIAN SEVERITY DAN PRIORITY

QA harus membedakan tingkat keparahan defect (*severity*) dari urgensi perbaikannya (*priority*).

## 4.1. Severity (Dampak Teknis & Fungsional)

| Tingkat | Kategori | Kriteria Umum |
|---|---|---|
| **S1** | **Critical** | Risiko serius terhadap keamanan, integritas transaksi atau data, aplikasi crash total/freeze tanpa workaround, atau alur utama (*blocker*) tidak bisa dilanjutkan |
| **S2** | **High** | Fungsi penting gagal atau menghasilkan data salah, berdampak pada banyak pengguna, proses utama terganggu tanpa alternatif yang mudah |
| **S3** | **Medium** | Fungsi sekunder terganggu tetapi tersedia alternatif (*workaround*) yang layak; dampak terbatas pada alur spesifik |
| **S4** | **Low** | Dampak kecil, ketidakkonsistenan visual minor, typo teks, atau glitch kosmetik yang tidak mengganggu fungsi |

## 4.2. Priority (Urutan Penanganan)

| Tingkat | Makna | Target Penanganan |
|---|---|---|
| **P1** | Harus segera ditangani (*immediate hotfix*) | Dikerjakan seketika, menghentikan deployment jika belum rilis |
| **P2** | Harus diprioritaskan dalam sprint berjalan | Dikerjakan sebelum fitur baru |
| **P3** | Dapat dijadwalkan sesuai kapasitas sprint | Dimasukkan ke backlog normal |
| **P4** | Dapat ditunda / improvement minor | Dikerjakan bila ada waktu luang atau saat revamp |

> [!TIP]
> **Severity vs Priority:** Bug visual (S4) bisa memiliki Priority P1 jika terjadi pada logo atau harga di layar presentasi tablet eksekutif/klien. Sebaliknya, crash pada fitur tersembunyi yang jarang digunakan (S2) bisa memiliki Priority P3 jika risikonya sudah disetujui stakeholder.

## 4.3. Kriteria Eskalasi Segera

QA **wajib segera mengeskalasi** ke Tech Lead dan PM jika menemukan:
- Terjadi transaksi ganda atau kegagalan pencatatan keuangan.
- Perhitungan saldo, bunga, atau nominal pembayaran keliru.
- Kehilangan data (*data loss*) atau kerusakan database lokal/server.
- Potensi akses tanpa otorisasi (*unauthorized access*) atau kebocoran data.
- Fatal crash berulang yang memblokir alur utama aplikasi.
- Masalah performa berat (memory leak progresif atau freeze/ANR lebih dari 5 detik).

---

# BAB 5 — SOP QA UNTUK DESIGN DAN UI/UX

Pemeriksaan desain mencakup kesesuaian spesifikasi, konsistensi komponen, tata letak tablet, dan kenyamanan pengguna.

## 5.1. Design Compliance
Periksa kesesuaian dengan design system/Figma:
- Posisi, margin, padding, dan alignment elemen.
- Ukuran komponen dan konsistensi ikonografi.
- Palet warna dan hierarki tipografi.
- Bentuk sudut (corner radius), border, dan shadow elevation.
- State interaksi: Normal, Focused, Pressed/Tapped, Disabled, Loading, Error, dan Success.

## 5.2. Layout dan Responsivitas (Khusus Tablet & Layar Lebar)
Khusus pengujian pada perangkat tablet, QA wajib memeriksa:
- **Pemanfaatan Ruang Layar:** Apakah layout memanfaatkan lebar layar tablet dengan baik (misal master-detail layout) atau hanya stretched memanjang tidak proporsional?
- **Orientasi:** Pengujian wajib dilakukan pada mode **Portrait** dan **Landscape**.
- **Keyboard Overlay:** Pastikan keyboard software tidak menutupi input field aktif atau tombol submit utama.
- **Overflow & Scrolling:** Pastikan tidak ada teks/tombol yang terpotong (*clipped*) atau bertumpuk (*overlapping*).
- **Display Scaling:** Uji dengan setelan ukuran font sistem Android: Default, Large, dan Largest.

## 5.3. Typography dan Readability
- Hierarki judul, subjudul, teks isi, dan catatan harus jelas dan kontras.
- Format angka, mata uang (contoh: format Rupiah `Rp 1.000.000`), tanggal, dan waktu harus konsisten di semua layar.
- Pesan kesalahan (*error message*) harus manusiawi, jelas, dan memberikan panduan solusi.

## 5.4. Usability
- Aksi berisiko tinggi (misal: Hapus Data, Reset, Batalkan Transaksi) **wajib memiliki dialog konfirmasi** sebelum dieksekusi.
- Setiap aksi asynchronous (proses penyimpanan, sinkronisasi) wajib menampilkan indikator loading.
- Pengguna harus selalu mengetahui status berhasil (*toast/snackbar/dialog*) atau gagal dari aksinya.

## 5.5. Accessibility
- Target sentuh (*touch target size*) minimal **48dp x 48dp** agar mudah ditekan di layar tablet.
- Rasio kontras teks terhadap latar belakang minimal 4.5:1 untuk teks normal.
- Elemen penting memiliki atribut `contentDescription` untuk pembaca layar (*screen reader*).

---

# BAB 6 — SOP FUNCTIONAL TESTING

## 6.1. Happy Path
Menguji skenario ideal di mana pengguna mengikuti alur normal dengan input valid dan lingkungan sistem tanpa gangguan:
- Input valid lengkap terisi.
- Koneksi internet dan perangkat eksternal stabil.
- Data berhasil tersimpan dan tampil konsisten di layar detail dan list.

## 6.2. Negative Testing
Menguji ketahanan sistem saat menerima input salah atau kondisi abnormal:
- Input field kosong atau hanya spasi (*whitespace*).
- Tipe data salah (misal huruf pada field angka).
- Koneksi internet tiba-tiba diputus saat proses simpan berlangsung (*network failure*).
- Menekan tombol simpan berkali-kali secara cepat (*rapid multi-tap / double submit*).
- Menekan tombol sistem Android *Back* atau *Home* di tengah proses transaksi.
- Aplikasi dipaksa berhenti (*force stop*) saat proses berjalan, lalu dibuka kembali.

## 6.3. Boundary Testing (Batas Nilai)
Memeriksa batas nilai minimum, maksimum, dan ambang batas ekstrim:
- Nilai minimum yang diizinkan (misal nominal 1 atau 0).
- Nilai nol (`0`), nilai negatif (`-1`), atau bilangan desimal pecahan.
- Nilai maksimum angka (batas tipe data integer `2,147,483,647` atau batas nominal bisnis).
- Panjang karakter teks: 0 karakter, 1 karakter, batas maksimal (misal 255 karakter), dan teks sangat panjang (1000+ karakter).
- Karakter khusus, simbol, dan emoji (contoh: `!@#$%^&*()_+{}[]:;"'<>?,./~` dan emoji multi-byte).

## 6.4. State Transition & Concurrency
- Verifikasi transisi status entitas (misal: Status Hutang dari *Belum Lunas* -> *Sebagian* -> *Lunas*).
- Pastikan tidak ada transisi status yang tidak valid atau terlewat.
- Uji concurrency jika dua proses mencoba memodifikasi data yang sama secara bersamaan.

---

# BAB 7 — PANDUAN EKSEKUSI PENGUJIAN LANGSUNG PADA TABLET ANDROID (LIVE ADB TESTING)

Skill ini dirancang agar QA / Agent dapat **langsung berinteraksi dan menguji aplikasi yang terinstall pada tablet Android** yang terhubung via USB Debugging atau Wi-Fi ADB.

## 7.1. Persiapan Koneksi Tablet & ADB

1. **Lokasi ADB:**
   Default lokasi SDK di Windows:
   `C:\Users\andre\AppData\Local\Android\Sdk\platform-tools\adb.exe`
2. **Cek Koneksi Tablet:**
   Jalankan pemeriksaan perangkat yang terhubung:
   ```powershell
   & "C:\Users\andre\AppData\Local\Android\Sdk\platform-tools\adb.exe" devices -l
   ```
   *Catatan:* Jika tablet terhubung via Wi-Fi, hubungkan dengan:
   ```powershell
   & "$adb" connect <IP_TABLET>:<PORT>
   ```

## 7.2. Deteksi Spesifikasi Tablet

Sebelum menguji, kumpulkan informasi lingkungan pengujian:
```powershell
# Model Tablet
& "$adb" shell getprop ro.product.model

# Versi Android & SDK
& "$adb" shell getprop ro.build.version.release

# Resolusi Layar Tablet
& "$adb" shell wm size

# Density Layar
& "$adb" shell wm density
```

## 7.3. Kontrol Lifecycle Aplikasi di Tablet

1. **Mengecek apakah aplikasi sudah terinstall:**
   ```powershell
   & "$adb" shell pm list packages | Select-String "my.notes.HHHH"
   ```
2. **Membuka Aplikasi di Tablet:**
   ```powershell
   # Membuka Launcher Activity
   & "$adb" shell am start -n my.notes.HHHH/.view.spalshscreen.SplashScreenActivity
   
   # Atau menggunakan Intent Launcher universal:
   & "$adb" shell monkey -p my.notes.HHHH -c android.intent.category.LAUNCHER 1
   ```
3. **Mengecek Activity yang Sedang Aktif di Layar Tablet:**
   ```powershell
   & "$adb" shell dumpsys window windows | Select-String "mCurrentFocus|mFocusedApp"
   ```
4. **Menghentikan (Kill/Stop) Aplikasi:**
   ```powershell
   & "$adb" shell am force-stop my.notes.HHHH
   ```
5. **Mereset Data Aplikasi (Fresh State Testing):**
   ```powershell
   & "$adb" shell pm clear my.notes.HHHH
   ```

## 7.4. Inspeksi UI & Pengambilan Bukti Visual

1. **Mengambil Screenshot Layar Tablet (Bukti Pengujian):**
   ```powershell
   & "$adb" exec-out screencap -p > bukti_layar_tablet.png
   ```
2. **Mengambil Dump Hirarki UI (Menemukan Koordinat Elemen):**
   ```powershell
   & "$adb" shell uiautomator dump /data/local/tmp/dump.xml
   & "$adb" pull /data/local/tmp/dump.xml ./dump.xml
   ```
   *Cari bounds elemen pada XML: `bounds="[startX,startY][endX,endY]"`. Koordinat sentuh tengah elemen adalah:*
   - `X = (startX + endX) / 2`
   - `Y = (startY + endY) / 2`

## 7.5. Eksekusi Tindakan Pengujian Fisik (Interaksi Sentuh)

1. **Tap / Sentuh Elemen:**
   ```powershell
   & "$adb" shell input tap <X> <Y>
   ```
2. **Scroll / Swipe (Navigasi Halaman Tablet):**
   ```powershell
   # Scroll ke bawah (swipe ke atas)
   & "$adb" shell input swipe <X> <Y_bawah> <X> <Y_atas> 500
   
   # Scroll ke atas (swipe ke bawah)
   & "$adb" shell input swipe <X> <Y_atas> <X> <Y_bawah> 500
   ```
3. **Mengisi Teks ke Input Form:**
   *Pastikan input field sudah dalam kondisi terfokus (sudah di-tap).*
   ```powershell
   # Spasi diganti dengan %s
   & "$adb" shell input text "Catatan%sTesting%sOtomatis"
   ```
4. **Menekan Tombol Navigasi Sistem:**
   - Tombol **Back:** `& "$adb" shell input keyevent 4`
   - Tombol **Home:** `& "$adb" shell input keyevent 3`
   - Tombol **Enter / Submit:** `& "$adb" shell input keyevent 66`
   - Tombol **Tab:** `& "$adb" shell input keyevent 61`

## 7.6. Pemantauan Logcat & Deteksi Crash / ANR

1. **Membersihkan buffer log sebelum uji coba:**
   ```powershell
   & "$adb" logcat -c
   ```
2. **Merekam error dan crash selama pengetesan:**
   ```powershell
   & "$adb" logcat -d -v time *:E > error_logcat.txt
   ```
3. **Mendeteksi FATAL EXCEPTION secara spesifik:**
   ```powershell
   & "$adb" logcat -d | Select-String -Pattern "FATAL EXCEPTION|ANR in|AndroidRuntime"
   ```

## 7.7. Menggunakan Skrip Otomasi Bawaan (`tablet_tester.ps1`)

Skill ini dilengkapi dengan helper script di folder `scripts/tablet_tester.ps1` untuk mempermudah eksekusi:

```powershell
# Cek perangkat tablet terhubung
.\scripts\tablet_tester.ps1 -Action check-device

# Lihat spesifikasi layar dan OS tablet
.\scripts\tablet_tester.ps1 -Action specs

# Buka aplikasi di tablet
.\scripts\tablet_tester.ps1 -Action launch -Package "my.notes.HHHH"

# Ambil screenshot kondisi saat ini
.\scripts\tablet_tester.ps1 -Action screenshot -OutputFile "bukti_home.png"

# Tap tombol tertentu
.\scripts\tablet_tester.ps1 -Action tap -X 540 -Y 960

# Input teks
.\scripts\tablet_tester.ps1 -Action type -Text "Catatan%sBaru"

# Tekan tombol Back
.\scripts\tablet_tester.ps1 -Action key -KeyCode 4

# Simpan logcat jika terjadi error
.\scripts\tablet_tester.ps1 -Action get-logs -OutputFile "error_log.txt"
```

---

# BAB 8 — TEMPLATE DOKUMENTASI DEFECT & LAPORAN QA

Setiap bug atau hasil pengujian yang ditemukan wajib didokumentasikan menggunakan format standar berikut:

```markdown
# [BUG] <Judul Defect yang Singkat dan Jelas>

- **ID Defect:** DEF-<TANGGAL>-<NO>
- **Kategori:** Bug / Defect | UI Defect | UX Issue | Requirement Gap | Performance
- **Severity:** S1 (Critical) | S2 (High) | S3 (Medium) | S4 (Low)
- **Priority:** P1 (Immediate) | P2 (High) | P3 (Medium) | P4 (Low)
- **Status:** New / In Investigation / Ready for Retest / Closed

### 1. Lingkungan Pengujian
- **Aplikasi / Package:** my.notes.HHHH
- **Versi / Build:** v1.0 (Commit <hash>)
- **Perangkat:** Tablet <Model> (Android <Versi>)
- **Resolusi & Density:** <Lebar>x<Tinggi>, <DPI>
- **Orientasi:** Landscape / Portrait

### 2. Langkah Reproduksi
1. Buka aplikasi di tablet.
2. Navigasi ke menu <Nama Menu>.
3. Tekan tombol <Nama Tombol> pada koordinat (<X>, <Y>).
4. Masukkan data: <Input Data>.
5. Tekan tombol Simpan.

### 3. Hasil Pengamatan
- **Expected Result:** <Perilaku yang seharusnya terjadi sesuai requirement>
- **Actual Result:** <Perilaku aktual yang terjadi saat pengujian>

### 4. Analisis Penyebab
- **Symptom:** <Gejala yang terlihat di layar tablet>
- **Suspected Cause:** <Dugaan teknis awal berdasarkan perilaku/log>
- **Confirmed Root Cause:** <Akar penyebab yang telah diverifikasi (atau isi 'Menunggu investigasi developer')>

### 5. Bukti Pengujian
- **Screenshot / Video:** [Lihat Bukti](file:///path/to/screenshot.png)
- **Cuplikan Logcat / Error:**
  ```text
  FATAL EXCEPTION: main
  java.lang.NullPointerException: ...
  ```

### 6. Rekomendasi Solusi QA
- <Saran perbaikan penanganan kondisi gagal, validasi input, atau perbaikan UI>
```
