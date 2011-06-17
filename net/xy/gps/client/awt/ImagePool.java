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
package net.xy.gps.client.awt;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * singleton image pool manager
 * 
 * @author xyan
 * 
 */
public class ImagePool {
    /**
     * stores already loaded images
     */
    private static final Map CACHE = new HashMap();

    /**
     * loads an image from nor into the pool
     * 
     * @param resname
     * @return
     */
    public static BufferedImage get(final String resname) {
        BufferedImage ret = (BufferedImage) CACHE.get(resname);
        if (ret != null) {
            return ret;
        }
        try {
            ret = ImageIO.read(ImagePool.class.getClassLoader().getResourceAsStream(resname));
            if (ret != null) {
                CACHE.put(resname, ret);
            }
            return ret;
        } catch (final IOException e) {
            return null;
        }
    }
}
