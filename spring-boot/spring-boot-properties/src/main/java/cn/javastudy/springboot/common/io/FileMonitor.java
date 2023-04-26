package cn.javastudy.springboot.common.io;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class FileMonitor {

    private FileAlterationMonitor monitor;

    public FileMonitor(long interval) {
        monitor = new FileAlterationMonitor(interval);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("file-monitor-%d")
            .build();
        monitor.setThreadFactory(threadFactory);
    }

    /**
     * 给文件添加监听
     *
     * @param path     文件路径
     * @param listener 文件监听器
     */
    public void monitor(String path, FileAlterationListener listener) {
        FileAlterationObserver observer = new FileAlterationObserver(new File(path), pathname -> {
            if (pathname.isDirectory()) {
                return false;
            }
            return "application.yml".equals(pathname.getName());
        });
        monitor.addObserver(observer);
        observer.addListener(listener);
    }

    public void stop() throws Exception {
        monitor.stop();
    }

    public void start() throws Exception {
        monitor.start();
    }

}
