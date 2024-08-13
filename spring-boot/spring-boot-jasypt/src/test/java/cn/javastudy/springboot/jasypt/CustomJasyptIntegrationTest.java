package cn.javastudy.springboot.jasypt;

import static org.junit.Assert.assertEquals;

import jakarta.annotation.Resource;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JasyptApplication.class})
public class CustomJasyptIntegrationTest {

    @Resource
    private ApplicationContext appCtx;

    @Resource
    @Qualifier("encryptorBean")
    private StringEncryptor stringEncryptor;

    @Test
    public void whenConfiguredExcryptorUsed_ReturnCustomEncryptor() {
        Environment environment = appCtx.getBean(Environment.class);
        System.out.println(stringEncryptor.encrypt("Password@3"));
        assertEquals("Password@3", environment.getProperty("encryptedv3.property"));
    }

}
