/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file Vicenty.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location.Distance;

import br.brastan.Location.CoordinateInterface;
import br.brastan.Location.Exception.NotConvergingException;

/**
 * Created by danil on 26/10/2016.
 */
public class Vicenty implements DistanceInterface {

    @Override
    public double getDistance(CoordinateInterface pointA, CoordinateInterface pointB) throws NotConvergingException {

        // convert lat/lng to radians
        double lat1 = Math.toRadians(pointA.getLatitude());
        double lat2 = Math.toRadians(pointB.getLatitude());
        double lng1 = Math.toRadians(pointA.getLongitude());
        double lng2 = Math.toRadians(pointB.getLongitude());

        float a = pointA.getEllipsoid().getA();
        float b = pointA.getEllipsoid().getB();
        float f = 1 / pointA.getEllipsoid().getF();

        double L = lng2 - lng1;
        double U1 = Math.atan((1 - f) * Math.tan(lat1));
        double U2 = Math.atan((1 - f) * Math.tan(lat2));

        int iterationLimit = 100;
        double lambda = L;
        double lambdaP;
        double cosSqAlpha;
        double cos2SigmaM;
        double cosSigma;
        double sinSigma;
        double sigma;

        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);

        do {
            double sinLambda = Math.sin(lambda);
            double cosLambda = Math.cos(lambda);

            sinSigma = Math.sqrt(
                    (cosU2 * sinLambda) * (cosU2 * sinLambda) +
                            (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );

            if (sinSigma == 0) {
                return 0.0;
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;

            sigma = Math.atan2(sinSigma, cosSigma);

            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;

            cosSqAlpha = 1 - sinAlpha * sinAlpha;

            cos2SigmaM = 0;

            if (cosSqAlpha != 0) {
                cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            }

            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));

            lambdaP = lambda;

            lambda = L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterationLimit > 0);

        if (iterationLimit == 0) {
            throw new NotConvergingException("distance calculation failed");
        }

        double uSq = cosSqAlpha * (a * b - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double s = b * A * (sigma - deltaSigma);

        return s;
    }
}
