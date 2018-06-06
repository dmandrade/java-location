/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file BearingInterface.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location.Bearing;

import br.com.nomadlog.Location.CoordinateInterface;

/**
 * Created by danil on 27/10/2016.
 */
public interface BearingInterface {

    double calculateBearing(CoordinateInterface point1, CoordinateInterface point2);

    double calculateFinalBearing(CoordinateInterface point1, CoordinateInterface point2);

    CoordinateInterface calculateDestination(CoordinateInterface point, double bearing, double distance);

}
