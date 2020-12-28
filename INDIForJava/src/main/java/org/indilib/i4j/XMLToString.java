package org.indilib.i4j;

/*
 * #%L
 * INDI for Java Base Library
 * %%
 * Copyright (C) 2013 - 2014 indiforjava
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * A class to transforms XML Elements into Strings.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public final class XMLToString {

    /**
     * A logger for the errors.
     */
    private static final Logger LOG = LoggerFactory.getLogger(XMLToString.class);

    /**
     * A private constructor to avoid instantiating this utility class.
     */
    private XMLToString() {
    }

    /**
     * Transforms a XML Element into a String.
     * 
     * @param xml
     *            The XML Element
     * @return A String representing the XML Element
     */
    public static String transform(final Element xml) {
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();

            Transformer transformer = transFactory.newTransformer();

            StringWriter buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(xml), new StreamResult(buffer));
            String str = buffer.toString();
            return str;
        } catch (Exception e) {
            LOG.error("Problem transforming XML to String", e);
        }

        return "";
    }
}
