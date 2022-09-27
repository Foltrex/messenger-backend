package com.example.whisper.service.impl;

import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.UtilMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UtilMessageServiceImpl implements UtilMessageService {

    private final MessageRepository messageRepository;
    private final FileServiceImpl fileService;

    public List<Message> processMessages(List<Message> messages) {
        setInstantData(messages);

        Message controlMessage = messages.get(0);
        if (controlMessage.getAttachments() == null || "".equals(controlMessage.getAttachments())) {
            return messageRepository.saveAll(messages);
        } else {
            return messageRepository.saveAll(processAttachments(messages));
        }
    }

    private void setInstantData(List<Message> messages) {
        Instant now = Instant.now();
        if (messages.get(0).getChat() == null) {
            UUID chatId = UUID.randomUUID();
            for (Message message : messages) {
                message.setId(UUID.randomUUID());
                message.setCreated(now);
                message.setChat(chatId);
            }
        } else {
            for (Message message : messages) {
                message.setId(UUID.randomUUID());
                message.setCreated(now);
            }
        }
    }

    private List<Message> processAttachments(List<Message> messages) {
        return messages.stream().map(this::processMessage).collect(Collectors.toList());
    }

    private Message processMessage(Message message) {
        String folderPath = fileService.retrieveFolderPath(message.getId());
        fileService.createDirectories(Paths.get(folderPath));
        String[] files = message.getAttachments().split(";");
        fileService.saveFiles(folderPath, files);
        message.setAttachments(retrieveAttachmentsString(files));
        return message;
    }

    private String retrieveAttachmentsString(String[] files) {
        List<String> indexes = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            indexes.add(String.valueOf(i));
        }
        return String.join(";", indexes);
    }

}