package org.indilib.i4j.properties;

/*
 * #%L
 * INDI for Java Base Library
 * %%
 * Copyright (C) 2012 - 2015 indiforjava
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

/**
 * The following tables describe standard properties pertaining to generic
 * devices and class-specific devices like telescope, CCDs...etc. The name of a
 * standard property and its members must be strictly reserved in all drivers.
 * However, it is permissible to change the label element of properties. You can
 * find numerous uses of the standard properties in the INDI library driver
 * repository. We use enum instead of constants to be better able to trace the
 * references.
 * 
 * @see http://indilib.org/develop/developer-manual/101-standard-properties.html
 * @author Richard van Nieuwenhoven
 */
public enum INDIStandardProperty {
    /**
     * property is no General Property.
     */
    NONE,
    /**
     * the switch property to connect the driver to the device.
     */
    CONNECTION(INDIStandardElement.CONNECT, INDIStandardElement.DISCONNECT),
    /**
     * the device text property of the connection port of to the device.
     */
    DEVICE_PORT(INDIStandardElement.PORT),
    /**
     * number property of the Local sidereal time HH:MM:SS.
     */
    TIME_LST(INDIStandardElement.LST),
    /**
     * text property of the UTC Time & Offset.
     */
    TIME_UTC(INDIStandardElement.UTC, INDIStandardElement.OFFSET),
    /**
     * number property of the Earth geodetic coordinate.
     */
    GEOGRAPHIC_COORD(INDIStandardElement.LAT, INDIStandardElement.LONG, INDIStandardElement.ELEV),
    /**
     * Equatorial astrometric epoch of date coordinate.
     */
    EQUATORIAL_EOD_COORD(INDIStandardElement.RA, INDIStandardElement.DEC),
    /**
     * Equatorial astrometric J2000 coordinate.
     */
    EQUATORIAL_COORD(INDIStandardElement.RA, INDIStandardElement.DEC),
    /**
     * Weather conditions.
     */
    ATMOSPHERE(INDIStandardElement.TEMPERATURE, INDIStandardElement.PRESSURE, INDIStandardElement.HUMIDITY),
    /**
     * upload settings for blobs.
     */
    UPLOAD_MODE(INDIStandardElement.UPLOAD_CLIENT, INDIStandardElement.UPLOAD_LOCAL, INDIStandardElement.UPLOAD_BOTH),
    /**
     * settings for the upload mode local.
     */
    UPLOAD_SETTINGS(INDIStandardElement.UPLOAD_DIR, INDIStandardElement.UPLOAD_PREFIX),
    /**
     * topocentric coordinate.
     */
    HORIZONTAL_COORD(INDIStandardElement.ALT, INDIStandardElement.AZ),
    /**
     * Action device takes when sent any *_COORD property.
     */
    ON_COORD_SET(INDIStandardElement.SLEW, INDIStandardElement.TRACK, INDIStandardElement.SYNC),
    /**
     * Move telescope north or south.
     */
    TELESCOPE_MOTION_NS(INDIStandardElement.MOTION_NORTH, INDIStandardElement.MOTION_SOUTH),
    /**
     * Move telescope west or east.
     */
    TELESCOPE_MOTION_WE(INDIStandardElement.MOTION_WEST, INDIStandardElement.MOTION_EAST),
    /**
     * Timed pulse guide in north/south direction.
     */
    TELESCOPE_TIMED_GUIDE_NS(INDIStandardElement.TIMED_GUIDE_N, INDIStandardElement.TIMED_GUIDE_S),
    /**
     * Timed pulse guide in west/east direction.
     */
    TELESCOPE_TIMED_GUIDE_WE(INDIStandardElement.TIMED_GUIDE_W, INDIStandardElement.TIMED_GUIDE_E),
    /**
     * Multiple switch slew rate. The driver can define as many switches as
     * desirable, but at minimum should implement the four switches below.
     */
    TELESCOPE_SLEW_RATE(INDIStandardElement.SLEW_GUIDE, INDIStandardElement.SLEW_CENTERING, INDIStandardElement.SLEW_FIND, INDIStandardElement.SLEW_MAX),
    /**
     * Park and unpark the telescope.
     */
    TELESCOPE_PARK(INDIStandardElement.PARK, INDIStandardElement.UNPARK),
    /**
     * Stop telescope rapidly, but gracefully.
     */
    TELESCOPE_ABORT_MOTION(INDIStandardElement.ABORT_MOTION),
    /**
     * tracking speed of the scope.
     */
    TELESCOPE_TRACK_RATE(INDIStandardElement.TRACK_SIDEREAL, INDIStandardElement.TRACK_SOLAR, INDIStandardElement.TRACK_LUNAR, INDIStandardElement.TRACK_CUSTOM),
    /**
     * information about the telescope.
     */
    TELESCOPE_INFO(INDIStandardElement.TELESCOPE_APERTURE, INDIStandardElement.TELESCOPE_FOCAL_LENGTH, INDIStandardElement.GUIDER_APERTURE, INDIStandardElement.GUIDER_FOCAL_LENGTH),
    /**
     * Expose the CCD chip for CCD_EXPOSURE_VALUE seconds.
     */
    CCDn_EXPOSURE(INDIStandardElement.CCD_EXPOSURE_VALUE),
    /**
     * Abort CCD exposure.
     */
    CCDn_ABORT_EXPOSURE(INDIStandardElement.ABORT),
    /**
     * CCD frame size.
     */
    CCDn_FRAME(INDIStandardElement.X, INDIStandardElement.Y, INDIStandardElement.WIDTH, INDIStandardElement.HEIGHT),
    /**
     * CCD chip temperature in degrees Celsius.
     */
    CCDn_TEMPERATURE(INDIStandardElement.CCD_TEMPERATURE_VALUE),
    /**
     * CCD Cooler control.
     */
    CCDn_COOLER(INDIStandardElement.COOLER_ON, INDIStandardElement.COOLER_OFF),
    /**
     * Percentage % of Cooler Power utilized.
     */
    CCDn_COOLER_POWER(INDIStandardElement.CCD_COOLER_VALUE),
    /**
     * frame exposure type.
     */
    CCDn_FRAME_TYPE(INDIStandardElement.FRAME_LIGHT, INDIStandardElement.FRAME_BIAS, INDIStandardElement.FRAME_DARK, INDIStandardElement.FRAME_FLAT),
    /**
     * ccd binning.
     */
    CCDn_BINNING(INDIStandardElement.HOR_BIN, INDIStandardElement.VER_BIN),
    /**
     * ccd frame compression.
     */
    CCDn_COMPRESSION(INDIStandardElement.CCD_COMPRESS, INDIStandardElement.CCD_RAW),
    /**
     * Reset CCD frame to default X,Y,W, and H settings. Set binning to 1x1.
     */
    CCDn_FRAME_RESET(INDIStandardElement.RESET),
    /**
     * CCD informations.
     */
    CCDn_INFO(INDIStandardElement.CCD_MAX_X, INDIStandardElement.CCD_MAX_Y, INDIStandardElement.CCD_PIXEL_SIZE, INDIStandardElement.CCD_PIXEL_SIZE_X, INDIStandardElement.CCD_PIXEL_SIZE_Y, INDIStandardElement.CCD_BITSPERPIXEL),
    /**
     * Color Filter Array information if the CCD produces a bayered image.
     * Debayering performed at client side.
     */
    CCDn_CFA(INDIStandardElement.CFA_OFFSET_X, INDIStandardElement.CFA_OFFSET_Y, INDIStandardElement.CFA_TYPE),
    /**
     * CCD1 for primary CCD, CCD2 for guider CCD.Binary fits data encoded in
     * base64. The CCD1.format is used to indicate the data type (e.g. ".fits").
     */
    CCDn,
    /**
     * The filter wheel's current slot number. Important: Filter numbers start
     * from 1 to N.
     */
    FILTER_SLOT(INDIStandardElement.FILTER_SLOT_VALUE),
    /**
     * The filter wheel's current slot name.
     */
    FILTER_NAME(INDIStandardElement.FILTER_NAME_VALUE),
    /**
     * Select focus speed from 0 to N where 0 maps to no motion, and N maps to
     * the fastest speed possible.
     */
    FOCUS_SPEED(INDIStandardElement.FOCUS_SPEED_VALUE),
    /**
     * focuser motion.
     */
    FOCUS_MOTION(INDIStandardElement.FOCUS_INWARD, INDIStandardElement.FOCUS_OUTWARD),
    /**
     * Focus in the direction of FOCUS_MOTION at rate FOCUS_SPEED for
     * FOCUS_TIMER_VALUE milliseconds.
     */
    FOCUS_TIMER(INDIStandardElement.FOCUS_TIMER_VALUE),
    /**
     * Relative position.
     */
    REL_FOCUS_POSITION(INDIStandardElement.FOCUS_RELATIVE_POSITION),
    /**
     * Absolute position.
     */
    ABS_FOCUS_POSITION(INDIStandardElement.FOCUS_ABSOLUTE_POSITION),

    /**
     * abort the focuser motion.
     */
    FOCUS_ABORT_MOTION(INDIStandardElement.ABORT),
    /**
     * Set dome speed in RPM.
     */
    DOME_SPEED(INDIStandardElement.DOME_SPEED_VALUE),
    /**
     * Move dome, looking down.
     */
    DOME_MOTION(INDIStandardElement.DOME_CW, INDIStandardElement.DOME_CCW),
    /**
     * Move the dome in the direction of DOME_MOTION at rate DOME_SPEED for
     * DOME_TIMER_VALUE milliseconds.
     */
    DOME_TIMER(INDIStandardElement.DOME_TIMER_VALUE),
    /**
     * Relative position.
     */
    REL_DOME_POSITION(INDIStandardElement.DOME_RELATIVE_POSITION),
    /**
     * Absolute position.
     */
    ABS_DOME_POSITION(INDIStandardElement.DOME_ABSOLUTE_POSITION),
    /**
     * Abort dome motion.
     */
    DOME_ABORT_MOTION(INDIStandardElement.ABORT),
    /**
     * dome shutter controll.
     */
    DOME_SHUTTER(INDIStandardElement.SHUTTER_OPEN, INDIStandardElement.SHUTTER_CLOSE),
    /**
     * Dome go to position.
     */
    DOME_GOTO(INDIStandardElement.DOME_HOME, INDIStandardElement.DOME_PARK),
    /**
     * Dome position parameters.
     */
    DOME_PARAMS(INDIStandardElement.HOME_POSITION, INDIStandardElement.PARK_POSITION, INDIStandardElement.AUTOSYNC_THRESHOLD),
    /**
     * (Dis/En)able dome slaving.
     */
    DOME_AUTOSYNC(INDIStandardElement.DOME_AUTOSYNC_ENABLE, INDIStandardElement.DOME_AUTOSYNC_DISABLE),
    /**
     * Dome mesurements / dimentions.
     */
    DOME_MEASUREMENTS(INDIStandardElement.DM_DOME_RADIUS, INDIStandardElement.DOME_SHUTTER_WIDTH, INDIStandardElement.DM_NORTH_DISPLACEMENT, INDIStandardElement.DM_EAST_DISPLACEMENT, INDIStandardElement.DM_UP_DISPLACEMENT, INDIStandardElement.DM_OTA_OFFSET),
    /**
     * text property of the Name of active devices. If defined, at least one
     * member below must be defined in the vector.ACTIVE_DEVICES is used to aid
     * clients in automatically providing the users with a list of active
     * devices (i.e. CONNECTION is ON) whenever needed. For example, a CCD
     * driver may define ACTIVE_DEVICES property with one member:
     * ACTIVE_TELESCOPE. Suppose that the client is also running LX200 Basic
     * driver to control the telescope. If the telescope is connected, the
     * client may automatically fill the ACTIVE_TELESCOPE field or provide a
     * drop-down list of active telescopes to select from. Once set, the CCD
     * driver may record, for example, the telescope name, RA, DEC, among other
     * metadata once it captures an image. Therefore, ACTIVE_DEVICES is
     * primarily used to link together different classes of devices to exchange
     * information if required.
     */
    ACTIVE_DEVICES(INDIStandardElement.ACTIVE_TELESCOPE, INDIStandardElement.ACTIVE_CCD, INDIStandardElement.ACTIVE_FILTER, INDIStandardElement.ACTIVE_FOCUSER, INDIStandardElement.ACTIVE_DOME, INDIStandardElement.ACTIVE_LOCATION, INDIStandardElement.ACTIVE_WEATHER, INDIStandardElement.ACTIVE_TIME, INDIStandardElement.ACTIVE_SWITCH),
    /**
     * generic SWICH property.
     */
    SWITCH_MODULE(INDIStandardElement.SWITCHn);

    /**
     * standard elements of this property.
     */
    private final INDIStandardElement[] elements;

    /**
     * constructor.
     * 
     * @param elements
     *            standard elements of the property.
     */
    INDIStandardProperty(INDIStandardElement... elements) {
        this.elements = elements;
    }

    /**
     * @return the array of elements this property generally has.
     */
    public final INDIStandardElement[] elements() {
        return this.elements;
    }
}
