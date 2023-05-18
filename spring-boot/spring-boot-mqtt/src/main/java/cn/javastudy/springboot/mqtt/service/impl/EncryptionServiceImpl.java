package cn.javastudy.springboot.mqtt.service.impl;

import cn.javastudy.springboot.mqtt.configuration.EncryptConfig;
import cn.javastudy.springboot.mqtt.service.EncryptionService;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptionServiceImpl.class);

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final SecretKey key;
    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    public EncryptionServiceImpl(EncryptConfig encryptConfig) {
        byte[] keyBytes = encryptConfig.getEncryptKey().getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = encryptConfig.getEncryptIv().getBytes(StandardCharsets.UTF_8);

        key = new SecretKeySpec(keyBytes, encryptConfig.getEncryptType());
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(encryptConfig.getCipherTransforms());
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                 | InvalidKeyException e) {
            LOG.error("Failed to create encrypt cipher.", e);
        }
        encryptCipher = cipher;

        cipher = null;
        try {
            cipher = Cipher.getInstance(encryptConfig.getCipherTransforms());
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                 | InvalidKeyException e) {
            LOG.error("Failed to create decrypt cipher.", e);
        }
        decryptCipher = cipher;
        LOG.info("EncryptionService initialed.");
    }

    @Override
    public String encrypt(final String data) {
        // We could not instantiate the encryption key, hence no encryption or
        // decryption will be done.
        if (key == null) {
            LOG.warn("Encryption Key is NULL, will not encrypt data.");
            return data;
        }

        final byte[] cryptobytes;
        try {
            synchronized (encryptCipher) {
                cryptobytes = encryptCipher.doFinal(data.getBytes(Charset.defaultCharset()));
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOG.error("Failed to encrypt data.", e);
            return data;
        }
        return Base64.getEncoder().encodeToString(cryptobytes);
    }

    @Override
    public byte[] encrypt(final byte[] data) {
        // We could not instantiate the encryption key, hence no encryption or
        // decryption will be done.
        if (key == null) {
            LOG.warn("Encryption Key is NULL, will not encrypt data.");
            return data;
        }
        try {
            synchronized (encryptCipher) {
                return encryptCipher.doFinal(data);
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOG.error("Failed to encrypt data.", e);
            return data;
        }
    }

    @Override
    public String decrypt(final String encryptedData) {
        if (key == null || encryptedData == null || encryptedData.length() == 0) {
            LOG.warn("String {} was not decrypted.", encryptedData);
            return encryptedData;
        }

        final byte[] cryptobytes = Base64.getDecoder().decode(encryptedData);
        final byte[] clearbytes;
        try {
            clearbytes = decryptCipher.doFinal(cryptobytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOG.error("Failed to decrypt encoded data", e);
            return encryptedData;
        }
        return new String(clearbytes, Charset.defaultCharset());
    }

    @Override
    public byte[] decrypt(final byte[] encryptedData) {
        if (encryptedData == null) {
            LOG.warn("encryptedData is null.");
            return encryptedData;
        }
        try {
            return decryptCipher.doFinal(encryptedData);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOG.error("Failed to decrypt encoded data", e);
        }
        return encryptedData;
    }

}
