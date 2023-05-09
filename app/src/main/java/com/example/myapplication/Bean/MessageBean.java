package com.example.myapplication.Bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_records")
public class MessageBean {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "from_user")
    private String fromUser;

    public Boolean getChallenge() {
        return challenge;
    }

    @ColumnInfo(name = "challenge")
    private Boolean challenge;
    @ColumnInfo(name = "to_user")
    private String toUser;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "send_time")
    private long sendTime;




    public MessageBean(String fromUser, String toUser, String content, long sendTime, Boolean challenge) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.content = content;
        this.sendTime = sendTime;
        this.challenge = challenge;
    }
    // 表示该构造方法不被 Room 使用
    @Ignore
    public MessageBean(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
}
