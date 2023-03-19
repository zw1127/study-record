package cn.javastudy.springboot.ldap;

import cn.javastudy.springboot.ldap.domain.Person;
import cn.javastudy.springboot.ldap.repo.PersonRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class LdapApplicationTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void findAll() {
        personRepository.findAll().forEach(System.out::println);
    }

    @Test
    public void save() {
        Person person = new Person();
        person.setUid("uid:1");
        person.setUserName("AAA");
        person.setCommonName("aaa");
        person.setUserPassword("123456");
        personRepository.save(person);

        personRepository.findAll().forEach(System.out::println);
    }

    @Test
    public void testThreadWait() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);

        service.submit(() -> process(1000, "job1"));
        service.submit(() -> process(2000, "job2"));
        service.submit(() -> process(10000, "job3"));
        service.submit(() -> process(1000, "job4"));

        service.awaitTermination(6, TimeUnit.SECONDS);
    }

    private void process(long timeout, String jobName) {
        System.out.println("begin to process thread:" + jobName + " current: " + System.currentTimeMillis());
        Object obj = new Object();
        try {
            synchronized (obj) {
                obj.wait(timeout);
            }
        } catch (Throwable throwable) {
            System.out.println("interrupted.");
            Thread.currentThread().interrupt();
        }

        System.out.println("end processed thread:" + jobName + " current: " + System.currentTimeMillis());
    }
}
