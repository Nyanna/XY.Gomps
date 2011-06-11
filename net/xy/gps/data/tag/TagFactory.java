package net.xy.gps.data.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.xy.gps.data.tag.TagConfiguration.ITagListener;
import net.xy.gps.data.tag.impl.OrCondition;
import net.xy.gps.data.tag.impl.TagMatchCondition;

/**
 * an factory for conversion and relative typehandling of tags
 * 
 * @author Xyan
 * 
 */
public class TagFactory implements ITagListener {
    /**
     * map of key+val+type => Tag or List<Tag>
     */
    private static final Map simpleMatch = new HashMap();
    /**
     * all registered tags
     */
    private static final Map tagList = new HashMap();

    /**
     * find all possible tags to an DataObject node,way,or area
     * 
     * @param objType
     * @param tags
     * @return
     */
    public static Integer[] getTags(final int objType, final Map tags) {
        final List res = new ArrayList();
        // first simple calculation strategy
        for (final Iterator iterator = tags.entrySet().iterator(); iterator.hasNext();) {
            final Entry entry = (Entry) iterator.next();
            final String hash = entry.getKey().toString() + entry.getValue().toString() + objType;
            final List tagsList = (List) simpleMatch.get(hash);
            if (tagsList != null) {
                for (final Iterator iterator2 = tagsList.iterator(); iterator2.hasNext();) {
                    res.add(Integer.valueOf(((Tag) iterator2.next()).id));
                }
            }
        }
        return (Integer[]) res.toArray(new Integer[res.size()]);
    }

    /**
     * get an specific tag by its index number
     * 
     * @param i
     * @return
     */
    public static Tag getTag(final Integer i) {
        return (Tag) tagList.get(i);
    }

    /**
     * registers an tag to the factory
     * 
     * @param tag
     */
    public void registerTag(final Tag tag) {
        // put in index
        tagList.put(Integer.valueOf(tag.id), tag);
        for (final Iterator i = tag.conditions.iterator(); i.hasNext();) {
            final Object condition = i.next();
            if (condition instanceof TagMatchCondition) {
                final TagMatchCondition match = (TagMatchCondition) condition;
                insertSimple(tag, match.key, match.value, match.type);
            } else if (condition instanceof OrCondition) {
                final OrCondition or = (OrCondition) condition;
                for (final Iterator j = or.conditions.iterator(); j.hasNext();) {
                    final Object condition2 = j.next();
                    if (condition2 instanceof TagMatchCondition) {
                        final TagMatchCondition match = (TagMatchCondition) condition2;
                        insertSimple(tag, match.key, match.value, match.type);
                    }
                }
            }
        }
    }

    /**
     * internally inserts an tag in various formats
     * 
     * @param tag
     * @param key
     * @param value
     * @param nodeType
     */
    private void insertSimple(final Tag tag, final String key, final String value,
            final Integer nodeType) {
        // first simple calculation strategy
        final String hash = key + value + nodeType.toString();
        List check = (List) simpleMatch.get(hash);
        if (check == null) {
            check = new ArrayList();
            simpleMatch.put(hash, check);
        }
        check.add(tag);
    }

    /**
     * implements the ITagListener and delegates
     */
    public void accept(final net.xy.gps.data.tag.Tag tag) {
        registerTag(tag);
    }
}