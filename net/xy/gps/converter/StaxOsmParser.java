package net.xy.gps.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.xy.gps.data.IDataObject;
import net.xy.gps.data.PoiData;

/**
 * stax parser for osm xml
 * 
 * @author Xyan
 * 
 */
public class StaxOsmParser {

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
        final XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(fin);
        boolean phase2_ways = false;
        while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT && reader.next() != XMLStreamConstants.END_DOCUMENT) {
            // <node...
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && "node".equals(reader.getName().getLocalPart())) {
                if (phase2_ways) {
                    throw new IllegalStateException(
                            "Found node after begin of ways. Streamline back reference not possible. XML has to be ordered nodes > ways > relations");
                }
                final int id = Integer.valueOf(reader.getAttributeValue(null, "id"));
                final double lat = Double.valueOf(reader.getAttributeValue(null, "lat"));
                final double lon = Double.valueOf(reader.getAttributeValue(null, "lon"));
                listener.put(new PoiData(lat, lon, String.valueOf(id)));
                // until </node>
                while (reader.next() != XMLStreamConstants.END_DOCUMENT) {
                    if (reader.getEventType() == XMLStreamConstants.END_ELEMENT
                            && "node".equals(reader.getName().getLocalPart())) {
                        break;
                    }
                }
            }
            // <way...
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && "way".equals(reader.getName().getLocalPart())) {
                phase2_ways = true;
                final int id = Integer.valueOf(reader.getAttributeValue(null, "id"));
                final List nodes = new ArrayList();
                // until </way>
                while (reader.next() != XMLStreamConstants.END_DOCUMENT) {
                    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT
                            && "nd".equals(reader.getName().getLocalPart())) {
                        nodes.add(Integer.valueOf(reader.getAttributeValue(null, "ref")));
                    } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT
                            && "way".equals(reader.getName().getLocalPart())) {
                        break;
                    }
                }
                listener.putWay(id, nodes);
            }
            listener.state(100 - fin.available() / (total / 100));
        }
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
         */
        public void putWay(int id, List path);
    }
}