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
@XStreamAlias("defNumber")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = {
    "textContent"
}, types = {
    DefElement.class
})
public class DefNumber extends DefElement<DefNumber> {

    /**
     * the format element attribute.
     */
    @XStreamAsAttribute
    private String format;

    /**
     * the max element attribute.
     */
    @XStreamAsAttribute
    private String max;

    /**
     * the min element attribute.
     */
    @XStreamAsAttribute
    private String min;

    /**
     * The step element attribute.
     */
    @XStreamAsAttribute
    private String step;

    /**
     * @return the format element attribute.
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return the max element attribute.
     */
    public String getMax() {
        return max;
    }

    /**
     * @return the min element attribute.
     */
    public String getMin() {
        return min;
    }

    /**
     * @return the step element attribute.
     */
    public String getStep() {
        return step;
    }

    @Override
    public boolean isDefNumberElement() {
        return true;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    /**
     * set the format element atttribute.
     * 
     * @param newFormat
     *            the new format value
     * @return this for builder pattern.
     */
    public DefNumber setFormat(String newFormat) {
        format = newFormat;
        return this;
    }

    /**
     * set the max element atttribute.
     * 
     * @param newMax
     *            the new max value
     * @return this for builder pattern.
     */
    public DefNumber setMax(String newMax) {
        max = newMax;
        return this;
    }

    /**
     * set the min element atttribute.
     * 
     * @param newMin
     *            the new min value
     * @return this for builder pattern.
     */
    public DefNumber setMin(String newMin) {
        min = newMin;
        return this;
    }

    /**
     * set the step element atttribute.
     * 
     * @param newLeft
     *            the new step value
     * @return this for builder pattern.
     */
    public DefNumber setStep(String newLeft) {
        step = newLeft;
        return this;
    }

    @Override
    public DefNumber trim() {
        min = trim(min);
        max = trim(max);
        format = trim(format);
        step = trim(step);
        return super.trim();
    }

}
