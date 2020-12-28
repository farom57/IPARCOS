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
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import org.indilib.i4j.protocol.converter.EncodedByteArrayConverter;

/**
 * This class represents an INDI XML protocol element.
 * 
 * @author Richard van Nieuwenhoven
 */
@XStreamAlias("oneBLOB")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = {
    "byteContent"
}, types = {
    OneBlob.class
})
public class OneBlob extends OneElement<OneBlob> {

    /**
     * The byte content of the blob. Attention the textContent should not be
     * used in the blob!
     */
    @XStreamConverter(EncodedByteArrayConverter.class)
    private byte[] byteContent;

    /**
     * the format attribute of the element.
     */
    private String format;

    /**
     * the size attribute of the element.
     */
    private String size;

    /**
     * @return the byte content of the element.
     */
    public byte[] getByteContent() {
        return byteContent;
    }

    /**
     * @return the format attribute of the element.
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return the size attribute of the element.
     */
    public String getSize() {
        return size;
    }

    @Override
    public boolean isBlob() {
        return true;
    }

    @Override
    public boolean isOneBlob() {
        return true;
    }

    /**
     * set the byte content of the element. (and use the length to set the
     * size).
     * 
     * @param newByteContent
     *            the new byte content.
     * @return this for builder pattern.
     */
    public OneBlob setByteContent(byte[] newByteContent) {
        byteContent = newByteContent;
        if (byteContent != null) {
            size = Integer.toString(byteContent.length);
        } else {
            size = Integer.toString(0);
        }
        return this;
    }

    /**
     * set the format attribute of the element.
     * 
     * @param newFormat
     *            the new format value of the element.
     * @return this for builder pattern.
     */
    public OneBlob setFormat(String newFormat) {
        format = newFormat;
        return this;
    }

    @Override
    public OneBlob trim() {
        format = trim(format);
        size = trim(size);
        return super.trim();
    }
}
