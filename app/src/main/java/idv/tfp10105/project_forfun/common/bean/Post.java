package idv.tfp10105.project_forfun.common.bean;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Post implements Serializable {

    private Integer postId;
    private String boardId;
    private Integer posterId;
    private String postTitle;
    private String postImg;
    private String posterImg;
    private String postContext;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp deleteTime;

    public Post(Integer postId, String boardId, Integer posterId, String postTitle, String postContext, String postImg) {
        this.postId = postId;
        this.boardId = boardId;
        this.posterId = posterId;
        this.postTitle = postTitle;
        this.postImg = postImg;
        this.postContext = postContext;
    }

    public Post(Integer postId, String boardId, Integer posterId, String postTitle, String postContext, String postImg,
                String posterImg, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime)
    {
        this.postId = postId;
        this.boardId = boardId;
        this.posterId = posterId;
        this.postTitle = postTitle;
        this.postContext = postContext;
        this.postImg = postImg;
        this.posterImg = posterImg;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
    }

    public Post() {

    }

    public void setFiles(Integer postId, Integer posterId,String postTitle ,String postContext, String postImg)
    {
        this.postId = postId;
        this.posterId = posterId;
        this.postTitle = postTitle;
        this.postContext = postContext;
        this.postImg = postImg;
    }

    public void setFilesNoImg(Integer postId, Integer posterId,String postTitle ,String postContext)
    {
        this.postId = postId;
        this.posterId = posterId;
        this.postTitle = postTitle;
        this.postContext = postContext;
    }

    @Override
    public boolean equals(Object obj) {
        return this.posterId == ((Post) obj).posterId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public Integer getPosterId() {
        return posterId;
    }

    public void setPosterId(Integer posterId) {
        this.posterId = posterId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getPosterImg() {
        return posterImg;
    }

    public void setPosterImg(String posterImg) {
        this.posterImg = posterImg;
    }

    public String getPostContext() {
        return postContext;
    }

    public void setPostContext(String postContext) {
        this.postContext = postContext;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Timestamp deleteTime) {
        this.deleteTime = deleteTime;
    }

}
