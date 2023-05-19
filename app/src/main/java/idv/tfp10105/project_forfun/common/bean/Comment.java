package idv.tfp10105.project_forfun.common.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class Comment implements Serializable {

    private Integer commentId;
    private Integer memberId;
    private Integer postId;
    private String commentMsg;
    private Boolean read;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp deleteTime;

    public Comment() {
    }

    public Comment(Integer commentId, Integer memberId, Integer postId, String commentMsg, Boolean read, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime)

    {
        this.commentId = commentId;
        this.memberId = memberId;
        this.postId = postId;
        this.commentMsg = commentMsg;
        this.read = read;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
    }

    public Comment(Integer commentId, Integer memberId, Integer postId, String commentMsg) {
        this.commentId = commentId;
        this.memberId = memberId;
        this.postId = postId;
        this.commentMsg = commentMsg;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getCommentMsg() {
        return commentMsg;
    }

    public void setCommentMsg(String commentMsg) {
        this.commentMsg = commentMsg;
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
