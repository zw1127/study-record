package cn.javastudy.springboot.jasypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;

public class JasyptUnitTest {

    @Test
    public void givenTextPrivateDataWhenDecryptThenCompareToEncrypted() {
        // given
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        String privateData = "secret-data";
        textEncryptor.setPasswordCharArray("some-random-data".toCharArray());

        // when
        String myEncryptedText = textEncryptor.encrypt(privateData);
        // myEncryptedText can be save in db
        assertNotSame(privateData, myEncryptedText);

        // then
        String plainText = textEncryptor.decrypt(myEncryptedText);
        assertEquals(plainText, privateData);
    }

    @Test
    public void givenTextPasswordWhenOneWayEncryptionThenCompareEncryptedPasswordsShouldBeSame() {
        String password = "secret-pass";
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(password);

        // when
        boolean result = passwordEncryptor.checkPassword("secret-pass", encryptedPassword);

        // then
        assertTrue(result);
    }

    @Test
    public void givenTextPasswordWhenOneWayEncryptionThenCompareEncryptedPasswordsShouldNotBeSame() {
        String password = "secret-pass";
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(password);

        // when
        boolean result = passwordEncryptor.checkPassword("secret-pass-not-same", encryptedPassword);

        // then
        assertFalse(result);
    }

}
