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

import org.indilib.i4j.INDIException;
import org.indilib.i4j.protocol.DefBlob;
import org.indilib.i4j.protocol.DefBlobVector;
import org.indilib.i4j.protocol.DefElement;
import org.indilib.i4j.protocol.NewBlobVector;
import org.indilib.i4j.protocol.NewVector;
import org.indilib.i4j.protocol.OneBlob;
import org.indilib.i4j.protocol.SetVector;

/**
 * A class representing a INDI BLOB Property.
 * <p>
 * It implements a listener mechanism to notify changes in its Elements.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDIBLOBProperty extends INDIProperty<INDIBLOBElement> {

    /**
     * A UI component that can be used in graphical interfaces for this BLOB
     * Property.
     */
    private INDIPropertyListener uiComponent;

    /**
     * Constructs an instance of <code>INDIBLOBProperty</code>.
     * <code>INDIBLOBProperty</code>s are not usually directly instantiated.
     * Usually used by <code>INDIDevice</code>.
     * 
     * @param xml
     *            A XML Element <code>&lt;defBLOBVector&gt;</code> describing
     *            the Property.
     * @param device
     *            The <code>INDIDevice</code> to which this Property belongs.
     */
    protected INDIBLOBProperty(DefBlobVector xml, INDIDevice device) {
        super(xml, device);

        for (DefElement<?> element : xml.getElements()) {
            if (element instanceof DefBlob) {
                String name = element.getName();

                INDIElement iel = getElement(name);

                if (iel == null) { // Does not exist
                    INDIBLOBElement ite = new INDIBLOBElement((DefBlob) element, this);
                    addElement(ite);
                }
            }

        }
    }

    @Override
    protected void update(SetVector<?> xml) {
        super.update(xml, OneBlob.class);
    }

    /**
     * Gets the opening XML Element &lt;newBLOBVector&gt; for this Property.
     * 
     * @return the opening XML Element &lt;newBLOBVector&gt; for this Property.
     */
    @Override
    protected NewVector<?> getXMLPropertyChangeInit() {
        return new NewBlobVector();

    }

    @Override
    public INDIPropertyListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIPropertyListener(uiComponent);
        }
        uiComponent = INDIViewCreator.getDefault().createBlobPropertyView(this);
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
    public INDIBLOBElement getElement(String name) {
        return super.getElement(name);
    }
}
