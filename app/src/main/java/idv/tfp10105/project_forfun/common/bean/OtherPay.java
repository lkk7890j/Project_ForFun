package idv.tfp10105.project_forfun.common.bean;

import java.sql.Timestamp;

public class OtherPay {

    private Integer otherpayId;
    private Integer agreementId;
    private Integer otherpayMoney;
    private String otherpayNote;
    private String suggestImg;
    private Integer otherpayStatus;
    private Timestamp createTime;
    private Timestamp deleteTime;

    public OtherPay(){

    }

    public OtherPay(Integer otherpayId, Integer agreementId, Integer otherpayMoney,
                    String otherpayNote, String suggestImg, Integer otherpayStatus,
                    Timestamp createTime, Timestamp deleteTime) {
        super();
        this.otherpayId = otherpayId;
        this.agreementId = agreementId;
        this.otherpayMoney = otherpayMoney;
        this.otherpayNote = otherpayNote;
        this.suggestImg = suggestImg;
        this.otherpayStatus = otherpayStatus;
        this.createTime = createTime;
        this.deleteTime = deleteTime;
    }

    public Integer getOtherpayId() {
        return otherpayId;
    }

    public void setOtherpayId(Integer otherpayId) {
        this.otherpayId = otherpayId;
    }

    public Integer getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(Integer agreementId) {
        this.agreementId = agreementId;
    }

    public Integer getOtherpayMoney() {
        return otherpayMoney;
    }

    public void setOtherpayMoney(Integer otherpayMoney) {
        this.otherpayMoney = otherpayMoney;
    }

    public String getOtherpayNote() {
        return otherpayNote;
    }

    public void setOtherpayNote(String otherpayNote) {
        this.otherpayNote = otherpayNote;
    }

    public String getSuggestImg() {
        return suggestImg;
    }

    public void setSuggestImg(String suggestImg) {
        this.suggestImg = suggestImg;
    }

    public Integer getOtherpayStatus() {
        return otherpayStatus;
    }

    public void setOtherpayStatus(Integer otherpayStatus) {
        this.otherpayStatus = otherpayStatus;
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
