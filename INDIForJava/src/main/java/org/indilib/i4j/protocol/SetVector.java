package org.indilib.i4j.protocol;

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

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * This class represents an INDI XML protocol element.
 * 
 * @param <T>
 *            type for the builder
 * @author Richard van Nieuwenhoven
 */
public abstract class SetVector<T> extends INDIProtocol<T> {

    /**
     * the child elements of the vector.
     */
    @XStreamImplicit
    private List<OneElement<?>> elements;

    /**
     * the state attribute of the element.
     */
    @XStreamAsAttribute
    private String state;

    /**
     * the timeout attribute of the element.
     */
    @XStreamAsAttribute
    private String timeout;

    /**
     * add one element to the list.
     * 
     * @param element
     *            the element to add.
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T addElement(OneElement<?> element) {
        getElements().add(element);
        return (T) this;
    }

    /**
     * @return the list of element children of this element.
     */
    public List<OneElement<?>> getElements() {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        return elements;
    }

    /**
     * @return the state attribute of the element.
     */
    public String getState() {
        return state;
    }

    /**
     * @return the timeout attribute of the element.
     */
    public String getTimeout() {
        return timeout;
    }

    @Override
    public boolean isSet() {
        return true;
    }

    @Override
    public boolean isSetVector() {
        return true;
    }

    /**
     * set the state attribute of the element.
     * 
     * @param newState
     *            the new attibute state value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setState(String newState) {
        this.state = newState;
        return (T) this;
    }

    /**
     * set the timeout attribute of the element.
     * 
     * @param newTimeout
     *            the new attibute timeout value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setTimeout(String newTimeout) {
        this.timeout = newTimeout;
        return (T) this;
    }

    @Override
    public T trim() {
        this.state = trim(this.state);
        this.timeout = trim(this.timeout);
        for (OneElement<?> element : getElements()) {
            element.trim();
        }
        return super.trim();
    }
}
