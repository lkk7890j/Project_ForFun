package idv.tfp10105.project_forfun.common.bean;

import java.sql.Timestamp;

public class Appointment {

	private Integer appointmentId;
	private Integer publishId;
	private Integer ownerId;
	private Integer tenantId;
	private Timestamp appointmentTime;
	private Boolean read;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Timestamp deleteTime;

	public Appointment(Integer appointmentId, Integer publishId, Integer ownerId, Integer tenantId,
			Timestamp appointmentTime, Boolean read, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime) {
		super();
		this.appointmentId = appointmentId;
		this.publishId = publishId;
		this.ownerId = ownerId;
		this.tenantId = tenantId;
		this.appointmentTime = appointmentTime;
		this.read = read;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.deleteTime = deleteTime;
	}

	public Appointment() {

	}

	public Integer getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Integer appointmentId) {
		this.appointmentId = appointmentId;
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

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public Timestamp getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(Timestamp appointmentTime) {
		this.appointmentTime = appointmentTime;
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
