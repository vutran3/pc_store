package com.pc.store.server.controllers;

import java.text.ParseException;
import java.time.Instant;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.nimbusds.jose.JOSEException;
import com.pc.store.server.dao.CustomerRespository;
import com.pc.store.server.dto.request.IntrospectRequest;
import com.pc.store.server.entities.WebSocketSession;
import com.pc.store.server.services.impl.AuthenticationService;
import com.pc.store.server.services.impl.WebSocketSessionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    SocketIOServer server;
    WebSocketSessionService webSocketSessionService;
    AuthenticationService authenticationService;
    CustomerRespository customerRespository;

    @OnConnect
    public void clientConnected(SocketIOClient client) throws ParseException, JOSEException {
        // Get Token from request param
        String token = client.getHandshakeData().getSingleUrlParam("token");
        log.info("Client connecting: {} with token: {}", client.getSessionId(), token);
        // Verify token
        var introspectResponse = authenticationService.introspect(
                IntrospectRequest.builder().token(token).build());

        var info = authenticationService.decode(token);
        var userprofile = customerRespository.findByUserName(info);

        // If Token is invalid disconnect
        if (introspectResponse.isValid()) {
            log.info("Client connected: {}", client.getSessionId());
            // Persist webSocketSession
            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(client.getSessionId().toString())
                    .userId(
                            userprofile.isPresent()
                                    ? String.valueOf(userprofile.get().getId())
                                    : "unknown")
                    .createdAt(Instant.now())
                    .build();
            webSocketSession = webSocketSessionService.create(webSocketSession);

            log.info("WebSocketSession created with id: {}", webSocketSession.getId());
        } else {
            log.error("Authentication fail: {}", client.getSessionId());
            client.disconnect();
        }
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client) {
        log.info("Client disConnected: {}", client.getSessionId());
        webSocketSessionService.deleteSession(client.getSessionId().toString());
    }

    @PostConstruct
    public void startServer() {
        server.start();
        server.addListeners(this);
        log.info("Socket server started");
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket server stoped");
    }
}
