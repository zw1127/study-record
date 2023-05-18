package cn.javastudy.springboot.mqtt.configuration;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "encrypt", ignoreInvalidFields = true)
@Component("encryptConfig")
public class EncryptConfig {

    private String encryptKey;

    // AES
    private String encryptType;

    // AES/CBC/PKCS7Padding jdk不支持，需要依赖bcprov-jdk18on
    private String cipherTransforms;

    private String encryptIv;

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(String encryptType) {
        this.encryptType = encryptType;
    }

    public String getCipherTransforms() {
        return cipherTransforms;
    }

    public void setCipherTransforms(String cipherTransforms) {
        this.cipherTransforms = cipherTransforms;
    }

    public String getEncryptIv() {
        return encryptIv;
    }

    public void setEncryptIv(String encryptIv) {
        this.encryptIv = encryptIv;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        EncryptConfig that = (EncryptConfig) obj;
        return Objects.equals(encryptKey, that.encryptKey)
            && Objects.equals(encryptType, that.encryptType)
            && Objects.equals(cipherTransforms, that.cipherTransforms)
            && Objects.equals(encryptIv, that.encryptIv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptKey, encryptType, cipherTransforms, encryptIv);
    }
}
