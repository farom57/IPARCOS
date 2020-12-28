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

/**
 * This class represents an INDI XML protocol element.
 * 
 * @author Richard van Nieuwenhoven
 */
@XStreamAlias("defSwitchVector")
public class DefSwitchVector extends DefVector<DefSwitchVector> {

    /**
     * the rule element attribute.
     */
    @XStreamAsAttribute
    private String rule;

    @Override
    public boolean isDefSwitchVector() {
        return true;
    }

    @Override
    public boolean isSwitch() {
        return true;
    }

    /**
     * @return the rule element attribute.
     */
    public String getRule() {
        return rule;
    }

    /**
     * set the rule element atttribute.
     * 
     * @param newRule
     *            the new rule value
     * @return this for builder pattern.
     */
    public DefSwitchVector setRule(String newRule) {
        rule = newRule;
        return this;
    }

    @Override
    public DefSwitchVector trim() {
        rule = trim(rule);
        return super.trim();
    }
}
