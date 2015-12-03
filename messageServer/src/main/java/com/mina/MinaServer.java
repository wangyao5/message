package com.mina;

import com.Application;
import com.mina.filter.SessionFilter;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MinaServer {
    public static ConcurrentHashMap<String, IoSession> sessions = new ConcurrentHashMap<String, IoSession>();
    private final static Logger log = LoggerFactory.getLogger(MinaServer.class);
    @Autowired
    private ServerHandler serverHandler;
    @Autowired
    private MessageCodecFactory messageCodecFactory;

    public void startMina() throws IOException {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
        chain.addLast("mdc", mdcInjectionFilter);
        chain.addLast("logger", new LoggingFilter());
        chain.addLast("session", new SessionFilter());
        chain.addLast("codec", new ProtocolCodecFilter(
                new TextLineCodecFactory()));

        // Bind
        acceptor.setHandler(serverHandler);
        acceptor.bind(new InetSocketAddress(Application.PORT));

        System.out.println("Listening on port " + Application.PORT);
        log.info("Listening on port " + Application.PORT);
    }

    public void sendMesage(String to, byte[] message){
        for (String key : sessions.keySet()){
            try {
                sessions.get(key).write(new String(message,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
