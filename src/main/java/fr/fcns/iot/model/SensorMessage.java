package fr.fcns.iot.model;

import java.time.ZonedDateTime;

/**
 * Created by lmarchau on 12/07/2016.
 */
public class SensorMessage {

    private String sensorId;
    private Boolean status;
    private ZonedDateTime createdAt;

    private String voitureId;
    private String couponId;
    private String ordreId;
    private String trainId;

    public SensorMessage(String message) {
        sensorId = message.substring(0, 12);
        createdAt = ZonedDateTime.parse(message.substring(12, 41));
        status = Boolean.valueOf(message.substring(41));
    }

    public String getSensorId() {
        return sensorId;
    }

    public Boolean getStatus() {
        return status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public String getVoitureId() {
        return voitureId;
    }

    public String getCouponId() {
        return couponId;
    }

    public String getOrdreId() {
        return ordreId;
    }

    public String getTrainId() {
        return trainId;
    }
}
