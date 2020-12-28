package org.indilib.i4j.protocol;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

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
/**
 * This class represents an INDI XML protocol element.
 * 
 * @author Richard van Nieuwenhoven
 */
@XStreamAlias("enableBLOB")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = {
    "textContent"
}, types = {
    EnableBLOB.class
})
public class EnableBLOB extends INDIProtocol<EnableBLOB> {

    @Override
    public boolean isEnableBLOB() {
        return true;
    }

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

    /**
     * set the test content of the element.
     * 
     * @param newTextContent
     *            the new text content value.
     * @return this for builder pattern.
     */
    public EnableBLOB setTextContent(String newTextContent) {
        textContent = newTextContent;
        return this;
    }

    @Override
    public EnableBLOB trim() {
        textContent = trim(textContent);
        return super.trim();
    }
}
