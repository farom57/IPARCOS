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

/**
 * A interface to be notified about changes in a <code>INDIDevice</code>.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public interface INDIDeviceListener {

    /**
     * Called when a new Property is added to the Device.
     * 
     * @param device
     *            The Device on which the Property has been addded.
     * @param property
     *            The Property that has been added.
     */
    void newProperty(INDIDevice device, INDIProperty<?> property);

    /**
     * Called when a Property is removed from a Device.
     * 
     * @param device
     *            The Device to which the Property has been removed.
     * @param property
     *            The Property that has been removed.
     */
    void removeProperty(INDIDevice device, INDIProperty<?> property);

    /**
     * Called when the message for a Device has changed.
     * 
     * @param device
     *            The device to which the message has changed.
     */
    void messageChanged(INDIDevice device);
}
