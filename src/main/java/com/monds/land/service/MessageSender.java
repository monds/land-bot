package com.monds.land.service;

import com.github.mustachejava.Mustache;
import com.monds.land.domain.Room;
import com.monds.land.dto.SlackWebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;

@RequiredArgsConstructor
@Service
public class MessageSender {
    private final SlackMessageApi slackApi;
    private final Mustache messageTemplate;

    public void send(Room room) {
        StringWriter writer = new StringWriter();
        messageTemplate.execute(writer, room);
//        System.out.println(writer);
        try {
            slackApi.sendMessage(new SlackWebhookRequest(writer.toString())).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
