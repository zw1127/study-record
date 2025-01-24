package cn.javastudy.spi.animal;

import cn.javastudy.spi.ISpeak;

public class Cat implements ISpeak {
    @Override
    public void speak() {
        System.out.println("miaomiao");
    }
}
