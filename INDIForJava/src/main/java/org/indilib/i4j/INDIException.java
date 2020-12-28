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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 * A class representing a generic INDI Exception.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDIException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a INDI Exception.
     * 
     * @param msg
     *            The message for the exception.
     */
    public INDIException(final String msg) {
        super(msg);
    }

    /**
     * Creates a INDI Exception.
     * 
     * @param msg
     *            The message for the exception.
     * @param cause
     *            The cause for the exception.
     */
    public INDIException(final String msg, Throwable cause) {
        super(msg, cause);
    }
}
