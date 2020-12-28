package org.indilib.i4j.protocol.url;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.indilib.i4j.protocol.websocket.INDIWebSocketStreamHandler;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * A class to handle INDI Streams.
 * 
 * @author Richard van Nieuwenhoven [ritchie [at] gmx.at]
 */
public class INDIURLStreamHandlerFactory implements URLStreamHandlerFactory {

    /**
     * is the handler already initialized.
     */
    private static boolean initialized = false;

    /**
     * initialize the indi protocol.
     */
    public static void init() {
        if (!initialized) {
            initialized = true;
            if (System.getProperty(INDIURLStreamHandlerFactory.class.getSimpleName() + ".auto.register", "true").equalsIgnoreCase("true")) {
                URL.setURLStreamHandlerFactory(new INDIURLStreamHandlerFactory());
            }
        }
    }

    @Override
    public final URLStreamHandler createURLStreamHandler(final String protocol) {
        if (INDIURLStreamHandler.PROTOCOL.equals(protocol)) {
            return new INDIURLStreamHandler();
        } else if (INDIURLZipStreamHandler.PROTOCOL.equals(protocol)) {
            return new INDIURLZipStreamHandler();
        } else if (INDIWebSocketStreamHandler.PROTOCOL.equals(protocol)) {
            return new INDIWebSocketStreamHandler();
        }
        return null;
    }

}
