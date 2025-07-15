package com.paranoia.ParanoiaAPI.utils;

import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import org.springframework.http.HttpStatus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hashing {
    public static String generateHash(String param) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(param.getBytes());

            byte byteData[] = md.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<byteData.length;i++) {
                String hex=Integer.toHexString(0xff & byteData[i]);
                if(hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ParanoiaException(HttpStatus.UNPROCESSABLE_ENTITY, HistoricoAcoes.MD5_HASH_ERROR, null, "Problema ao processar criptografia.");
        }
    }
}
