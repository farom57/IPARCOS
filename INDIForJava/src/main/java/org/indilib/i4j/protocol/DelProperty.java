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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This class represents an INDI XML protocol element.
 * 
 * @author Richard van Nieuwenhoven
 */
@XStreamAlias("delProperty")
public class DelProperty extends INDIProtocol<DelProperty> {

    /**
     * the version attribute of the element.
     */
    @XStreamAsAttribute
    private String version;

    /**
     * @return the version attribute of the element.
     */
    public String getVersion() {
        return version;
    }

    /**
     * set the version attribute of the element.
     * 
     * @param newVersion
     *            the new attibute version value
     * @return this for builder pattern.
     */
    public DelProperty setVersion(String newVersion) {
        version = newVersion;
        return this;
    }

    @Override
    public DelProperty trim() {
        version = trim(version);
        return super.trim();
    }
}
