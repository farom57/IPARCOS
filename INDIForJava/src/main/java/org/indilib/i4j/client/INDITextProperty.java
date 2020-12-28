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
import org.indilib.i4j.protocol.DefElement;
import org.indilib.i4j.protocol.DefText;
import org.indilib.i4j.protocol.DefTextVector;
import org.indilib.i4j.protocol.NewTextVector;
import org.indilib.i4j.protocol.NewVector;
import org.indilib.i4j.protocol.OneText;
import org.indilib.i4j.protocol.SetVector;

/**
 * A class representing a INDI Text Property.
 * <p>
 * It implements a listener mechanism to notify changes in its Elements.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDITextProperty extends INDIProperty<INDITextElement> {

    /**
     * A UI component that can be used in graphical interfaces for this Text
     * Property.
     */
    private INDIPropertyListener uiComponent;

    /**
     * Constructs an instance of <code>INDITextProperty</code>.
     * <code>INDITextProperty</code>s are not usually directly instantiated.
     * Usually used by <code>INDIDevice</code>. Throws IllegalArgumentException
     * if the XML Property is not well formed (for example if the Elements are
     * not well formed).
     * 
     * @param xml
     *            A XML Element <code>&lt;defTextVector&gt;</code> describing
     *            the Property.
     * @param device
     *            The <code>INDIDevice</code> to which this Property belongs.
     */
    protected INDITextProperty(DefTextVector xml, INDIDevice device) {
        super(xml, device);
        for (DefElement<?> element : xml.getElements()) {
            if (element instanceof DefText) {
                String name = element.getName();
                INDIElement iel = getElement(name);
                if (iel == null) { // Does not exist
                    INDITextElement ite = new INDITextElement((DefText) element, this);
                    addElement(ite);
                }
            }

        }
    }

    @Override
    protected void update(SetVector<?> el) {
        super.update(el, OneText.class);
    }

    /**
     * Gets the opening XML Element &lt;newTextVector&gt; for this Property.
     * 
     * @return the opening XML Element &lt;newTextVector&gt; for this Property.
     */
    @Override
    protected NewVector<?> getXMLPropertyChangeInit() {
        return new NewTextVector();
    }

    @Override
    public INDIPropertyListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIPropertyListener(uiComponent);
        }

        uiComponent = INDIViewCreator.getDefault().createTextPropertyView(this);
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
    public final INDITextElement getElement(String name) {
        return super.getElement(name);
    }
}
