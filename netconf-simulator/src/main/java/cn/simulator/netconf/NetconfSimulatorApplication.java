package cn.simulator.netconf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "cn.simulator.netconf")
public class NetconfSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetconfSimulatorApplication.class);
    }
}
