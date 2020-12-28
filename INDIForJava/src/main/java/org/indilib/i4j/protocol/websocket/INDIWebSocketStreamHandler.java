package org.indilib.i4j.protocol.websocket;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The webseocket url handler.
 * 
 * @author Richard van Nieuwenhoven
 */
public class INDIWebSocketStreamHandler extends URLStreamHandler {

    /**
     * Logger to log to.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIWebSocketStreamHandler.class);

    /**
     * The indi default port number.
     */
    public static final int WEBSOCKET_DEFAULT_PORT = 8080;

    /**
     * The protokol name for indi over websockets.
     */
    public static final String PROTOCOL = "indiw";

    @Override
    protected final int getDefaultPort() {
        return WEBSOCKET_DEFAULT_PORT;
    }

    @Override
    protected final URLConnection openConnection(final URL url) throws IOException {
        return new INDIWebsocketURLConnection(url);
    }

    @Override
    protected final void parseURL(final URL u, final String spec, final int start, final int end) {
        super.parseURL(u, spec, start, end);
    }
}
