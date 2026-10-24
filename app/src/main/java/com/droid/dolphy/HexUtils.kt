package com.droid.dolphy

object HexUtils {




    fun hexToByteArray(input: String): ByteArray {
        val clean = input
            .trim()
            .removePrefix("0x")
            .removePrefix("0X")
            .replace("\\s+".toRegex(), "")

        require(clean.isNotEmpty()) { "Payload hex kosong" }
        require(clean.length % 2 == 0) { "Payload hex harus berisi jumlah karakter genap" }
        require(clean.matches(Regex("^[0-9a-fA-F]+$"))) { "Payload hex berisi karakter tidak valid" }

        return ByteArray(clean.length / 2) { index ->
            clean.substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
    }




    fun parseCompanyCode(input: String): Int {
        val clean = input
            .trim()
            .removePrefix("0x")
            .removePrefix("0X")

        require(clean.isNotEmpty()) { "Kode merek wajib diisi" }
        require(clean.matches(Regex("^[0-9a-fA-F]+$"))) { "Kode merek harus berformat hex" }

        return clean.toInt(16)
    }
}


