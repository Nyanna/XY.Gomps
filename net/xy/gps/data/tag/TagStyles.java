package net.xy.gps.data.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import net.xy.codebasel.config.Config;
import net.xy.codebasel.config.Config.ConfigKey;
import net.xy.codebasel.config.TextPropertyRetriever;
import net.xy.gps.data.IDataObject;

/**
 * helper class to manage tag styles
 * 
 * @author Xyan
 * 
 */
public class TagStyles {
    private static final ConfigKey[] CONF_TAGS_COLOR = new ConfigKey[TagFactory
            .getRegisteredTagsCount()];
    private static final ConfigKey[] CONF_TAGS_FILL = new ConfigKey[TagFactory
            .getRegisteredTagsCount()];
    static {
        try {
            Config.addRetriever(new TextPropertyRetriever("net/xy/gps/render/styles.properties"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        final String colorBase = "type.style.color.";
        final String fillBase = "type.style.fill.";
        final Integer[] defaultColor = new Integer[] { Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(100) };
        for (int i = 0; i < TagFactory.getRegisteredTagsCount(); i++) {
            final Tag tag = TagFactory.getTag(i);
            CONF_TAGS_COLOR[i] = Config.registerValues(colorBase + tag.getName(), new ArrayList(
                    Arrays.asList(defaultColor)));
            CONF_TAGS_FILL[i] = Config.registerValues(fillBase + tag.getName(), Boolean.FALSE);
        }
    }

    /**
     * checks if the tag should be filled
     * 
     * @param tagType
     * @return
     */
    public static boolean isFill(final int tagType) {
        if (Config.getBoolean(CONF_TAGS_FILL[tagType]).booleanValue()) {
            return true;
        }
        return false;
    }

    /**
     * checks if this object is via an flag marked as to fill
     * 
     * @param dat
     * @return
     */
    public static boolean isFill(final IDataObject dat) {
        if (dat.getTags() == null || dat.getTags().length == 0) {
            return false;
        }
        for (int i = 0; i < dat.getTags().length; i++) {
            final Tag tag = (Tag) dat.getTags()[i];
            final boolean ret = isFill(tag.type);
            if (ret) {
                return true;
            }
        }
        return false;
    }

    /**
     * gets the tag color in an absolute integer
     * 
     * @param tagType
     * @return
     */
    public static int[] getColor(final int tagType) {
        final int[] res = new int[4];
        int c = 0;
        for (final Iterator iterator = Config.getList(CONF_TAGS_COLOR[tagType]).iterator(); iterator
                .hasNext() && c < res.length;) {
            final Integer in = (Integer) iterator.next();
            res[c] = in.intValue();
            c++;
        }
        return res;
    }
}
