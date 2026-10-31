package com.droid.dolphy

/** Answers come from the user's own router administration page, not active probing. */
enum class AuditAnswer {
    Unknown,
    Yes,
    No,
}

data class WpsAuditAnswers(
    val wpsPinEnabled: AuditAnswer = AuditAnswer.Unknown,
    val strongEncryption: AuditAnswer = AuditAnswer.Unknown,
    val firmwareCurrent: AuditAnswer = AuditAnswer.Unknown,
    val remoteManagementDisabled: AuditAnswer = AuditAnswer.Unknown,
) : java.io.Serializable

enum class WpsAuditFinding {
    DisableWpsPin,
    CheckWpsPin,
    UseStrongEncryption,
    CheckEncryption,
    UpdateFirmware,
    CheckFirmware,
    DisableRemoteManagement,
    CheckRemoteManagement,
}

fun evaluateWpsAudit(answers: WpsAuditAnswers): List<WpsAuditFinding> = buildList {
    when (answers.wpsPinEnabled) {
        AuditAnswer.Yes -> add(WpsAuditFinding.DisableWpsPin)
        AuditAnswer.Unknown -> add(WpsAuditFinding.CheckWpsPin)
        AuditAnswer.No -> Unit
    }
    when (answers.strongEncryption) {
        AuditAnswer.No -> add(WpsAuditFinding.UseStrongEncryption)
        AuditAnswer.Unknown -> add(WpsAuditFinding.CheckEncryption)
        AuditAnswer.Yes -> Unit
    }
    when (answers.firmwareCurrent) {
        AuditAnswer.No -> add(WpsAuditFinding.UpdateFirmware)
        AuditAnswer.Unknown -> add(WpsAuditFinding.CheckFirmware)
        AuditAnswer.Yes -> Unit
    }
    when (answers.remoteManagementDisabled) {
        AuditAnswer.No -> add(WpsAuditFinding.DisableRemoteManagement)
        AuditAnswer.Unknown -> add(WpsAuditFinding.CheckRemoteManagement)
        AuditAnswer.Yes -> Unit
    }
}

data class UsbDeviceDiagnostics(
    val deviceName: String,
    val displayName: String,
    val vendorId: Int,
    val productId: Int,
    val deviceClass: Int,
    val interfaceCount: Int,
    val hidInterfaceCount: Int,
    val hasPermission: Boolean,
)

/** USB HID interface class from the USB specification; kept pure for JVM tests. */
fun countHidInterfaces(interfaceClasses: List<Int>): Int =
    interfaceClasses.count { it == USB_CLASS_HID }

private const val USB_CLASS_HID = 3
