package cn.javastudy.spi.animal;

import cn.javastudy.spi.ISpeak;

public class Dog implements ISpeak {

    @Override
    public void speak() {
        System.out.println("wangwang");
    }
}
