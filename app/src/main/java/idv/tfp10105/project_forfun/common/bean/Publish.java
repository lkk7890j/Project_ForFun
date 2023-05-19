package idv.tfp10105.project_forfun.common.bean;

import java.sql.Timestamp;

public class Publish {

	private Integer publishId;
	private Integer ownerId;
	private String title;
	private String titleImg;
	private String publishInfo;
	private String publishImg1;
	private String publishImg2;
	private String publishImg3;
	private Integer cityId;
	private Integer areaId;
	private String address;
	private Double latitude;
	private Double longitude;
	private Integer rent;
	private Integer deposit;
    private Integer square;
    private Integer gender;
    private Integer type;
    private String furnished;
    private Integer status;
    private Timestamp createTime; 
	private Timestamp updateTime;
    private Timestamp deleteTime;

	
	public Publish() {

	}

	public Publish(Integer publishId, Integer ownerId, String title, String titleImg, String publishInfo,
			String publishImg1, String publishImg2, String publishImg3, Integer cityId, Integer areaId, String address,
			Double latitude, Double longitude, Integer rent, Integer deposit, Integer square, Integer gender,
			Integer type, String furnished, Integer status, Timestamp createTime, Timestamp updateTime,
			Timestamp deleteTime) {
		super();
		this.publishId = publishId;
		this.ownerId = ownerId;
		this.title = title;
		this.titleImg = titleImg;
		this.publishInfo = publishInfo;
		this.publishImg1 = publishImg1;
		this.publishImg2 = publishImg2;
		this.publishImg3 = publishImg3;
		this.cityId = cityId;
		this.areaId = areaId;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.rent = rent;
		this.deposit = deposit;
		this.square = square;
		this.gender = gender;
		this.type = type;
		this.furnished = furnished;
		this.status = status;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.deleteTime = deleteTime;
	}


	public Integer getPublishId() {
		return publishId;
	}

	public void setPublishId(Integer publishId) {
		this.publishId = publishId;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleImg() {
		return titleImg;
	}

	public void setTitleImg(String titleImg) {
		this.titleImg = titleImg;
	}

	public String getPublishInfo() {
		return publishInfo;
	}

	public void setPublishInfo(String publishInfo) {
		this.publishInfo = publishInfo;
	}

	public String getPublishImg1() {
		return publishImg1;
	}

	public void setPublishImg1(String publishImg1) {
		this.publishImg1 = publishImg1;
	}

	public String getPublishImg2() {
		return publishImg2;
	}

	public void setPublishImg2(String publishImg2) {
		this.publishImg2 = publishImg2;
	}

	public String getPublishImg3() {
		return publishImg3;
	}

	public void setPublishImg3(String publishImg3) {
		this.publishImg3 = publishImg3;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getRent() {
		return rent;
	}

	public void setRent(Integer rent) {
		this.rent = rent;
	}

	public Integer getDeposit() {
		return deposit;
	}

	public void setDeposit(Integer deposit) {
		this.deposit = deposit;
	}

	public Integer getSquare() {
		return square;
	}

	public void setSquare(Integer square) {
		this.square = square;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getFurnished() {
		return furnished;
	}

	public void setFurnished(String furnished) {
		this.furnished = furnished;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
