package cn.javastudy.springboot.netconf.client;

class Parent {

    public void speak() {
        System.out.println("Parent");
    }
}

class Son extends Parent {// 1.有类继承或者接口实现

    public void speak() {// 2.子类要重写父类的方法
        System.out.println("Son");
    }
}

class Daughter extends Parent {// 1.有类继承或者接口实现

    public void speak() {// 2.子类要重写父类的方法
        System.out.println("Daughter");
    }
}

public class Test {

    public int add(int a, int b) {
        return a + b;
    }

    public long add(long a, long b) {
        return a + b;
    }

    public int add (int a, int b, int c) {
        return a + b + c;
    }
}

