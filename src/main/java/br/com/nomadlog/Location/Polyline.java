/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file Polyline.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by danil on 25/10/2016.
 */
public class Polyline extends ArrayList<Coordinate> {

    public ArrayList<Line> getSegments() {
        ArrayList<Line> segments = new ArrayList<>();
        ListIterator<Coordinate> interator = listIterator();

        Coordinate previousPoint = interator.next();

        while (interator.hasNext()) {
            Coordinate point = interator.next();
            segments.add(new Line(previousPoint, point));
            previousPoint = point;
        }

        return segments;
    }
}
