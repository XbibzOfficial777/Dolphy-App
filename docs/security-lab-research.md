# Riset defensif Dolphy Security Lab

**Tanggal akses sumber:** 31 Oktober 2026  
**Ruang lingkup:** perangkat dan router milik pengguna di laboratorium.  
**Batas:** dokumen dan implementasi ini tidak membuat PIN generation, wordlist, percobaan autentikasi otomatis, injeksi lock screen, atau koneksi WPS dengan PIN hasil tebakan.

## 1. Kesimpulan implementasi

Fitur yang aman dan berguna untuk laboratorium adalah **Security Lab** dengan empat kemampuan:

1. **Diagnostik USB OTG read-only** melalui `UsbManager`: dukungan USB host, daftar perangkat, VID/PID, kelas, jumlah interface, jumlah interface HID, dan status izin. Tidak membuka koneksi maupun mengirim laporan HID.
2. **Bidang keyboard lokal** untuk memeriksa input keyboard USB/Bluetooth pada layar milik aplikasi. Isinya tidak disertakan dalam laporan dan tidak diteruskan ke perangkat lain.
3. **Lembar audit WPS manual** dengan jawaban Ya/Tidak/Belum tahu. Jawaban berasal dari halaman admin router; Dolphy tidak mengklaim membaca konfigurasi router secara otomatis.
4. **Catatan observasi lockout dan laporan yang dapat dibagikan**, ditambah tautan dokumentasi resmi dan pemulihan Android. Tidak ada kolom PIN atau password.

## 2. Android LSKF, Gatekeeper, Weaver, dan rate limiting

Android melindungi credential-encrypted storage dan kunci Keystore yang terikat autentikasi dengan *lockscreen knowledge factor* (LSKF), seperti PIN, pola, atau password. Karena sebagian LSKF memiliki entropi rendah, rate limiting merupakan bagian dari perlindungan data, bukan sekadar penundaan visual pada lock screen.

### Gatekeeper

Dokumentasi AOSP menjelaskan bahwa Gatekeeper melakukan autentikasi pola/password di **Trusted Execution Environment (TEE)** dengan kunci yang didukung hardware. Gatekeeper harus:

- membatasi percobaan verifikasi yang gagal berturut-turut;
- menolak permintaan ketika timeout masih berlaku;
- menulis failure counter **sebelum** verifikasi password;
- menghapus counter setelah verifikasi berhasil;
- menggunakan secure storage atau RPMB bila tersedia/kondisi perangkat memerlukannya.

Penulisan counter sebelum verifikasi penting agar penundaan tidak dapat dihindari hanya dengan memutus penyimpanan pada titik tertentu. Oleh karena itu, test plan laboratorium tidak boleh memutus daya, menghapus data sistem, atau me-reboot untuk mencoba mengatur ulang counter.

### Weaver dan LockSettingsService

`LockSettingsService` mengelola penyimpanan dan verifikasi LSKF. Rate-limiter utama berada di TEE atau Secure Element melalui Gatekeeper atau Weaver; Android lebih memilih Weaver bila implementasinya tersedia. Saat jawaban salah, counter bertambah dan timeout diterapkan. Selama timeout, seluruh tebakan ditolak dan sisa waktu dikembalikan.

### Android 16 QPR2 dan lebih baru

Dokumentasi rate-limiting AOSP menyebut kebijakan default yang jauh lebih kuat pada Android 16 QPR2+, software rate-limiter opsional, serta deteksi tebakan duplikat pada kombinasi implementasi yang mendukung. Tujuannya adalah mengurangi hukuman bagi pengguna yang mengulang kesalahan yang sama tanpa membuat rangkaian tebakan unik lebih mudah.

**Konsekuensi untuk pengujian:** hasil tidak boleh digeneralisasi dari satu ponsel. Versi Android, patch/QPR, implementasi Gatekeeper/Weaver, konfigurasi vendor, dan kebijakan perangkat dapat mengubah perilaku. Catat pesan lockout dan waktu yang terlihat; jangan menganggap jadwal satu dokumentasi sebagai jadwal semua perangkat.

### Persyaratan CDD

Android 16 CDD bagian 9.11 mensyaratkan pembatasan kegagalan autentikasi dan interval waktu di antara percobaan. CDD juga membahas rekomendasi batas kegagalan serta opsi factory reset berbasis persetujuan pengguna. Ini membuat pengujian berulang pada perangkat produksi berisiko terhadap ketersediaan data. Gunakan perangkat khusus laboratorium dan backup.

### Pertanyaan yang dapat dijawab Security Lab

- Apakah perangkat menampilkan timeout setelah observasi manual pengguna?
- Apakah pesan dan durasi berubah menurut versi/vendor?
- Apakah perilaku tetap konsisten setelah perangkat dinyalakan ulang secara normal sesuai prosedur vendor?
- Apakah dokumentasi recovery tersedia dan backup telah diverifikasi?

Security Lab tidak menjawabnya dengan mengirim PIN. Pengguna mencatat observasi manual; aplikasi hanya menyusun laporan.

## 3. WPS PIN, PBC, dan hardening router

### Risiko WPS PIN

CERT/CC VU#723755 dan CISA menjelaskan bahwa metode **external registrar PIN** memiliki kelemahan desain yang membuat serangan tebakan menjadi praktis pada banyak access point. Banyak router juga tidak memiliki lockout yang memadai. Dampak yang didokumentasikan mencakup terbukanya kredensial jaringan, perubahan konfigurasi, dan gangguan layanan.

Rekomendasi defensif utama:

- nonaktifkan **WPS PIN / external registrar / router PIN** bila tidak benar-benar diperlukan;
- perbarui firmware router dari situs vendor;
- gunakan WPA3 Personal atau WPA2-AES sesuai kemampuan perangkat;
- nonaktifkan remote management dan layanan yang tidak digunakan;
- ubah kredensial admin default dan gunakan password Wi-Fi yang kuat dan unik.

CISA juga memperingatkan bahwa pada sebagian access point, tampilan “WPS disabled” di antarmuka manajemen tidak selalu berarti WPS benar-benar tidak aktif. Karena itu, hasil checklist Dolphy adalah **evaluasi jawaban pengguna**, bukan jaminan konfigurasi aktual. Verifikasi lanjutan harus mengikuti dokumentasi vendor atau pengujian defensif yang sah.

### Push Button Configuration (PBC)

Wi-Fi Alliance menjelaskan bahwa PBC menggunakan periode setup sekitar dua menit setelah tombol ditekan. Selama periode tersebut, perangkat lain yang berada dalam jangkauan dapat mencoba bergabung. PBC bukan pengganti audit: gunakan hanya ketika lingkungan radio terkendali dan pastikan perangkat yang bergabung memang perangkat yang diinginkan.

### Alternatif provisioning yang sah

Dokumentasi Android `WifiNetworkSuggestion` menjelaskan penambahan kredensial jaringan oleh aplikasi pada Android 10+ dengan pilihan dan persetujuan pengguna/platform. Ini berbeda dari WPS PIN guessing: kredensial berasal dari pengguna dan sistem tetap memutuskan koneksi. Security Lab tahap ini tidak mengumpulkan password Wi-Fi; tautan API disediakan untuk pengembangan provisioning sah berikutnya.

## 4. Batas API USB Android

Dokumentasi USB host Android menyatakan bahwa dalam host mode perangkat Android memberi daya pada bus dan melakukan enumerasi perangkat. `UsbManager` dapat mengambil daftar perangkat, sedangkan komunikasi lanjutan memerlukan izin pengguna dan pengelolaan interface/endpoint.

`UsbManager` secara eksplisit menyatakan bahwa **hanya host mode** yang didukung API publik. Oleh sebab itu, enumerasi read-only adalah batas yang tepat untuk Security Lab:

- `deviceList` untuk daftar perangkat;
- metadata perangkat/interface untuk klasifikasi;
- `hasPermission` hanya untuk melaporkan status;
- tidak memanggil `requestPermission`, `openDevice`, `claimInterface`, atau `bulkTransfer`;
- tidak menganggap ponsel sebagai USB HID gadget untuk perangkat lain;
- tidak mengirim input ke lock screen.

Bidang keyboard lokal hanya menguji bahwa input keyboard dapat diterima Compose. Nilai bidang ini sengaja tidak dimasukkan ke laporan.

## 5. Keystore, BiometricPrompt, dan autentikasi milik aplikasi

Android Keystore menyimpan key material agar sulit diekstrak dan dapat membatasi penggunaannya setelah autentikasi pengguna. Dokumentasi resmi juga menjelaskan integrasi dengan BiometricPrompt serta kredensial lock screen sesuai konfigurasi kunci.

Ini adalah pendekatan yang benar bila Dolphy nantinya memerlukan autentikasi untuk data milik aplikasi: gunakan Keystore/BiometricPrompt dan kebijakan sistem, bukan membuat verifikasi PIN sendiri yang dapat dicoba berulang tanpa rate limit.

## 6. Pemulihan perangkat resmi

Google menyatakan bahwa perangkat yang tidak dapat dibuka mungkin harus dihapus sebelum dapat disiapkan kembali. Proses tersebut menghapus data lokal; data yang telah di-backup dapat dipulihkan sesuai kondisi akun dan backup. Opsi resmi mencakup erase dari perangkat lain bila persyaratan Find Hub terpenuhi atau reset melalui tombol sesuai instruksi produsen.

Security Lab hanya menautkan panduan ini dan menekankan backup. Fitur tidak melakukan erase atau perubahan lock screen.

## 7. Test plan laboratorium defensif

### Persiapan

- Gunakan perangkat/router khusus laboratorium dan catat model serta versi firmware.
- Verifikasi backup sebelum pengujian autentikasi atau pemulihan.
- Jangan memasukkan PIN/password ke catatan Dolphy.
- Hentikan observasi ketika lockout atau risiko reset muncul; jangan mencoba mengatur ulang counter dengan mematikan daya atau mengubah data sistem.

### USB

1. Jalankan pada perangkat dengan dan tanpa `FEATURE_USB_HOST`.
2. Periksa keadaan tanpa perangkat, satu perangkat HID, perangkat multi-interface, dan perangkat non-HID.
3. Tekan Refresh setelah attach/detach; jangan mengklaim pemantauan live otomatis.
4. Pastikan tidak ada dialog izin USB dan tidak ada koneksi yang dibuka.
5. Ketik pada bidang lokal menggunakan keyboard USB/Bluetooth; bagikan laporan dan pastikan isi bidang tidak ikut.

### Audit WPS manual

1. Buka halaman admin router dan isi Ya/Tidak/Belum tahu berdasarkan konfigurasi aktual.
2. Pastikan jawaban “Belum tahu” menghasilkan tindakan verifikasi, bukan status aman.
3. Pastikan WPS PIN aktif atau enkripsi lemah menghasilkan rekomendasi perbaikan.
4. Periksa firmware, remote management, dan petunjuk vendor. Jangan menganggap checklist sebagai pemeriksaan radio otomatis.
5. Bila PBC digunakan, batasi periode setup dan verifikasi perangkat yang bergabung.

### Laporan dan UI

- Uji dark/light theme, accent custom, font scale, reduced motion, dan layar sempit/tablet.
- Pastikan laporan memuat jawaban audit dan metadata USB, tetapi bukan input keyboard lokal.
- Validasi fisik tetap diperlukan: GitHub Actions tidak memiliki OTG, router, lock screen, atau radio Wi-Fi laboratorium.

## 8. Sumber resmi/primer

1. AOSP — Gatekeeper: https://source.android.com/docs/security/features/authentication/gatekeeper
2. AOSP — Rate-limiting: https://source.android.com/docs/security/features/authentication/rate-limiting
3. AOSP — Weaver: https://source.android.com/docs/security/features/authentication/weaver
4. AOSP — Android 16 Compatibility Definition, bagian 9.11: https://source.android.com/docs/compatibility/16/android-16-cdd#911_keys_and_credentials
5. CERT/CC — VU#723755, WPS PIN brute-force vulnerability: https://www.kb.cert.org/vuls/id/723755
6. CISA — Home Network Security: https://www.cisa.gov/news-events/news/home-network-security
7. CISA — TA12-006A, WPS vulnerable to brute-force attack: https://www.cisa.gov/news-events/alerts/2012/01/06/wi-fi-protected-setup-wps-vulnerable-brute-force-attack
8. CISA — Securing Your Home Wi-Fi: https://www.cisa.gov/audiences/high-risk-communities/projectupskill/module5
9. Wi-Fi Alliance — How does Wi-Fi Protected Setup work?: https://www.wi-fi.org/knowledge-center/faq/how-does-wi-fi-protected-setup-work
10. Android Developers — USB host overview: https://developer.android.com/develop/connectivity/usb/host
11. Android Developers — UsbManager: https://developer.android.com/reference/android/hardware/usb/UsbManager
12. Android Developers — Wi-Fi suggestion API: https://developer.android.com/develop/connectivity/wifi/wifi-suggest
13. Android Developers — Android Keystore system: https://developer.android.com/privacy-and-security/keystore
14. Android Developers — Secure user authentication: https://developer.android.com/security/fraud-prevention/authentication
15. Google Android Help — Can't unlock your Android device: https://support.google.com/android/answer/7663172

## 9. Batas validasi

Riset ini bersumber dari dokumentasi resmi, tetapi belum membuktikan perilaku setiap vendor. Enumerasi USB, tampilan lockout, status WPS aktual, dan performa pada perangkat harus diuji pada hardware laboratorium. Tidak ada klaim bahwa checklist menemukan semua konfigurasi berisiko atau bahwa semua perangkat menerapkan kebijakan Android dengan cara identik.
