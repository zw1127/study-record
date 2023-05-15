package cn.javastudy.springboot.snmp.utils;

public interface SnmpConstans {

    String DEFAULT_LISTENER_PORT = "10162";

    Integer DEFAULT_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    Integer DEFAULT_TABLE_REQUEST_TIMEOUT = 20;
}
