/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file GeoUtils.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  24/02/17 15:16 Modified 24/02/17 15:15
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

public class GeoUtils {

    public static int EARTH_RADIUS_KM = 6371;

    public static double geoDistanceInKm(double firstLatitude,
                                         double firstLongitude, double secondLatitude, double secondLongitude) {

        // Conversão de graus pra radianos das latitudes
        double firstLatToRad = Math.toRadians(firstLatitude);
        double secondLatToRad = Math.toRadians(secondLatitude);

        // Diferença das longitudes
        double deltaLongitudeInRad = Math.toRadians(secondLongitude
                - firstLongitude);

        // Cálcula da distância entre os pontos
        return Math.acos(Math.cos(firstLatToRad) * Math.cos(secondLatToRad)
                * Math.cos(deltaLongitudeInRad) + Math.sin(firstLatToRad)
                * Math.sin(secondLatToRad))
                * EARTH_RADIUS_KM;
    }

    public static double geoDistanceInKm(CoordinateInterface first,
                                         CoordinateInterface second) {
        return geoDistanceInKm(first.getLatitude(), first.getLongitude(),
                second.getLatitude(), second.getLongitude());
    }

    public static String bearing(Coordinate first, Coordinate second) {

        return bearing(first.getLatitude(), first.getLongitude(), second.getLatitude(), second.getLongitude());

    }

    public static String bearing(double lat1, double lon1, double lat2,
                                 double lon2) {
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(longitude2 - longitude1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2)
                - Math.sin(latitude1) * Math.cos(latitude2)
                * Math.cos(longDiff);
        double resultDegree = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;

        String coordNames[] = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE",
                "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};

        double directionid = Math.round(resultDegree / 22.5);

        // no of array contain 360/16=22.5
        if (directionid < 0) {
            directionid = directionid + 16;
            // no. of contains in array
        }
        String compasLoc = coordNames[(int) directionid];

        return resultDegree + " " + compasLoc;
    }

}
