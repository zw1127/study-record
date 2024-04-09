package cn.javastudy.springboot.validate.domain.ntp;

import lombok.Data;

@Data
public class NtpAuthEntry {

    //设备id
    private String nodeId;
    //认证ID秘钥 int（+-）2147483648存不下42944967295
    private Long authKeyId;
    //认证MD5秘钥
    private String md5KeyId;
    //信任认证秘钥
    private Boolean trustedKeyId;

}
