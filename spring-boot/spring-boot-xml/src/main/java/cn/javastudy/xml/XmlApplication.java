package cn.javastudy.xml;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class XmlApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmlApplication.class);
    }

    @GetMapping(value = "/student/xml/{studentId}", produces = MediaType.APPLICATION_XML_VALUE)
    public StudentInfo getUserInfo(@PathVariable("studentId") String studentId) {
        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setStudentName("张三");
        studentInfo.setStudentId(studentId);

        List<ScoreInfo> scoreInfoList = new ArrayList<>();
        ScoreInfo scoreInfo1 = new ScoreInfo("Language", 98, Instant.now(Clock.systemDefaultZone()));
        ScoreInfo scoreInfo2 = new ScoreInfo("Math", 100, Instant.now(Clock.systemDefaultZone()));
        ScoreInfo scoreInfo3 = new ScoreInfo("English", 99, Instant.now(Clock.systemDefaultZone()));

        scoreInfoList.add(scoreInfo1);
        scoreInfoList.add(scoreInfo2);
        scoreInfoList.add(scoreInfo3);
        studentInfo.setOrderList(scoreInfoList);

        return studentInfo;
    }
}
