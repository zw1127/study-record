package cn.javastudy.springboot.validate.domain.mac;

import lombok.Data;

@Data
public class MacLearning {

    //设备id(key)
    private String nodeId;  //node_id
    //端口索引
    private String ifIndex;  //if_index

    private String ethifName;

    private Integer ethifNum;

    //mac学习使能
    private Boolean macLearningEnable;  //learning_enable
    //mac learning disable action : drop(1),forward (2)
    private LearningAction macLearningDisableAction; //learning_action

    private LearningAction macOverMaxAction; //max_action
    //最大mac数目
    private Integer macLimitNum; //limit_num
    //学习限制告警使能
    private Boolean macLimitIfAlarmStatus;  //alarm_status

}
