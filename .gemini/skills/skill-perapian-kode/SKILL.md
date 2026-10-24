---
name: skill-perapian-kode
description: Panduan dan aturan kerja untuk merapikan kode hasil perubahan, menghapus komentar tambahan, menyusun struktur class, meng-inline fungsi private kecil, dan membuang kode mati tanpa mengubah perilaku.
---

# SKILL_PERAPIAN_KODE — Panduan Merapikan Kode Hasil Perubahan

Aturan kerja untuk merapikan kode yang **ditambahkan** oleh suatu pekerjaan: menghapus komentar tambahan, menyusun
struktur class, meng-inline fungsi private kecil, dan membuang kode mati, **tanpa mengubah perilaku**.

Cakupannya fleksibel: bisa untuk class tertentu, commit tertentu, rentang commit, atau gabungannya.

---

## 1. Parameter (semua opsional, boleh digabung)

| Parameter | Isi | Contoh |
|---|---|---|
| `target` | Nama class, path file, atau daftar | `<NamaClass>`, `<path/ke/File.kt>` |
| `dari` | Commit/tag/branch awal (**termasuk** commit itu). Baseline = `<dari>^` | `<hash-commit>` |
| `sampai` | Commit akhir | default `HEAD` |
| `aturan` | Subset: `komentar`, `struktur`, `inline`, `mati` | default: semua |
| `verifikasi` | `build`, `unit`, `device` | default: `build` + `unit`; `device` bila menyentuh alur hardware/pembayaran |

Cara menentukan cakupan dari kombinasi parameter:

| Yang disebut user | File yang dirapikan | Baseline kode "tambahan" |
|---|---|---|
| hanya `target` | file/class itu | `git merge-base HEAD master` (semua yang ditambahkan branch ini) |
| hanya `dari` / rentang commit | `git diff --name-only <dari>^ <sampai>` (hanya `.kt`/`.java`, bukan file generated) | `<dari>^` |
| `target` + `dari` | file/class itu saja | `<dari>^` |
| tidak ada yang jelas | **tanya satu pertanyaan** (jangan menebak) | - |

Contoh permintaan: "rapikan class `<NamaClass>` sejak commit `<hash>`", "rapikan commit `<hash-a>..<hash-b>`",
"hapus komentar tambahan di `<File>`", "rapikan semua yang ditambahkan branch ini".

Hanya baris yang **ditambahkan di dalam cakupan** yang boleh disentuh. Kode/komentar yang sudah ada sebelum baseline
tidak diubah.

---

## 2. Aturan

### A. Komentar tambahan (`komentar`)
- Hapus komentar `//`, `/* ... */`, dan `/** ... */` yang **ditambahkan** dalam cakupan, termasuk komentar di akhir baris kode.
- Pertahankan: direktif tool/compiler (`@Suppress`, `//noinspection`, `// language=`, `// region`), header lisensi, dan
  baris `Log.*` (logcat di luar cakupan).
- Tidak menyentuh: komentar yang sudah ada sebelum baseline, serta kode lama yang dikomentari.
- Gaya mengikuti sekitar: kalau file itu memang tidak berkomentar, hasilnya juga tidak berkomentar.

### B. Struktur class (`struktur`)
Urutan: **properti/field → objek properti (receiver, Runnable, callback) → interface/tipe dalam → fungsi →
`companion object` / blok static di paling bawah**.
- Mode class (user menyebut class tanpa batas commit): seluruh class disusun mengikuti urutan itu.
- Mode commit (user menyebut commit/rentang): hanya anggota yang ditambahkan dalam cakupan yang diposisikan sesuai
  urutan; anggota lama tidak dipindah.
- Pemindahan hanya mengubah posisi, bukan isi. Urutan antar fungsi yang sudah ada dibiarkan.

### C. Inline fungsi private kecil (`inline`)
Sebuah fungsi di-inline ke pemanggilnya bila **semua** terpenuhi:
- `private`, ditambahkan dalam cakupan, dan pendek (sekitar ≤ 10 baris);
- dipanggil **1–3 kali**;
- tidak dipakai lintas file dan bukan API publik.

Tetap sebagai fungsi: yang dipanggil > 3 kali, fungsi besar, helper logging, dan semua fungsi yang sudah ada sebelum
cakupan. Saat meng-inline jaga: `return` (lokal vs non-lokal), `synchronized`/lock, ruang lingkup `try/catch`, nama variabel
yang bentrok, dan urutan efek samping. Dua panggilan yang sama boleh digabung jadi loop kecil bila perilakunya identik.

### D. Kode mati (`mati`)
Properti, fungsi, parameter, dan import yang **ditambahkan dalam cakupan** dan sudah tidak punya referensi (`grep -rnw`
= 0) dihapus.

### E. Selalu berlaku
1. **API publik tidak berubah**: signature, `@JvmStatic`, `@JvmOverloads`, dan semua pemanggil (Kotlin **dan** Java)
   tetap kompilasi.
2. **Perilaku tidak berubah.** Bila menemukan bug, jangan diperbaiki di sini; catat di laporan.
3. Tidak ada reformat massal, rename publik, atau perubahan logika. Diff hanya berisi hal yang diminta aturan.
4. Tidak menyentuh `Log.*`, `logState()`, `callerHint()`.

---

## 3. Prosedur

1. **Tetapkan cakupan** (bagian 1) dan catat `HEAD` awal. Pastikan file cakupan bersih (`git status --short`).
2. **Survei**, lalu tampilkan ringkasan ke user: file, baseline, jumlah komentar tambahan, anggota yang salah urut,
   kandidat inline (nama + jumlah pemanggil), kandidat kode mati.
3. **Kerjakan berurutan** `komentar` → `struktur` → `inline` → `mati`. Setelah tiap aturan, kompilasi. Default **satu commit
   per aturan** agar mudah di-revert; satu commit gabungan hanya bila user minta.
4. **Verifikasi** (bagian 5) lalu **lapor** (bagian 6).
5. Jangan `git push`. Jangan membuat task/PRD Sera-App kecuali diminta (arahkan ke `sera-scrum:workflow prd` bila user
   mau pekerjaan ini tercatat).

---

## 4. Perintah bantu

JAVA_HOME dan perintah gradle mengikuti `skill_generate_apk.md` (`C:\Program Files\Android\Android Studio\jbr`).

```bash
BASE=$(git merge-base HEAD master)              # default bila hanya target; atau BASE=<dari>^
FILES="<path/File1.kt> <path/File2.java>"

# komentar tambahan (baris penuh + di akhir baris kode)
git diff -U0 $BASE HEAD -- $FILES | grep -E '^\+' | grep -vE '^\+\+\+' | grep -E '^\+\s*(//|/\*|\*)|\S\s+//'

# urutan blok class
grep -nE '^(open )?class |^    (@Volatile |@JvmOverloads |private |fun |interface |companion)' $FILES

# jumlah pemanggil sebuah fungsi / properti
grep -rnw "namaFungsi" app/src --include=*.kt --include=*.java

# kompilasi Kotlin + Java, lalu unit test class terkait
./gradlew :app:compileStagingDebugKotlin :app:compileStagingDebugJavaWithJavac
./gradlew :app:testStagingDebugUnitTest --tests "pkg.NamaTest"
```

---

## 5. Verifikasi

| Bukti | Syarat lulus |
|---|---|
| Diff | perintah komentar di atas tidak menghasilkan baris; `grep -rnw` kode mati = 0 |
| Build | Kotlin **dan** Java kompilasi (pemanggil lintas bahasa ikut diperiksa) |
| Unit test | semua test class terkait hijau (sebutkan n/n) |
| Device | untuk alur hardware/pembayaran: build staging debug, install, ulangi skenario regresi area itu dan bandingkan dengan hasil sebelum perapian |

Skenario device dipilih sesuai area kode yang dirapikan; ambil dari alur yang dilewati kode itu, misalnya: membuka dan
menutup layar terkait, transaksi sukses dan batal, aplikasi ke background lalu kembali, kehilangan koneksi lalu pulih,
serta perangkat atau izin dicabut saat proses berjalan. Minta bantuan user bila butuh aksi fisik di perangkat.

Bila device tidak tersedia, tulis jelas bahwa verifikasi device **tidak dilakukan**.

---

## 6. Format laporan

Tabel singkat per aturan:

| Aturan | Hasil | Bukti |
|---|---|---|
| Komentar | 0 baris tersisa (n dihapus) | keluaran perintah diff |
| Struktur | anggota yang dipindah | urutan blok |
| Inline | fungsi yang di-inline / yang dipertahankan beserta alasannya | jumlah pemanggil |
| Kode mati | yang dihapus | `grep` = 0 |
| Build / test / device | lulus / n dari n / skenario | log |

Sertakan juga: baseline yang dipakai, commit yang dibuat, hal yang sengaja **tidak** disentuh, dan temuan di luar
cakupan (bug, dsb.).

---

## 7. Hal yang tidak boleh

- Menghapus atau mengubah `Log.*`.
- Menyentuh komentar atau kode lama di luar cakupan.
- Mengubah `synchronized`, lock, atau urutan efek samping saat meng-inline.
- Memformat ulang seluruh file atau mengganti nama API publik.
- Menggabungkan perapian dengan perbaikan bug dalam satu commit.
- Push otomatis.

---

## 8. Contoh isi laporan

Permintaan: "rapikan class `<NamaClass>` sejak commit `<hash>`".
- Cakupan: `<File1>`, `<File2>` (`dari=<hash>`, baseline `<hash>^`).
- Komentar: n baris dihapus, 0 tersisa. Struktur: `<properti>` dipindah ke blok properti.
- Inline: `<fungsiA>`, `<fungsiB>` (masing-masing 1–2 pemanggil) digabung ke pemanggilnya; `<fungsiC>` dipertahankan
  karena 4 pemanggil. Kode mati: `<properti>` dihapus (0 referensi).
- Bukti: build Kotlin+Java lulus, n/n unit test hijau, skenario device `<daftar>` sama dengan sebelum perapian.
