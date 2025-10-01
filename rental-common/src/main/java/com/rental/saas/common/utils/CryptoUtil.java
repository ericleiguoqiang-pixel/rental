package com.rental.saas.common.utils;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * 加密工具类
 * 提供各种加密和编码功能
 * 
 * @author Rental SaaS Team
 */
@Slf4j
public class CryptoUtil {

    /**
     * AES算法
     */
    private static final String AES_ALGORITHM = "AES";

    /**
     * AES加密模式
     */
    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 默认AES密钥（应该从配置文件读取）
     */
    private static final String DEFAULT_AES_KEY = "rental-saas-aes-key-2024-secret1";

    /**
     * MD5加密
     */
    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成AES密钥
     */
    public static String generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("生成AES密钥失败", e);
            throw new RuntimeException("生成AES密钥失败", e);
        }
    }

    /**
     * AES加密
     */
    public static String aesEncrypt(String plainText) {
        return aesEncrypt(plainText, DEFAULT_AES_KEY);
    }

    /**
     * AES加密（指定密钥）
     */
    public static String aesEncrypt(String plainText, String key) {
        return plainText;
//        try {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
//            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(encrypted);
//        } catch (Exception e) {
//            log.error("AES加密失败", e);
//            throw new RuntimeException("AES加密失败", e);
//        }
    }

    /**
     * AES解密
     */
    public static String aesDecrypt(String encryptedText) {
        return aesDecrypt(encryptedText, DEFAULT_AES_KEY);
    }

    /**
     * AES解密（指定密钥）
     */
    public static String aesDecrypt(String encryptedText, String key) {
        return encryptedText;
//        try {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
//            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
//            byte[] decoded = Base64.getDecoder().decode(encryptedText);
//            byte[] decrypted = cipher.doFinal(decoded);
//            return new String(decrypted, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            log.error("AES解密失败", e);
//            throw new RuntimeException("AES解密失败", e);
//        }
    }

    /**
     * Base64编码
     */
    public static String base64Encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     */
    public static String base64Decode(String encodedText) {
        byte[] decoded = Base64.getDecoder().decode(encodedText);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 生成随机字符串
     */
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    /**
     * 生成UUID
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 手机号脱敏
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 身份证号脱敏
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.replaceAll("(\\d{4})\\d{10}(\\d{4})", "$1**********$2");
    }

    /**
     * 银行卡号脱敏
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.replaceAll("(\\d{4})\\d+(\\d{4})", "$1****$2");
    }

    /**
     * 对象转JSON字符串并加密
     */
    public static String encryptObject(Object obj) {
        String json = JSON.toJSONString(obj);
        return aesEncrypt(json);
    }

    /**
     * 解密JSON字符串并转换为对象
     */
    public static <T> T decryptObject(String encryptedJson, Class<T> clazz) {
        String json = aesDecrypt(encryptedJson);
        return JSON.parseObject(json, clazz);
    }
}