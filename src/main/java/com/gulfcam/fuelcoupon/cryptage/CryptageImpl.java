package com.gulfcam.fuelcoupon.cryptage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Objects;

@Service
public class CryptageImpl implements Cryptage{

    private static final Logger LOGGER = LogManager.getLogger(AESUtil.class);

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String KEY_ALGORITHM = "AES";

    private final int IV_SIZE = 128;

    private int iterationCount = 1989;
    private int keySize = 256;

    private int saltLength;

    private final AESUtil.DataType dataType = AESUtil.DataType.BASE64;

    private Cipher cipher;

    public String encryptObject(String salt, String iv, String passPhrase, Object plainText) {
        try {
            SecretKey secretKey = generateKey(salt, passPhrase);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, secretKey, iv, plainText.toString().getBytes(StandardCharsets.UTF_8));
            String cipherText;

            if (dataType.equals(AESUtil.DataType.HEX)) {
                cipherText = toHex(encrypted);
            } else {
                cipherText = toBase64(encrypted);
            }
            return cipherText;
        } catch (Exception e) {
            return null;
        }
    }


//    public Object encryptObject(String passPhrase, Object plainText) {
//        try {
//            String salt = toHex(generateRandom(keySize / 8));
//            String iv = toHex(generateRandom(IV_SIZE / 8));
//            String cipherText = encrypt(salt, iv, passPhrase, plainText.toString());
//            return salt + iv + cipherText;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public Object decryptObject(String salt, String iv, String passPhrase, String cipherText) {
        try {
            SecretKey key = generateKey(salt, passPhrase);
            byte[] encrypted;
            if (dataType.equals(AESUtil.DataType.HEX)) {
                encrypted = fromHex(cipherText);
            } else {
                encrypted = fromBase64(cipherText);
            }
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, encrypted);
            return new String(Objects.requireNonNull(decrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public Object decryptObject(String passPhrase, String cipherText) {
        try {
            String salt = cipherText.substring(0, saltLength);
            int ivLength = IV_SIZE / 4;
            String iv = cipherText.substring(saltLength, saltLength + ivLength);
            String ct = cipherText.substring(saltLength + ivLength);
            return decryptObject(salt, iv, passPhrase, ct);
        } catch (Exception e) {
            return null;
        }
    }

    private SecretKey generateKey(String salt, String passPhrase) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), fromHex(salt), iterationCount, keySize);
            return new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }

    private static byte[] fromBase64(String str) {
        return DatatypeConverter.parseBase64Binary(str);
    }

    private static String toBase64(byte[] ba) {
        return DatatypeConverter.printBase64Binary(ba);
    }

    private static byte[] fromHex(String str) {
        return DatatypeConverter.parseHexBinary(str);
    }

    private static String toHex(byte[] ba) {
        return DatatypeConverter.printHexBinary(ba);
    }

    private byte[] doFinal(int mode, SecretKey secretKey, String iv, byte[] bytes) {
        try {
            cipher.init(mode, secretKey, new IvParameterSpec(fromHex(iv)));
            return cipher.doFinal(bytes);
        } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
                 | InvalidKeyException e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }

    private static byte[] generateRandom(int length) {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return randomBytes;
    }
    @Override
    public String encryptObject(String passPhrase, Object plainText) {
        try {
            String salt = toHex(generateRandom(keySize / 8));
            String iv = toHex(generateRandom(IV_SIZE / 8));
            String cipherText = encryptObject(salt, iv, passPhrase, plainText.toString());
            return salt + iv + cipherText;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object deryptObject(String passPhrase, String cipherText) {
        try {
            String salt = cipherText.substring(0, saltLength);
            int ivLength = IV_SIZE / 4;
            String iv = cipherText.substring(saltLength, saltLength + ivLength);
            String ct = cipherText.substring(saltLength + ivLength);
            return decryptObject(salt, iv, passPhrase, ct);
        } catch (Exception e) {
            return null;
        }
    }


}
