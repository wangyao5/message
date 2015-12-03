package com;

import com.jgroups.CloudApi;
import com.jgroups.CloudService;
import com.mina.MinaServer;
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

import static com.jgroups.CloudService.selfAddress;
import static com.jgroups.CloudService.selfCloudViewId;

@Configuration
@ComponentScan({"com.jgroups", "com.mina"})
@Component
public class Application {
    public static final int PORT = 9123;
    public static Address address = selfAddress;
    public static String cloundViewId = selfCloudViewId;
    @Autowired
    private CloudService cloudService;
    @Autowired
    private MinaServer minaServer;

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
            cloudService.start();
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
            minaServer.startMina();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        ApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);
        final Application app = (Application) context.getBean("application");
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
}
