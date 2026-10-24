# Contoh plugin Dolphy

Setiap file `.dolphyplugin` dapat dibuka langsung dengan Dolphy. Periksa source code Python yang dapat dibaca dan izin yang diminta sebelum instalasi.

Plugin adalah kode buatan pengguna yang dapat dieksekusi dan memiliki akses luas ke kapabilitas aplikasi serta perangkat. Instal plugin hanya dari sumber tepercaya dan periksa source code-nya sebelum digunakan. Pengembang Dolphy tidak bertanggung jawab atas kerusakan yang disebabkan oleh plugin berbahaya, termodifikasi, atau tidak tepercaya.

## Contoh pemindai

### Wi-Fi Scanner MD3E

[Unduh plugin](wifi_scanner_md3e.dolphyplugin)

Menggunakan bridge perangkat Dolphy native dan UI Material 3 Expressive. Plugin menampilkan koneksi saat ini, jaringan di sekitar, SSID dan BSSID, RSSI, persentase sinyal, jenis keamanan, band, frekuensi, channel, lebar channel, serta flag kapabilitas mentah. Daftar mendukung filter Semua, Aman, dan Terbuka.

Grup izin yang diperlukan: `wifi`.

### BLE Scanner DEX MD3E

[Unduh plugin](ble_scanner_dex_md3e.dolphyplugin)

Menggunakan pemindai BLE batch native dan memuat class DEX tersemat dari file plugin teks yang sama. Analisis DEX memperkaya advertising dengan kualitas sinyal, perkiraan jarak, ukuran payload, jenis advertising, dan nama produsen umum. Layar detail mencakup alamat, RSSI, TX power, kemampuan koneksi, UUID service, record produsen, dan payload mentah.

Grup izin yang diperlukan: `bluetooth`, `dex`.

Class tersemat adalah `com.dolphy.examples.ble.BleAnalyzer`. Source code Java yang dapat dibaca tersedia di [`dex-source`](dex-source/com/dolphy/examples/ble/BleAnalyzer.java). Plugin memverifikasi DEX tersemat dengan SHA-256 sebelum memuatnya.
