package cn.javastudy.springboot.xml;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoreInfo {

    private String course;

    private Integer score;

    @JacksonXmlProperty(localName = "update-time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSZ", timezone = "GMT+8")
    private Instant updateTime;
}
