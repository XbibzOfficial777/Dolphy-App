const messages = {
  "id": {
    "skip": "Lewati ke konten",
    "navHome": "Beranda",
    "navDocs": "Dokumentasi plugin",
    "heroEyebrow": "Toolkit nirkabel Android terbuka",
    "heroTitleA": "Hack the",
    "heroTitleB": "World.",
    "heroLead": "Dolphy menggabungkan Bluetooth, infrared, NFC, HID, dan alat jaringan dalam antarmuka Material 3 Expressive yang berani.",
    "download": "Unduh",
    "androidApp": "Aplikasi Android native",
    "badgeText": "Lab nirkabel saku",
    "plugins": "Plugin",
    "featureEyebrow": "Satu aplikasi. Lebih banyak kemungkinan.",
    "featureTitle": "Toolkit yang dibuat untuk eksplorasi.",
    "featureLead": "Pindai, analisis, kendalikan, dan bereksperimen dengan teknologi nirkabel dari satu aplikasi Android yang dirancang dengan cermat.",
    "featureWirelessTitle": "Bluetooth & nirkabel",
    "featureWirelessBody": "Temukan perangkat BLE dan advertising, uji audio Bluetooth, gunakan HID, dan jelajahi sinyal di sekitar.",
    "featureIrTitle": "Alat infrared",
    "featureIrBody": "Gunakan remote universal, TV Be Gone, database IR, dan alat sinyal dengan perangkat keras bawaan atau eksternal.",
    "featureNfcTitle": "NFC & jaringan",
    "featureNfcBody": "Baca tag NFC, periksa jaringan lokal, dan akses diagnostik penting dengan mudah.",
    "featurePluginsTitle": "Plugin Python yang andal",
    "featurePluginsBody": "Buat layar, hook action aplikasi, dan tambahkan integrasi perangkat keras dengan satu file .dolphyplugin yang mudah dibaca.",
    "pluginEyebrow": "Dibuat untuk dikembangkan",
    "pluginTitle": "Ubah ide menjadi alat Dolphy.",
    "pluginLead": "Mesin plugin menyediakan UI ekspresif, hook aplikasi, dan kapabilitas perangkat Android melalui API Python yang terfokus.",
    "pluginPointOne": "Bangun layar Material 3 yang lengkap",
    "pluginPointTwo": "Perluas fitur dan action yang tersedia",
    "pluginPointThree": "Integrasikan Bluetooth, Wi-Fi, USB, IR, dan lainnya",
    "readDocs": "Baca dokumentasi plugin",
    "downloadEyebrow": "Siap menjelajah?",
    "downloadTitle": "Dapatkan rilis Dolphy terbaru.",
    "downloadLead": "Unduh APK dari GitHub Releases dan ubah perangkat Android Anda menjadi toolkit nirkabel saku.",
    "openReleases": "Buka Releases",
    "footerTagline": "Hack the World.",
    "integrationTruthTitle": "Titik integrasi, bukan patching sembarangan",
    "integrationTruthBody": "Hook layar bekerja pada setiap route yang dihosting Dolphy. Hook action dan service hanya bekerja di tempat aplikasi secara eksplisit memanggil titik integrasi bernama. Plugin tidak otomatis mencegat setiap fungsi Kotlin.",
    "screenHookTitle": "Hook layar secara mendetail",
    "screenHookLead": "Route dan pola tidak peka huruf besar/kecil. Tanda bintang cocok dengan sejumlah karakter apa pun. Jika beberapa plugin cocok, prioritas lebih tinggi digunakan ketika hanya satu kontribusi yang dapat dipakai.",
    "modeOverlay": "Merender lapisan transparan ukuran penuh di atas layar yang ada.",
    "modeTop": "Menempatkan UI plugin pada surface Material di bagian atas layar.",
    "modeBottom": "Menempatkan UI plugin di atas area navigasi bawah.",
    "modeFab": "Menempatkan UI plugin yang ringkas di ujung bawah dengan padding aman untuk navigasi.",
    "modeReplace": "Pengganti dengan prioritas tertinggi menutupi layar bawaan secara visual. Layar Compose di bawahnya tetap ada, sehingga ini merupakan penggantian UI, bukan patching bytecode.",
    "routePatternsTitle": "Pola route yang berguna",
    "routePatternsBody": "Gunakan event navigation saat pengembangan untuk memeriksa route saat ini. Route dengan argumen dilaporkan sebagai template navigasinya, misalnya nfc_result/{id}.",
    "logicHooksTitle": "Hook action dan service",
    "logicHooksLead": "Hook action membentuk pipeline berurutan berdasarkan prioritas. Service membentuk daftar provider berdasarkan prioritas. Keduanya menggunakan payload yang kompatibel dengan JSON.",
    "returnNone": "Lanjutkan tanpa mengubah action.",
    "returnPayload": "Ganti payload yang diteruskan ke hook berprioritas lebih rendah dan implementasi bawaan.",
    "returnHandled": "Tandai action sebagai ditangani plugin. Gunakan result untuk mengembalikan nilai.",
    "returnCancel": "Hentikan rantai hook dan batalkan operasi bawaan.",
    "returnFalse": "Batalkan segera. True menandai action telah ditangani.",
    "currentPointsTitle": "Titik integrasi yang tersedia saat ini",
    "currentPointsBody": "Dolphy menyediakan transmisi infrared bersama, pemilihan bagian Bluetooth, action All, mode BLE, preset Advert, dan setiap paket advertising BLE keluar. Daftar lengkapnya tersedia di katalog hook.",
    "fullApiTitle": "Peta runtime lengkap",
    "fullApiLead": "Grup ini mencerminkan seluruh API publik yang tersedia dalam rilis Dolphy saat ini. Buka grup untuk melihat setiap nama helper publik.",
    "baseHelpers": "Helper plugin",
    "uiHelpers": "Builder UI Compose native",
    "bridgeHelpers": "Bridge runtime",
    "androidHelpers": "Android, file, dan Bluetooth lanjutan",
    "deviceHelpers": "Alat nirkabel, jaringan, dan perangkat dengan hak akses tinggi",
    "docsEyebrow": "Dokumentasi pengembang",
    "docsTitle": "Bangun sesuatu yang andal untuk Dolphy.",
    "docsLead": "Plugin adalah satu file Python yang mudah dibaca dengan ekstensi .dolphyplugin. Plugin dapat membuat layar, mengintegrasikan perangkat keras, dan memperluas fitur Dolphy yang ada.",
    "quickStart": "Mulai cepat",
    "examples": "Contoh",
    "onThisPage": "Di halaman ini",
    "tocOverview": "Ringkasan",
    "tocMetadata": "Metadata",
    "tocQuick": "Mulai cepat",
    "tocUi": "UI Expressive",
    "tocIntegration": "Integrasi aplikasi",
    "tocApis": "API Android & perangkat",
    "tocLifecycle": "Siklus hidup",
    "tocInstall": "Instalasi & keamanan",
    "overviewTitle": "Yang dapat dilakukan plugin",
    "overviewLead": "Plugin berjalan di dalam Dolphy dan menggunakan sistem desain serta lapisan akses perangkat keras yang sama dengan aplikasi.",
    "capScreens": "Buat layar lengkap",
    "capScreensBody": "Bangun alat Material 3 Expressive multi-layar dengan state dan callback.",
    "capHooks": "Perluas Dolphy",
    "capHooksBody": "Tambahkan overlay dan FAB, ganti layar, serta hook action bernama.",
    "capHardware": "Integrasikan perangkat keras",
    "capHardwareBody": "Gunakan BLE, Bluetooth klasik, Wi-Fi, USB, NFC, dan infrared.",
    "capSystem": "Gunakan kapabilitas Android",
    "capSystemBody": "Akses activity, intent, penyimpanan, shell, Root, dan Shizuku jika tersedia.",
    "trustedTitle": "Kemampuan besar disertai tanggung jawab",
    "trustedBody": "Plugin adalah kode Python yang dapat dieksekusi. Instal hanya file yang Anda percayai dan periksa source code sebelum memberikan akses dengan hak tinggi.",
    "metadataTitle": "Metadata file",
    "metadataLead": "Deklarasikan identitas plugin di awal file. Dolphy menggunakannya untuk pratinjau dan library plugin.",
    "copy": "Salin",
    "metaId": "Identifier unik yang stabil",
    "metaName": "Nama tampilan",
    "metaVersion": "Versi rilis",
    "metaAuthor": "Penulis atau tim",
    "metaDescription": "Ringkasan singkat",
    "metaIcon": "Nama ikon Material Symbols",
    "quickTitle": "Plugin pertama Anda",
    "quickLead": "Turunkan BasePlugin, definisikan layar, dan biarkan Dolphy merendernya dengan skema warna aplikasi yang aktif.",
    "uiTitle": "UI Expressive dari Python",
    "uiLead": "Builder UI membuat komponen Compose native. Pembaruan state dan callback tetap berada di file plugin Anda.",
    "integrationTitle": "Perluas fitur Dolphy yang ada",
    "integrationLead": "Hook memungkinkan plugin berpartisipasi dalam layar dan logika yang ada tanpa menyalin implementasi aplikasi.",
    "hookScreenBody": "Tambahkan overlay atau FAB ke route, atau ganti layar dengan layar plugin Anda sendiri.",
    "hookActionBody": "Amati, ubah, tangani, atau batalkan action bernama sebelum implementasi default dijalankan.",
    "serviceBody": "Sediakan implementasi baru untuk suatu kapabilitas, misalnya pemancar infrared eksternal.",
    "apisTitle": "API Android dan perangkat",
    "apisLead": "Bridge memberi plugin akses terkontrol ke platform dan perangkat keras nirkabel yang tersedia pada perangkat.",
    "apiContext": "Konteks aplikasi",
    "apiActivity": "Activity saat ini",
    "apiAndroid": "Intent & penyimpanan",
    "apiDevice": "Bluetooth, Wi-Fi, NFC, IR & USB",
    "apiShell": "Perintah shell",
    "apiRoot": "Akses Root",
    "apiShizuku": "Akses Shizuku",
    "lifecycleTitle": "Siklus hidup plugin",
    "lifecycleLead": "Gunakan metode siklus hidup runtime agar inisialisasi, UI, dan pembersihan berjalan konsisten.",
    "lifeLoad": "Plugin dimuat",
    "lifeScreen": "Layar dibuat",
    "lifeEvent": "Event & action",
    "lifeUnload": "Plugin dilepas",
    "installTitle": "Instal, perbarui, dan pulihkan",
    "installLead": "Dolphy menangani file .dolphyplugin dan menampilkan sheet Material ringkas yang terbuka sebagian sebelum kode apa pun dijalankan. Setelah instalasi, sheet ditutup dan keberhasilan dikonfirmasi melalui toast Dolphy.",
    "stepOpen": "Buka file",
    "stepOpenBody": "Ketuk file .dolphyplugin di file manager, lalu pilih Dolphy.",
    "stepReview": "Periksa metadata",
    "stepReviewBody": "Periksa ikon, penulis, versi, deskripsi, serta kapabilitas yang terdeteksi atau dideklarasikan, seperti pemuatan DEX, download, file, hook, Bluetooth, Wi-Fi, USB, IR, Root, Shizuku, dan Shell.",
    "stepInstall": "Instal",
    "stepInstallBody": "Kode hanya berjalan setelah Anda menekan Instal. Membuka kembali file akan memperbarui plugin.",
    "safeTitle": "Mode Aman otomatis",
    "safeBody": "Jika aplikasi tertutup saat kode plugin berjalan, peluncuran berikutnya akan menonaktifkan semua plugin dan menawarkan log crash. Setelah itu, Anda dapat mengaktifkan plugin satu per satu.",
    "docsCtaTitle": "Siap membangun?",
    "docsCtaBody": "Mulailah dengan contoh di repositori Dolphy.",
    "viewExamples": "Lihat contoh",
    "metaLibrary": "Muat sebelum plugin biasa dan tandai file sebagai library",
    "metaDesignLibrary": "Muat lebih awal dan tandai file sebagai library desain",
    "settingsContribTitle": "Pengaturan plugin native",
    "settingsContribBody": "api.registerSettings menambahkan bagian Material 3 Expressive langsung ke Pengaturan Dolphy. Nilai switch dan slider disimpan di penyimpanan plugin dan memancarkan setting_changed; item nav dan card membuka layar plugin.",
    "lifecycleAliasesTitle": "Nama siklus hidup yang didukung",
    "lifecycleAliasesBody": "Callback load dan enable berjalan saat sesi dimulai. Callback disable dan unload berjalan saat sesi berhenti. Handler on_<event> yang spesifik didahulukan daripada on_event.",
    "stormHookTitle": "Contoh: cegat setiap transmisi IR Storm",
    "stormHookLead": "IR Storm memanggil action infrared.transmit bersama satu kali untuk setiap sinyal. Payload action tidak berisi layar sumber, sehingga plugin yang hanya menargetkan IR Storm harus mengingat event navigation terakhir.",
    "stormPayloadName": "Nama sinyal atau tombol jika tersedia",
    "stormPayloadFrequency": "Frekuensi carrier dalam hertz",
    "stormPayloadPattern": "Durasi mark dan space yang bergantian dalam mikrodetik",
    "stormPayloadProtocol": "Label protokol jika diketahui",
    "stormPayloadCode": "Nilai hasil encoding asli jika tersedia",
    "stormPayloadTimings": "Representasi timing asli jika tersedia",
    "stormScopeTitle": "Bagian yang dipengaruhi contoh ini",
    "stormScopeBody": "Contoh ini mencegat ir_storm dan other/ir_storm. Mengembalikan None pada route lain mempertahankan fungsi remote biasa, TV Be Gone, dan IR Jammer. Hapus pemeriksaan route untuk mencegat setiap transmisi IR Dolphy.",
    "stormModify": "Ubah frekuensi atau pattern, lalu biarkan Dolphy melanjutkan pengiriman.",
    "stormHandle": "Plugin telah mengirim sinyal; lewati pemancar bawaan dan laporkan keberhasilan.",
    "stormCancel": "Blokir sinyal tanpa mengirimnya.",
    "stormAdapterTitle": "Pemancar bawaan dan eksternal",
    "stormAdapterBody": "Contoh ini mengirim melalui ConsumerIrManager bawaan Android tanpa masuk kembali ke hook. Untuk pemancar USB, ganti hanya send_to_external_ir dengan protokol perangkat yang diimplementasikan melalui api.getAndroid(), class USB Android, atau provider service.",
    "tocHookCatalog": "Katalog hook",
    "hookCatalogTitle": "Katalog hook lengkap",
    "hookCatalogLead": "Setiap route layar, action bernama, operasi service, dan event yang saat ini dipancarkan Dolphy.",
    "hookCatalogRuleTitle": "Memanggil dan mencegat adalah hal berbeda",
    "hookCatalogRuleBody": "Daftar API Android dan Device berisi fungsi yang dapat dipanggil plugin. Hanya entri di bawah ini yang dapat dicegat. Mendaftarkan hook_action(\"*\") tidak menyediakan fungsi yang tidak pernah dikirim Dolphy ke dispatcher.",
    "actionCatalogTitle": "Hook action bernama",
    "catalogName": "Nama",
    "catalogHandler": "Handler Python",
    "catalogWhere": "Dipicu oleh",
    "infraredActionWhere": "Setiap pengiriman melalui backend IR bersama: IR Storm, IR Jammer, TV Be Gone, remote universal, dan remote database.",
    "actionCatalogNote": "Pendaftaran menggunakan hook_action(name, priority). Wildcard didukung: bluetooth.*, bluetooth.ble.*, dan bluetooth.advert.preset.* dapat mencegat seluruh kelompok.",
    "serviceCatalogTitle": "Provider service dan operasi",
    "infraredAvailableWhere": "Saat Dolphy memeriksa apakah ada pemancar IR yang tersedia.",
    "infraredTransmitWhere": "Setelah hook action, sebelum fallback ke ConsumerIrManager bawaan.",
    "serviceCatalogNote": "Pendaftaran: provide_service(\"infrared.transmitter\", priority). Identifier service harus tepat dan tidak mendukung pencocokan wildcard.",
    "eventCatalogTitle": "Event",
    "genericEventPayload": "Fallback untuk event tanpa metode on_<event> khusus",
    "routeCatalogTitle": "Semua route layar yang tersedia untuk hook_screen",
    "routeCatalogLead": "Terdapat 108 route pada host navigasi saat ini. Route berparameter dicocokkan berdasarkan template yang ditampilkan di bawah, termasuk tanda kurung kurawal.",
    "allRoutesTitle": "108 nama route persis",
    "allRoutesHint": "Gunakan nama persis atau pola tanda bintang",
    "newHookTitle": "Jika fungsi tidak ada dalam katalog ini",
    "newHookBody": "Fungsi tersebut saat ini tidak dapat dicegat melalui API hook plugin publik. Dolphy harus menambahkan invokeActionHooks(name, payload) atau invokeServices(service, operation, payload) pada lokasi pemanggilan Kotlin tersebut sebelum pengembang plugin dapat meng-hook-nya.",
    "manifestRulesTitle": "Aturan parsing manifest",
    "manifestRulesBody": "File berupa teks UTF-8 dengan ukuran maksimum 48 MiB. Field string harus berupa assignment tingkat atas dengan tanda kutip tunggal atau ganda. id diubah menjadi huruf kecil, karakter tidak valid diganti underscore, dan hasilnya dibatasi hingga 64 karakter. Field yang tidak ada menggunakan nama file, versi 1.0, dan ikon extension sebagai default.",
    "libraryTruthTitle": "Plugin library dan dependensi",
    "libraryTruthBody": "Plugin library dimulai sebelum konsumennya. __dependencies__ mendeklarasikan id plugin yang diperlukan, menentukan urutan startup, dan mencegah konsumen berjalan ketika dependensi tidak ada atau dinonaktifkan. Class dan object Java dapat diekspor dengan nama lalu diimpor oleh plugin dependen.",
    "runtimeTruthTitle": "Format publik dan runtime",
    "runtimeTruthBody": "File yang dibuka atau dibagikan sebagai .dolphyplugin selalu dijalankan oleh runtime Python tersemat. Repositori masih memuat runtime kompatibilitas JavaScript internal untuk plugin .js yang sebelumnya tersimpan, tetapi format tersebut bukan format publik yang didokumentasikan untuk membuat .dolphyplugin.",
    "baseSignatureTitle": "API BasePlugin lengkap",
    "baseSignatureBody": "Helper ini mendelegasikan pemanggilan ke bridge native. api dan ui juga tersedia langsung melalui self.",
    "moduleSectionsTitle": "Lokasi kartu yang dapat dipilih add_module",
    "moduleSectionsBody": "Bagian bawaan adalah INFRARED, BLUETOOTH, OTHER, dan PLUGINS. Alias seperti ir, bt, ble, misc, serta nama terlokalisasi dinormalisasi; teks lain membuat bagian kustom. order mengurutkan kartu, screen memilih layar plugin, dan icon menerima sintaks Material Icon.",
    "uiTextStyles": "headlineLarge/Medium/Small, titleLarge/Medium/Small, bodyLarge/Medium/Small, dan labelLarge/Medium/Small; camelCase dan snake_case diterima.",
    "uiColorValues": "primary/accent, muted/secondary, error/red, success/green, warning/orange, atau #RGB/#ARGB/#RRGGBB/#AARRGGBB.",
    "uiIconValues": "Alias bawaan atau ikon Material apa pun yang tersedia dalam format style:name: filled, outlined, rounded, sharp, twotone, atau automirrored.",
    "uiImageValues": "Path asset, URL asset://, URI data:image/...;base64, atau Base64 mentah. Skala: fit, crop, fill, fillWidth, fillHeight, inside, atau none.",
    "uiCallbackValues": "Click tidak menerima nilai; text field menerima str, switch dan checkbox menerima bool, slider menerima float, tab menerima int, dan kontrol opsi menerima nilai yang dipilih.",
    "uiWebViewValues": "Memuat url atau html dengan JavaScript dan DOM storage diaktifkan. Perlakukan konten remote dan tersemat sebagai UI tepercaya yang dapat dieksekusi.",
    "uiSignatureTitle": "Signature UiFactory lengkap",
    "uiSignatureBody": "Semua argumen di bawah didukung runtime Python. Dimensi dan spacing menggunakan dp.",
    "settingsHeaderSchema": "title",
    "settingsSwitchSchema": "key, title, subtitle, default: bool",
    "settingsSliderSchema": "key, title, subtitle, min, max, default, steps",
    "settingsNavSchema": "title, subtitle, icon, screen; selalu membuka plugin/{pluginId}/{screen}",
    "settingsCardSchema": "title, subtitle, icon, dan screen opsional",
    "settingsStorageTitle": "Penyimpanan dan pembaruan",
    "settingsStorageBody": "Nilai switch dan slider menggunakan penyimpanan JSON per plugin yang sama dengan get_setting/set_setting. Host menyimpan nilai sebelum memanggil on_setting_changed({key, value}); slider memancarkan event setelah interaksi selesai.",
    "settingKeysTitle": "Asal kunci pengaturan",
    "settingKeysBody": "Tidak ada katalog kunci yang telah ditentukan. Pengembang plugin membuat kunci string apa pun dan menggunakan ejaan yang sama saat membaca, menulis, atau mendeklarasikan item Settings. Kunci hanya dapat bertabrakan dalam plugin yang sama karena setiap pluginId memiliki penyimpanan terpisah. Gunakan nama stabil seperti capture_enabled atau radio.power; mengubah kunci akan membuat pengaturan baru.",
    "settingKeyContract": "String nonkosong apa pun yang dipilih plugin. Saat ini tidak ada kunci host yang direservasi.",
    "settingValueContract": "Nilai apa pun yang kompatibel dengan json.dumps: None, bool, number, str, list, atau dict.",
    "settingDefaultContract": "get_setting mengembalikan default yang diberikan jika kunci belum pernah disimpan.",
    "settingLifetimeContract": "Nilai tetap tersimpan setelah restart serta enable/disable. Menghapus plugin akan menghapus seluruh penyimpanan pengaturannya.",
    "layerBase": "Helper praktis untuk modul, navigasi, state, hook screen/action/service, dan akses shell.",
    "layerUi": "Membangun tree UI yang kompatibel dengan JSON dan dirender sebagai komponen Compose Material 3 native.",
    "layerBridge": "Registrasi, akses Context/Activity, pengaturan JSON persisten, eksekusi, dan pemindaian BLE batch.",
    "layerAndroid": "Integrasi Android langsung: penyimpanan, asset, media, crypto, IR, Bluetooth klasik, RFCOMM, GATT, BLE server/advertiser, Wi-Fi, dan NSD.",
    "layerDevice": "Operasi nirkabel, NFC, jaringan, Root, dan Shizuku tingkat tinggi dengan hasil berorientasi JSON.",
    "layerJava": "Chaquopy dapat mengimpor class Android dan Java secara langsung ketika helper bridge tidak mencukupi.",
    "apiContractsTitle": "Kontrak data dan eksekusi",
    "apiContractsBody": "Sebagian besar helper perangkat keras tidak menampilkan prompt izin. Periksa metode hasPermission/capability dan minta izin Android melalui Activity API jika diperlukan. Pemindaian berkepanjangan, GATT, RFCOMM, sensor, audio, advertising, BLE server, NSD, dan Wi-Fi P2P harus dihentikan dalam on_plugin_disable/on_plugin_unload.",
    "bridgeSignatureTitle": "Signature bridge langsung",
    "bridgeSignatureBody": "Gunakan wrapper BasePlugin jika tersedia. Metode bridge langsung mempertahankan tipe return Java; metode pengaturan dan perintah bertukar teks JSON.",
    "deviceCompleteTitle": "Kontrol perangkat dan alat sistem",
    "deviceCompleteBody": "Status perangkat, kontrol radio, dan operasi Root lanjutan",
    "genericActionBody": "Fallback umum untuk setiap action terdaftar yang tidak memiliki metode hook_<normalized_action> yang cocok. Titik dan karakter nonalfanumerik lain dinormalisasi menjadi underscore.",
    "serviceAvailableContract": "Menandai pemancar eksternal sebagai tersedia; {\"ok\": true} atau True mentah juga diterima.",
    "serviceTransmitContract": "Menghentikan fallback provider dan melaporkan keberhasilan. handled false memungkinkan provider berikutnya atau ConsumerIrManager bawaan berjalan.",
    "serviceGenericContract": "Fallback umum ketika service_<service>_<operation> tidak didefinisikan.",
    "screenResolverContract": "Renderer utama. main juga menerima create_screen atau screen_main. Layar yang tidak ditemukan menampilkan placeholder error native.",
    "callbackContract": "Callable Python yang diteruskan ke builder UI hanya menerima tipe nilai yang dihasilkan komponen tersebut. Deklarasi dengan lebih sedikit argumen posisional didukung.",
    "eventResolverContract": "Handler event spesifik didahulukan; jika tidak ada, on_event(name, payload) dipanggil.",
    "actionResolverContract": "Handler action ternormalisasi yang spesifik didahulukan; jika tidak ada, on_action(action, payload) dipanggil.",
    "serviceResolverContract": "Handler service ternormalisasi yang spesifik didahulukan; jika tidak ada, on_service(id, operation, payload) dipanggil.",
    "refreshContract": "Membatalkan cache UI plugin terdaftar agar fungsi layar berjalan kembali dengan state plugin terbaru.",
    "installPreviewContract": "Membaca metadata dan ukuran tanpa menjalankan Python; menunjukkan apakah id yang sama sudah terinstal.",
    "installUpdateContract": "Menyimpan source secara privat dan langsung menjalankannya kecuali Mode Aman aktif. Menginstal id yang sama menggantikan sesi sebelumnya dan mempertahankan status sematan.",
    "installToggleContract": "Memulai atau menghentikan runtime dan menghapus semua modul, pengaturan, layar, service, dan action terdaftar saat dinonaktifkan.",
    "installShareContract": "Pin mengubah urutan. Share mengekspor source asli sebagai application/x-dolphy-plugin dengan ekstensi .dolphyplugin.",
    "installDeleteContract": "Menghentikan runtime dan menghapus source, metadata, registrasi, serta penyimpanan pengaturan privat plugin secara permanen.",
    "installFailureContract": "Plugin yang tidak dapat dimulai disimpan dalam keadaan nonaktif saat instalasi; source tersimpan yang rusak dapat dikarantina sebagai .broken saat pemulihan.",
    "safeDetailsTitle": "Perilaku Mode Aman",
    "safeDetailsBody": "Sheet pemulihan menjelaskan bahwa error plugin telah menutup aplikasi dan menawarkan Salin log atau Nonaktifkan Mode Aman. Menutup sheet membiarkan Mode Aman aktif dan item persisten tetap tampil di layar Plugin. Selama aktif, semua plugin dinonaktifkan dan tidak dapat diaktifkan hingga Mode Aman dimatikan.",
    "coreReplaceTitle": "Ganti seluruh layar inti",
    "coreReplaceBody": "Gunakan replace dengan other, bluetooth, atau settings untuk menutupi seluruh layar inti. Bluetooth juga menyediakan tiga target sub-layar aktif, sehingga plugin dapat mengganti All, BLE, atau Advert secara independen.",
    "bleHookTitle": "Contoh: ganti logika advertising BLE",
    "bleHookLead": "bluetooth.advertising.transmit berjalan untuk preset Advert kustom dan paket yang dihasilkan setiap mode BLE spam bawaan. Ubah data untuk menulis ulang paket, kembalikan cancel untuk memblokirnya, atau handled setelah mengirim melalui backend Anda sendiri.",
    "bleModeApiTitle": "Tambahkan mode BLE Spam setara fitur bawaan",
    "bleModeApiLead": "register_ble_mode menambahkan plugin di samping mode BLE bawaan dengan kartu, status aktif, delay bersama, dan perilaku Stop All yang sama. Dolphy mengirim ble_mode_start, ble_mode_stop, dan ble_mode_delay_changed hanya kepada plugin pemilik. Plugin mengelola loop advertising-nya sendiri dan harus menghentikannya saat stop maupun unload.",
    "assetApiTitle": "Download dan terapkan asset plugin",
    "assetApiLead": "download_assets meminta semua file dalam satu batch. Setiap tujuan dibatasi pada direktori privat plugin ini; maxBytes membatasi setiap respons dan sha256 dapat digunakan untuk verifikasi. Kecuali pengguna menonaktifkan konfirmasi di Pengaturan plugin → Keamanan, Dolphy menampilkan nama plugin, jumlah file, dan total ukuran yang dideklarasikan sebelum transfer jaringan. apply_asset mengimpor file tersimpan ke editor Bad USB bawaan, remote IR pengguna, atau HTML kustom QR Audio Spoofer.",
    "assetPathContract": "Mengembalikan path absolut lokasi di dalam folder privat plugin untuk API Java dan DEX.",
    "assetDownloadContract": "Item menerima url, path, sizeBytes, maxBytes, sha256, dan headers. Callback menerima {ok, files} sebagai teks JSON.",
    "assetApplyContract": "Tipe yang didukung: bad_hid, ir_remote, dan qr_audio_html. Mengembalikan dictionary hasil parsing.",
    "bleAssetHelpers": "Mode BLE dan asset plugin",
    "bleAssetBridgeHelpers": "Bridge BLE dan asset",
    "metaPermissions": "Kapabilitas yang ditampilkan sebelum instalasi; Dolphy juga mendeteksi penggunaan API langsung dalam source",
    "btSectionWhere": "Sebelum beralih antara All, BLE, dan Advert. section atau index dapat diubah.",
    "btDeviceWhere": "Sebelum memilih perangkat Bluetooth klasik yang ditemukan.",
    "btIntervalWhere": "Saat slider interval All berubah. intervalMs dapat diubah.",
    "btScanWhere": "Sebelum penemuan Bluetooth klasik dan BLE dimulai pada All.",
    "btAllStartWhere": "Sebelum All memulai permintaan pairing klasik berulang.",
    "btAllStopWhere": "Sebelum All menghentikan operasi aktifnya.",
    "bleDelayWhere": "Sebelum mengubah delay mode BLE. delayMs dapat diubah.",
    "bleModeWhere": "Sebelum mengaktifkan atau menonaktifkan mode BLE tertentu.",
    "bleKitchenWhere": "Sebelum mengaktifkan atau menonaktifkan Kitchen Sink.",
    "bleStopWhere": "Sebelum menghentikan semua mode BLE.",
    "bleSectionToggleWhere": "Sebelum mengaktifkan atau menonaktifkan setiap mode dalam grup iOS, Samsung, Android, Windows, Xiaomi, atau Phantom.",
    "bleSectionOpenWhere": "Sebelum membuka layar grup BLE. section dapat dialihkan.",
    "advertAddWhere": "Sebelum menyimpan preset Advert baru; semua field dapat diubah.",
    "advertUpdateWhere": "Sebelum memperbarui preset Advert; semua field dapat diubah.",
    "advertDeleteWhere": "Sebelum menghapus preset Advert.",
    "advertStartWhere": "Sebelum memulai preset; field dapat diubah atau proses mulai dapat ditangani sepenuhnya.",
    "advertStopWhere": "Sebelum menghentikan preset Advert.",
    "advertTransmitWhere": "Sebelum setiap paket advertising keluar dari Advert atau mode BLE bawaan.",
    "advertisingStopWhere": "Sebelum siklus Advert atau advertiser BLE bawaan berhenti; backend eksternal dapat menghentikan transmisinya sendiri dan mengembalikan handled.",
    "advertPayloadTitle": "Payload bluetooth.advertising.transmit",
    "advertPayloadBody": "metadata.source berisi ble_spam atau advert; Advert juga menyertakan metadata.preset. data dan scanResponse berisi includeDeviceName, includeTxPowerLevel, manufacturerData [{id,data}], serviceUuids, dan serviceData [{uuid,data}]. Nilai biner berupa hex huruf besar. Mengembalikan payload yang diubah akan membangun ulang AdvertiseData native.",
    "virtualRoutesTitle": "Target sub-layar Bluetooth",
    "virtualRoutesBody": "Ini adalah target hook aktif di dalam route bluetooth, bukan tujuan navigasi. Target ini mendukung semua mode hook_screen.",
    "tocSdkReference": "Referensi SDK",
    "sdkReferenceTitle": "Referensi SDK yang dapat dipanggil",
    "sdkReferenceLead": "Cari API publik lengkap. Setiap entri mencakup signature persis, parameter, kontrak return, persyaratan, dan contoh pemanggilan Python.",
    "sdkReadingTitle": "Cara membaca kontrak",
    "sdkReadingBody": "Tipe nullable dapat menjadi None. Action Boolean melaporkan apakah Android menerima permintaan, bukan apakah perangkat remote telah menyelesaikannya. Metode dengan callback selesai secara asinkron. Hasil JSON tetap berupa string hingga json.loads dipanggil.",
    "tocDexLibraries": "DEX & library",
    "metaDependencies": "id plugin yang diperlukan dan dimuat sebelum plugin ini",
    "surfaceHooksTitle": "Surface kerangka aplikasi",
    "surfaceHooksBody": "hook_surface menargetkan UI di luar route layar biasa. bottom_bar dapat dilapisi atau diganti sepenuhnya. Penggantian mengikuti prioritas plugin dan menghilang otomatis ketika plugin dinonaktifkan.",
    "surfaceCatalogTitle": "Surface host yang dapat di-hook",
    "surfaceCatalogBody": "Gunakan hook_surface, bukan hook_screen, untuk UI yang dimiliki shell aplikasi.",
    "dexLibrariesTitle": "Pemuatan DEX dan plugin library",
    "dexLibrariesLead": "Pertahankan plugin sebagai satu file Python yang mudah dibaca sambil menyematkan kode Java atau Kotlin terkompilasi dan berbagi kapabilitas native dengan plugin lain.",
    "dexTrustTitle": "DEX adalah kode native tepercaya",
    "dexTrustBody": "DEX yang dimuat berjalan di dalam proses Dolphy dengan izin aplikasi. Selalu publikasikan SHA-256, verifikasi sebelum pemuatan, dan jangan pernah menjalankan payload hasil download yang tidak tepercaya.",
    "dexEmbeddedTitle": "Sematkan dan muat DEX mentah",
    "dexEmbeddedLead": "Simpan DEX mentah sebagai Base64 dalam file .dolphyplugin. Dolphy menghapus whitespace, mendekode, memverifikasi SHA-256, dan membuat class loader. DEX mentah menggunakan InMemoryDexClassLoader pada Android 8+; Android lebih lama serta payload JAR/APK berbasis ZIP menggunakan file code-cache privat read-only dan DexClassLoader.",
    "dexLoadBase64Contract": "Memuat byte DEX, JAR, atau APK mentah yang disimpan sebagai Base64 dan mengembalikan ClassLoader modul.",
    "dexLoadFileContract": "Menyalin dan memuat file DEX/JAR/APK. Gunakan penyimpanan privat plugin dan verifikasi digest-nya.",
    "dexClassContract": "Memuat Class dari modul yang dimiliki plugin saat ini.",
    "dexInstanceContract": "Memanggil konstruktor tanpa argumen yang dapat diakses dan mengembalikan object Java.",
    "dexLoaderContract": "Mengembalikan ClassLoader modul yang telah dimuat atau None.",
    "dexDependencyLoaderContract": "Mengembalikan parent loader aplikasi yang juga dapat me-resolve modul yang diekspor oleh dependensi terdeklarasi.",
    "libraryExportsTitle": "Ekspor class dan object aktif",
    "libraryExportsLead": "Export adalah referensi bernama yang bersifat lokal terhadap proses. Library memiliki setiap class dan object yang diekspornya; menonaktifkan, memperbarui, atau menghapus library akan menghapus modul dan export-nya dari registry.",
    "exportDexClassBody": "Resolve dan publikasikan class dari modul DEX saat ini.",
    "exportAppClassBody": "Publikasikan class Dolphy atau dependensi berdasarkan fully qualified name-nya.",
    "exportJavaClassBody": "Publikasikan object Class yang telah diperoleh melalui Java reflection.",
    "exportJavaObjectBody": "Publikasikan object Java aktif, adapter, controller, atau facade.",
    "importJavaClassBody": "Dapatkan export Class bernama dari dependensi mana pun yang aktif.",
    "importJavaObjectBody": "Dapatkan export object aktif bernama dari dependensi mana pun yang aktif.",
    "javaExportsBody": "Periksa semua modul yang dimuat serta nama class/object yang diekspor untuk diagnostik.",
    "sourceBridgeTitle": "Sediakan internal Dolphy dari open source",
    "sourceBridgeLead": "Gunakan repositori untuk menemukan fully qualified JVM class name, lalu ekspor Class tersebut atau bungkus dalam facade DEX yang stabil. Konsumen sebaiknya bergantung pada facade library daripada menduplikasi kode reflection yang rapuh.",
    "sourceBridgeBoundaryTitle": "Akses class versus hook host",
    "sourceBridgeBoundaryBody": "Export class memungkinkan reflection, konstruksi, dan pemanggilan metode; export tidak menulis ulang fungsi Compose yang telah dikompilasi. Gunakan hook_screen untuk layar, hook_surface(\"bottom_bar\") untuk panel navigasi, hook action untuk logika, dan provider service untuk backend perangkat keras yang dapat diganti. Library DEX dapat menyembunyikan reflection di balik facade stabil yang dipakai bersama plugin lain.",
    "dexLimitsTitle": "Batas dan siklus hidup",
    "dexLimitsBody": "Ukuran .dolphyplugin dapat mencapai 48 MiB; setiap payload DEX/JAR/APK hasil decode dapat mencapai 32 MiB. Menonaktifkan, menghapus, atau memperbarui library akan lebih dulu menonaktifkan plugin dependen yang aktif; aktifkan kembali konsumen setelah library siap. Android tidak dapat me-unload Class setelah direferensikan kode. Dolphy melepas loader dan export ketika pemilik berhenti, tetapi restart proses adalah satu-satunya cara yang menjamin unload lengkap setelah pembaruan. Buat nama export unik secara global dan tutup thread, receiver, socket, serta handle perangkat keras dalam on_plugin_unload.",
    "docsSearch": "Cari dokumentasi",
    "tocStartGroup": "Memulai",
    "tocUiGroup": "Antarmuka",
    "tocIntegrationGroup": "Integrasi",
    "tocNativeGroup": "Kode native",
    "tocReferenceGroup": "Referensi",
    "tocPluginSettings": "Pengaturan plugin",
    "tocScreenHooks": "Hook layar",
    "tocSurfaceHooks": "Surface aplikasi",
    "tocLogicHooks": "Action & service",
    "tocLibraryExports": "Export library",
    "tocReflection": "Java reflection",
    "reflectionTitle": "Helper Java reflection",
    "reflectionLead": "Helper ini mencari aplikasi serta modul DEX dari dependensi terdeklarasi. Helper dapat memeriksa member privat, membaca atau menulis field, memanggil metode yang cocok, dan membuat object. Helper tidak memasang method hook dan tidak mencegat pemanggilan berikutnya.",
    "reflectionFind": "Mengembalikan Class atau None menggunakan ClassLoader aplikasi dan dependensi.",
    "reflectionInspect": "Mengembalikan className beserta field, metode, konstruktor, tipe parameter, dan flag static yang diwarisi.",
    "reflectionGet": "Membaca field instance publik atau privat. Berikan Class untuk field static.",
    "reflectionSet": "Menulis field instance publik atau privat. Berikan Class untuk field static.",
    "reflectionInvoke": "Memanggil overload kompatibel pertama berdasarkan nama, jumlah argumen, dan tipe runtime. Berikan Class untuk metode static.",
    "reflectionNew": "Memanggil konstruktor terdeklarasi kompatibel pertama, termasuk konstruktor privat.",
    "reflectionLimitsTitle": "Kompatibilitas reflection",
    "reflectionLimitsBody": "Nama dan signature privat dapat berubah antar-build Dolphy. Tangkap exception, deklarasikan dependensi library, verifikasi sendiri versi aplikasi, dan utamakan hook screen, surface, action, serta service yang stabil jika tersedia. Resolusi overload tidak melakukan konversi tipe sembarangan.",
    "reflectionHelpers": "Helper reflection",
    "reflectionBridgeHelpers": "Bridge reflection"
  }
}

const pageText = {
  "home": {
    "id": [
      "Dolphy — Toolkit nirkabel untuk Android",
      "Dolphy adalah toolkit Android ekspresif untuk Bluetooth, infrared, NFC, HID, jaringan, dan plugin Python."
    ]
  },
  "docs": {
    "id": [
      "Dokumentasi Plugin Dolphy",
      "Dokumentasi untuk membuat ekstensi Python .dolphyplugin bagi Dolphy."
    ]
  }
}

const language = 'id'

function setLanguage() {
  document.documentElement.lang = 'id'
  document.querySelectorAll('[data-i18n]').forEach(element => {
    const value = messages.id[element.dataset.i18n]
    if (value) element.textContent = value
  })
  document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
    const value = messages.id[element.dataset.i18nPlaceholder]
    if (value) element.placeholder = value
  })
  const page = document.body.dataset.page || 'home'
  const [title, description] = pageText[page].id
  document.title = title
  const meta = document.querySelector('meta[name="description"]')
  if (meta) meta.content = description
}

document.querySelectorAll('[data-current-year]').forEach(element => {
  element.textContent = new Date().getFullYear()
})

document.querySelectorAll('.copy-button, .copy-code').forEach(button => {
  button.addEventListener('click', async () => {
    const code = button.closest('.code-block, .code-card')?.querySelector('code')?.innerText || ''
    await navigator.clipboard.writeText(code)
    const label = button.querySelector('[data-i18n]')
    if (label) label.textContent = 'Disalin'
    setTimeout(() => {
      if (label) label.textContent = messages[language].copy
    }, 1400)
  })
})

const revealObserver = new IntersectionObserver(entries => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('visible')
      revealObserver.unobserve(entry.target)
    }
  })
}, { threshold: .01 })

document.querySelectorAll('.reveal').forEach(element => revealObserver.observe(element))

const tocLinks = [...document.querySelectorAll('.docs-toc a')]
const docNavSearch = document.querySelector('[data-doc-nav-search]')
if (docNavSearch) {
  docNavSearch.addEventListener('input', () => {
    const query = docNavSearch.value.trim().toLowerCase()
    tocLinks.forEach(link => { link.hidden = query && !link.textContent.toLowerCase().includes(query) })
    document.querySelectorAll('.docs-toc .toc-title').forEach(title => {
      let sibling = title.nextElementSibling
      let visible = false
      while (sibling && !sibling.classList.contains('toc-title')) {
        if (sibling.matches('a') && !sibling.hidden) visible = true
        sibling = sibling.nextElementSibling
      }
      title.hidden = Boolean(query) && !visible
    })
  })
}
const docSections = [...document.querySelectorAll('.doc-section[id]')]
if (tocLinks.length && docSections.length) {
  const tocObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        tocLinks.forEach(link => link.classList.toggle('active', link.getAttribute('href') === `#${entry.target.id}`))
      }
    })
  }, { rootMargin: '-25% 0px -65% 0px' })
  docSections.forEach(section => tocObserver.observe(section))
}

setLanguage()
