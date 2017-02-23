/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file NearestPoint.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

/**
 * Created by danil on 25/10/2016.
 */
public class NearestPoint extends MiddlePoint implements CoordinateInterface {
    private double km;
    private double distance;
    private Long branch;
    private Line segment;

    public NearestPoint(double latitude, double longitude) {
        super(latitude, longitude);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public Long getBranch() {
        return branch;
    }

    public void setBranch(Long branch) {
        this.branch = branch;
    }

    public Line getSegment() {
        return segment;
    }

    public void setSegment(Line segment) {
        this.segment = segment;
    }

    @Override
    public String toString() {
        return "Coordinate [latitude=" + getLatitude() + ", longitude="
                + getLongitude() + ", branch=" + getBranch() + ", distance=" + getDistance() + ", km=" + getKm() + ", segment=" + getSegment() + "]";
    }

    public long getKmInMeters() {
        return (long) getKm() * 1000000;
    }
}
