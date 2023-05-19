package idv.tfp10105.project_forfun.common.bean;

import android.os.Build;

import java.util.Arrays;
import java.util.Objects;

// 首頁用的資料，只是方便資料處理而建立的
public class PublishHome {
    private Publish publish;
    private double distance;

    public PublishHome() {
    }

    public PublishHome(Publish publish, double distance) {
        this.publish = publish;
        this.distance = distance;
    }

    public Publish getPublish() {
        return publish;
    }

    public void setPublish(Publish publish) {
        this.publish = publish;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublishHome publishHome = (PublishHome) o;
        return publish.getPublishId().intValue() == publishHome.getPublish().getPublishId().intValue();
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(publish.getPublishId());
        } else {
            return Arrays.hashCode(new Integer[]{publish.getPublishId()});
        }
    }
}
