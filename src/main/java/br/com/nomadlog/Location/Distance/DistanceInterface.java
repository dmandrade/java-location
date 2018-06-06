/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file DistanceInterface.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location.Distance;

import br.com.nomadlog.Location.CoordinateInterface;
import br.com.nomadlog.Location.Exception.NotConvergingException;

/**
 * Created by danil on 26/10/2016.
 */
public interface DistanceInterface {

    double getDistance(CoordinateInterface pointA, CoordinateInterface pointB) throws NotConvergingException;

}
