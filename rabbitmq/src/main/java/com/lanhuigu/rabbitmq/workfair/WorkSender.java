package com.lanhuigu.rabbitmq.workfair;

import com.lanhuigu.rabbitmq.utils.ConnectionUtil;
import com.lanhuigu.rabbitmq.utils.CommonConsant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 基于工作队列--生产者
 * @author yihonglei
 * @date 2018/12/18 11:18
 */
public class WorkSender {

    /**
     *      |--->C1
     * P--->|
     *      |--->C2
     *
     * 消息发送，一个生产者，两个消费者
     * @author yihonglei
     * @date 2018/12/18 13:43
     */
    public static void send() throws IOException, TimeoutException, InterruptedException {
        // 创建连接
        Connection connection = ConnectionUtil.getConnection();

        // 创建通道
        Channel channel = connection.createChannel();

        // 声明消息队列
        channel.queueDeclare(CommonConsant.WORK_QUEUE_NAME, false,false, false, null);

        /*
          每个消费者发送确认消息之前，消息队列不发送下一个消息到消费者，一次只处理一个消息。
          限制发送给同一个消费者不得超过1条消息。
        */
        int perfetchCount = 1;
        channel.basicQos(perfetchCount);

        // 消息发送
        for (int i = 0; i < 50; i++) {
            String message = "hello " + i;

            System.out.println("生产者：" + message);

            channel.basicPublish("", CommonConsant.WORK_QUEUE_NAME, null, message.getBytes());

            // 每次消息发送后休眠下，避免消息发送太快，看不出效果
            Thread.sleep(i + 20);
        }

        // 关闭资源
        channel.close();
        connection.close();
    }

}
