package cn.javastudy.disruptor.basic.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class StringEvent {

    private String value;
}
