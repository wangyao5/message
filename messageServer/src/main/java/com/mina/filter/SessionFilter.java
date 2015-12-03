package com.mina.filter;

import com.mina.MinaServer;
import org.apache.mina.core.buffer.AbstractIoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

public class SessionFilter extends IoFilterAdapter {
    private final static Logger log = LoggerFactory.getLogger(SessionFilter.class);
    @Override
    public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        log.debug("EXCEPTION :" + cause);
        nextFilter.exceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        log.debug("RECEIVED: {}", message+session.toString());
        AbstractIoBuffer buf = (AbstractIoBuffer)message;
        byte[] buffer = new byte[buf.limit()];
        buf.get(buffer);
        log.debug("RECEIVED message: {} end", new String(buffer, "UTF-8"));
        nextFilter.messageReceived(session, message);
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        log.debug("SENT: {}", writeRequest.getOriginalRequest().getMessage());
        nextFilter.messageSent(session, writeRequest);
    }

    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
        log.debug("CREATED");
        nextFilter.sessionCreated(session);
    }

    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
        log.debug("OPENED");
        String uuid = UUID.randomUUID().toString().replace("-","");
        MinaServer.sessions.put(uuid, session);
        session.write(uuid);
        session.setAttribute("uuid", uuid);
        //将uuid跟ClondService中的cloudViewId添加到zookeeper上作为临时结点
        //Application.cloundViewId;
        nextFilter.sessionOpened(session);
    }

    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        log.debug("IDLE");
        session.write("IDLE");
        nextFilter.sessionIdle(session, status);
    }

    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
        log.debug("CLOSED");
        session.write("CLOSED");
        MinaServer.sessions.remove(session.getAttribute("uuid").toString());
        //将uuid跟ClondService中的cloudViewId从zookeeper结点中移除
        //Application.cloundViewId;
        nextFilter.sessionClosed(session);
    }
}
