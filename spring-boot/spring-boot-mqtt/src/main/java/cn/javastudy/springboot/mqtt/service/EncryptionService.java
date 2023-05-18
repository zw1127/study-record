package cn.javastudy.springboot.mqtt.service;

public interface EncryptionService {

    String encrypt(String data);

    byte[] encrypt(byte[] data);

    String decrypt(String encryptedData);

    byte[] decrypt(byte[] encryptedData);
}
