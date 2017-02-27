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

import br.com.nomadlog.Location.Coordinate;
import br.com.nomadlog.Location.CoordinateInterface;
import br.com.nomadlog.Location.Exception.NotConvergingException;

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
        double fi1 = Math.toRadians(point.getLatitude());
        double lambda1 = Math.toRadians(point.getLongitude());
        double alpha1 = Math.toRadians(bearing);

        double a = point.getEllipsoid().getA();
        double b = point.getEllipsoid().getB();
        double f = 1 / point.getEllipsoid().getF();

        double sinalpha1 = Math.sin(alpha1);
        double cosalpha1 = Math.cos(alpha1);

        double tanU1 = (1 - f) * Math.tan(fi1);
        double cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;
        double sigma1 = Math.atan2(tanU1, cosalpha1);
        double sinalpha = cosU1 * sinalpha1;
        double cosSqalpha = 1 - sinalpha * sinalpha;
        double uSq = cosSqalpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

        double sigma = distance / (b * A);
        double iterations = 0;
        double cos2sigmam;
        double sigmas;
        double sinsigma;
        double cossigma;

        do {
            cos2sigmam = Math.cos(2 * sigma1 + sigma);
            sinsigma = Math.sin(sigma);
            cossigma = Math.cos(sigma);
            double deltaSigma = B * sinsigma * (cos2sigmam + B / 4 * (cossigma * (-1 + 2 * cos2sigmam * cos2sigmam) - B / 6 * cos2sigmam * (-3 + 4 * sinsigma * sinsigma) * (-3 + 4 * cos2sigmam * cos2sigmam)));
            sigmas = sigma;
            sigma = distance / (b * A) + deltaSigma;
        } while (Math.abs(sigma - sigmas) > 1e-12 && ++iterations < 200);

        if (iterations >= 200) {
            throw new NotConvergingException("Inverse Vincenty Formula did not converge");
        }

        double tmp = sinU1 * sinsigma - cosU1 * cossigma * cosalpha1;
        double fi2 = Math.atan2(sinU1 * cossigma + cosU1 * sinsigma * cosalpha1, (1 - f) * Math.sqrt(sinalpha * sinalpha + tmp * tmp));
        double lambda = Math.atan2(sinsigma * sinalpha1, cosU1 * cossigma - sinU1 * sinsigma * cosalpha1);
        double C = f / 16 * cosSqalpha * (4 + f * (4 - 3 * cosSqalpha));
        double L = lambda - (1 - C) * f * sinalpha * (sigma + C * sinsigma * (cos2sigmam + C * cossigma * (-1 + 2 * cos2sigmam * cos2sigmam)));
        double lambda2 = Math.IEEEremainder(lambda1 + L + 3 * Math.PI, 2 * Math.PI) - Math.PI;

        double alpha2 = Math.atan2(sinalpha, -tmp);
        alpha2 = Math.IEEEremainder(alpha2 + 2 * Math.PI, 2 * Math.PI);

        CoordinateInterface destinationPoint = new Coordinate(Math.toDegrees(fi2), Math.toDegrees(lambda2));

        return new DestinationPoint(destinationPoint, Math.toDegrees(alpha2));
    }

    private LineBearing inverseVincenty(CoordinateInterface point1, CoordinateInterface point2) throws NotConvergingException {
        double fi1 = Math.toRadians(point1.getLatitude());
        double fi2 = Math.toRadians(point2.getLatitude());
        double lambda1 = Math.toRadians(point1.getLongitude());
        double lambda2 = Math.toRadians(point2.getLongitude());

        double a = point1.getEllipsoid().getA();
        double b = point1.getEllipsoid().getB();
        double f = 1 / point1.getEllipsoid().getF();

        double L = lambda2 - lambda1;

        double tanU1 = (1 - f) * Math.tan(fi1);
        double cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;
        double tanU2 = (1 - f) * Math.tan(fi2);
        double cosU2 = 1 / Math.sqrt(1 + tanU2 * tanU2);
        double sinU2 = tanU2 * cosU2;

        double lambda = L;

        double iterations = 0;
        double cos2sigmaM = 0;
        double sinlambda;
        double coslambda;
        double sinsigma;
        double cossigma;
        double sigma;
        double cosSqalpha;
        double lambdap;

        do {
            sinlambda = Math.sin(lambda);
            coslambda = Math.cos(lambda);
            double sinSqsigma = (cosU2 * sinlambda) * (cosU2 * sinlambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * coslambda) * (cosU1 * sinU2 - sinU1 * cosU2 * coslambda);
            sinsigma = Math.sqrt(sinSqsigma);

            if (sinsigma == 0) {
                return new LineBearing(0, 0, 0);
            }

            cossigma = sinU1 * sinU2 + cosU1 * cosU2 * coslambda;
            sigma = Math.atan2(sinsigma, cossigma);
            double sinalpha = cosU1 * cosU2 * sinlambda / sinsigma;
            cosSqalpha = 1 - sinalpha * sinalpha;

            if (cosSqalpha != 0.0) {
                cos2sigmaM = cossigma - 2 * sinU1 * sinU2 / cosSqalpha;
            }

            double C = f / 16 * cosSqalpha * (4 + f * (4 - 3 * cosSqalpha));
            lambdap = lambda;
            lambda = L + (1 - C) * f * sinalpha * (sigma + C * sinsigma * (cos2sigmaM + C * cossigma * (-1 + 2 * cos2sigmaM * cos2sigmaM)));
        } while (Math.abs(lambda - lambdap) > 1e-12 && ++iterations < 200);

        if (iterations >= 200) {
            throw new NotConvergingException("Inverse Vincenty Formula did not converge");
        }

        double uSq = cosSqalpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B * sinsigma * (cos2sigmaM + B / 4 * (cossigma * (-1 + 2 * cos2sigmaM * cos2sigmaM) - B / 6 * cos2sigmaM * (-3 + 4 * sinsigma * sinsigma) * (-3 + 4 * cos2sigmaM * cos2sigmaM)));

        double s = b * A * (sigma - deltaSigma);

        double alpha1 = Math.atan2(cosU2 * sinlambda, cosU1 * sinU2 - sinU1 * cosU2 * coslambda);
        double alpha2 = Math.atan2(cosU1 * sinlambda, -sinU1 * cosU2 + cosU1 * sinU2 * coslambda);

        alpha1 = Math.IEEEremainder(alpha1 + 2 * Math.PI, 2 * Math.PI);
        alpha2 = Math.IEEEremainder(alpha2 + 2 * Math.PI, 2 * Math.PI);

        return new LineBearing(s, Math.toDegrees(alpha1), Math.toDegrees(alpha2));
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
