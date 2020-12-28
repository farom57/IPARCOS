package org.indilib.i4j.protocol.url;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.indilib.i4j.protocol.api.INDIConnection;
import org.indilib.i4j.protocol.api.INDIInputStream;
import org.indilib.i4j.protocol.api.INDIOutputStream;
import org.indilib.i4j.protocol.io.INDISocketConnection;
import org.indilib.i4j.protocol.io.INDIZipSocketConnection;

/**
 * This class represents a indi connection to a server over an url referense.
 * The url is decoded to get the connection data. Future extentions could also
 * handle the selection of device and property as part of the url path.
 * 
 * @author Richard van Nieuwenhoven
 */
public class INDIURLConnection extends URLConnection implements INDIConnection {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * the undelaying socket indi connection.
     */
    private INDISocketConnection socketConnection;

    /**
     * constructor using the url.
     * 
     * @param url
     *            the connection specification.
     */
    protected INDIURLConnection(URL url) {
        super(url);
    }

    @Override
    public synchronized void connect() throws IOException {
        if (socketConnection == null) {
            int port = getURL().getPort();
            if (port <= 0) {
                port = getURL().getDefaultPort();
            }
            String host = getURL().getHost();
            if (host == null || host.isEmpty()) {
                host = "localhost";
            }
            try {
                if (INDIURLZipStreamHandler.PROTOCOL.equals(getURL().getProtocol())) {
                    socketConnection = new INDIZipSocketConnection(host, port);
                } else {
                    socketConnection = new INDISocketConnection(host, port);
                }
            } catch (IOException e) {
                throw new IOException("Problem connecting to " + host + ":" + port);
            }
            connected = true;
        }
    }

    @Override
    public INDIInputStream getINDIInputStream() throws IOException {
        return getSocketConnection().getINDIInputStream();
    }

    @Override
    public INDIOutputStream getINDIOutputStream() throws IOException {
        return getSocketConnection().getINDIOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return (InputStream) getINDIInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return (OutputStream) getINDIOutputStream();
    }

    /**
     * @return the initialized socket connection.
     * @throws IOException
     *             is the connection could not be initialized.
     */
    private INDIConnection getSocketConnection() throws IOException {
        connect();
        return socketConnection;
    }

    @Override
    public void close() throws IOException {
        if (socketConnection != null) {
            socketConnection.close();
        }
    }

    /**
     * helper method to parse an url for its query parameters. This method does
     * not take all possibilities in account but it is good anouth for indi
     * urls.
     * 
     * @param url
     *            the url to parse the query
     * @return the map with query keys and there list with values.(never null)
     */
    public static Map<String, List<String>> splitQuery(URL url) {
        final Map<String, List<String>> queryPairs = new LinkedHashMap<String, List<String>>();
        try {
            if (url != null && url.getQuery() != null) {
                final String[] pairs = url.getQuery().split("&");
                for (String pair : pairs) {
                    final int idx = pair.indexOf("=");
                    final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                    if (!queryPairs.containsKey(key)) {
                        queryPairs.put(key, new LinkedList<String>());
                    }
                    final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                    queryPairs.get(key).add(value);
                }
            }
            return queryPairs;
        } catch (UnsupportedEncodingException e) {
            return queryPairs;
        }
    }

}
