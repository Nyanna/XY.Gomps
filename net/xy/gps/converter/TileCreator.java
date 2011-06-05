package net.xy.gps.converter;

import java.io.IOException;
import java.sql.SQLException;

import net.xy.gps.data.HSQLDriver;
import net.xy.gps.data.TileDriver;

/**
 * converts the db to mobile usable tile sets
 * 
 * @author Xyan
 * 
 */
public class TileCreator {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final TileDriver p = new TileDriver();
        try {
            System.out.println("Starting...");
            p.writeTiles(new HSQLDriver());
            System.out.println("Done.");
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
