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

import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * This class represents an INDI XML protocol element.
 * 
 * @param <T>
 *            type for the builder
 * @author Richard van Nieuwenhoven
 */
@XStreamConverter(value = ToAttributedValueConverter.class, strings = {
    "textContent"
}, types = {
    OneElement.class
})
public abstract class OneElement<T> extends INDIProtocol<T> {

    /**
     * the text content value of the element.
     */
    private String textContent;

    /**
     * @return the text content of the element.
     */
    public String getTextContent() {
        return textContent;
    }

    @Override
    public boolean isOne() {
        return true;
    }

    /**
     * set the test content of the element.
     * 
     * @param newTextContent
     *            the new text content value.
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setTextContent(String newTextContent) {
        this.textContent = newTextContent;
        return (T) this;
    }

    @Override
    public T trim() {
        this.textContent = trim(this.textContent);
        return super.trim();
    }
}
