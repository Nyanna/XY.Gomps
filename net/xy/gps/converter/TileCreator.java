package net.xy.gps.converter;

import java.io.IOException;
import java.sql.SQLException;

import net.xy.codebasel.Log;
import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
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
     * configuration & messages
     */
    private static ConfigKey CONF_CREATOR_START = Config.registerValues("creator.start", "Starting...");
    private static ConfigKey CONF_CREATOR_END = Config.registerValues("creator.end", "Done");

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final TileDriver p = new TileDriver();
        try {
            Log.trace(CONF_CREATOR_START);
            p.writeTiles(new HSQLDriver());
            Log.trace(CONF_CREATOR_END);
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
