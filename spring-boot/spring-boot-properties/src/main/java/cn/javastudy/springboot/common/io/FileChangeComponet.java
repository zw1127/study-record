package cn.javastudy.springboot.common.io;

import jakarta.annotation.PostConstruct;
import java.io.File;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class FileChangeComponet {

    @PostConstruct
    public void start() throws Exception {
        FileMonitor fileMonitor = new FileMonitor(1000);
        File file = ResourceUtils.getFile("classpath:");
        fileMonitor.monitor(file.getCanonicalPath(), new FileListener());
        fileMonitor.start();
    }
}
