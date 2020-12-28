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
import java.io.OutputStream;

import org.indilib.i4j.protocol.INDIProtocol;
import org.indilib.i4j.protocol.api.INDIOutputStream;
import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;

/**
 * OutPut stream of INDIProtocol objects. Serialized to a xml stream.
 * 
 * @author Richard van Nieuwenhoven
 */
public class INDIOutputStreamImpl extends OutputStream implements INDIOutputStream {

    /**
     * logger to log to.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIOutputStreamImpl.class);

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * The xstream serializer.
     */
    private CustomObjectOutputStream out;

    /**
     * Constructor of the indi output stream.
     * 
     * @param out
     *            the underlaying stream
     * @throws IOException
     *             when something went wrong with the underlaying output stream.
     */
    protected INDIOutputStreamImpl(CustomObjectOutputStream out) throws IOException {
        this.out = out;
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void write(int b) throws IOException {
        throw new IOException("not supported method");
    }

    @Override
    public synchronized void writeObject(INDIProtocol<?> element) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("sending indi object " + element);
        }
        out.writeObject(element);
        out.flush();
    }
}
