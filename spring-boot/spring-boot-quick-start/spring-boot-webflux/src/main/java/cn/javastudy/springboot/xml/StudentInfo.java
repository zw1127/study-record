package cn.javastudy.springboot.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "student-info")
public class StudentInfo {

    @JacksonXmlProperty(localName = "student-id")
    private String studentId;

    @JacksonXmlProperty(localName = "student-name")
    private String studentName;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "score-info")
    private List<ScoreInfo> orderList;
}
