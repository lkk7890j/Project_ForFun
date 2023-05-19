package idv.tfp10105.project_forfun.common.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class ChatRoom implements Serializable {

    private Integer chatroomId;
    private Integer memberId1;
    private Integer memberId2;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp deleteTime;

    public ChatRoom() {
    }

    public ChatRoom(Integer chatroomId, Integer memberId1, Integer memberId2, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime) {
        this.chatroomId = chatroomId;
        this.memberId1 = memberId1;
        this.memberId2 = memberId2;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
    }

    public ChatRoom(Integer chatroomId, Integer memberId1, Integer memberId2) {
        this.chatroomId = chatroomId;
        this.memberId1 = memberId1;
        this.memberId2 = memberId2;
    }

    public Integer getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Integer getMemberId1() {
        return memberId1;
    }

    public void setMemberId1(Integer memberId1) {
        this.memberId1 = memberId1;
    }

    public Integer getMemberId2() {
        return memberId2;
    }

    public void setMemberId2(Integer memberId2) {
        this.memberId2 = memberId2;
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
