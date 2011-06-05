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
