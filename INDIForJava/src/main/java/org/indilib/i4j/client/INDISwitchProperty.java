package org.indilib.i4j.client;

/*
 * #%L
 * INDI for Java Client Library
 * %%
 * Copyright (C) 2013 - 2014 indiforjava
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

import org.indilib.i4j.Constants;
import org.indilib.i4j.INDIException;
import org.indilib.i4j.properties.INDIStandardProperty;
import org.indilib.i4j.protocol.DefElement;
import org.indilib.i4j.protocol.DefSwitch;
import org.indilib.i4j.protocol.DefSwitchVector;
import org.indilib.i4j.protocol.NewSwitchVector;
import org.indilib.i4j.protocol.NewVector;
import org.indilib.i4j.protocol.OneSwitch;
import org.indilib.i4j.protocol.SetVector;

/**
 * A class representing a INDI Switch Property.
 * <p>
 * It implements a listener mechanism to notify changes in its Elements.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDISwitchProperty extends INDIProperty<INDISwitchElement> {

    /**
     * A UI component that can be used in graphical interfaces for this Switch
     * Property.
     */
    private INDIPropertyListener uiComponent;

    /**
     * The current Rule for this Switch Property.
     */
    private Constants.SwitchRules rule;

    /**
     * Constructs an instance of <code>INDISwitchProperty</code>.
     * <code>INDISwitchProperty</code>s are not usually directly instantiated.
     * Usually used by <code>INDIDevice</code>. Throws IllegalArgumentException
     * if the XML Property is not well formed (for example if the Elements are
     * not well formed or if the Rule is not valid).
     * 
     * @param xml
     *            A XML Element <code>&lt;defSwitchVector&gt;</code> describing
     *            the Property.
     * @param device
     *            The <code>INDIDevice</code> to which this Property belongs.
     */
    protected INDISwitchProperty(DefSwitchVector xml, INDIDevice device) {
        super(xml, device);
        String rul = xml.getRule();
        if (rul.compareTo("OneOfMany") == 0) {
            rule = Constants.SwitchRules.ONE_OF_MANY;
        } else if (rul.compareTo("AtMostOne") == 0) {
            rule = Constants.SwitchRules.AT_MOST_ONE;
        } else if (rul.compareTo("AnyOfMany") == 0) {
            rule = Constants.SwitchRules.ANY_OF_MANY;
        } else {
            throw new IllegalArgumentException("Illegal Rule for the Switch Property");
        }
        for (DefElement<?> element : xml.getElements()) {
            if (element instanceof DefSwitch) {
                String name = element.getName();

                INDIElement iel = getElement(name);

                if (iel == null) { // Does not exist
                    INDISwitchElement ite = new INDISwitchElement((DefSwitch) element, this);
                    addElement(ite);
                }
            }
        }
        if (!checkCorrectValues()) {
            if (getSelectedCount() != 0) { // Sometimes de CONFIG_PROCESS is not
                                           // correct at the beginning. skip
                throw new IllegalArgumentException("Illegal initial value for Switch Property");
            }
            setState(Constants.PropertyStates.ALERT);
        }
    }

    @Override
    protected void update(SetVector<?> el) {
        super.update(el, OneSwitch.class);

        if (!checkCorrectValues()) {
            setState(Constants.PropertyStates.ALERT);
        }
    }

    /**
     * Gets the current Rule for this Switch Property.
     * 
     * @return the current Rule for this Switch Property
     */
    public Constants.SwitchRules getRule() {
        return rule;
    }

    /**
     * Sets the Permission of this Property. If set to Write Only it defaults to
     * Read Only (Switch properties cannot be Read Only).
     * 
     * @param permission
     *            the new Permission for this Property.
     */
    @Override
    protected void setPermission(Constants.PropertyPermissions permission) {
        if (permission == Constants.PropertyPermissions.WO) {
            super.setPermission(Constants.PropertyPermissions.RO);
        } else {
            super.setPermission(permission);
        }
    }

    /**
     * Checks if the Rule of this Switch property holds.
     * 
     * @return <code>true</code> if the values of the Elements of this Property
     *         comply with the Rule. <code>false</code> otherwise.
     */
    private boolean checkCorrectValues() {
        if (getState() == Constants.PropertyStates.OK) {

            int selectedCount = getSelectedCount();

            if (rule == Constants.SwitchRules.ONE_OF_MANY && selectedCount != 1) {
                return false;
            }

            if (rule == Constants.SwitchRules.AT_MOST_ONE && selectedCount > 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the number of selected Switch Elements.
     * 
     * @return the number of selected Elements.
     */
    private int getSelectedCount() {
        int selectedCount = 0;
        for (INDISwitchElement el : this) {
            if (el.getValue() == Constants.SwitchStatus.ON) {
                selectedCount++;
            }
        }
        return selectedCount;
    }

    /**
     * Gets the opening XML Element &lt;newSwitchVector&gt; for this Property.
     * 
     * @return the opening XML Element &lt;newSwitchVector&gt; for this
     *         Property.
     */
    @Override
    protected NewVector<?> getXMLPropertyChangeInit() {
        return new NewSwitchVector();
    }

    @Override
    public INDIPropertyListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIPropertyListener(uiComponent);
        }
        uiComponent = INDIViewCreator.getDefault().createSwitchPropertyView(this);
        addINDIPropertyListener(uiComponent);

        return uiComponent;
    }

    /**
     * Gets a particular Element of this Property by its name.
     * 
     * @param name
     *            The name of the Element to be returned
     * @return The Element of this Property with the given <code>name</code>.
     *         <code>null</code> if there is no Element with that
     *         <code>name</code>.
     */
    @Override
    public final INDISwitchElement getElement(String name) {
        return super.getElement(name);
    }

    /**
     * Gets a particular Element of this Property by its name.
     * 
     * @param name
     *            The name of the Element to be returned
     * @return The Element of this Property with the given <code>name</code>.
     *         <code>null</code> if there is no Element with that
     *         <code>name</code>.
     */
    @Override
    public final INDISwitchElement getElement(INDIStandardProperty name) {
        return super.getElement(name.name());
    }

    /**
     * Gets the values of the Property as a String.
     * 
     * @return A String representation of the value of the Property.
     */
    @Override
    public String getValuesAsString() {
        StringBuffer aux = new StringBuffer();
        int n = 0;
        for (INDISwitchElement element : this) {
            if (element.getValue() == Constants.SwitchStatus.ON) {
                if (n != 0) {
                    aux.append(", ");
                }
                n++;
                aux.append(element.getLabel());
            }
        }
        if (n > 1) {
            aux.insert(0, "[");
            aux.append("]");
        }
        return aux.toString();
    }
}
