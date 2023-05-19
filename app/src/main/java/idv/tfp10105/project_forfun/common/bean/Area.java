package idv.tfp10105.project_forfun.common.bean;

import java.sql.Timestamp;

public class Area {

	private Integer areaId;
	private Integer cityId;
	private String areaName;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Timestamp deleteTime;

	public Area(Integer areaId, Integer cityId, String areaName, Timestamp createTime, Timestamp updateTime,
			Timestamp deleteTime) {
		super();
		this.areaId = areaId;
		this.cityId = cityId;
		this.areaName = areaName;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.deleteTime = deleteTime;
	}

	public Area() {

	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
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
