package net.xy.gps.render;

import net.xy.codebase.ThreadLocal;

/**
 * inter thread communicator
 * 
 * @author Xyan
 * 
 */
public class ThreadController {
    /**
     * var for aborting painting thread if time is over
     */
    public static final ThreadLocal ABBORT = new ThreadLocal();
}
