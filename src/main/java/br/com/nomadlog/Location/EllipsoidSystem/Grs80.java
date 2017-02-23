/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file Grs80.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location.EllipsoidSystem;

/**
 * Created by danil on 26/10/2016.
 */
public class Grs80 implements EllipsoidInterface {

    private String name = "Geodetic Reference System 1980";

    private float maj = (float) 6378137.0;

    private float min = (float) 6356752.314140;

    private float f = (float) 298.257222100;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getMaj() {
        return maj;
    }

    @Override
    public float getMin() {
        return min;
    }

    @Override
    public float getF() {
        return f;
    }
}