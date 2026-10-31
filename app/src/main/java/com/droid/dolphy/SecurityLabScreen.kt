package com.droid.dolphy

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.DateFormat
import java.util.Date

@Composable
fun SecurityLabScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var usbDevices by remember { mutableStateOf(emptyList<UsbDeviceDiagnostics>()) }
    var usbLoaded by remember { mutableStateOf(false) }
    var localKeyboardInput by rememberSaveable { mutableStateOf("") }
    var wpsAnswers by rememberSaveable { mutableStateOf(WpsAuditAnswers()) }
    var lockoutNote by rememberSaveable { mutableStateOf("") }
    val usbHostSupported = remember {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)
    }
    val findings = remember(wpsAnswers) { evaluateWpsAudit(wpsAnswers) }

    val yes = stringResource(R.string.security_lab_answer_yes)
    val no = stringResource(R.string.security_lab_answer_no)
    val unknown = stringResource(R.string.security_lab_answer_unknown)
    val shareTitle = stringResource(R.string.security_lab_share_title)
    val reportGenerated = stringResource(R.string.security_lab_report_generated)
    val reportDevice = stringResource(R.string.security_lab_report_device)
    val reportUsb = stringResource(R.string.security_lab_report_usb)
    val reportNoDevices = stringResource(R.string.security_lab_report_no_devices)
    val reportWps = stringResource(R.string.security_lab_report_wps)
    val reportLockout = stringResource(R.string.security_lab_report_lockout)
    val reportWpsPin = stringResource(R.string.security_lab_question_wps)
    val reportEncryption = stringResource(R.string.security_lab_question_encryption)
    val reportFirmware = stringResource(R.string.security_lab_question_firmware)
    val reportRemote = stringResource(R.string.security_lab_question_remote)
    val reportPermission = stringResource(R.string.security_lab_report_permission)

    fun refreshUsb() {
        usbDevices = readUsbDiagnostics(context)
        usbLoaded = true
    }

    fun answerLabel(answer: AuditAnswer): String = when (answer) {
        AuditAnswer.Yes -> yes
        AuditAnswer.No -> no
        AuditAnswer.Unknown -> unknown
    }

    fun shareReport() {
        val report = buildString {
            appendLine(context.getString(R.string.security_lab_title))
            appendLine("$reportGenerated: ${DateFormat.getDateTimeInstance().format(Date())}")
            appendLine("$reportDevice: ${Build.MANUFACTURER} ${Build.MODEL} / Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine()
            appendLine(reportUsb)
            appendLine("${context.getString(R.string.security_lab_host_support)}: ${if (usbHostSupported) yes else no}")
            if (usbDevices.isEmpty()) {
                appendLine(reportNoDevices)
            } else {
                usbDevices.forEach { device ->
                    appendLine("- ${device.displayName} [${device.deviceName}] VID=${formatUsbId(device.vendorId)} PID=${formatUsbId(device.productId)}; ${context.getString(R.string.security_lab_report_hid, device.hidInterfaceCount, device.interfaceCount)}; $reportPermission=${if (device.hasPermission) yes else no}")
                }
            }
            appendLine()
            appendLine(reportWps)
            appendLine("- $reportWpsPin: ${answerLabel(wpsAnswers.wpsPinEnabled)}")
            appendLine("- $reportEncryption: ${answerLabel(wpsAnswers.strongEncryption)}")
            appendLine("- $reportFirmware: ${answerLabel(wpsAnswers.firmwareCurrent)}")
            appendLine("- $reportRemote: ${answerLabel(wpsAnswers.remoteManagementDisabled)}")
            appendLine(context.getString(R.string.security_lab_report_findings))
            if (findings.isEmpty()) {
                appendLine("- ${context.getString(R.string.security_lab_audit_clear_title)}")
            } else {
                findings.forEach { finding ->
                    appendLine("- ${context.getString(wpsFindingTitleRes(finding))}")
                }
            }
            appendLine(context.getString(R.string.security_lab_report_wps_disclaimer))
            if (lockoutNote.isNotBlank()) {
                appendLine()
                appendLine(reportLockout)
                appendLine(lockoutNote.trim())
            }
            appendLine()
            appendLine(context.getString(R.string.security_lab_report_privacy))
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.security_lab_title))
            putExtra(Intent.EXTRA_TEXT, report)
        }
        context.startActivity(Intent.createChooser(intent, shareTitle))
    }

    LaunchedEffect(Unit) { refreshUsb() }

    MaterialBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            SectionTopBar(
                title = stringResource(R.string.security_lab_title),
                onBack = { navController.popBackStack() },
                actions = {
                    DolphyIconButton(onClick = { shareReport() }, forTopBar = true) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.security_lab_share),
                        )
                    }
                },
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    LabCard(
                        title = stringResource(R.string.security_lab_intro_title),
                        icon = Icons.Default.Science,
                    ) {
                        Text(
                            text = stringResource(R.string.security_lab_intro_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            stringResource(R.string.security_lab_tab_usb),
                            stringResource(R.string.security_lab_tab_wps),
                            stringResource(R.string.security_lab_tab_android),
                        ).forEachIndexed { index, label ->
                            FilterChip(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                label = {
                                    Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                when (selectedTab) {
                    0 -> {
                        item {
                            LabCard(
                                title = stringResource(R.string.security_lab_usb_title),
                                icon = Icons.Default.Usb,
                            ) {
                                Text(
                                    text = stringResource(R.string.security_lab_usb_description),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(12.dp))
                                AccentButton(
                                    onClick = { refreshUsb() },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.security_lab_refresh))
                                }
                            }
                        }
                        if (!usbHostSupported) {
                            item {
                                DolphyStateCard(
                                    icon = Icons.Default.Info,
                                    title = stringResource(R.string.security_lab_usb_unsupported_title),
                                    message = stringResource(R.string.security_lab_usb_unsupported_body),
                                )
                            }
                        } else if (!usbLoaded) {
                            item {
                                DolphyStateCard(
                                    title = stringResource(R.string.security_lab_usb_loading),
                                    message = stringResource(R.string.security_lab_usb_description),
                                    icon = Icons.Default.Usb,
                                    loading = true,
                                )
                            }
                        } else if (usbDevices.isEmpty()) {
                            item {
                                DolphyStateCard(
                                    icon = Icons.Default.Usb,
                                    title = stringResource(R.string.security_lab_usb_empty_title),
                                    message = stringResource(R.string.security_lab_usb_empty_body),
                                )
                            }
                        } else {
                            items(usbDevices, key = { it.deviceName }) { device ->
                                UsbDiagnosticCard(device)
                            }
                        }
                        item {
                            LabCard(
                                title = stringResource(R.string.security_lab_keyboard_title),
                                icon = Icons.Default.Keyboard,
                            ) {
                                Text(
                                    text = stringResource(R.string.security_lab_keyboard_body),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(12.dp))
                                DolphyTextField(
                                    value = localKeyboardInput,
                                    onValueChange = { localKeyboardInput = it.take(300) },
                                    label = { Text(stringResource(R.string.security_lab_keyboard_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = false,
                                    minLines = 2,
                                    maxLines = 4,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.security_lab_keyboard_privacy),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    1 -> {
                        item {
                            LabCard(
                                title = stringResource(R.string.security_lab_wps_title),
                                icon = Icons.Default.Lan,
                            ) {
                                Text(
                                    text = stringResource(R.string.security_lab_wps_body),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        item {
                            AuditQuestionCard(
                                question = stringResource(R.string.security_lab_question_wps),
                                detail = stringResource(R.string.security_lab_question_wps_detail),
                                answer = wpsAnswers.wpsPinEnabled,
                                onAnswer = { wpsAnswers = wpsAnswers.copy(wpsPinEnabled = it) },
                            )
                        }
                        item {
                            AuditQuestionCard(
                                question = stringResource(R.string.security_lab_question_encryption),
                                detail = stringResource(R.string.security_lab_question_encryption_detail),
                                answer = wpsAnswers.strongEncryption,
                                onAnswer = { wpsAnswers = wpsAnswers.copy(strongEncryption = it) },
                            )
                        }
                        item {
                            AuditQuestionCard(
                                question = stringResource(R.string.security_lab_question_firmware),
                                detail = stringResource(R.string.security_lab_question_firmware_detail),
                                answer = wpsAnswers.firmwareCurrent,
                                onAnswer = { wpsAnswers = wpsAnswers.copy(firmwareCurrent = it) },
                            )
                        }
                        item {
                            AuditQuestionCard(
                                question = stringResource(R.string.security_lab_question_remote),
                                detail = stringResource(R.string.security_lab_question_remote_detail),
                                answer = wpsAnswers.remoteManagementDisabled,
                                onAnswer = { wpsAnswers = wpsAnswers.copy(remoteManagementDisabled = it) },
                            )
                        }
                        if (findings.isEmpty()) {
                            item {
                                DolphyStateCard(
                                    icon = Icons.Default.CheckCircle,
                                    title = stringResource(R.string.security_lab_audit_clear_title),
                                    message = stringResource(R.string.security_lab_audit_clear_body),
                                )
                            }
                        } else {
                            items(findings, key = { it.name }) { finding ->
                                WpsFindingCard(finding)
                            }
                        }
                        item {
                            LabCard(
                                title = stringResource(R.string.security_lab_pbc_title),
                                icon = Icons.Default.Info,
                            ) {
                                Text(
                                    text = stringResource(R.string.security_lab_pbc_body),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(12.dp))
                                ExternalLinkButton(
                                    label = stringResource(R.string.security_lab_open_cisa),
                                    url = "https://www.cisa.gov/news-events/alerts/2012/01/06/wi-fi-protected-setup-wps-vulnerable-brute-force-attack",
                                )
                            }
                        }
                    }

                    else -> {
                        item {
                            LabCard(
                                title = stringResource(R.string.security_lab_android_title),
                                icon = Icons.Default.Android,
                            ) {
                                Text(
                                    text = stringResource(R.string.security_lab_android_body),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(12.dp))
                                LabInfoRow(
                                    label = stringResource(R.string.security_lab_device_model),
                                    value = "${Build.MANUFACTURER} ${Build.MODEL}",
                                )
                                LabInfoRow(
                                    label = stringResource(R.string.security_lab_android_version),
                                    value = "${Build.VERSION.RELEASE} / API ${Build.VERSION.SDK_INT}",
                                )
                            }
                        }
                        item {
                            LabCard(
                                title = stringResource(R.string.security_lab_lockout_title),
                                icon = Icons.Default.Lock,
                            ) {
                                Text(
                                    text = stringResource(R.string.security_lab_lockout_body),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(12.dp))
                                DolphyTextField(
                                    value = lockoutNote,
                                    onValueChange = { lockoutNote = it.take(500) },
                                    label = { Text(stringResource(R.string.security_lab_lockout_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = false,
                                    minLines = 3,
                                    maxLines = 6,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.security_lab_no_secrets),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                        item {
                            LabCard(
                                title = stringResource(R.string.security_lab_test_plan_title),
                                icon = Icons.Default.Security,
                            ) {
                                LabBullet(stringResource(R.string.security_lab_test_plan_1))
                                LabBullet(stringResource(R.string.security_lab_test_plan_2))
                                LabBullet(stringResource(R.string.security_lab_test_plan_3))
                            }
                        }
                        item {
                            LabCard(
                                title = stringResource(R.string.security_lab_official_docs),
                                icon = Icons.Default.OpenInNew,
                            ) {
                                ExternalLinkButton(
                                    label = stringResource(R.string.security_lab_open_gatekeeper),
                                    url = "https://source.android.com/docs/security/features/authentication/gatekeeper",
                                )
                                Spacer(Modifier.height(8.dp))
                                ExternalLinkButton(
                                    label = stringResource(R.string.security_lab_open_rate_limit),
                                    url = "https://source.android.com/docs/security/features/authentication/rate-limiting",
                                )
                                Spacer(Modifier.height(8.dp))
                                ExternalLinkButton(
                                    label = stringResource(R.string.security_lab_open_recovery),
                                    url = "https://support.google.com/android/answer/7663172",
                                )
                                Spacer(Modifier.height(8.dp))
                                ExternalLinkButton(
                                    label = stringResource(R.string.security_lab_open_usb_docs),
                                    url = "https://developer.android.com/develop/connectivity/usb/host",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun readUsbDiagnostics(context: Context): List<UsbDeviceDiagnostics> {
    val manager = context.getSystemService(UsbManager::class.java) ?: return emptyList()
    return manager.deviceList.values.map { device ->
        val interfaceClasses = (0 until device.interfaceCount).map { index ->
            device.getInterface(index).interfaceClass
        }
        UsbDeviceDiagnostics(
            deviceName = device.deviceName,
            displayName = device.productName?.takeIf { it.isNotBlank() }
                ?: device.manufacturerName?.takeIf { it.isNotBlank() }
                ?: device.deviceName,
            vendorId = device.vendorId,
            productId = device.productId,
            deviceClass = device.deviceClass,
            interfaceCount = device.interfaceCount,
            hidInterfaceCount = countHidInterfaces(interfaceClasses),
            hasPermission = manager.hasPermission(device),
        )
    }.sortedBy { it.displayName.lowercase() }
}

private fun formatUsbId(value: Int): String = "%04X".format(value and 0xFFFF)

@Composable
private fun LabCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    MaterialCard(
        modifier = Modifier.fillMaxWidth(),
        containerType = "glass",
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun UsbDiagnosticCard(device: UsbDeviceDiagnostics) {
    LabCard(title = device.displayName, icon = Icons.Default.Usb) {
        LabInfoRow(label = "VID / PID", value = "${formatUsbId(device.vendorId)} / ${formatUsbId(device.productId)}")
        LabInfoRow(
            label = stringResource(R.string.security_lab_usb_class),
            value = "${device.deviceClass}",
        )
        LabInfoRow(
            label = stringResource(R.string.security_lab_usb_interfaces),
            value = "${device.hidInterfaceCount} HID / ${device.interfaceCount}",
        )
        LabInfoRow(
            label = stringResource(R.string.security_lab_usb_permission),
            value = stringResource(
                if (device.hasPermission) R.string.security_lab_answer_yes
                else R.string.security_lab_answer_no,
            ),
        )
    }
}

@Composable
private fun AuditQuestionCard(
    question: String,
    detail: String,
    answer: AuditAnswer,
    onAnswer: (AuditAnswer) -> Unit,
) {
    MaterialCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = question,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AuditAnswer.entries.forEach { option ->
                    FilterChip(
                        selected = answer == option,
                        onClick = { onAnswer(option) },
                        label = {
                            Text(
                                text = stringResource(
                                    when (option) {
                                        AuditAnswer.Yes -> R.string.security_lab_answer_yes
                                        AuditAnswer.No -> R.string.security_lab_answer_no
                                        AuditAnswer.Unknown -> R.string.security_lab_answer_unknown
                                    },
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun WpsFindingCard(finding: WpsAuditFinding) {
    val title = stringResource(wpsFindingTitleRes(finding))
    val body = stringResource(wpsFindingBodyRes(finding))
    val critical = finding in setOf(
        WpsAuditFinding.DisableWpsPin,
        WpsAuditFinding.UseStrongEncryption,
        WpsAuditFinding.UpdateFirmware,
        WpsAuditFinding.DisableRemoteManagement,
    )
    LabCard(
        title = title,
        icon = if (critical) Icons.Default.Warning else Icons.Default.Info,
    ) {
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun wpsFindingTitleRes(finding: WpsAuditFinding): Int = when (finding) {
    WpsAuditFinding.DisableWpsPin -> R.string.security_lab_finding_wps_title
    WpsAuditFinding.CheckWpsPin -> R.string.security_lab_finding_check_wps_title
    WpsAuditFinding.UseStrongEncryption -> R.string.security_lab_finding_encryption_title
    WpsAuditFinding.CheckEncryption -> R.string.security_lab_finding_check_encryption_title
    WpsAuditFinding.UpdateFirmware -> R.string.security_lab_finding_firmware_title
    WpsAuditFinding.CheckFirmware -> R.string.security_lab_finding_check_firmware_title
    WpsAuditFinding.DisableRemoteManagement -> R.string.security_lab_finding_remote_title
    WpsAuditFinding.CheckRemoteManagement -> R.string.security_lab_finding_check_remote_title
}

private fun wpsFindingBodyRes(finding: WpsAuditFinding): Int = when (finding) {
    WpsAuditFinding.DisableWpsPin -> R.string.security_lab_finding_wps_body
    WpsAuditFinding.CheckWpsPin -> R.string.security_lab_finding_check_wps_body
    WpsAuditFinding.UseStrongEncryption -> R.string.security_lab_finding_encryption_body
    WpsAuditFinding.CheckEncryption -> R.string.security_lab_finding_check_encryption_body
    WpsAuditFinding.UpdateFirmware -> R.string.security_lab_finding_firmware_body
    WpsAuditFinding.CheckFirmware -> R.string.security_lab_finding_check_firmware_body
    WpsAuditFinding.DisableRemoteManagement -> R.string.security_lab_finding_remote_body
    WpsAuditFinding.CheckRemoteManagement -> R.string.security_lab_finding_check_remote_body
}

@Composable
private fun LabInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1.4f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun LabBullet(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ExternalLinkButton(label: String, url: String) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(Icons.Default.OpenInNew, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
