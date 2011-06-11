package net.xy.gps.data.tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.xy.codebasel.Utils;
import net.xy.codebasel.parser.AbstractStaxParser;
import net.xy.gps.data.tag.impl.OrCondition;
import net.xy.gps.data.tag.impl.Style;
import net.xy.gps.data.tag.impl.Tag;
import net.xy.gps.data.tag.impl.TagMatchCondition;

/**
 * implements an xml reader for reading tag configurations
 * 
 * @author Xyan
 * 
 */
public class TagConfiguration extends AbstractStaxParser {

    /**
     * parses an inputfile and transmits tags
     * 
     * @param inputXml
     * @param listener
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public void parse(final File inputXml, final ITagListener listener) throws XMLStreamException,
            FileNotFoundException {
        final FileInputStream fin = new FileInputStream(inputXml);
        // TODO add statelistener support
        // final long total = inputXml.length();
        final XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(fin);
        setReader(reader);
        foreach(new Found() {
            public void tag() throws XMLStreamException {
                if (isTag("tag")) {
                    final Tag tag = new Tag();
                    tag.name = (String) def(attribute("name"), tag.name);
                    tag.priority = (Integer) def(intval("priority"), tag.priority);
                    tag.enabled = (Boolean) def(intval("enabled"), tag.enabled);
                    foreach(new Found() {
                        public void tag() throws XMLStreamException {
                            if (isTag("style")) {
                                final Style style = new Style();
                                style.fill = (Boolean) def(intval("fill"), style.fill);
                                style.color = (List) def(intlist("color"), style.color);
                            } else if (isTag("conditions")) {
                                foreach(new Found() {
                                    public void tag() throws XMLStreamException {
                                        if (isTag("tagmatch")) {
                                            tag.conditions.add(getTagMatch());
                                        } else if (isTag("orCondition")) {
                                            final OrCondition orcond = (OrCondition) Utils
                                                    .returnAdd(new OrCondition(), tag.conditions);
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

    /**
     * reads an TagMatchCondition from reader
     * 
     * @return
     */
    private TagMatchCondition getTagMatch() {
        final TagMatchCondition tagmatch = new TagMatchCondition();
        tagmatch.key = strval("key");
        tagmatch.value = strval("value");
        tagmatch.type = intval("type");
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