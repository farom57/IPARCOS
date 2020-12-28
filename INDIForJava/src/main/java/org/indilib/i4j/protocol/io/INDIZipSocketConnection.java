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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.indilib.i4j.protocol.url.INDIURLZipStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Create a socket connection that communicates with a zipped data streams. And
 * by that vastly reducing the xml overhead.
 * 
 * @author Richard van Nieuwenhoven
 */
public class INDIZipSocketConnection extends INDISocketConnection {

    /**
     * constructor for the zip compressed socket stream.
     * 
     * @param socket
     *            the socket to connect the in and output streams.
     */
    public INDIZipSocketConnection(Socket socket) {
        super(socket);
    }

    /**
     * constructor for the zip compressed socket stream.
     * 
     * @param host
     *            the host name to connect to.
     * @param port
     *            the port to connect to.
     * @throws IOException
     *             if the connection fails.
     */
    public INDIZipSocketConnection(String host, int port) throws IOException {
        super(host, port);
    }

    @Override
    protected InputStream wrap(InputStream coreInputStream) {
        return new InflaterInputStream(new MinimalBlockinInputStream(coreInputStream)) {

            /**
             * available() should return the number of bytes that can be read
             * without running into blocking wait. Accomplishing this feast
             * would eventually require to pre-inflate a huge chunk of data, so
             * we rather opt for a more relaxed contract
             * (java.util.zip.InflaterInputStream does not fit the bill). This
             * code has been tested to work with BufferedReader.readLine();
             */
            @Override
            public int available() throws IOException {
                if (!inf.finished() && !inf.needsInput()) {
                    return 1;
                } else {
                    return in.available();
                }
            }
        };
    }

    @Override
    protected OutputStream wrap(OutputStream coreOutputStream) {
        return new DeflaterOutputStream(coreOutputStream, true);
    }

    @Override
    protected String getProtocol() {
        return INDIURLZipStreamHandler.PROTOCOL;
    }
}
