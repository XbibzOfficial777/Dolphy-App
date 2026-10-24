package com.droid.dolphy.plugin.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.droid.dolphy.MaterialBackground
import com.droid.dolphy.MaterialCard
import com.droid.dolphy.R
import com.droid.dolphy.SectionTopBar

@Composable
fun PluginAboutScreen(navController: NavController) {
    val accent = MaterialTheme.colorScheme.primary
    MaterialBackground(accentColor = accent) {
        Column(Modifier.fillMaxSize()) {
            SectionTopBar(
                title = stringResource(R.string.plugin_about_title),
                onBack = { navController.popBackStack() },
                accentColor = accent,
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
            ) {
                item {
                    GuideCard(
                        icon = Icons.Default.Code,
                        title = stringResource(R.string.plugin_about_guide_title),
                        body = stringResource(R.string.plugin_about_guide_body),
                        accent = accent,
                    )
                }
                item { SectionTitle("Plugin minimal") }
                item {
                    CodeBlock(
                        """
                        __id__ = "radio_lab"
                        __name__ = "Radio Lab"
                        __version__ = "1.0.0"
                        __author__ = "Developer"
                        __description__ = "Bluetooth and Wi-Fi toolkit"
                        __icon__ = "filled:bluetooth_searching"

                        class RadioLab(BasePlugin):
                            def on_plugin_load(self):
                                self.add_module(
                                    title="Radio Lab",
                                    description="Wireless diagnostics",
                                    icon="bluetooth",
                                    section="BLUETOOTH"
                                )

                            def screen_main(self, ui, api, state):
                                return ui.scaffold(
                                    title="Radio Lab",
                                    content=ui.column([
                                        ui.card([
                                            ui.text("Bluetooth", "titleLarge"),
                                            ui.button("Scan", self.scan, fill_max_width=True)
                                        ])
                                    ], padding=16, spacing=12, fill_max_size=True)
                                )

                            def scan(self):
                                self.toast("Starting scan")
                        """.trimIndent(),
                    )
                }
                item { SectionTitle("Akses penuh") }
                item {
                    GuideCard(
                        icon = Icons.Default.Terminal,
                        title = "Android, Root dan Shizuku",
                        body = "api.getContext(), api.getApplicationContext(), api.getClassLoader(), api.getAndroid(), dan api.getDevice() mengembalikan objek Java Dolphy yang sebenarnya. Perintah tersedia melalui api.shellExec(), api.rootExec(), api.shizukuExec(), dan api.smartExec().",
                        accent = accent,
                    )
                }
                item {
                    CodeBlock(
                        """
                        from java import jclass

                        BluetoothAdapter = jclass("android.bluetooth.BluetoothAdapter")
                        adapter = BluetoothAdapter.getDefaultAdapter()
                        result = self.root("id; getenforce; ls /sys/class/udc")
                        context = self.api.getContext()
                        app_class = self.api.getClassLoader().loadClass(
                            "com.droid.dolphy.MainActivity"
                        )
                        """.trimIndent(),
                    )
                }
                item { SectionTitle("Antarmuka radio") }
                item {
                    GuideCard(
                        icon = Icons.Default.Bluetooth,
                        title = "Bluetooth dan BLE",
                        body = "Melalui api.getAndroid(), tersedia advertising, GATT client/server, RFCOMM, bonding, dan informasi adaptor. api.getDevice() menyediakan pemindaian BLE, kapabilitas perangkat keras, dan operasi sistem.",
                        accent = accent,
                    )
                }
                item {
                    GuideCard(
                        icon = Icons.Default.Wifi,
                        title = "Wi-Fi dan jaringan",
                        body = "Tersedia hasil pemindaian Wi-Fi, antarmuka, DHCP, channel, NSD, socket, HTTP, dan akses langsung ke Android WifiManager. Root dan Shizuku memungkinkan penggunaan alat jaringan sistem perangkat.",
                        accent = accent,
                    )
                }
                item { SectionTitle("Modifikasi aplikasi") }
                item {
                    GuideCard(
                        icon = Icons.Default.Code,
                        title = "Layar, tindakan, dan layanan",
                        body = "hook_screen() menambahkan layer, FAB, atau menggantikan layar bawaan sepenuhnya. hook_action() mencegat operasi bernama dan dapat mengubah data, memproses, atau membatalkan tindakan. provide_service() menghubungkan implementasi baru untuk kapabilitas sistem dengan prioritas dan fallback bawaan.",
                        accent = accent,
                    )
                }
                item {
                    CodeBlock(
                        """
                        def on_plugin_load(self):
                            self.hook_screen("ir_*", "ir_tools", "fab", 50)
                            self.hook_action("infrared.transmit", 50)
                            self.provide_service("infrared.transmitter", 100)

                        def screen_ir_tools(self, ui, api, state):
                            return ui.icon_button("usb", self.open_usb)

                        def hook_infrared_transmit(self, payload):
                            payload["source"] = "plugin"
                            return {"payload": payload}

                        def service_infrared_transmitter_available(self, payload):
                            return {"available": self.usb_connected()}

                        def service_infrared_transmitter_transmit(self, payload):
                            if not self.usb_connected():
                                return {"handled": False}
                            ok = self.send_usb(
                                payload["frequency"],
                                payload["pattern"]
                            )
                            return {"handled": True, "ok": ok}
                        """.trimIndent(),
                    )
                }
                item {
                    GuideCard(
                        icon = Icons.Default.Security,
                        title = "Mode hook layar",
                        body = "replace menggantikan layar, overlay menggambar layer bebas di atasnya, top dan bottom menambahkan panel Material 3, sedangkan fab menambahkan tindakan mengambang yang ringkas. Mask route mendukung *, misalnya ir_* atau other/*.",
                        accent = accent,
                    )
                }
                item { SectionTitle("Status dan pengaturan") }
                item {
                    CodeBlock(
                        """
                        enabled = self.get_setting("enabled", True)
                        self.set_setting("enabled", not enabled)

                        self.api.registerSettings(
                            "Radio Lab",
                            '[{"type":"switch","key":"enabled","title":"Enabled","default":true}]'
                        )
                        """.trimIndent(),
                    )
                }
                item {
                    GuideCard(
                        icon = Icons.Default.Security,
                        title = "Model tepercaya",
                        body = "Plugin Python berjalan di dalam proses Dolphy dan dapat memperoleh izin aplikasi, data internal, Root, dan Shizuku. Instal hanya kode yang sudah Anda baca atau yang sepenuhnya Anda percayai.",
                        accent = accent,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, start = 4.dp),
    )
}

@Composable
private fun GuideCard(icon: ImageVector, title: String, body: String, accent: Color) {
    MaterialCard(Modifier.fillMaxWidth(), accentColor = accent, contentPadding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = accent)
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CodeBlock(code: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Text(
            text = code,
            modifier = Modifier.padding(16.dp).horizontalScroll(rememberScrollState()),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
