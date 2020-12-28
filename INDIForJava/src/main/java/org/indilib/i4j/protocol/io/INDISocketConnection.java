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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import org.indilib.i4j.protocol.api.INDIConnection;
import org.indilib.i4j.protocol.api.INDIInputStream;
import org.indilib.i4j.protocol.api.INDIOutputStream;
import org.indilib.i4j.protocol.url.INDIURLStreamHandler;
import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Indi protocol connection around a tcp/ip socket.
 * 
 * @author Ricard van Nieuwenhoven
 */
public class INDISocketConnection implements INDIConnection {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * the logger to log to.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDISocketConnection.class);

    /**
     * timeout to use with tcp connections.
     */
    private static final int CONNECT_TIMEOUT = 20000;

    /**
     * the indi protocol input stream.
     */
    private INDIInputStream inputStream;

    /**
     * the indi protocol output stream.
     */
    private INDIOutputStream ouputStream;

    /**
     * the socket over with to communicate.
     */
    private final Socket socket;

    /**
     * constructor around an existing socket. this is probalby only usefull in a
     * server case where the accept of a server socket returns a client socket.
     * 
     * @param socket
     *            the socket to connecto to
     */
    public INDISocketConnection(Socket socket) {
        this.socket = socket;
    }

    /**
     * create a indi socket connection the the specified host and port.
     * 
     * @param host
     *            the host name to connect to.
     * @param port
     *            the port to connect to.
     * @throws IOException
     *             if the connection fails.
     */
    public INDISocketConnection(String host, int port) throws IOException {
        this(new Socket());
        socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT);
    }

    @Override
    public INDIInputStream getINDIInputStream() throws IOException {
        if (inputStream == null) {
            inputStream = INDIProtocolFactory.createINDIInputStream(wrap(socket.getInputStream()));
        }
        return inputStream;
    }

    /**
     * possibility for subclasses to wrap the input stream.
     * 
     * @param coreInputStream
     *            the input stream.
     * @return the inputstream itself or a wrapped version of it
     */
    protected InputStream wrap(InputStream coreInputStream) {
        return coreInputStream;
    }

    /**
     * possibility for subclasses to wrap the output stream.
     * 
     * @param coreOutputStream
     *            the output stream.
     * @return the outputStream itself or a wrapped version of it
     */
    protected OutputStream wrap(OutputStream coreOutputStream) {
        return coreOutputStream;
    }

    @Override
    public INDIOutputStream getINDIOutputStream() throws IOException {
        if (ouputStream == null) {
            ouputStream = INDIProtocolFactory.createINDIOutputStream(wrap(socket.getOutputStream()));
        }
        return ouputStream;
    }

    @Override
    public void close() throws IOException {
        socket.shutdownInput();
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            LOG.warn("inputStream close problem", e);
        }
        try {
            if (ouputStream != null) {
                ouputStream.close();
            }
        } catch (Exception e) {
            LOG.warn("ouputStream close problem", e);
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            LOG.warn("socket close problem", e);
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "(" + getURL().toString() + ")";
    }

    @Override
    public URL getURL() {
        try {
            return new URL(getProtocol(), socket.getInetAddress().getHostAddress(), socket.getPort(), "/");
        } catch (MalformedURLException e) {
            LOG.error("illegal std url, should never happen!", e);
            return null;
        }
    }

    /**
     * @return the protokol for this connection.
     */
    protected String getProtocol() {
        return INDIURLStreamHandler.PROTOCOL;
    }
}
