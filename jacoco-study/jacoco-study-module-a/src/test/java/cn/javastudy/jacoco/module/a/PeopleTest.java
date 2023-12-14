package cn.javastudy.jacoco.module.a;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PeopleTest {

    private People people;

    @Before
    public void setUp() {
        people = new People();
    }

    @After
    public void tearDown() {
        people.stop();
    }

    @Test
    public void testSpeak() {
        people.speak();
    }

    @Test(expected = RuntimeException.class)
    public void testException() {
        people.exception();
    }
}
