package com.cna.mineru.cna.Utils

import android.util.Base64

import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SecurityUtil {
    fun encryptSHA256(str: String): ByteArray? {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            val byteData = str.toByteArray(Charset.forName("UTF-8"))
            md.update(byteData)
            return Base64.encode(md.digest(), 0)
        } catch (e: NoSuchAlgorithmException) {
            return null
        }

    }
}