package cn.javastudy.spi;

import java.util.ServiceLoader;

public class SpiMain {

    public static void main(String[] args) {
        ServiceLoader<ISpeak> speaks = ServiceLoader.load(ISpeak.class);
        for (ISpeak speak : speaks) {
            speak.speak();
        }
    }
}
