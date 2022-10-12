package cn.javastudy.springboot.web.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "response")
public class UserXml {

    @JacksonXmlProperty(localName = "user-id")
    private String id;

    @JacksonXmlProperty(localName = "user-name")
    private String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "order-info")
    private List<OrderInfo> orderList;

}