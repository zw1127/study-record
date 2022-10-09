package cn.javastudy.disruptor.consumer.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class OrderEvent {

    private String value;
}
