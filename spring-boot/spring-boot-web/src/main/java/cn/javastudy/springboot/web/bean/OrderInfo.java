package cn.javastudy.springboot.web.bean;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderInfo {

    private String orderNo;

    private long amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSZ", timezone = "GMT+8")
    private Date time;

}
