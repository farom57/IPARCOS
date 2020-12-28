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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

import org.indilib.i4j.protocol.INDIProtocol;
import org.indilib.i4j.protocol.api.INDIConnection;
import org.indilib.i4j.protocol.api.INDIInputStream;
import org.indilib.i4j.protocol.api.INDIOutputStream;
import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create two connected INDI protocol streams that have a blocking connection,
 * reading a protokol object will block until one becomes available.
 * 
 * @author Richard van Nieuwenhoven
 */
public final class INDIPipedConnections {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * The one end of a piped connection.
     */
    private static final class INDIPipedConnection implements INDIConnection {

        /**
         * a closed indicator to indicate the connection was closed.
         */
        private boolean closed = false;

        /**
         * the input stream of the connection.
         */
        private INDIInputStream inputStream;

        /**
         * the output stream of the connection.
         */
        private INDIOutputStream outputStream;

        /**
         * constructor of the piped connection with the in and out blocking
         * queue s as a parameter.
         * 
         * @param inputQueue
         *            the blocking input queue
         * @param outputQueue
         *            the blocking output queue
         */
        public INDIPipedConnection(final LinkedBlockingQueue<INDIProtocol<?>> inputQueue, final LinkedBlockingQueue<INDIProtocol<?>> outputQueue) {
            inputStream = new INDIPipedInputStream(inputQueue, this);
            outputStream = new INDIPipedOutputStream(outputQueue, this);
        }

        /**
         * close the connection.
         */
        @Override
        public void close() {
            closed = true;
        }

        @Override
        public INDIInputStream getINDIInputStream() throws IOException {
            return inputStream;
        }

        @Override
        public INDIOutputStream getINDIOutputStream() throws IOException {
            return outputStream;
        }

        /**
         * @return is the connection closed.
         */
        public boolean isClosed() {
            return closed;
        }

        @Override
        public URL getURL() {
            try {
                return new URL("indi:///?pipe");
            } catch (MalformedURLException e) {
                LOG.error("illegal std url, should never happen!", e);
                return null;
            }
        }
    }

    /**
     * This class wrapes an INDIInputstream around a blocking queue.
     */
    private static final class INDIPipedInputStream implements INDIInputStream {

        /**
         * the parent connection this end is part of.
         */
        private final INDIPipedConnection connection;

        /**
         * the wrapped blocking queue.
         */
        private final LinkedBlockingQueue<INDIProtocol<?>> inputQueue;

        /**
         * constructor of the wrapper.
         * 
         * @param inputQueue
         *            the wrapped blocking queue.
         * @param connection
         *            the parent connection this end is part of.
         */
        private INDIPipedInputStream(LinkedBlockingQueue<INDIProtocol<?>> inputQueue, INDIPipedConnection connection) {
            this.inputQueue = inputQueue;
            this.connection = connection;
        }

        @Override
        public void close() throws IOException {
            connection.close();
        }

        @Override
        public INDIProtocol<?> readObject() throws IOException {
            if (connection.isClosed()) {
                return null;
            }
            try {
                INDIProtocol<?> readObject = inputQueue.take();
                if (readObject instanceof INDIProtokolEndMarker) {
                    return null;
                }
                readObject.trim();
                return readObject;
            } catch (InterruptedException e) {
                connection.close();
                return null;
            }
        }
    }

    /**
     * This class wrapes an INDIOutputstream around a blocking queue.
     */
    private static final class INDIPipedOutputStream implements INDIOutputStream {

        /**
         * the parent connection this end is part of.
         */
        private final INDIPipedConnection connection;

        /**
         * the wrapped blocking queue.
         */
        private final LinkedBlockingQueue<INDIProtocol<?>> outputQueue;

        /**
         * constructor of the wrapper.
         * 
         * @param outputQueue
         *            the wrapped blocking queue.
         * @param connection
         *            the parent connection this end is part of.
         */
        private INDIPipedOutputStream(LinkedBlockingQueue<INDIProtocol<?>> outputQueue, INDIPipedConnection connection) {
            this.outputQueue = outputQueue;
            this.connection = connection;
        }

        @Override
        public void close() throws IOException {
            connection.close();
            try {
                outputQueue.put(new INDIProtokolEndMarker());
            } catch (InterruptedException e) {
                LOG.error("closing on a closed stream, ignoring it", e);
            }
        }

        @Override
        public synchronized void writeObject(INDIProtocol<?> element) throws IOException {
            if (connection.isClosed()) {
                throw new IOException("stream closed");
            }
            try {
                outputQueue.put(element);
            } catch (InterruptedException e) {
                connection.close();
                throw new IOException("queue closed", e);
            }
        }
    }

    /**
     * Indicator class to indicate the end of stream.
     */
    private static final class INDIProtokolEndMarker extends INDIProtocol<Object> {

    }

    /**
     * the logger to log to.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIPipedConnections.class);

    /**
     * the first connection that is internaly connected to the second.
     */
    private final INDIPipedConnection first;

    /**
     * the second connection that is internaly connected to the first.
     */
    private final INDIPipedConnection second;

    /**
     * create the piped connection pair.
     */
    public INDIPipedConnections() {
        LinkedBlockingQueue<INDIProtocol<?>> firstToSecondQueue = new LinkedBlockingQueue<INDIProtocol<?>>();
        LinkedBlockingQueue<INDIProtocol<?>> secondToFirstQueue = new LinkedBlockingQueue<INDIProtocol<?>>();
        first = new INDIPipedConnection(firstToSecondQueue, secondToFirstQueue);
        second = new INDIPipedConnection(secondToFirstQueue, firstToSecondQueue);
    }

    /**
     * @return the first connection that is internaly connected to the second.
     */
    public INDIConnection first() {
        return first;
    }

    /**
     * @return the second connection that is internaly connected to the first.
     */
    public INDIConnection second() {
        return second;
    }

}
