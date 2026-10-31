package com.droid.dolphy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityLabLogicTest {
    @Test
    fun safeManualAnswersProduceNoFindings() {
        val answers = WpsAuditAnswers(
            wpsPinEnabled = AuditAnswer.No,
            strongEncryption = AuditAnswer.Yes,
            firmwareCurrent = AuditAnswer.Yes,
            remoteManagementDisabled = AuditAnswer.Yes,
        )

        assertTrue(evaluateWpsAudit(answers).isEmpty())
    }

    @Test
    fun riskyAnswersProduceConcreteMitigations() {
        val answers = WpsAuditAnswers(
            wpsPinEnabled = AuditAnswer.Yes,
            strongEncryption = AuditAnswer.No,
            firmwareCurrent = AuditAnswer.No,
            remoteManagementDisabled = AuditAnswer.No,
        )

        assertEquals(
            listOf(
                WpsAuditFinding.DisableWpsPin,
                WpsAuditFinding.UseStrongEncryption,
                WpsAuditFinding.UpdateFirmware,
                WpsAuditFinding.DisableRemoteManagement,
            ),
            evaluateWpsAudit(answers),
        )
    }

    @Test
    fun unknownAnswersNeverCountAsSafe() {
        val findings = evaluateWpsAudit(WpsAuditAnswers())

        assertEquals(
            listOf(
                WpsAuditFinding.CheckWpsPin,
                WpsAuditFinding.CheckEncryption,
                WpsAuditFinding.CheckFirmware,
                WpsAuditFinding.CheckRemoteManagement,
            ),
            findings,
        )
    }

    @Test
    fun mixedAnswersOnlyReportRelevantItems() {
        val answers = WpsAuditAnswers(
            wpsPinEnabled = AuditAnswer.No,
            strongEncryption = AuditAnswer.Yes,
            firmwareCurrent = AuditAnswer.Unknown,
            remoteManagementDisabled = AuditAnswer.No,
        )

        assertEquals(
            listOf(
                WpsAuditFinding.CheckFirmware,
                WpsAuditFinding.DisableRemoteManagement,
            ),
            evaluateWpsAudit(answers),
        )
    }

    @Test
    fun hidCounterOnlyCountsClassThreeInterfaces() {
        assertEquals(2, countHidInterfaces(listOf(3, 8, 3, 2, 1)))
        assertEquals(0, countHidInterfaces(listOf(8, 2, 1)))
    }

    @Test
    fun emptyInterfaceListHasNoHid() {
        assertEquals(0, countHidInterfaces(emptyList()))
    }
}
