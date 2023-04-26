package cn.javastudy.springboot.common.io;

import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class FileListener extends FileAlterationListenerAdaptor {

    private static final Logger LOG = LoggerFactory.getLogger(FileListener.class);

    private final Yaml yaml = new Yaml();

    @Override
    public void onFileCreate(File file) {
        String path = file.getAbsolutePath();
        LOG.info("file:{} create.", path);
        if (file.canRead()) {
            // TODO 读取或重新加载文件内容
            LOG.info("file:{} update, process.", path);
        }
    }

    @Override
    public void onFileChange(File file) {
        String compressedPath = file.getAbsolutePath();
        LOG.info("file:{} changed.", compressedPath);
        try {
            BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
            Properties properties = yaml.loadAs(reader, Properties.class);
            LOG.info("value:{}", properties.get("user"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFileDelete(File file) {
        LOG.info("file:{} deleted.", file.getAbsolutePath());
    }

}