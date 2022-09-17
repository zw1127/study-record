package cn.javastudy.disruptor.common;

public class ObjectEvent<T> {

    private T obj;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public void clear() {
        obj = null;
    }
}
