package net.xy.gps.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
     * @throws FileNotFoundException
     */
    public static void parse(final File inputXml, final IObjectListener listener) throws FileNotFoundException,
            XMLStreamException, FactoryConfigurationError {
        final XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new FileInputStream(inputXml));
        while (reader.nextTag() != XMLStreamConstants.END_DOCUMENT) {
            // <node...
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && "node".equals(reader.getName())) {
                final int id = Integer.valueOf(reader.getAttributeValue(null, "id"));
                final double lat = Double.valueOf(reader.getAttributeValue(null, "lat"));
                final double lon = Double.valueOf(reader.getAttributeValue(null, "lon"));
                listener.put(new PoiData(lat, lon, String.valueOf(id)));
                // until </node>
                while (reader.next() != XMLStreamConstants.END_ELEMENT && !"node".equals(reader.getName())) {
                }
            }
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
    }
}