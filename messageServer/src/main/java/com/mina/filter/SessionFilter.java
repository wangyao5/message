package com.mina.filter;

import com.Settings;
import com.jgroups.CloudService;
import com.mina.MinaServer;
import com.zookeeper.ZkOperator;

import org.apache.mina.core.buffer.AbstractIoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SessionFilter extends IoFilterAdapter {
    @Autowired
    private Settings settings;
    @Autowired(required = false)
    private ZkOperator zkOperator;

    @Override
    public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        nextFilter.exceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        nextFilter.messageReceived(session, message);
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        nextFilter.messageSent(session, writeRequest);
    }

    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
        nextFilter.sessionCreated(session);
    }

    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        MinaServer.sessions.put(uuid, session);
        session.write(uuid);
        session.setAttribute("uuid", uuid);
        //将uuid跟ClondService中的cloudViewId添加到zookeeper上作为临时结点
        //Application.cloundViewId;
        zkOperator.createEphemeral(settings.getZnodePath() + "/" + CloudService.selfCloudViewId + "-" + uuid);
        System.out.println("in :" + CloudService.selfCloudViewId + "-" + uuid);
        nextFilter.sessionOpened(session);
    }

    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        nextFilter.sessionIdle(session, status);
    }

    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
        MinaServer.sessions.remove(session.getAttribute("uuid").toString());
        //将uuid跟ClondService中的cloudViewId从zookeeper结点中移除
        //Application.cloundViewId;
        zkOperator.deleteEphemeral(settings.getZnodePath() + "/" + CloudService.selfCloudViewId + "-" + session.getAttribute("uuid").toString());
        nextFilter.sessionClosed(session);
    }
}
