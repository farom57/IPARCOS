package org.indilib.i4j.protocol.api;

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
import java.net.URL;

/**
 * Abstract interface that defines a indi protocol connection. The different
 * implementations must provide and deliver the stream of indi objects. The
 * difference betreen the implementations will be the means of transport.
 * 
 * @author Richard van Nieuwenhoven
 */
public interface INDIConnection {

    /**
     * @return the input stream of indi protokol objects.
     * @throws IOException
     *             if there was a connection problem.
     */
    INDIInputStream getINDIInputStream() throws IOException;

    /**
     * @return the output stream of indi protokol objects.
     * @throws IOException
     *             if there was a connection problem.
     */
    INDIOutputStream getINDIOutputStream() throws IOException;

    /**
     * close the connection together with the in and output stream.
     * 
     * @throws IOException
     *             if there was a connection problem.
     */
    void close() throws IOException;

    /**
     * @return the url representing this connection.
     */
    URL getURL();
}
