package net.xy.gps.render.draw;

/**
 * main protocol object interface
 * 
 * @author Xyan
 * 
 */
public interface IDrawAction {

    /**
     * actions type constants used for casting
     */
    public static final int ACTION_POINT = 0;
    public static final int ACTION_WAY = 1;
    public static final int ACTION_AREA = 2;
    public static final int ACTION_TEXT = 3;

    /**
     * returns type constant of this action
     * 
     * @return
     */
    public int getType();
}
