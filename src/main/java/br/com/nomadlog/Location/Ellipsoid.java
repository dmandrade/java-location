/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file Ellipsoid.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

import br.brastan.Location.EllipsoidSystem.EllipsoidInterface;
import br.brastan.Location.EllipsoidSystem.Wgs84;

/**
 * Created by danil on 26/10/2016.
 */
public class Ellipsoid {

    private EllipsoidInterface ellipsoidSystem;

    public Ellipsoid(EllipsoidInterface ellipsoid) {
        ellipsoidSystem = ellipsoid;
    }

    public static Ellipsoid createDefault() {
        return new Ellipsoid(new Wgs84());
    }

    /**
     * get name of the ellipsoid used
     *
     * @return string
     */
    public String getName() {
        return ellipsoidSystem.getName();
    }

    /**
     * get major axis
     *
     * @return float
     */
    public float getMaj() {
        return ellipsoidSystem.getMaj();
    }

    /**
     * get minor axis
     *
     * @return float
     */
    public float getMin() {
        return ellipsoidSystem.getMin();
    }

    /**
     * get major axis
     *
     * @return float
     */
    public float getA() {
        return ellipsoidSystem.getMaj();
    }

    /**
     * get calculated semi-minor axis
     *
     * @return float
     */
    public float getB() {
        return ellipsoidSystem.getMaj() * (1 - 1 / ellipsoidSystem.getF());
    }

    /**
     * get eccentricity
     *
     * @return float
     */
    public float getEcc() {
        return ((ellipsoidSystem.getMaj() * ellipsoidSystem.getMaj()) - (ellipsoidSystem.getMin() * ellipsoidSystem.getMin())) / (ellipsoidSystem.getMaj() * ellipsoidSystem.getMaj());
    }

    /**
     * Get the inverse flattening
     *
     * @return float
     */
    public float getF() {
        return ellipsoidSystem.getF();
    }

    /**
     * Calculates the arithmetic mean radius
     *
     * @return float
     * @see "http://home.online.no/~sigurdhu/WGS84_Eng.html"
     */
    public float getArithmeticMeanRadius() {
        return ((2 * ellipsoidSystem.getMaj()) + ellipsoidSystem.getMin()) / 3;
    }
}
