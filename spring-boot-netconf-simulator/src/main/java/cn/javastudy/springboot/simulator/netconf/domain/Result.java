package cn.javastudy.springboot.simulator.netconf.domain;

public interface Result<T> {

    boolean isSuccessful();

    T getData();

    String errorMessage();
}