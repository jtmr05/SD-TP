package common;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import common.Consts.MessageType;

public class Frame {

    public final MessageType tag;
    private final byte[] data;

    public Frame(MessageType tag, byte[] data){
        this.tag = tag;
        this.data = data;
    }

    public Frame(MessageType tag, String s){
        this.tag = tag;
        this.data = this.serialize(s);
    }

    public byte[] getData(){
        return Arrays.copyOf(this.data, this.data.length);
    }

    public String getDataAsString(){
        return new String(this.data, StandardCharsets.UTF_8);
    }

    private byte[] serialize(String str){
        return str.getBytes(StandardCharsets.UTF_8);
    }
}