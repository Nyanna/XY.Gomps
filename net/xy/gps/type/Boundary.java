/**
 * This file is part of XY.Gomps, Copyright 2011 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.Gomps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * XY.Gomps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with XY.Gomps. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.xy.gps.type;

import java.io.Serializable;

/**
 * dynamic boundary checker
 * 
 * @author Xyan
 * 
 */
public class Boundary implements Serializable {
    private static final long serialVersionUID = -8110063760720348193L;

    /**
     * stores min and max values
     */
    public final double[] values;
/**
     * stores ruleset e.g. {">","<"} keep greater, keep smaller
     */
    public final char[] rules;

/**
     * default, ruleset e.g. {">","<"} keep greater, keep smaller
     *
     * @param maxValues
     */
    public Boundary(final char[] valrules) {
        values = new double[valrules.length];
        rules = valrules;
        for (int i = 0; i < valrules.length; i++) {
            if ('>' == valrules[i]) {
                values[i] = Double.MIN_VALUE;
            } else {
                values[i] = Double.MAX_VALUE;
            }
        }
    }

    /**
     * do incrementals checks for the given values
     * 
     * @param vals
     */
    public void check(final double[] vals) {
        for (int i = 0; i < vals.length; i++) {
            if ('>' == rules[i]) {
                if (vals[i] > values[i]) {
                    values[i] = vals[i];
                }
            } else {
                if (vals[i] < values[i]) {
                    values[i] = vals[i];
                }
            }
        }
    }
}
