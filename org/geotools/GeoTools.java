/*
 * GeoTools - The Open Source Java GIS Toolkit
 * http://geotools.org
 * (C) 2001-2008, Open Source Geospatial Foundation (OSGeo)
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * This class contains formulas from the public FTP area of NOAA.
 * NOAAS's work is fully acknowledged here.
 */
package org.geotools;

/**
 * ported funtions from the GeoTools library
 * 
 * @author xyan
 * 
 */
public class GeoTools {

    /**
     * Returns the orthodromic distance between two geographic coordinates.
     * The orthodromic distance is the shortest distance between two points
     * on our Earth sphere's surface. The orthodromic path is always on a great circle.
     * This is different from the <cite>loxodromic distance</cite>, which is a
     * longer distance on a path with a constant direction on the compass.
     * 
     * @param lon1
     *            Longitude of first point (in decimal degrees).
     * @param lat1
     *            Latitude of first point (in decimal degrees).
     * @param lon2
     *            Longitude of second point (in decimal degrees).
     * @param lat2
     *            Latitude of second point (in decimal degrees).
     * @return The orthodromic distance (in the units of this ellipsoid's axis).
     */
    public static Double orthodromicDistance(double lon1, double lat1, double lon2, double lat2) {
        lon1 = Math.toRadians(lon1);
        lat1 = Math.toRadians(lat1);
        lon2 = Math.toRadians(lon2);
        lat2 = Math.toRadians(lat2);
        final double semiMayorAxis = 6378137.0;
        final double inverseFlattening = 298.257223563;
        /*
         * Solution of the geodetic inverse problem after T.Vincenty.
         * Modified Rainsford's method with Helmert's elliptical terms.
         * Effective in any azimuth and at any distance short of antipodal.
         * Latitudes and longitudes in radians positive North and East.
         * Forward azimuths at both points returned in radians from North.
         * Programmed for CDC-6600 by LCDR L.Pfeifer NGS ROCKVILLE MD 18FEB75
         * Modified for IBM SYSTEM 360 by John G.Gergen NGS ROCKVILLE MD 7507
         * Ported from Fortran to Java by Martin Desruisseaux.
         * Source: ftp://ftp.ngs.noaa.gov/pub/pcsoft/for_inv.3d/source/inverse.for
         * subroutine INVER1
         */
        final int MAX_ITERATIONS = 100;
        final double EPS = 0.5E-13;
        final double F = 1 / inverseFlattening;
        final double R = 1 - F;

        double tu1 = R * Math.sin(lat1) / Math.cos(lat1);
        double tu2 = R * Math.sin(lat2) / Math.cos(lat2);
        final double cu1 = 1 / Math.sqrt(tu1 * tu1 + 1);
        final double cu2 = 1 / Math.sqrt(tu2 * tu2 + 1);
        final double su1 = cu1 * tu1;
        double s = cu1 * cu2;
        final double baz = s * tu2;
        final double faz = baz * tu1;
        double x = lon2 - lon1;
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            final double sx = Math.sin(x);
            final double cx = Math.cos(x);
            tu1 = cu2 * sx;
            tu2 = baz - su1 * cu2 * cx;
            final double sy = Math.hypot(tu1, tu2);
            final double cy = s * cx + faz;
            final double y = Math.atan2(sy, cy);
            final double SA = s * sx / sy;
            final double c2a = 1 - SA * SA;
            double cz = faz + faz;
            if (c2a > 0) {
                cz = -cz / c2a + cy;
            }
            final double e = cz * cz * 2 - 1;
            double c = ((-3 * c2a + 4) * F + 4) * c2a * F / 16;
            double d = x;
            x = ((e * cy * c + cz) * sy * c + y) * SA;
            x = (1 - c) * x * F + lon2 - lon1;

            if (Math.abs(d - x) <= EPS) {
                x = Math.sqrt((1 / (R * R) - 1) * c2a + 1) + 1;
                x = (x - 2) / x;
                c = 1 - x;
                c = (x * x / 4 + 1) / c;
                d = (0.375 * x * x - 1) * x;
                x = e * cy;
                s = 1 - 2 * e;
                s = ((((sy * sy * 4 - 3) * s * cz * d / 6 - x) * d / 4 + cz) * sy * d + y) * c * R * semiMayorAxis;
                return Double.valueOf(s);
            }
        }
        // No convergence. It may be because coordinate points
        // are equals or because they are at antipodes.
        final double LEPS = 1E-10;
        if (Math.abs(lon1 - lon2) <= LEPS && Math.abs(lat1 - lat2) <= LEPS) {
            return Double.valueOf(0); // Coordinate points are equals
        }
        if (Math.abs(lat1) <= LEPS && Math.abs(lat2) <= LEPS) {
            return Double.valueOf(Math.abs(lon1 - lon2) * semiMayorAxis); // Points are on the equator.
        }
        // Other cases: no solution for this algorithm.
        return null;
    }
}
