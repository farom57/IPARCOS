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

import org.indilib.i4j.protocol.api.INDIConnection;
import org.indilib.i4j.protocol.api.INDIInputStream;
import org.indilib.i4j.protocol.api.INDIOutputStream;
import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an indi protocol connector to an other process. the in
 * and out put stream are connected to the in an d output stream of the external
 * program.
 * 
 * @author Richard van Nieuwenhoven
 */
public class INDIProcessConnection implements INDIConnection {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * the logger to log to.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIProcessConnection.class);

    /**
     * the input stream from the process deserialized as indi protocol objects.
     */
    private INDIInputStream inputStream;

    /**
     * the ouput stream from the process serialized from indi protocol objects.
     */
    private INDIOutputStream outputStream;

    /**
     * the process from with the in and output stream come.
     */
    private final Process process;

    /**
     * construct the indi connection around the process.
     * 
     * @param process
     *            the process to use.
     */
    public INDIProcessConnection(Process process) {
        this.process = process;
    }

    @Override
    public INDIInputStream getINDIInputStream() throws IOException {
        if (inputStream == null) {
            inputStream = INDIProtocolFactory.createINDIInputStream(process.getInputStream());
        }
        return inputStream;
    }

    @Override
    public INDIOutputStream getINDIOutputStream() throws IOException {
        if (inputStream == null) {
            outputStream = INDIProtocolFactory.createINDIOutputStream(process.getOutputStream());
        }
        return outputStream;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public URL getURL() {
        try {
            return new URL("indi:///?process=" + process.toString());
        } catch (MalformedURLException e) {
            LOG.error("illegal std url, should never happen!", e);
            return null;
        }
    }
}
