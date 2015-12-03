package com.mina;

/**
 * Created by wangyao5 on 15/12/2.
 */
public enum MessageType {
    TEXT,
    IMAGE;

    public byte toByte(){
        return 1;
    }
}
