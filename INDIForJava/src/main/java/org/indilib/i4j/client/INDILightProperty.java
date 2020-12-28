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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.indilib.i4j.protocol.DefElement;
import org.indilib.i4j.protocol.DefLight;
import org.indilib.i4j.protocol.DefLightVector;
import org.indilib.i4j.protocol.NewVector;
import org.indilib.i4j.protocol.OneLight;
import org.indilib.i4j.protocol.SetVector;
import org.indilib.i4j.Constants;
import org.indilib.i4j.INDIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing a INDI Light Property.
 * <p>
 * It implements a listener mechanism to notify changes in its Elements.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDILightProperty extends INDIProperty<INDILightElement> {

    /**
     * A logger for the errors.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDILightProperty.class);

    /**
     * A UI component that can be used in graphical interfaces for this Light
     * Property.
     */
    private INDIPropertyListener uiComponent;

    /**
     * Constructs an instance of <code>INDILightProperty</code>.
     * <code>INDILightProperty</code>s are not usually directly instantiated.
     * Usually used by <code>INDIDevice</code>.
     * 
     * @param xml
     *            A XML Element <code>&lt;defLightVector&gt;</code> describing
     *            the Property.
     * @param device
     *            The <code>INDIDevice</code> to which this Property belongs.
     */
    protected INDILightProperty(DefLightVector xml, INDIDevice device) {
        super(xml, device);
        for (DefElement<?> element : xml.getElements()) {
            if (element instanceof DefLight) {
                String name = element.getName();

                INDIElement iel = getElement(name);

                if (iel == null) { // Does not exist
                    INDILightElement ite = new INDILightElement((DefLight) element, this);
                    addElement(ite);
                }
            }
        }
    }

    @Override
    protected void update(SetVector<?> el) {
        super.update(el, OneLight.class);
    }

    /**
     * Always sets the permission to Read Only as lights may not change.
     * 
     * @param permission
     *            ignored.
     */
    @Override
    protected void setPermission(Constants.PropertyPermissions permission) {
        super.setPermission(Constants.PropertyPermissions.RO);
    }

    /**
     * Sets the timeout to 0 as lights may not change.
     * 
     * @param timeout
     *            ignored.
     */
    @Override
    protected void setTimeout(int timeout) {
        super.setTimeout(0);
    }

    /**
     * Gets an empty <code>String</code> as Light Properties cannot be changed
     * by clients.
     * 
     * @return "" a empty <code>String</code>
     */
    @Override
    protected NewVector<?> getXMLPropertyChangeInit() {
        LOG.error("changed but not possible, it should not be possible to change a light!");
        // A light cannot change
        return null;
    }

    @Override
    public INDIPropertyListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIPropertyListener(uiComponent);
        }

        uiComponent = INDIViewCreator.getDefault().createLightPropertyView(this);

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
    public INDILightElement getElement(String name) {
        return super.getElement(name);
    }
}
