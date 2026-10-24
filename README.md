# Dolphy

Dolphy adalah multitool Android yang andal untuk riset protokol nirkabel, pengujian keamanan siber, dan emulasi perangkat keras. Aplikasi ini menghadirkan fungsi utilitas tingkat lanjut yang berfokus pada perangkat keras ke perangkat Android standar, sepenuhnya tanpa akses root.

Kanal Telegram rilis Dolphy — https://t.me/Dolphy_app_official

## Proyek ini dilindungi lisensi; harap ditinjau!

## Fitur & Modul

### 🚨 Infrared (IR)
- **Remote Universal:** Integrasi database lengkap yang mendukung TV, AC, sistem audio, dan proyektor (disusun dari Flipper-IRDB-main dan flipperzero-firmware-dev).
- **IR-Storm:** Modul otomatis untuk mengirim sinyal pemadaman secara berurutan ke beberapa target perangkat keras.
- **IR Jammer:** Mencegah perangkat remote control di sekitar berinteraksi dengan perangkat keras terdekat.

### 📶 Bluetooth & BLE
- **Pemindai Perangkat Audio:** Alat khusus untuk menemukan, menguji, dan terhubung ke berbagai perangkat keras audio Bluetooth, speaker, dan headphone.
- **Pemindai NRF Lanjutan:** Penemuan perangkat Bluetooth Low Energy (BLE) dan analisis paket tingkat profesional.
- **Dolphy Chat:** Aplikasi chat lokal yang beroperasi sepenuhnya melalui protokol BLE dengan pengguna Dolphy lain.
- **Keyboard & Mouse HID:** Lapisan emulasi perangkat keras yang memungkinkan smartphone berfungsi sebagai keyboard atau mouse Bluetooth nirkabel.
- **Bluetooth Jammer:** Modul stress testing yang menggunakan L2CAP flood untuk mengevaluasi ketahanan jaringan nirkabel.

### 📡 NFC & Alat QR
- **Instrumen NFC:** Modul tingkat rendah untuk membaca, menulis, dan berinteraksi dengan berbagai tag serta kartu NFC.
- **Alat QR:** Pembuatan kode QR yang cepat dan mekanisme khusus untuk kontrol audio jarak jauh.

### 🌐 Pengujian Jaringan & Wi-Fi
- **Serangan Wi-Fi:** Lingkungan diagnostik jaringan bawaan untuk pemindaian jaringan nirkabel, simulasi Wi-Fi DoS, dan brute force kredensial.
- **Pemindai LAN:** Alat penemuan cepat untuk memetakan seluruh host dan perangkat yang terhubung dalam subnet Wi-Fi lokal saat ini.

### 📺 Protokol Media
- **SmartTV Cast:** Dukungan streaming dan broadcasting media native melalui protokol Chromecast, DIAL, DLNA, dan UPnP.

## Pengembangan & Arsitektur

Aplikasi dikembangkan dari awal menggunakan stack Java dan Kotlin modern.

## Build APK Bertanda Tangan melalui GitHub Actions

Workflow `.github/workflows/build-apk.yml` menjalankan `:app:assembleRelease` dengan JDK 17 dan Gradle 8.11.1 pada setiap push ke `main` atau melalui **Run workflow**. APK hasil build diunggah sebagai artifact `dolphy-release-apk`.

Signing release memakai empat GitHub Actions Secrets berikut:

- `DOLPHY_KEYSTORE_BASE64`
- `DOLPHY_KEYSTORE_PASSWORD`
- `DOLPHY_KEY_ALIAS`
- `DOLPHY_KEY_PASSWORD`

Keystore yang sama harus disimpan dan digunakan kembali untuk setiap rilis. Jangan membuat keystore baru pada setiap build karena perubahan keystore akan mengubah signature aplikasi dan dapat menghalangi update. Jika keempat secret tidak tersedia, konfigurasi Gradle menggunakan debug signing untuk build lokal.

## Kredit & Terima Kasih

Terima kasih kepada para pengembang dan peneliti yang membantu mewujudkan proyek ini:

- ZalexDev
- ars3nb
- Astrocodee
