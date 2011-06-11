package net.xy.gps.data.tag.impl;

/**
 * style objects for tags the render engine should enforce as much possible
 * styles
 * 
 * @author Xyan
 * 
 */
public class Style {
    /**
     * if an area should it be filled
     */
    public Boolean fill = null;
    /**
     * line color
     */
    public Integer[] color = new Integer[4];
    /**
     * if line width of the line in meters
     */
    public Integer width = null;
    /**
     * if borders should be rendered
     */
    public Boolean borders = null;
    /**
     * if borders enabled their color
     */
    public Integer[] borderColor = new Integer[4];
    /**
     * if this an node render with an image instead
     */
    public String image = null;
}