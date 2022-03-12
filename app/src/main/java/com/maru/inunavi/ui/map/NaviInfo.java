package com.maru.inunavi.ui.map;

import com.google.android.gms.maps.model.LatLng;

public class NaviInfo {

    private String startPlaceCode="NONE";
    private String endPlaceCode="NONE";
    private LatLng startLocation = null;
    private LatLng endLocation = null;

    public NaviInfo(String startPlaceCode, String endPlaceCode, LatLng startLocation, LatLng endLocation) {
        this.startPlaceCode = startPlaceCode;
        this.endPlaceCode = endPlaceCode;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public String getStartPlaceCode() {
        return startPlaceCode;
    }

    public void setStartPlaceCode(String startPlaceCode) {
        this.startPlaceCode = startPlaceCode;
    }

    public String getEndPlaceCode() {
        return endPlaceCode;
    }

    public void setEndPlaceCode(String endPlaceCode) {
        this.endPlaceCode = endPlaceCode;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }


}
