/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file CoordinateInterface.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

import br.com.nomadlog.Location.Distance.DistanceInterface;

/**
 * Created by danil on 25/10/2016.
 */
public interface CoordinateInterface {

    double getLatitude();

    double getLongitude();

    double distanceInKm(CoordinateInterface coordinate);

    Ellipsoid getEllipsoid();

    double distanceTo(Coordinate point, DistanceInterface distanceCalc);
}
