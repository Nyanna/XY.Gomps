package net.xy.gps.converter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.xy.codebasel.serialization.SerialContext;
import net.xy.gps.data.BasicTile;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.WayData;
import net.xy.gps.type.Dimension;
import net.xy.gps.type.Point;
import net.xy.gps.type.Rectangle;

public class SerialTest {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final WayData way = new WayData(new Double[][] { { 1.0, 1.0 }, { 7.0, 8.5 } });
        final BasicTile tile = new BasicTile(new IDataObject[] { way });
        final SerialContext ctx = new SerialContext(new Class[] { WayData.class, Point.class,
                Dimension.class, Rectangle.class, IDataObject.class, BasicTile.class });
        try {
            ctx.serialize(new DataOutputStream(new FileOutputStream(new File("test.way"))), tile);
            final BasicTile in = (BasicTile) ctx.deserialize(new DataInputStream(
                    new FileInputStream(new File("test.way"))));
            System.out.println(in);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
