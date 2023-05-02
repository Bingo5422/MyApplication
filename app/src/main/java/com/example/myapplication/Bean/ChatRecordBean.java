package com.example.myapplication.Bean;

public class ChatRecordBean {

    private int id;  // 记录 id
    private String senderId;  // 发送者 id
    private String receiverId;  // 接收者 id
    private String content;  // 聊天内容
    private long timestamp;  // 发送时间戳

    public ChatRecordBean(int id, String senderId, String receiverId, String content, long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
