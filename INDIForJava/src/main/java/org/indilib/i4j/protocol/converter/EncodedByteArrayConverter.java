package org.indilib.i4j.protocol.converter;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * The cunking of the normal xstream base64 encoder is not good in our case so
 * we use the commons encoding one.
 * 
 * @author Richard van Nieuwenhoven
 */
public class EncodedByteArrayConverter implements Converter, SingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        return type.isArray() && type.getComponentType().equals(byte.class);
    }

    @Override
    public Object fromString(String str) {
        return Base64.decodeBase64(str);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(toString(source));

    }

    @Override
    public String toString(Object obj) {
        return new String(Base64.encodeBase64((byte[]) obj), Charsets.UTF_8);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return fromString(reader.getValue());
    }

}
