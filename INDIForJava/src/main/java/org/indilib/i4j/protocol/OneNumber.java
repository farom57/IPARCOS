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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * This class represents an INDI XML protocol element.
 * 
 * @author Richard van Nieuwenhoven
 */
@XStreamAlias("oneNumber")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = {
    "textContent"
}, types = {
    OneElement.class
})
public class OneNumber extends OneElement<OneNumber> {

    /**
     * the max attribute of the element.
     */
    @XStreamAsAttribute
    private String max;

    /**
     * the min attribute of the element.
     */
    @XStreamAsAttribute
    private String min;

    /**
     * @return the max attribute of the element.
     */
    public String getMax() {
        return max;
    }

    /**
     * @return the min attribute of the element.
     */
    public String getMin() {
        return min;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public boolean isOneNumber() {
        return true;
    }

    /**
     * set the max attribute of the element.
     * 
     * @param newMax
     *            the new max attribute value of the element
     * @return this for builder pattern.
     */
    public OneNumber setMax(String newMax) {
        max = newMax;
        return this;
    }

    /**
     * set the min attribute of the element.
     * 
     * @param newMin
     *            the new min attribute value of the element
     * @return this for builder pattern.
     */
    public OneNumber setMin(String newMin) {
        min = newMin;
        return this;
    }

    @Override
    public OneNumber trim() {
        min = trim(min);
        max = trim(max);
        return super.trim();
    }
}
