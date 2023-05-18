package cn.javastudy.springboot.mqtt.service.impl;

import cn.javastudy.springboot.mqtt.configuration.EncryptConfig;
import cn.javastudy.springboot.mqtt.service.EncryptionService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class EncryptionServiceImplTest {

    @Test
    public void testEncrypt() {
        EncryptConfig encryptConfig = new EncryptConfig();
        encryptConfig.setCipherTransforms("AES/CBC/PKCS7Padding");
        encryptConfig.setEncryptType("AES");
        encryptConfig.setEncryptIv("0000000000000000");
        encryptConfig.setEncryptKey("10293847561029384756102938475600");

        EncryptionService encryptionService = new EncryptionServiceImpl(encryptConfig);

        String planText = "mibcounter_get?";
        String encrypt = encryptionService.encrypt(planText);
        String decrypt = encryptionService.decrypt(encrypt);
        Assert.assertEquals(planText, decrypt);
    }
}
