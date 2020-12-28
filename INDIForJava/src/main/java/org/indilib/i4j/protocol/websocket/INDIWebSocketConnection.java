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
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.indilib.i4j.protocol.api.INDIConnection;
import org.indilib.i4j.protocol.api.INDIInputStream;
import org.indilib.i4j.protocol.api.INDIOutputStream;
import org.indilib.i4j.protocol.io.INDIProtocolFactory;
import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * Indi protocol connection around a websocket entpoint on a websocket server.
 * 
 * @author Ricard van Nieuwenhoven
 */
public class INDIWebSocketConnection implements INDIConnection {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * Logger to log to.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIWebSocketConnection.class);

    /**
     * the indi protocol input stream.
     */
    private INDIInputStream inputStream;

    /**
     * the indi protocol output stream.
     */
    private INDIOutputStream ouputStream;

    /**
     * the websocket server session.
     */
    private Session session;

    /**
     * constructor around an existing session.
     * 
     * @param session
     *            the web socket session to connecto to
     */
    public INDIWebSocketConnection(Session session) {

        this.session = session;
        try {
            createINDIInputStream();
            createINDIOutPutStream();
        } catch (IOException e) {
            LOG.error("cound not create INDI streams on websocket endpoint", e);
            try {
                close();
            } catch (IOException e1) {
                LOG.warn("cound not close websocket endpoint properly", e1);
            }
        }
    }

    @Override
    public INDIInputStream getINDIInputStream() throws IOException {
        return inputStream;
    }

    /**
     * create a indiinputstream around the websocket.
     * 
     * @throws IOException
     *             if some streams where instable.
     */
    protected void createINDIInputStream() throws IOException {
        PipedInputStream bytesIn = new PipedInputStream();
        final PipedOutputStream bytesOut = new PipedOutputStream(bytesIn);

        inputStream = INDIProtocolFactory.createINDIInputStream(bytesIn);
        session.addMessageHandler(new MessageHandler.Partial<byte[]>() {

            @Override
            public void onMessage(byte[] message, boolean last) {
                try {
                    bytesOut.write(message);
                    bytesOut.flush();
                } catch (IOException e) {
                    LOG.error("cound not create INDI input stream on websocket endpoint", e);
                    try {
                        close();
                    } catch (IOException e1) {
                        LOG.warn("cound not close websocket endpoint properly", e1);
                    }
                }
            }
        });
    }

    @Override
    public INDIOutputStream getINDIOutputStream() throws IOException {
        return ouputStream;
    }

    /**
     * create an indi output stream around a websocket endpoint.
     * 
     * @throws IOException
     *             if some streams where instable.
     */
    protected void createINDIOutPutStream() throws IOException {
        ouputStream = INDIProtocolFactory.createINDIOutputStream(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                byte[] bytes = new byte[1];
                bytes[0] = (byte) b;
                session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
            }

            @Override
            public void write(byte[] bytes) throws IOException {
                session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
            }

            @Override
            public void write(byte[] bytes, int off, int len) throws IOException {
                session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes, off, len));
            }

            @Override
            public void flush() throws IOException {
                session.getBasicRemote().sendBinary(ByteBuffer.wrap(new byte[0]), true);
            }
        });

    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        ouputStream.close();
        session.close();
    }

    @Override
    public String toString() {
        return getClass().getName() + "(" + session.getRequestURI() + ")";
    }

    @Override
    public URL getURL() {
        try {
            return session.getRequestURI().toURL();
        } catch (MalformedURLException e) {
            LOG.error("illegal std url, should never happen!", e);
            return null;
        }
    }

}
