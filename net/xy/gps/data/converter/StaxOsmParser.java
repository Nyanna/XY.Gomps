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
package net.xy.gps.data.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.xy.codebasel.Log;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;
import net.xy.gps.data.IDataObject;
import net.xy.gps.data.PoiData;
import net.xy.gps.data.tag.TagFactory;

/**
 * stax parser for osm xml
 * 
 * @author Xyan
 * 
 */
public class StaxOsmParser {
    /**
     * messages
     */
    private static final Config TEXT_WRONG_SORTED = Cfg
            .register(
                    "osm.xml.praser.error.sorting",
                    " Found node after begin of ways. Streamline back reference not possible. XML has to be ordered nodes > ways > relations");

    /**
     * main parse method
     * 
     * @param inputXml
     * @param listener
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     * @throws IOException
     */
    public static void parse(final File inputXml, final IObjectListener listener) throws XMLStreamException,
            FactoryConfigurationError, IOException {
        final FileInputStream fin = new FileInputStream(inputXml);
        final long total = inputXml.length();
        final XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(fin);
        boolean phase2_ways = false;
        int nodeCount = 0;
        while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
            doNext(reader);
            // <node...
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT
                    && "node".equals(reader.getName().getLocalPart())) {
                Log.log(Log.LVL_TRACE, "Parse node...", null);
                nodeCount++;
                if (phase2_ways) {
                    throw new IllegalStateException(Cfg.string(TEXT_WRONG_SORTED));
                }
                final Integer id = Integer.valueOf(reader.getAttributeValue(null, "id"));
                final Double lat = Double.valueOf(reader.getAttributeValue(null, "lat"));
                final Double lon = Double.valueOf(reader.getAttributeValue(null, "lon"));
                final Map tags = new HashMap();
                // until </node>
                while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
                    doNext(reader);
                    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT
                            && "tag".equals(reader.getName().getLocalPart())) {
                        tags.put(reader.getAttributeValue(null, "k"), reader.getAttributeValue(null, "v"));
                    } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT
                            && "node".equals(reader.getName().getLocalPart())) {
                        break;
                    }
                }
                listener.put(new PoiData(lat.doubleValue(), lon.doubleValue(), id.intValue(), TagFactory.getTags(
                        IDataObject.DATA_POINT, tags)));
                Log.log(Log.LVL_TRACE, "Parse node done", null);
            } else
            // <way...
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT
                    && "way".equals(reader.getName().getLocalPart())) {
                Log.log(Log.LVL_TRACE, "Parse way...", null);
                phase2_ways = true;
                final Integer id = Integer.valueOf(reader.getAttributeValue(null, "id"));
                final Map tags = new HashMap();
                final List nodes = new ArrayList();
                int dataType = IDataObject.DATA_WAY;
                // until </way>
                while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
                    doNext(reader);
                    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT
                            && "tag".equals(reader.getName().getLocalPart())) {
                        tags.put(reader.getAttributeValue(null, "k"), reader.getAttributeValue(null, "v"));
                    } else if (reader.getEventType() == XMLStreamConstants.START_ELEMENT
                            && "nd".equals(reader.getName().getLocalPart())) {
                        nodes.add(Integer.valueOf(reader.getAttributeValue(null, "ref")));
                    } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT
                            && "way".equals(reader.getName().getLocalPart())) {
                        break;
                    }
                }
                // check for area firts and alst node are the same
                if (nodes.size() > 1
                        && ((Integer) nodes.get(0)).intValue() == ((Integer) nodes.get(nodes.size() - 1)).intValue()) {
                    dataType = IDataObject.DATA_AREA;
                }
                listener.putWay(id.intValue(), nodes, TagFactory.getTags(dataType, tags));

                Log.log(Log.LVL_TRACE, "Parse way done", null);
            }
            try {
                listener.state(100 - fin.available() / (total / 100));
            } catch (final IOException e) {
                // in case reader closses fin
            }
        }
    }

    /**
     * interceptor
     * 
     * @param reader
     * @throws XMLStreamException
     */
    private static final void doNext(final XMLStreamReader reader) throws XMLStreamException {
        reader.next();
    }

    /**
     * object observer
     * 
     * @author Xyan
     * 
     */
    public static interface IObjectListener {

        /**
         * method should accept various data objects
         * 
         * @param data
         */
        public void put(IDataObject data);

        /**
         * percentage state updates
         * 
         * @param l
         */
        public void state(long l);

        /**
         * construct an way data back referenced to node db
         * 
         * @param id
         * @param path
         * @param objects
         */
        public void putWay(final int id, final List path, final Integer[] tags);
    }
}