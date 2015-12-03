package com.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ServerHandler extends IoHandlerAdapter {
    private final static Logger log = LoggerFactory.getLogger(ServerHandler.class);

    private final Set<IoSession> sessions = Collections
            .synchronizedSet(new HashSet<IoSession>());

    private final Set<String> users = Collections
            .synchronizedSet(new HashSet<String>());

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        log.warn("Unexpected exception.", cause);
        // Close connection when unexpected exception is caught.
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        log.info("received: " + message);
    }

    public void broadcast(String message) {
        synchronized (sessions) {
            for (IoSession session : sessions) {
                if (session.isConnected()) {
                    session.write("BROADCAST OK " + message);
                }
            }
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        String user = (String) session.getAttribute("user");
        users.remove(user);
        sessions.remove(session);
        broadcast("The user " + user + " has left the chat session.");
    }

    public boolean isChatUser(String name) {
        return users.contains(name);
    }

    public int getNumberOfUsers() {
        return users.size();
    }

    public void kick(String name) {
        synchronized (sessions) {
            for (IoSession session : sessions) {
                if (name.equals(session.getAttribute("user"))) {
                    session.close(true);
                    break;
                }
            }
        }
    }
}
