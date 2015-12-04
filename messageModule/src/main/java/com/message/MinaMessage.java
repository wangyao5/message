package com.message;

/**
 * Created by wangyao5 on 15/12/4.
 */
public class MinaMessage {
    private String id;//消息ID,当消息插入数据库中生成消息ID,用于集群中发送消息ID
    private MessageType type;//消息类型,0.普通文本 1.图片文件
    private String from;//默认不传时为服务端分配的UUID
    private String to;//收信方UUID值
    private String body;//base64 消息体


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
