/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file BearingEllipsoidal.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location.Bearing;

import br.brastan.Location.Coordinate;
import br.brastan.Location.CoordinateInterface;
import br.brastan.Location.Exception.NotConvergingException;

public class BearingEllipsoidal implements BearingInterface {

    /**
     * This method calculates the initial bearing between the
     * two points.
     *
     * @param point1 coordinate poin 1
     * @param point2 coordinate point 2
     * @return double
     */
    @Override
    public double calculateBearing(CoordinateInterface point1, CoordinateInterface point2) {
        double bearing = 0;
        try {
            bearing = inverseVincenty(point1, point2).getInitialBearing();
        } catch (NotConvergingException e) {
            e.printStackTrace();
        }

        return bearing;
    }

    /**
     * Calculates the final bearing between the two points.
     *
     * @param point1 coordinate poin 1
     * @param point2 coordinate point 2
     * @return double
     */
    @Override
    public double calculateFinalBearing(CoordinateInterface point1, CoordinateInterface point2) {
        double bearing = 0;
        try {
            bearing = inverseVincenty(point1, point2).getFinalBearing();
        } catch (NotConvergingException e) {
            e.printStackTrace();
        }

        return bearing;
    }

    /**
     * Calculates a destination point for the given point, bearing angle,
     * and distance.
     *
     * @param point    coordinate point
     * @param bearing  point bearing
     * @param distance destination point distance
     * @return double
     */
    @Override
    public CoordinateInterface calculateDestination(CoordinateInterface point, double bearing, double distance) {
        CoordinateInterface destination = null;
        try {
            destination = directVincenty(point, bearing, distance).getDestinationPoint();
        } catch (NotConvergingException e) {
            e.printStackTrace();
        }

        return destination;
    }

    /**
     * Calculates the final bearing angle for a destination point.
     * The method expects a starting point point, the bearing angle,
     * and the distance to destination.
     *
     * @param point    coordinate point
     * @param bearing  point bearing
     * @param distance destination point distance
     * @return double
     */
    public double calculateDestinationFinalBearing(CoordinateInterface point, double bearing, double distance) {
        double destinationBearing = 0;
        try {
            destinationBearing = directVincenty(point, bearing, distance).getDestinationBearing();
        } catch (NotConvergingException e) {
            e.printStackTrace();
        }

        return destinationBearing;
    }

    private DestinationPoint directVincenty(CoordinateInterface point, double bearing, double distance) throws NotConvergingException {
        double φ1 = Math.toRadians(point.getLatitude());
        double λ1 = Math.toRadians(point.getLongitude());
        double α1 = Math.toRadians(bearing);

        double a = point.getEllipsoid().getA();
        double b = point.getEllipsoid().getB();
        double f = 1 / point.getEllipsoid().getF();

        double sinα1 = Math.sin(α1);
        double cosα1 = Math.cos(α1);

        double tanU1 = (1 - f) * Math.tan(φ1);
        double cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;
        double σ1 = Math.atan2(tanU1, cosα1);
        double sinα = cosU1 * sinα1;
        double cosSqα = 1 - sinα * sinα;
        double uSq = cosSqα * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

        double σ = distance / (b * A);
        double iterations = 0;
        double cos2σm;
        double σs;
        double sinσ;
        double cosσ;

        do {
            cos2σm = Math.cos(2 * σ1 + σ);
            sinσ = Math.sin(σ);
            cosσ = Math.cos(σ);
            double Δσ = B * sinσ * (cos2σm + B / 4 * (cosσ * (-1 + 2 * cos2σm * cos2σm) - B / 6 * cos2σm * (-3 + 4 * sinσ * sinσ) * (-3 + 4 * cos2σm * cos2σm)));
            σs = σ;
            σ = distance / (b * A) + Δσ;
        } while (Math.abs(σ - σs) > 1e-12 && ++iterations < 200);

        if (iterations >= 200) {
            throw new NotConvergingException("Inverse Vincenty Formula did not converge");
        }

        double tmp = sinU1 * sinσ - cosU1 * cosσ * cosα1;
        double φ2 = Math.atan2(sinU1 * cosσ + cosU1 * sinσ * cosα1, (1 - f) * Math.sqrt(sinα * sinα + tmp * tmp));
        double λ = Math.atan2(sinσ * sinα1, cosU1 * cosσ - sinU1 * sinσ * cosα1);
        double C = f / 16 * cosSqα * (4 + f * (4 - 3 * cosSqα));
        double L = λ - (1 - C) * f * sinα * (σ + C * sinσ * (cos2σm + C * cosσ * (-1 + 2 * cos2σm * cos2σm)));
        double λ2 = Math.IEEEremainder(λ1 + L + 3 * Math.PI, 2 * Math.PI) - Math.PI;

        double α2 = Math.atan2(sinα, -tmp);
        α2 = Math.IEEEremainder(α2 + 2 * Math.PI, 2 * Math.PI);

        CoordinateInterface destinationPoint = new Coordinate(Math.toDegrees(φ2), Math.toDegrees(λ2));

        return new DestinationPoint(destinationPoint, Math.toDegrees(α2));
    }

    private LineBearing inverseVincenty(CoordinateInterface point1, CoordinateInterface point2) throws NotConvergingException {
        double φ1 = Math.toRadians(point1.getLatitude());
        double φ2 = Math.toRadians(point2.getLatitude());
        double λ1 = Math.toRadians(point1.getLongitude());
        double λ2 = Math.toRadians(point2.getLongitude());

        double a = point1.getEllipsoid().getA();
        double b = point1.getEllipsoid().getB();
        double f = 1 / point1.getEllipsoid().getF();

        double L = λ2 - λ1;

        double tanU1 = (1 - f) * Math.tan(φ1);
        double cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;
        double tanU2 = (1 - f) * Math.tan(φ2);
        double cosU2 = 1 / Math.sqrt(1 + tanU2 * tanU2);
        double sinU2 = tanU2 * cosU2;

        double λ = L;

        double iterations = 0;
        double cos2σM = 0;
        double sinλ;
        double cosλ;
        double sinσ;
        double cosσ;
        double σ;
        double cosSqα;
        double λp;

        do {
            sinλ = Math.sin(λ);
            cosλ = Math.cos(λ);
            double sinSqσ = (cosU2 * sinλ) * (cosU2 * sinλ)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosλ) * (cosU1 * sinU2 - sinU1 * cosU2 * cosλ);
            sinσ = Math.sqrt(sinSqσ);

            if (sinσ == 0) {
                return new LineBearing(0, 0, 0);
            }

            cosσ = sinU1 * sinU2 + cosU1 * cosU2 * cosλ;
            σ = Math.atan2(sinσ, cosσ);
            double sinα = cosU1 * cosU2 * sinλ / sinσ;
            cosSqα = 1 - sinα * sinα;

            if (cosSqα != 0.0) {
                cos2σM = cosσ - 2 * sinU1 * sinU2 / cosSqα;
            }

            double C = f / 16 * cosSqα * (4 + f * (4 - 3 * cosSqα));
            λp = λ;
            λ = L + (1 - C) * f * sinα * (σ + C * sinσ * (cos2σM + C * cosσ * (-1 + 2 * cos2σM * cos2σM)));
        } while (Math.abs(λ - λp) > 1e-12 && ++iterations < 200);

        if (iterations >= 200) {
            throw new NotConvergingException("Inverse Vincenty Formula did not converge");
        }

        double uSq = cosSqα * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double Δσ = B * sinσ * (cos2σM + B / 4 * (cosσ * (-1 + 2 * cos2σM * cos2σM) - B / 6 * cos2σM * (-3 + 4 * sinσ * sinσ) * (-3 + 4 * cos2σM * cos2σM)));

        double s = b * A * (σ - Δσ);

        double α1 = Math.atan2(cosU2 * sinλ, cosU1 * sinU2 - sinU1 * cosU2 * cosλ);
        double α2 = Math.atan2(cosU1 * sinλ, -sinU1 * cosU2 + cosU1 * sinU2 * cosλ);

        α1 = Math.IEEEremainder(α1 + 2 * Math.PI, 2 * Math.PI);
        α2 = Math.IEEEremainder(α2 + 2 * Math.PI, 2 * Math.PI);

        return new LineBearing(s, Math.toDegrees(α1), Math.toDegrees(α2));
    }

    private class DestinationPoint {

        private final CoordinateInterface destinationPoint;
        private final double destinationBearing;

        DestinationPoint(CoordinateInterface destinationPoint, double bearing) {
            this.destinationPoint = destinationPoint;
            this.destinationBearing = bearing;
        }

        CoordinateInterface getDestinationPoint() {
            return destinationPoint;
        }

        double getDestinationBearing() {
            return destinationBearing;
        }
    }

    private class LineBearing {
        private final double distance;
        private final double initialBearing;
        private final double finalBearing;

        LineBearing(double distance, double initialBearing, double finalBearing) {
            this.distance = distance;
            this.initialBearing = initialBearing;
            this.finalBearing = finalBearing;
        }

        double getDistance() {
            return distance;
        }

        double getInitialBearing() {
            return initialBearing;
        }

        double getFinalBearing() {
            return finalBearing;
        }
    }
}
