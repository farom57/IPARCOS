package org.indilib.i4j.protocol;

/*
 * #%L INDI Protocol implementation %% Copyright (C) 2012 - 2014 indiforjava %%
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Lesser Public License for more details. You should have received a copy of
 * the GNU General Lesser Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. #L%
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
public abstract class DefVector<T> extends INDIProtocol<T> {

    /**
     * the group element attribute.
     */
    @XStreamAsAttribute
    private String group;

    /**
     * the label element attribute.
     */
    @XStreamAsAttribute
    private String label;

    /**
     * the perm element attribute.
     */
    @XStreamAsAttribute
    private String perm;

    /**
     * the state element attribute.
     */
    @XStreamAsAttribute
    private String state;

    /**
     * the timeout element attribute.
     */
    @XStreamAsAttribute
    private String timeout;

    /**
     * the child elements of the vector.
     */
    @XStreamImplicit
    private List<DefElement<?>> elements;

    /**
     * @return the group element attribute.
     */
    public String getGroup() {
        return group;
    }

    /**
     * @return the label element attribute.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the perm element attribute.
     */
    public String getPerm() {
        return perm;
    }

    /**
     * @return the state element attribute.
     */
    public String getState() {
        return state;
    }

    /**
     * @return the timeout element attribute.
     */
    public String getTimeout() {
        return timeout;
    }

    @Override
    public boolean isDef() {
        return true;
    }

    @Override
    public boolean isVector() {
        return true;
    }

    /**
     * set the group element atttribute.
     * 
     * @param newGroup
     *            the new group value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setGroup(String newGroup) {
        this.group = newGroup;
        return (T) this;
    }

    /**
     * set the label element atttribute.
     * 
     * @param newLabel
     *            the new label value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setLabel(String newLabel) {
        this.label = newLabel;
        return (T) this;
    }

    /**
     * set the perm element atttribute.
     * 
     * @param newPerm
     *            the new perm value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setPerm(String newPerm) {
        this.perm = newPerm;
        return (T) this;
    }

    /**
     * set the state element atttribute.
     * 
     * @param newState
     *            the new state value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setState(String newState) {
        this.state = newState;
        return (T) this;
    }

    /**
     * set the timeout element atttribute.
     * 
     * @param newTimeout
     *            the new timeout value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setTimeout(String newTimeout) {
        this.timeout = newTimeout;
        return (T) this;
    }

    /**
     * @return the list of element children of this element.
     */
    public List<DefElement<?>> getElements() {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        return elements;
    }

    @Override
    public T trim() {
        this.group = trim(this.group);
        this.label = trim(this.label);
        this.perm = trim(this.perm);
        this.state = trim(this.state);
        this.timeout = trim(this.timeout);
        for (DefElement<?> element : getElements()) {
            element.trim();
        }
        return super.trim();
    }
}
