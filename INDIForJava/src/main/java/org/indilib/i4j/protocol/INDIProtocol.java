package org.indilib.i4j.protocol;

/*
 * #%L INDI Protocol implementation %% Copyright (C) 2012 - 2014 indiforjava %%
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Lesser Public License for more details. You should have received a copy of
 * the GNU General Lesser Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. #L%
 */

import org.indilib.i4j.protocol.io.INDIProtocolFactory;
import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This class represents an INDI XML protocol element.
 * 
 * @param <T>
 *            type for the builder
 * @author Richard van Nieuwenhoven
 */
public abstract class INDIProtocol<T> {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * the device element attribute.
     */
    @XStreamAsAttribute
    private String device;

    /**
     * the name element attribute.
     */
    @XStreamAsAttribute
    private String name;

    /**
     * the message element attribute.
     */
    @XStreamAsAttribute
    private String message;

    /**
     * the timestamp attribute of the element.
     */
    @XStreamAsAttribute
    private String timestamp;

    /**
     * @return the message element attribute.
     */
    public final String getMessage() {
        return message;
    }

    /**
     * @return the timestamp attribute of the element.
     */
    public final String getTimestamp() {
        return timestamp;
    }

    /**
     * set the max message atttribute.
     * 
     * @param newMessage
     *            the new message value
     * @return this for builder pattern.
     */

    @SuppressWarnings("unchecked")
    public T setMessage(String newMessage) {
        this.message = newMessage;
        return (T) this;
    }

    /**
     * set the timestamp attribute of the element.
     * 
     * @param newTimestamp
     *            the new attibute timestamp value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setTimestamp(String newTimestamp) {
        this.timestamp = newTimestamp;
        return (T) this;
    }

    /**
     * @return the name element attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * @return is this a blob element.
     */
    public boolean isBlob() {
        return false;
    }

    /**
     * @return is this a definition element.
     */
    public boolean isDef() {
        return false;
    }

    /**
     * @return is this a definition blob element.
     */
    public boolean isDefBlobElement() {
        return false;
    }

    /**
     * @return is this a definition blob vector.
     */
    public boolean isDefBlobVector() {
        return false;
    }

    /**
     * @return is this a definition element.
     */
    public boolean isDefElement() {
        return false;
    }

    /**
     * @return is this a definition light element.
     */
    public boolean isDefLightElement() {
        return false;
    }

    /**
     * @return is this a definition light vector.
     */
    public boolean isDefLightVector() {
        return false;
    }

    /**
     * @return is this a definition number element.
     */
    public boolean isDefNumberElement() {
        return false;
    }

    /**
     * @return is this a definition number vector.
     */
    public boolean isDefNumberVector() {
        return false;
    }

    /**
     * @return is this a definition switch vector.
     */
    public boolean isDefSwitchVector() {
        return false;
    }

    /**
     * @return is this a definition text element.
     */
    public boolean isDefTextElement() {
        return false;
    }

    /**
     * @return is this a definition text vector.
     */
    public boolean isDefTextVector() {
        return false;
    }

    /**
     * @return is this a definition vector.
     */
    public boolean isDefVector() {
        return false;
    }

    /**
     * @return is this a definition element.
     */
    public boolean isElement() {
        return false;
    }

    /**
     * @return is this a enable blob message.
     */
    public boolean isEnableBLOB() {
        return false;
    }

    /**
     * @return is this a get properties.
     */
    public boolean isGetProperties() {
        return false;
    }

    /**
     * @return is this a light element.
     */
    public boolean isLight() {
        return false;
    }

    /**
     * @return is this a new properties.
     */
    public boolean isNew() {
        return false;
    }

    /**
     * @return is this a new properties.
     */
    public boolean isNewBlobVector() {
        return false;
    }

    /**
     * @return is this a n ewproperties.
     */
    public boolean isNewLightVector() {
        return false;
    }

    /**
     * @return is this a new properties.
     */
    public boolean isNewNumberVector() {
        return false;
    }

    /**
     * @return is this a new properties.
     */
    public boolean isNewSwitchVector() {
        return false;
    }

    /**
     * @return is this a new properties.
     */
    public boolean isNewTextVector() {
        return false;
    }

    /**
     * @return is this a new properties.
     */
    public boolean isNewVector() {
        return false;
    }

    /**
     * @return is this a number element.
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * @return is this a one element.
     */
    public boolean isOne() {
        return false;
    }

    /**
     * @return is this a one blob element.
     */
    public boolean isOneBlob() {
        return false;
    }

    /**
     * @return is this a one element.
     */
    public boolean isOneElement() {
        return false;
    }

    /**
     * @return is this a one light element.
     */
    public boolean isOneLight() {
        return false;
    }

    /**
     * @return is this a one number element.
     */
    public boolean isOneNumber() {
        return false;
    }

    /**
     * @return is this a one text element.
     */
    public boolean isOneText() {
        return false;
    }

    /**
     * @return is this a set element.
     */
    public boolean isSet() {
        return false;
    }

    /**
     * @return is this a set blob vector.
     */
    public boolean isSetBlobVector() {
        return false;
    }

    /**
     * @return is this a set light vector.
     */
    public boolean isSetLightVector() {
        return false;
    }

    /**
     * @return is this a set number vector.
     */
    public boolean isSetNumberVector() {
        return false;
    }

    /**
     * @return is this a set switch vector.
     */
    public boolean isSetSwitchVector() {
        return false;
    }

    /**
     * @return is this a set text vector.
     */
    public boolean isSetTextVector() {
        return false;
    }

    /**
     * @return is this a set vector.
     */
    public boolean isSetVector() {
        return false;
    }

    /**
     * @return is this a switch.
     */
    public boolean isSwitch() {
        return false;
    }

    /**
     * @return is this a text element.
     */
    public boolean isText() {
        return false;
    }

    /**
     * @return is this a vector.
     */
    public boolean isVector() {
        return false;
    }

    /**
     * set the name element attribute.
     * 
     * @param newName
     *            the new name of the attribute.
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public T setName(String newName) {
        this.name = newName;
        return (T) this;
    }

    /**
     * set the device element atttribute.
     * 
     * @param newDevice
     *            the new device value
     * @return this for builder pattern.
     */
    @SuppressWarnings("unchecked")
    public final T setDevice(String newDevice) {
        this.device = newDevice;
        return (T) this;
    }

    /**
     * @return the device element attribute.
     */
    public final String getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return INDIProtocolFactory.toString(this);
    }

    /**
     * @return true when a non empty name is available.
     */
    public boolean hasName() {
        return getName() != null && !getName().trim().isEmpty();
    }

    /**
     * @return true when a non empty device is available.
     */
    public boolean hasDevice() {
        return getDevice() != null && !getDevice().trim().isEmpty();
    }

    /**
     * @return true when a non empty message is available.
     */
    public boolean hasMessage() {
        return getMessage() != null && !getMessage().trim().isEmpty();
    }

    /**
     * thrim all strings in the structure so all places working with the object
     * do not have to trim any stings.
     * 
     * @return myself
     */
    public T trim() {
        this.name = trim(this.name);
        this.device = trim(this.device);
        this.message = trim(this.message);
        this.timestamp = trim(this.timestamp);
        return (T) this;
    }

    /**
     * Trim one value but keep it null when it was null.
     * 
     * @param value
     *            the value to trim
     * @return the trimmed string or null.
     */
    protected String trim(String value) {
        if (value != null) {
            return value.trim();
        }
        return null;
    }
}
