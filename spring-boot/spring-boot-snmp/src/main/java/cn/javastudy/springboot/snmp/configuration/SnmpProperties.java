package cn.javastudy.springboot.snmp.configuration;

import static cn.javastudy.springboot.snmp.utils.SnmpConstans.DEFAULT_LISTENER_PORT;
import static cn.javastudy.springboot.snmp.utils.SnmpConstans.DEFAULT_POOL_SIZE;
import static cn.javastudy.springboot.snmp.utils.SnmpConstans.DEFAULT_TABLE_REQUEST_TIMEOUT;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "snmp", ignoreInvalidFields = true)
@Component
public class SnmpProperties {

    private String listenAddress = "0.0.0.0";

    private String listenPort = DEFAULT_LISTENER_PORT;

    private Integer tableRequestTimeout = DEFAULT_TABLE_REQUEST_TIMEOUT;

    private Integer poolSize = DEFAULT_POOL_SIZE;

    private boolean checkUsmUserPassphraseLength = false;

    public String getListenAddress() {
        return listenAddress;
    }

    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }

    public String getListenPort() {
        return listenPort;
    }

    public void setListenPort(String listenPort) {
        this.listenPort = listenPort;
    }

    public boolean isCheckUsmUserPassphraseLength() {
        return checkUsmUserPassphraseLength;
    }

    public void setCheckUsmUserPassphraseLength(boolean checkUsmUserPassphraseLength) {
        this.checkUsmUserPassphraseLength = checkUsmUserPassphraseLength;
    }

    public Integer getTableRequestTimeout() {
        return tableRequestTimeout;
    }

    public void setTableRequestTimeout(Integer tableRequestTimeout) {
        this.tableRequestTimeout = tableRequestTimeout;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

}
