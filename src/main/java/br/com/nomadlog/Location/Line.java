/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file Line.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

import br.brastan.Location.Bearing.BearingInterface;
import br.brastan.Location.Distance.DistanceInterface;

/**
 * Created by danil on 25/10/2016.
 */
public class Line {

    private final CoordinateInterface point1;
    private final CoordinateInterface point2;
    private double precision = 5;

    public Line(CoordinateInterface point1, CoordinateInterface point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public CoordinateInterface getNearestPoint(CoordinateInterface landMarkPoint) {
        CoordinateInterface middle = nearestPointMiddle(getPoint1(), getPoint2(), landMarkPoint, 1);

        return new NearestPoint(middle.getLatitude(), middle.getLongitude());
    }

    private CoordinateInterface nearestPointMiddle(CoordinateInterface point1, CoordinateInterface point2, CoordinateInterface landMarkPoint, int interactions) {
        MiddlePoint middle = getMiddlePoint(point1, point2);
        double distanceA = onSegmentDistance(point1, middle, landMarkPoint);
        double distanceB = onSegmentDistance(point2, middle, landMarkPoint);

        middle.setSegmentPrecision(interactions);

        if (distanceB < distanceA) {
            point1 = point2;
        }

        double distance = middle.distanceInKm(point2) * 1000;

        return distance > 1 ? nearestPointMiddle(point1, middle, landMarkPoint, (interactions + 1)) : middle;
    }

    public boolean onSegment(CoordinateInterface point1, CoordinateInterface point2, CoordinateInterface landMarkPoint) {
        double distance = onSegmentDistance(point1, point2, landMarkPoint);
        return distance < precision;
    }

    public double onSegmentDistance(CoordinateInterface point1, CoordinateInterface point2, CoordinateInterface landMarkPoint) {
        double distance = Math.abs(point1.distanceInKm(point2) - point1.distanceInKm(landMarkPoint) - point2.distanceInKm(landMarkPoint));

        return distance * 1000;
    }

    public MiddlePoint getMiddlePoint(CoordinateInterface point1, CoordinateInterface point2) {
        double latA = Math.toRadians(point1.getLatitude());
        double lngA = Math.toRadians(point1.getLongitude());
        double latB = Math.toRadians(point2.getLatitude());
        double lngB = Math.toRadians(point2.getLongitude());

        double bx = Math.cos(latB) * Math.cos(lngB - lngA);
        double by = Math.cos(latB) * Math.sin(lngB - lngA);

        double latC = Math.toDegrees(Math.atan2(Math.sin(latA) + Math.sin(latB), Math.sqrt((Math.cos(latA) + bx) * (Math.cos(latA) + bx) + by * by)));
        double lngC = Math.toDegrees(lngA + Math.atan2(by, Math.cos(latA) + bx));

        return new MiddlePoint(latC, lngC);
    }

    public CoordinateInterface getPoint1() {
        return point1;
    }

    public CoordinateInterface getPoint2() {
        return point2;
    }

    public double distanceTo(Coordinate point, DistanceInterface distanceCalc) {
        double distance = -1;
        try {
            distance = getNearestPoint(point).distanceTo(point, distanceCalc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return distance;
    }

    public double getBearing(BearingInterface bearingCalc) {
        return bearingCalc.calculateBearing(point1, point2);
    }
}
