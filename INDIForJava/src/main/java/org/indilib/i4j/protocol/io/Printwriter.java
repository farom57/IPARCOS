package org.indilib.i4j.protocol.io;

/*
 * #%L
 * INDI Protocol implementation
 * %%
 * Copyright (C) 2012 - 2014 indiforjava
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.Writer;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;

/**
 * Print writer for xml output where all elements get an end tag. even if the
 * value is empty. If that is not nessesary this class can be deleted.
 * 
 * @author Richard van Nieuwenhoven
 */
final class Printwriter extends PrettyPrintWriter {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * tagIsEmpty field of the superclass.
     */
    private final Field tagIsEmpty;

    /**
     * instanciate the writer.
     * 
     * @param writer
     *            the deeper writer to write the xml on.
     */
    protected Printwriter(Writer writer) {
        super(writer, PrettyPrintWriter.XML_QUIRKS, new char[0], new XmlFriendlyNameCoder());
        try {
            tagIsEmpty = PrettyPrintWriter.class.getDeclaredField("tagIsEmpty");
            final Field field = tagIsEmpty;
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    field.setAccessible(true);
                    return null;
                }
            });
        } catch (Exception e) {
            throw new IllegalStateException("this should not happen, did the super class change?", e);
        }
    }

    @Override
    public void endNode() {
        try {
            tagIsEmpty.set(this, false);
            super.endNode();
        } catch (Exception e) {
            throw new RuntimeException("?", e);
        }
    }

    @Override
    protected String getNewLine() {
        return "";
    }

}
