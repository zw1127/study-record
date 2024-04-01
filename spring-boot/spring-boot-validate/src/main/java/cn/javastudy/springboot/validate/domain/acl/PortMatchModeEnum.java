package cn.javastudy.springboot.validate.domain.acl;

//端口匹配规则中前面的选项，match_fragment能被多选故单独处理
public enum PortMatchModeEnum {
    Precedence,
    IpDscp,
    IpTtl,
    ProtoType
}
