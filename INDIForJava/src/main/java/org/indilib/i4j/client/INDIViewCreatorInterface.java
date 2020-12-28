package org.indilib.i4j.client;

/*
 * #%L
 * INDI for Java Client Library
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

import org.indilib.i4j.Constants;
import org.indilib.i4j.INDIException;

/**
 * The view creator factory interface. implementors are used to create views for
 * different types of gui views like swing / androis / javafx or swt.
 * 
 * @author Richard van Nieuwenhoven
 */
public interface INDIViewCreatorInterface {

    /**
     * create a device listener for the device.
     * 
     * @param indiDevice
     *            the device to listen to
     * @return the created device listener
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIDeviceListener createDeviceListener(INDIDevice indiDevice) throws INDIException;

    /**
     * Create the view for a blob element with the specified permissions.
     * 
     * @param indiblobElement
     *            the blob element to create a view for
     * @param permission
     *            the permissions for the parent property
     * @return the element listener around the view
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIElementListener createBlobElementView(INDIBLOBElement indiblobElement, Constants.PropertyPermissions permission) throws INDIException;

    /**
     * Create the view for a light element with the specified permissions.
     * 
     * @param indiLightElement
     *            the light element to create a view for
     * @param permission
     *            the permissions for the parent property
     * @return the element listener around the view
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIElementListener createLightElementView(INDILightElement indiLightElement, Constants.PropertyPermissions permission) throws INDIException;

    /**
     * Create the view for a number element with the specified permissions.
     * 
     * @param indiNumberElement
     *            the number element to create a view for
     * @param permission
     *            the permissions for the parent property
     * @return the element listener around the view
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIElementListener createNumberElementView(INDINumberElement indiNumberElement, Constants.PropertyPermissions permission) throws INDIException;

    /**
     * Create the view for a switch element with the specified permissions.
     * 
     * @param indiSwitchElement
     *            the switch element to create a view for
     * @param permission
     *            the permissions for the parent property
     * @return the element listener around the view
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIElementListener createSwitchElementView(INDISwitchElement indiSwitchElement, Constants.PropertyPermissions permission) throws INDIException;

    /**
     * Create the view for a text element with the specified permissions.
     * 
     * @param indiTextElement
     *            the text element to create a view for
     * @param permission
     *            the permissions for the parent property
     * @return the element listener around the view
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIElementListener createTextElementView(INDITextElement indiTextElement, Constants.PropertyPermissions permission) throws INDIException;

    /**
     * Create the view for a blob property.
     * 
     * @param indiProperty
     *            the blob property to create the view for.
     * @return the property listener of the view.
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIPropertyListener createBlobPropertyView(INDIBLOBProperty indiProperty) throws INDIException;

    /**
     * Create the view for a number property.
     * 
     * @param indiProperty
     *            the number property to create the view for.
     * @return the property listener of the view.
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIPropertyListener createNumberPropertyView(INDINumberProperty indiProperty) throws INDIException;

    /**
     * Create the view for a text property.
     * 
     * @param indiProperty
     *            the text property to create the view for.
     * @return the property listener of the view.
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIPropertyListener createTextPropertyView(INDITextProperty indiProperty) throws INDIException;

    /**
     * Create the view for a switch property.
     * 
     * @param indiProperty
     *            the switch property to create the view for.
     * @return the property listener of the view.
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIPropertyListener createSwitchPropertyView(INDISwitchProperty indiProperty) throws INDIException;

    /**
     * Create the view for a light property.
     * 
     * @param indiProperty
     *            the light property to create the view for.
     * @return the property listener of the view.
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIPropertyListener createLightPropertyView(INDILightProperty indiProperty) throws INDIException;

    /**
     * create the view for a device.
     * 
     * @param indiDevice
     *            the device to create the view for.
     * @return the device listener for the view
     * @throws INDIException
     *             if something unexpected went wrong.
     */
    INDIDeviceListener createDeviceView(INDIDevice indiDevice) throws INDIException;

}
