package net.xy.gps.render.draw;

/**
 * draw text mostly used for gui creation
 * 
 * @author Xyan
 * 
 */
public class DrawText extends DrawPoint {
    /**
     * stores the text to render
     */
    public final String text;

    /**
     * default
     * 
     * @param lat
     * @param lon
     * @param color
     */
    public DrawText(final double lat, final double lon, final String text, final int[] color) {
        super(lat, lon, color);
        this.text = text;
    }

    @Override
    public int getType() {
        return IDrawAction.ACTION_TEXT;
    }
}