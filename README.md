# Stok Barang Android

Aplikasi inventaris material proyek berbasis Java dan SQLite (offline).

## Fitur
- Dashboard total material dan stok minimum
- 200 data awal material konstruksi (40 kelompok x 5 ukuran/varian)
- Master data: tambah, edit, cari, nonaktifkan
- Transaksi stok masuk dan keluar, termasuk validasi stok
- Riwayat transaksi
- Laporan stok dan ekspor CSV melalui penyimpanan Android

## Build di AndroidIDE
1. Ekstrak ZIP dan buka folder `StockBarang-GitHub` sebagai project.
2. Pastikan SDK Platform 29 dan Build Tools 29.0.3 tersedia.
3. Jalankan `assembleDebug`.
4. APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Build otomatis di GitHub
Upload seluruh isi folder ke repository. Workflow di `.github/workflows/android.yml` membangun APK pada setiap push ke `main`. Buka tab **Actions**, pilih workflow yang selesai, lalu unduh artifact **StokBarang-debug-apk**.

- Package: `com.example.stokbarang`
- Minimum Android: 6.0 (API 23)
- Versi: 1.0.0

## Catatan
Ini versi awal. Database lokal di perangkat; belum ada login, sinkronisasi cloud, atau backup/restore database. Data awal adalah data contoh dan dapat disunting.
