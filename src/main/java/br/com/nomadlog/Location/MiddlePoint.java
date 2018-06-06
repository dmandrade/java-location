/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file MiddlePoint.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

/**
 * Created by danil on 25/10/2016.
 */
public class MiddlePoint extends Coordinate implements CoordinateInterface {
    private int segmentPrecision;

    public MiddlePoint(double latitude, double longitude) {
        super(latitude, longitude);
    }

    public int getSegmentPrecision() {
        return segmentPrecision;
    }

    public void setSegmentPrecision(int precision) {
        this.segmentPrecision = precision;
    }

    @Override
    public String toString() {
        return "Coordinate [latitude=" + getLatitude() + ", longitude="
                + getLongitude() + ", segmentPrecision=" + getSegmentPrecision() + "]";
    }
}
