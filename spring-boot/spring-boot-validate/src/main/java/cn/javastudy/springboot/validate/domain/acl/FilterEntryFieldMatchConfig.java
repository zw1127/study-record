package cn.javastudy.springboot.validate.domain.acl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FilterEntryFieldMatchConfig {

    //配置各字段匹配规则
    //配置各字段匹配规则 开、关
    private Boolean fieldMatchConfig;
    //TcpState使能
    private Boolean tcpState;
    //Tcp旁边这个单选框: tcpsyn, tcpack, tcpsynack, tcpfin, tcpfinack, tcpurg, tcprst, tcppsh
    private TcpStateOptionEnum tcpStateOption;
    //匹配hop_limit字段使能
    private Boolean matchHoplimit;
    //匹配hop_limit字段参数
    private Integer matchHoplimitField;
    //匹配next_header字段使能
    private Boolean matchNextHeader;
    //匹配next_header字段参数
    private Integer matchNextHeaderField;

}
