package com.pc.store.server.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.pc.store.server.dto.request.ChatMessageRequest;
import com.pc.store.server.dto.response.ChatMessageResponse;
import com.pc.store.server.entities.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);

    ChatMessage toChatMessage(ChatMessageRequest request);

    List<ChatMessageResponse> toChatMessageResponses(List<ChatMessage> chatMessages);
}
