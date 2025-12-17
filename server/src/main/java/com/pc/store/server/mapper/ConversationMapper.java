package com.pc.store.server.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.pc.store.server.dto.response.ConversationResponse;
import com.pc.store.server.entities.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);

    List<ConversationResponse> toConversationResponseList(List<Conversation> conversations);
}
