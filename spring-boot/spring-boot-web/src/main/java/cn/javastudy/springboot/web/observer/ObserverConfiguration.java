package cn.javastudy.springboot.web.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ObserverConfiguration.class);

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return (args) -> {
            LOG.info("发布事件：什么是观察者模式？");
            context.publishEvent(new Event("什么是观察者模式？"));
        };
    }

    @Bean
    public ReaderListener readerListener1(){
        return new ReaderListener("小明");
    }

    @Bean
    public ReaderListener readerListener2(){
        return new ReaderListener("小张");
    }

    @Bean
    public ReaderListener readerListener3(){
        return new ReaderListener("小爱");
    }
}
