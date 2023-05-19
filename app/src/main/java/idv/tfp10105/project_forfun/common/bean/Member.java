package idv.tfp10105.project_forfun.common.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class Member implements Serializable {

	private Integer memberId;
	private Integer role;
	private String nameL;
	private String nameF;
	private Integer phone;
	private String headshot;
	private Integer gender;
	private String id;
	private Timestamp birthady;
	private String address;
	private String mail;
	private Integer type;
	private String token;
	private String idImgf;
	private String idImgb;
	private String citizen;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Timestamp deleteTime;

	public Member(Integer memberId, Integer role, String nameL, String nameF, Integer phone, String headshot,
			Integer gender, String id, Timestamp birthady, String address, String mail, Integer type, String token,
			String idImgf, String idImgb, String citizen, Timestamp createTime, Timestamp updateYime,
			Timestamp deleteTime) {
		super();
		this.memberId = memberId;
		this.role = role;
		this.nameL = nameL;
		this.nameF = nameF;
		this.phone = phone;
		this.headshot = headshot;
		this.gender = gender;
		this.id = id;
		this.birthady = birthady;
		this.address = address;
		this.mail = mail;
		this.type = type;
		this.token = token;
		this.idImgf = idImgf;
		this.idImgb = idImgb;
		this.citizen = citizen;
		this.createTime = createTime;
		this.updateTime = updateYime;
		this.deleteTime = deleteTime;
	}

	public Member() {

	}

	@Override
	public boolean equals(Object obj) {
		return this.memberId == ((Member) obj).memberId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public String getNameL() {
		return nameL;
	}

	public void setNameL(String nameL) {
		this.nameL = nameL;
	}

	public String getNameF() {
		return nameF;
	}

	public void setNameF(String nameF) {
		this.nameF = nameF;
	}

	public Integer getPhone() {
		return phone;
	}

	public void setPhone(Integer phone) {
		this.phone = phone;
	}

	public String getHeadshot() {
		return headshot;
	}

	public void setHeadshot(String headshot) {
		this.headshot = headshot;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getBirthady() {
		return birthady;
	}

	public void setBirthady(Timestamp birthady) {
		this.birthady = birthady;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIdImgf() {
		return idImgf;
	}

	public void setIdImgf(String idImgf) {
		this.idImgf = idImgf;
	}

	public String getIdImgb() {
		return idImgb;
	}

	public void setIdImgb(String idImgb) {
		this.idImgb = idImgb;
	}

	public String getCitizen() {
		return citizen;
	}

	public void setCitizen(String citizen) {
		this.citizen = citizen;
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

	public void setUpdateTime(Timestamp updateYime) {
		this.updateTime = updateYime;
	}

	public Timestamp getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Timestamp deleteTime) {
		this.deleteTime = deleteTime;
	}

}
