package com;

import com.jgroups.CloudApi;
import com.jgroups.CloudService;
import com.mina.MinaServer;
import com.zookeeper.ZkOperator;

import org.jgroups.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Configuration
@ComponentScan({"com.jgroups", "com.mina", "com.zookeeper", "com"})
@Component
public class Application {
    public Address address;
    public String cloundViewId;
    @Autowired
    private Settings settings;
    @Autowired
    private CloudService cloudService;
    @Autowired
    private MinaServer minaServer;
    @Autowired
    private ZkOperator zkOperator;

    public void initZk(){
        zkOperator.initZK();
        if (!zkOperator.exist(settings.getZnodePath())) {
            zkOperator.createPersistent(settings.getZnodePath());
        }
    }

    public void startClound(){
        cloudService.setCloudApi(new CloudApi() {
            @Override
            public void receive(byte[] message) {
                //1.获取messageId
                //2.通过messageId,得到{messageId:"11245",messageType:1,from:"wy",to:"xx",body:"base64"}
                //3.从zookeeper中找出to的
                minaServer.sendMesage("", message);
            }
        });
        try {
            cloudService.start(settings.getJClusterName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if (line.startsWith("quit") || line.startsWith("exit")) {
                    break;
                }
                cloudService.sendBuffer(line.getBytes());
            } catch (Exception e) {
            }
        }
    }

    public void startMina(){
        try {
            minaServer.startMina(settings.getMinaPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Settings getSettings(){
        return settings;
    }
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);
        final Application app = (Application) context.getBean("application");
        app.initZk();
        app.startClound();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                app.eventLoop();
            }
        });
        thread.start();
        app.startMina();
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setCloundViewId(String cloundViewId) {
        this.cloundViewId = cloundViewId;
    }
}
