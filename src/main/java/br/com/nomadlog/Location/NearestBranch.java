/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file NearestBranch.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

import br.com.nomadlog.Location.Distance.DistanceInterface;
import br.com.nomadlog.Location.Distance.Vicenty;
import br.com.nomadlog.Utils.Utils;
//import br.brastan.model.GeographicMarks_model;
//import br.brastan.model.dao.GeographicMarksDAO;

import java.util.*;

/**
 * make this class works independetly like a module
 */
public class NearestBranch {

    //GeographicMarksDAO geoDAO = new GeographicMarksDAO();
    protected Coordinate landMarkPoint;
    protected DistanceInterface distanceCalc = new Vicenty();
    protected float precision = 15;
    protected HashMap<Long, NearestPoint> nearest = new HashMap<>();

    public NearestBranch() {
        setDefaultPrecision();
    }

    protected void setDefaultPrecision() {
        float configuredPrecision = 15;
        if (Utils.GPS_PRECISION != null) {
            configuredPrecision = Utils.GPS_PRECISION;
        }

        precision = configuredPrecision;
    }

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public void setLandMarkPoint(Coordinate landMarkPoint) {
        this.landMarkPoint = landMarkPoint;
    }

    public Collection<NearestPoint> getNearestBranchs(HashMap<Long, Polyline> branchs) {
        setDefaultPrecision();

        nearest.clear();
        for (Map.Entry<Long, Polyline> branch : branchs.entrySet()) {
            foundClosestSegments(branch.getKey(), branch.getValue());
        }


        return nearest.values();
    }

    /*
    TODO: work on this
    public List<Long> getNearBranchs() {
        return new ArrayList<>(getBranchs().keySet());
    }

    public HashMap<Long, Polyline> getBranchs() {
        //List<GeographicMarks_model> marks = getMarksData();
        return createBranchMarkers(marks);
    }

    public List<GeographicMarks_model> getMarksData() {
        return geoDAO.getGeoMarksByLocation(landMarkPoint);
    }*/

    /**
     * Group kilometric markers by branch
     * todo: work on this
     *
     */
    /*private HashMap<Long, Polyline> createBranchMarkers(List<GeographicMarks_model> marks) {
        ListIterator<GeographicMarks_model> markInterator = marks.listIterator();
        HashMap<Long, Polyline> branchs = new HashMap<>();

        while (markInterator.hasNext()) {
            GeographicMarks_model mark = markInterator.next();
            if (!branchs.containsKey(mark.getFk_branch())) {
                branchs.put(mark.getFk_branch(), new Polyline());
            }

            MarkCoordinate coord = new MarkCoordinate(mark.getNu_lat(), mark.getNu_lon());

            coord.setKm(mark.getKm());

            branchs.get(mark.getFk_branch()).add(coord);
        }

        return branchs;
    }*/

    private void foundClosestSegments(Long id, Polyline branch) {
        NearestPoint nearestPoint = foundNearestPoint(id, branch);
        if (nearestPoint == null) {
            setPrecision(30);
            nearestPoint = foundNearestPoint(id, branch);
        }

        if (!nearest.containsKey(id) && nearestPoint != null) {
            nearest.put(id, nearestPoint);
        }
        if (nearest.containsKey(id) && nearestPoint != null) {
            //TODO: this require api level 24 in android
            //nearest.replace(id, nearestPoint);
        }
    }

    public NearestPoint foundNearestPoint(Long id, Polyline branch) {
        NearestPoint nearestPoint = null;

        for (Line segment : branch.getSegments()) {
            NearestPoint segmentPoint = (NearestPoint) segment.getNearestPoint(landMarkPoint);
            double distance = segmentPoint.distanceTo(landMarkPoint, distanceCalc);
            if (distance <= getPrecision() || (nearestPoint == null || nearestPoint.getDistance() > distance)) {
                nearestPoint = segmentPoint;
                nearestPoint.setSegment(segment);
                nearestPoint.setBranch(id);
                nearestPoint.setDistance(distance);
                nearestPoint.setKm(calcPos(segment, landMarkPoint));
                return nearestPoint;
            }
        }

        return nearestPoint;
    }

    /**
     * Calc truck km in branch segment
     *
     * @param segment
     * @param landMarkPoint
     * @return double
     */
    private double calcPos(Line segment, Coordinate landMarkPoint) {
        MarkCoordinate pointA = (MarkCoordinate) segment.getPoint1();
        MarkCoordinate pointB = (MarkCoordinate) segment.getPoint2();
        double ra = landMarkPoint.distanceInKm(pointA);
        double rb = landMarkPoint.distanceInKm(pointB);

        return pointA.getKm() + (ra / (ra * rb)) * (pointB.getKm() - pointA.getKm());
    }

}
