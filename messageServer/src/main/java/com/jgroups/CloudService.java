package com.jgroups;

import com.Application;

import org.jgroups.*;
import org.jgroups.util.Util;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CloudService extends ReceiverAdapter {
    public static String selfCloudViewId;
    public static Address selfAddress;
    private CloudApi api;
    JChannel channel;
    final List<String> state = new LinkedList<String>();
    private ConcurrentHashMap<String, Address> cloudAddresses = new ConcurrentHashMap<String, Address>();

    @Override
    public void viewAccepted(View new_view) {
        if (null == selfCloudViewId || "".equals(selfCloudViewId)) {
            selfCloudViewId = new_view.getViewId().getCreator().toString().replace("-","");
            selfAddress = new_view.getViewId().getCreator();
        }
        cloudAddresses.clear();
        for (Address address : new_view.getMembersRaw()) {
            if (!address.toString().equals(selfCloudViewId)){
                cloudAddresses.put(address.toString().replace("-",""), address);
            }
        }

        System.out.println("** view: " + new_view);
    }

    @Override
    public void receive(Message msg) {
        //message as {messageId:"123456"}
        String line = msg.getSrc() + ": " + new String(msg.getBuffer());
        System.out.println(line);
        synchronized (state) {
            api.receive(msg.getBuffer());
            state.add(line);
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setState(InputStream input) throws Exception {
        List<String> list = (List<String>) Util.objectFromStream(new DataInputStream(input));
        synchronized (state) {
            state.clear();
            state.addAll(list);
        }
        System.out.println("received state (" + list.size() + " messages in chat history):");
        for (String str : list) {
            System.out.println(str);
        }
    }

    public void setCloudApi(CloudApi api) {
        this.api = api;
    }

    public void start(String clusterName) throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect(clusterName);
        channel.getState(null, 10000);
    }

    public void finish() {
        channel.close();
    }

    public void sendBuffer(byte[] buffer) {
        Message msg = new Message(null, null, buffer);
        try {
            channel.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
