package idv.tfp10105.project_forfun.common.bean;

import java.sql.Timestamp;

public class ChatRoomMessage {
    private Integer msgId;
    private Integer chatroomId;
    private Integer memberId;
    private String msgChat;
    private String msgImg;
    private String record;
    private Boolean read;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp deleteTime;

    public ChatRoomMessage() {
    }

    public ChatRoomMessage(Integer msgId, Integer chatroomId, Integer memberId, String msgChat, String msgImg, String record, Boolean read, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime)
    {
        this.msgId = msgId;
        this.chatroomId = chatroomId;
        this.memberId = memberId;
        this.msgChat = msgChat;
        this.msgImg = msgImg;
        this.record = record;
        this.read = read;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
    }

    public ChatRoomMessage(Integer msgId, Integer chatroomId, Integer memberId, String msgChat) {
        this.msgId = msgId;
        this.chatroomId = chatroomId;
        this.memberId = memberId;
        this.msgChat = msgChat;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public Integer getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMsgChat() {
        return msgChat;
    }

    public void setMsgChat(String msgChat) {
        this.msgChat = msgChat;
    }

    public String getMsgImg() {
        return msgImg;
    }

    public void setMsgImg(String msgImg) {
        this.msgImg = msgImg;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Timestamp deleteTime) {
        this.deleteTime = deleteTime;
    }
}
