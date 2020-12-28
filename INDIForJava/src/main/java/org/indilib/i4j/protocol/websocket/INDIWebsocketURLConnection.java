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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.glassfish.tyrus.client.ClientManager;
import org.indilib.i4j.protocol.api.INDIConnection;
import org.indilib.i4j.protocol.api.INDIInputStream;
import org.indilib.i4j.protocol.api.INDIOutputStream;
import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * This class represents a indi connection to a server over an url referense.
 * The url is decoded to get the connection data. Future extentions could also
 * handle the selection of device and property as part of the url path.
 * 
 * @author Richard van Nieuwenhoven
 */
public class INDIWebsocketURLConnection extends URLConnection implements INDIConnection {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * Logger to log to.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIWebsocketURLConnection.class);

    /**
     * the undelaying socket indi connection.
     */
    private INDIWebSocketConnection socketConnection;

    /**
     * constructor using the url.
     * 
     * @param url
     *            the connection specification.
     */
    protected INDIWebsocketURLConnection(URL url) {
        super(url);
    }

    @Override
    public synchronized void connect() throws IOException {
        if (socketConnection == null) {
            try {
                final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

                ClientManager client = ClientManager.createClient();
                Session session = client.connectToServer(new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig config) {
                        socketConnection = new INDIWebSocketConnection(session);
                    }
                }, cec, new URI(getURL().toExternalForm().replace(INDIWebSocketStreamHandler.PROTOCOL + ":", "ws:")));
                socketConnection = new INDIWebSocketConnection(session);
            } catch (Exception e) {
                LOG.error("could not connect to websocket", e);
            }
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

}
