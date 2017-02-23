/*
 * Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 * All Rights Reserved
 *
 * This file is part of the android project.
 *
 * @project android
 * @file Coordinate.java
 * @author Danilo Andrade <danilo@webbingbrasil.com.br>
 * @date  21/02/17 17:24 Modified 28/11/16 16:24
 * @copyright  Copyright (c) 2017 Webbing Brasil (http://www.webbingbrasil.com.br)
 */

package br.com.nomadlog.Location;

import br.brastan.Location.Distance.DistanceInterface;
import br.brastan.Location.Exception.NotConvergingException;
import br.brastan.Utils.GeoUtils;

import java.io.Serializable;

public class Coordinate implements Serializable, Cloneable, CoordinateInterface {

	private static final long serialVersionUID = 201009101110L;
	private double latitude;
	private double longitude;
    private Ellipsoid ellipsoid;

	public Coordinate(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;

        setEllipsoid(Ellipsoid.createDefault());
    }

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "Coordinate [latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double distanceInKm(CoordinateInterface coordinate) {
		return GeoUtils.geoDistanceInKm(this, coordinate);
	}

    @Override
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    public void setEllipsoid(Ellipsoid ellipsoid) {
        this.ellipsoid = ellipsoid;
    }

	public boolean HasValue() {
        return ((this.longitude != 0.0d) && (this.latitude != 0.0d));
    }

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Coordinate(this.getLatitude(), this.getLatitude());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
        return Double.doubleToLongBits(longitude) == Double
                .doubleToLongBits(other.longitude);
    }

    public double distanceTo(Coordinate point, DistanceInterface distanceCalc) {
        double distance = -1;
        try {
            distance = distanceCalc.getDistance(this, point);
        } catch (NotConvergingException e) {
            e.printStackTrace();
        }

        return distance;
    }
}
