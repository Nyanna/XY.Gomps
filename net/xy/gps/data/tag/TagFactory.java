package net.xy.gps.data.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.xy.codebasel.ObjectArray;
import net.xy.gps.data.tag.TagConfiguration.ITagListener;

/**
 * an factory for conversion and relative typehandling of tags
 * 
 * @author Xyan
 * 
 */
public class TagFactory implements ITagListener {
    /**
     * map of key+val => Tag or List<Tag>
     */
    private static final Map simpleMatch = new HashMap();
    /**
     * all registered tags
     */
    private static final Map tagList = new HashMap();
    static {
        // load tags
        Tags.Streets.class.toString();
        Tags.Signs.class.toString();
        Tags.TraficAreas.class.toString();
    }

    /**
     * find all possible tags to an DataObject node,way,or area
     * 
     * @param objType
     * @param tags
     * @return
     */
    public static int[] getTags(final int objType, final Map tags) {
        final ObjectArray res = new ObjectArray();
        // first simple calculation strategy
        for (final Iterator iterator = tags.entrySet().iterator(); iterator.hasNext();) {
            final Entry entry = (Entry) iterator.next();
            final String hash = entry.getKey().toString() + entry.getValue().toString() + objType;
            final List tagsList = (List) simpleMatch.get(hash);
            if (tagsList != null) {
                for (final Iterator iterator2 = tagsList.iterator(); iterator2.hasNext();) {
                    res.add(Integer.valueOf(((Tag) iterator2.next()).type));
                }
            }
        }
        return res.get();
    }

    public static Tag getTag(final int i) {
        return (Tag) tagList.get(Integer.valueOf(i));
    }

    public void registerTag(final net.xy.gps.data.tag.impl.Tag tag) {
        // put in index
        tagList.put(Integer.valueOf(tag.id), tag);
        // first simple calculation strategy
        final String hash = key + value + nodeType;
        List check = (List) simpleMatch.get(hash);
        if (check == null) {
            check = new ArrayList();
            simpleMatch.put(hash, check);
        }
        check.add(res);
    }

    public void accept(final net.xy.gps.data.tag.impl.Tag tag) {
        registerTag(tag);
    }
}
