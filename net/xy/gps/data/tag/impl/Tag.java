package net.xy.gps.data.tag.impl;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    public int id = 0;
    public String name = null;
    public Boolean enabled = null;
    public Integer priority = null;
    public Style style = null;
    public List conditions = new ArrayList();

    public int hashCode() {
        return id;
    }
}