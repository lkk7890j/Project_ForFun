package idv.tfp10105.project_forfun.common.bean;

import java.sql.Timestamp;

public class City {

	private Integer cityId;
	private String cityName;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Timestamp deleteTime;

	public City(Integer cityId, String cityName, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime) {
		super();
		this.cityId = cityId;
		this.cityName = cityName;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.deleteTime = deleteTime;
	}

	public City() {

	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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
