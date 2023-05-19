package idv.tfp10105.project_forfun.common.bean;

import java.sql.Timestamp;

public class Agreement {

    private Integer agreementId;
    private Integer orderId;
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer agreementMoney;
    private String agreementNote;
    private String landlordSign;
    private String tenantSign;
    private Timestamp createTime;
    private Timestamp deleteTime;

    public Agreement(){

    }

    public Agreement(Integer agreementId, Integer orderId, Timestamp startDate, Timestamp endDate, Integer agreementMoney, String agreementNote, String landlordSign, String tenantSign, Timestamp createTime, Timestamp deleteTime) {
        this.agreementId = agreementId;
        this.orderId = orderId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.agreementMoney = agreementMoney;
        this.agreementNote = agreementNote;
        this.landlordSign = landlordSign;
        this.tenantSign = tenantSign;
        this.createTime = createTime;
        this.deleteTime = deleteTime;
    }

    public Integer getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(Integer agreementId) {
        this.agreementId = agreementId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Integer getAgreementMoney() {
        return agreementMoney;
    }

    public void setAgreementMoney(Integer agreementMoney) {
        this.agreementMoney = agreementMoney;
    }

    public String getAgreementNote() {
        return agreementNote;
    }

    public void setAgreementNote(String agreementNote) {
        this.agreementNote = agreementNote;
    }

    public String getLandlordSign() {
        return landlordSign;
    }

    public void setLandlordSign(String landlordSign) {
        this.landlordSign = landlordSign;
    }

    public String getTenantSign() {
        return tenantSign;
    }

    public void setTenantSign(String tenantSign) {
        this.tenantSign = tenantSign;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Timestamp deleteTime) {
        this.deleteTime = deleteTime;
    }

}
