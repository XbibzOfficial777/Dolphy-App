package com.droid.dolphy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IrRemoteIndexTest {
    @Test
    fun flatRemoteIsIndexed() {
        val location = flipperAssetLocation("Flipper-IRDB-main/TVs/LG/LG_55UN7300AUD.ir")
        assertEquals("TVs/LG", location?.key)
        assertEquals("LG", location?.brand)
        assertEquals("LG_55UN7300AUD", location?.model)
    }

    @Test
    fun deeplyNestedRemoteIsNotLost() {
        val location = flipperAssetLocation(
            "Flipper-IRDB-main/Car_Multimedia/DVD_Players/Lexus/Lexus_GX470_DVD_2008.ir"
        )
        assertEquals("Car_Multimedia/DVD_Players/Lexus", location?.key)
        assertEquals("DVD_Players/Lexus", location?.brand)
        assertEquals("Lexus_GX470_DVD_2008", location?.model)
    }

    @Test
    fun categoryRootRemoteGetsStableGroup() {
        val location = flipperAssetLocation("Flipper-IRDB-main/_Converted_/Power_off.ir")
        assertEquals("_Converted_/Uncategorized", location?.key)
        assertEquals("Power_off", location?.model)
    }

    @Test
    fun nonIrAndMetadataFilesAreIgnored() {
        assertNull(flipperAssetLocation("Flipper-IRDB-main/TVs/LG/readme.txt"))
        assertNull(flipperAssetLocation("Flipper-IRDB-main/.github/workflows/test.ir"))
        assertNull(flipperAssetLocation("other/TVs/LG/model.ir"))
    }

    @Test
    fun indexRetrievesNestedRemoteByCategoryBrandAndModel() {
        val path = "Flipper-IRDB-main/Car_Multimedia/DVD_Players/Lexus/Lexus_GX470_DVD_2008.ir"
        val index = buildFlipperIndex(listOf(path))
        assertEquals(path, index["Car_Multimedia/DVD_Players/Lexus"]?.get("Lexus_GX470_DVD_2008"))
    }
}
