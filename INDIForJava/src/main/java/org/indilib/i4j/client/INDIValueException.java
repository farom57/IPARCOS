package org.indilib.i4j.client;

/*
 * #%L
 * INDI for Java Client Library
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

import org.indilib.i4j.INDIException;

/**
 * A class representing an exception on the value of a <code>INDIElement</code>.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDIValueException extends INDIException {

    /**
     * serialisation version.
     */
    private static final long serialVersionUID = -3338903931160160408L;

    /**
     * The element that produced the exception.
     */
    private INDIElement element;

    /**
     * Constructs an instance of <code>INDIValueException</code> with the
     * specified detail message.
     * 
     * @param element
     *            The element that produced the error.
     * @param msg
     *            the detail message.
     */
    public INDIValueException(INDIElement element, String msg) {
        super(msg);
        this.element = element;
    }

    /**
     * Gets the <code>INDIElement</code> that produced the exception.
     * 
     * @return the <code>INDIElement</code> that produced the exception
     */
    public INDIElement getINDIElement() {
        return element;
    }
}
