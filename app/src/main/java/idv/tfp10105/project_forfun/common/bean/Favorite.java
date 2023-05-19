package idv.tfp10105.project_forfun.common.bean;

import java.sql.Timestamp;

public class Favorite {

	private Integer favoriteId;
	private Integer memberId;
	private Integer publishId;
	private Timestamp createTime;

	public Favorite(Integer favoriteId, Integer memberId, Integer publishId, Timestamp createTime) {
		super();
		this.favoriteId = favoriteId;
		this.memberId = memberId;
		this.publishId = publishId;
		this.createTime = createTime;
	}

	public Favorite() {

	}

	public Integer getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(Integer favoriteId) {
		this.favoriteId = favoriteId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getPublishId() {
		return publishId;
	}

	public void setPublishId(Integer publishId) {
		this.publishId = publishId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}
