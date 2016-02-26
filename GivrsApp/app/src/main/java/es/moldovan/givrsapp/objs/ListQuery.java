package es.moldovan.givrsapp.objs;

import android.graphics.Path;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public class ListQuery extends Operation {

    private double latitude, longitude, distance;

    public ListQuery(){
        super("list");
    }

    public ListQuery(String operation, double latitude, double longitude, double distance) {
        super("list");
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
