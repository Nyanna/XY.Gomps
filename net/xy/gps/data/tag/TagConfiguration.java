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
package net.xy.gps.data.tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.xy.codebasel.Utils;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;
import net.xy.codebasel.parser.AbstractStaxParser;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.tag.impl.OrCondition;
import net.xy.gps.data.tag.impl.Style;
import net.xy.gps.data.tag.impl.TagMatchCondition;

/**
 * implements an xml reader for reading tag configurations
 * 
 * @author Xyan
 * 
 */
public class TagConfiguration extends AbstractStaxParser {
    /**
     * global configuration
     */
    public static final Config CONF_TAG_CONF = Cfg.register("tags.config.src", "net/xy/gps/data/tag/tags.conf.xml");
    public static final Config CONF_TAG_CONF_POI = Cfg.register("tags.config.poi.src",
            "net/xy/gps/data/tag/pois.conf.xml");

    /**
     * package resource constructor
     * 
     * @param inputXml
     * @param listener
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    public TagConfiguration(final String inputXml, final ITagListener listener) throws FileNotFoundException,
            XMLStreamException {
        parse(TagConfiguration.class.getClassLoader().getResourceAsStream(inputXml), listener);
    }

    /**
     * default constructor delegate
     * 
     * @param inputXml
     * @param listener
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    public TagConfiguration(final File inputXml, final ITagListener listener) throws FileNotFoundException,
            XMLStreamException {
        parse(new FileInputStream(inputXml), listener);
    }

    /**
     * parses an inputfile and transmits tags
     * 
     * @param inputXml
     * @param listener
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    private void parse(final InputStream fin, final ITagListener listener) throws XMLStreamException,
            FileNotFoundException {
        // TODO [9] add statelistener support, parser should send its state
        // final long total = inputXml.length();
        final XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(fin);
        setReader(reader);
        foreach(new Found() {
            public void tag() throws XMLStreamException {
                if (isTag("tags")) {
                    foreach(new Found() {
                        public void tag() throws XMLStreamException {
                            if (isTag("tag")) {
                                final Tag tag = new Tag();
                                tag.name = (String) def(attribute("name"), tag.name);
                                tag.priority = (Integer) def(intval("priority"), tag.priority);
                                tag.enabled = (Boolean) def(boolval("enabled"), tag.enabled);
                                final Double zoom = doubleval("zoom");
                                tag.zoom = zoom != null ? zoom.doubleValue() : tag.zoom;
                                foreach(new Found() {
                                    public void tag() throws XMLStreamException {
                                        if (isTag("style")) {
                                            final Style style = new Style();
                                            tag.style = style;
                                            style.fill = (Boolean) def(boolval("fill"), style.fill);
                                            style.color = (Integer[]) def(intarray("color"), style.color);
                                            if (style.color.length < 4) {
                                                throw new IllegalArgumentException("Invalid color found");
                                            }
                                            final Integer width = intval("width");
                                            style.width = width != null ? width : style.width;
                                            style.border = (Float[]) def(floatarray("border"), style.border);
                                            style.borderColor = (Integer[]) def(intarray("borderColor"),
                                                    style.borderColor);
                                            style.image = strval("image");
                                        } else if (isTag("conditions")) {
                                            foreach(new Found() {
                                                public void tag() throws XMLStreamException {
                                                    if (isTag("tagmatch")) {
                                                        tag.conditions.add(getTagMatch());
                                                    } else if (isTag("orCondition")) {
                                                        final OrCondition orcond = (OrCondition) Utils.returnAdd(
                                                                new OrCondition(), tag.conditions);
                                                        foreach(new Found() {
                                                            public void tag() throws XMLStreamException {
                                                                if (isTag("tagmatch")) {
                                                                    orcond.conditions.add(getTagMatch());
                                                                }
                                                            };
                                                        });
                                                    }
                                                };
                                            });
                                        }
                                    };
                                });
                                listener.accept(tag);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * reads an TagMatchCondition from reader
     * 
     * @return
     */
    private TagMatchCondition getTagMatch() {
        final TagMatchCondition tagmatch = new TagMatchCondition();
        tagmatch.key = strval("key");
        tagmatch.value = strval("value");
        final String type = strval("type");
        if ("area".equalsIgnoreCase(type)) {
            tagmatch.type = Integer.valueOf(IDataObject.DATA_AREA);
        } else if ("way".equalsIgnoreCase(type)) {
            tagmatch.type = Integer.valueOf(IDataObject.DATA_WAY);
        } else if ("point".equalsIgnoreCase(type)) {
            tagmatch.type = Integer.valueOf(IDataObject.DATA_POINT);
        } else {
            throw new IllegalArgumentException("Mtachtag type has an inproper value");
        }
        return tagmatch;
    }

    /**
     * receives parsed tags
     * 
     * @author Xyan
     * 
     */
    public static interface ITagListener {
        /**
         * accept one ready parsed tag
         * 
         * @param tag
         */
        public void accept(final Tag tag);
    }
}