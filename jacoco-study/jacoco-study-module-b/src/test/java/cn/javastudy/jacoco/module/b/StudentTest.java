package cn.javastudy.jacoco.module.b;

import static org.junit.Assert.*;

import org.junit.Test;

public class StudentTest {

    @Test
    public void testRead() {
        new Student().read();
    }
}
