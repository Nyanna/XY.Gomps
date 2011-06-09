package net.xy.gps.data.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.xy.codebasel.ObjectArray;
import net.xy.gps.data.tag.Tag.SimpleCondition;

/**
 * an factory for conversion and relative typehandling of tags
 * 
 * @author Xyan
 * 
 */
public class TagFactory {
    /**
     * map of key+val => Tag or List<Tag>
     */
    private static final Map simpleMatch = new HashMap();

    /**
     * creates an simple key value based matcher for an tag
     * 
     * @param key
     * @param value
     * @return
     */
    public static Tag simple(final int idx, final String key, final String value, final int nodeType) {
        final Tag res = new Tag(idx, new SimpleCondition(key, value));
        final String hash = key + value + nodeType;

        // first simple calculation strategy
        List check = (List) simpleMatch.get(hash);
        if (check == null) {
            check = new ArrayList();
            simpleMatch.put(hash, check);
        }
        check.add(res);
        return res;
    }

    /**
     * find all possible tags to an DataObject node,way,or area
     * 
     * @param nodeType
     * @param tags
     * @return
     */
    public static Object[] getTags(final int nodeType, final Map tags) {
        final ObjectArray res = new ObjectArray();
        // first simple calculation strategy
        for (final Iterator iterator = tags.entrySet().iterator(); iterator.hasNext();) {
            final Entry entry = (Entry) iterator.next();
            final String hash = entry.getKey().toString() + entry.getValue().toString() + nodeType;
            final List tagsList = (List) simpleMatch.get(hash);
            if (tagsList != null) {
                for (final Iterator iterator2 = tagsList.iterator(); iterator2.hasNext();) {
                    final Tag tag = (Tag) iterator2.next();
                    res.add(tag);
                }
            }
        }
        return res.get();
    }
}
