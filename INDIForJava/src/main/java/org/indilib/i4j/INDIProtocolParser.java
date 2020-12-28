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

import org.indilib.i4j.protocol.INDIProtocol;
import org.indilib.i4j.protocol.api.INDIInputStream;

/**
 * A interface representing a generic INDI Protocol Parser.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @author Richard van Nieuwenhoven
 */
public interface INDIProtocolParser {

    /**
     * Parses a protocol message.
     * 
     * @param message
     *            The protocol message to parse.
     */
    void processProtokolMessage(INDIProtocol<?> message);

    /**
     * Gets the input stream from where the messages will be read.
     * 
     * @return The input stream from where the messages will be read.
     */
    INDIInputStream getInputStream();

    /**
     * Called when the reader finishes the readings (communications broken /
     * stopped).
     */
    void finishReader();
}
