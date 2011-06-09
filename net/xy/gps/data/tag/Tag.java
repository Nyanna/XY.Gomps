package net.xy.gps.data.tag;

import java.util.Map;

/**
 * self checkking autonomic tag
 * 
 * @author Xyan
 * 
 */
public class Tag {
    /**
     * type integer for comparison
     */
    public final int type;
    /**
     * validator for condition checking
     */
    private final ITagCondition validator;

    /**
     * default constructor
     * 
     * @param type
     */
    public Tag(final int type, final ITagCondition validator) {
        this.type = type;
        this.validator = validator;
    }

    /**
     * checks if this tags is specified in the tags
     * 
     * @param tags
     * @return
     */
    public boolean isApplicable(final Map tags) {
        if (validator != null) {
            return validator.check(tags);
        }
        return false;
    }

    /**
     * checks off specific conditions if this tag could be applied
     * 
     * @author Xyan
     * 
     */
    public static interface ITagCondition {
        /**
         * checking method
         * 
         * @param tags
         * @return
         */
        public boolean check(Map tags);
    }

    /**
     * implements an simple pair matching
     * 
     * @author Xyan
     * 
     */
    public static class SimpleCondition implements ITagCondition {
        /**
         * key to look for
         */
        private final String key;
        /**
         * value to compare with
         */
        private final String value;

        /**
         * default
         * 
         * @param key
         * @param value
         */
        public SimpleCondition(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * checks if key is present and contains value
         */
        public boolean check(final Map tags) {
            final String val = (String) tags.get(key);
            if (value.equalsIgnoreCase(val)) {
                return true;
            }
            return false;
        }
    }
}
