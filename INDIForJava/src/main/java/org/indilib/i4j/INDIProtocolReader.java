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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import java.io.IOException;

import org.indilib.i4j.protocol.INDIProtocol;
import org.indilib.i4j.protocol.api.INDIInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that reads from a input stream and sends the read messages to a
 * parser.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDIProtocolReader extends Thread {

    /**
     * A logger for the errors.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIProtocolReader.class);

    /**
     * The parser to which the messages will be sent.
     */
    private INDIProtocolParser parser;

    /**
     * Used to friendly stop the reader.
     */
    private boolean stop;

    /**
     * Creates the reader.
     * 
     * @param parser
     *            The parser to which the readed messages will be sent.
     * @param name
     *            the thread name to use.
     */
    public INDIProtocolReader(final INDIProtocolParser parser, String name) {
        super(name);
        this.parser = parser;
    }

    /**
     * The main body of the reader.
     */
    @Override
    public final void run() {
        INDIInputStream inputStream = parser.getInputStream();
        if (inputStream == null) {
            LOG.warn("Null protocol reader inputStream");
            parser.finishReader();
            return;
        }
        try {
            INDIProtocol<?> readObject = inputStream.readObject();
            while (!stop && readObject != null) {
                parser.processProtokolMessage(readObject);
                readObject = inputStream.readObject();
            }
        } catch (IOException e) {
            LOG.error("could not parse indi stream", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOG.error("Could not close Doc", e);
            }
            parser.finishReader();
        }
    }

    /**
     * Sets the stop parameter. If set to <code>true</code> the reader will
     * gracefully stop after the next read.
     * 
     * @param stop
     *            The stop parameter
     */
    public final void setStop(final boolean stop) {
        this.stop = stop;
    }
}
