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
@XStreamAlias("setSwitchVector")
public class SetSwitchVector extends SetVector<SetSwitchVector> {

    /**
     * the rule attribute of the element.
     */
    @XStreamAsAttribute
    private String rule;

    /**
     * @return the rule attribute of the element.
     */
    public String getRule() {
        return rule;
    }

    @Override
    public boolean isSetSwitchVector() {
        return true;
    }

    @Override
    public boolean isSwitch() {
        return true;
    }

    /**
     * set the rule attribute of the element.
     * 
     * @param newRule
     *            the new value for the rule attribute.
     * @return this for builder pattern.
     */
    public SetSwitchVector setRule(String newRule) {
        rule = newRule;
        return this;
    }

    @Override
    public SetSwitchVector trim() {
        rule = trim(rule);
        return super.trim();
    }
}
