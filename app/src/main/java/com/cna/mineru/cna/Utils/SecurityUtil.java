package com.cna.mineru.cna.Utils;

import android.util.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {
    public byte[] encryptSHA256(String str){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] byteData = str.getBytes(Charset.forName("UTF-8"));
            md.update(byteData);
            return Base64.encode(md.digest(),0);
        }catch (NoSuchAlgorithmException e){
            return null;
        }
    }
}