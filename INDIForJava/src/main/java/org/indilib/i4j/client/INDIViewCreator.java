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

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * The view createtion factory, uses the service loader pattern to get the
 * correct implementor for the current situation.
 * 
 * @author Richard van Nieuwenhoven
 */
public final class INDIViewCreator {

    /**
     * The dummy creator when nothing was found, this one is used that does
     * "nothing".
     */
    private static final class DummyViewCreator implements INDIViewCreatorInterface {

        @Override
        public INDIPropertyListener createTextPropertyView(INDITextProperty indiProperty) throws INDIException {
            return DUMMY_PROPERTY_LISTENER;
        }

        @Override
        public INDIElementListener createTextElementView(INDITextElement indiTextElement, Constants.PropertyPermissions permission) throws INDIException {
            return DUMMY_ELEMENT_LISTENER;
        }

        @Override
        public INDIPropertyListener createSwitchPropertyView(INDISwitchProperty indiProperty) throws INDIException {
            return DUMMY_PROPERTY_LISTENER;
        }

        @Override
        public INDIElementListener createSwitchElementView(INDISwitchElement indiSwitchElement, Constants.PropertyPermissions permission) throws INDIException {
            return DUMMY_ELEMENT_LISTENER;
        }

        @Override
        public INDIPropertyListener createNumberPropertyView(INDINumberProperty indiProperty) throws INDIException {
            return DUMMY_PROPERTY_LISTENER;
        }

        @Override
        public INDIElementListener createNumberElementView(INDINumberElement indiNumberElement, Constants.PropertyPermissions permission) throws INDIException {
            return DUMMY_ELEMENT_LISTENER;
        }

        @Override
        public INDIPropertyListener createLightPropertyView(INDILightProperty indiProperty) throws INDIException {
            return DUMMY_PROPERTY_LISTENER;
        }

        @Override
        public INDIElementListener createLightElementView(INDILightElement indiLightElement, Constants.PropertyPermissions permission) throws INDIException {
            return DUMMY_ELEMENT_LISTENER;
        }

        @Override
        public INDIDeviceListener createDeviceListener(INDIDevice indiDevice) throws INDIException {
            return DUMMY_DEVICE_LISTENER;
        }

        @Override
        public INDIPropertyListener createBlobPropertyView(INDIBLOBProperty indiProperty) throws INDIException {
            return DUMMY_PROPERTY_LISTENER;
        }

        @Override
        public INDIElementListener createBlobElementView(INDIBLOBElement indiblobElement, Constants.PropertyPermissions permission) throws INDIException {
            return DUMMY_ELEMENT_LISTENER;
        }

        @Override
        public INDIDeviceListener createDeviceView(INDIDevice indiDevice) throws INDIException {
            return DUMMY_DEVICE_LISTENER;
        }
    }

    /**
     * Dummy device listener, that does nothing.
     */
    private static final class DummyDeviceListener implements INDIDeviceListener {

        @Override
        public void removeProperty(INDIDevice device, INDIProperty<?> property) {
        }

        @Override
        public void newProperty(INDIDevice device, INDIProperty<?> property) {
        }

        @Override
        public void messageChanged(INDIDevice device) {
        }
    }

    /**
     * Dummy element listener, that does nothing.
     */
    private static final class DummyElementListener implements INDIElementListener {

        @Override
        public void elementChanged(INDIElement element) {
        }
    }

    /**
     * Dummy property listener, that does nothing.
     */
    private static final class DummyPropertyListener implements INDIPropertyListener {

        @Override
        public void propertyChanged(INDIProperty<?> property) {
        }
    }

    /**
     * static cached dummy device listener.
     */
    private static final DummyDeviceListener DUMMY_DEVICE_LISTENER = new DummyDeviceListener();

    /**
     * static cached dummy property listener.
     */
    private static final DummyPropertyListener DUMMY_PROPERTY_LISTENER = new DummyPropertyListener();

    /**
     * static cached dummy element listener.
     */
    private static final DummyElementListener DUMMY_ELEMENT_LISTENER = new DummyElementListener();

    /**
     * the staticaly cached creator interface resolved using the serviceloader
     * pattern.
     */
    private static INDIViewCreatorInterface creatorInterface;

    /**
     * utility class should never be instanciated.
     */
    private INDIViewCreator() {
    }

    /**
     * @return the default implementation dependeing on what is first available
     *         in the classpath.
     */
    public static INDIViewCreatorInterface getDefault() {
        if (creatorInterface == null) {
            ServiceLoader<INDIViewCreatorInterface> loader = ServiceLoader.load(INDIViewCreatorInterface.class);
            Iterator<INDIViewCreatorInterface> iterator = loader.iterator();
            if (iterator.hasNext()) {
                creatorInterface = iterator.next();
            }
            if (creatorInterface == null) {
                creatorInterface = new DummyViewCreator();
            }
        }
        return creatorInterface;
    }
}
