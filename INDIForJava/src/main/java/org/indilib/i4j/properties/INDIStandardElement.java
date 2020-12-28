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
 * statndard element names.
 * 
 * @author Richard van Nieuwenhoven
 */
public enum INDIStandardElement {
    /**
     * Abort CCD exposure/any motion.
     */
    ABORT,
    /**
     * Stop telescope rapidly, but gracefully.
     */
    ABORT_MOTION,
    /**
     * text element of the active ccd.
     */
    ACTIVE_CCD,
    /**
     * text element of the active dome.
     */
    ACTIVE_DOME,
    /**
     * text element of the active filter.
     */
    ACTIVE_FILTER,
    /**
     * text element of the active focuser.
     */
    ACTIVE_FOCUSER,
    /**
     * text element of the active location provider.
     */
    ACTIVE_LOCATION,
    /**
     * text element of the active switch provider. some drivers need a switch do
     * do something, this property allows you to select the driver to provide
     * the switch.
     */
    ACTIVE_SWITCH,
    /**
     * text element of the active telescope.
     */
    ACTIVE_TELESCOPE,
    /**
     * text element of the current time provider.
     */
    ACTIVE_TIME,
    /**
     * text element of the active weather provider.
     */
    ACTIVE_WEATHER,
    /**
     * Altitude, degrees above horizon.
     */
    ALT,
    /**
     * If dome is slaved, AUTOSYNC_THRESHOLD is the number of acceptable azimuth
     * degrees error between reported and requested dome position. Once the
     * difference between target and current dome positions exceed this value,
     * the dome shall be commanded to move to the target position.
     */
    AUTOSYNC_THRESHOLD,
    /**
     * Azimuth, degrees E of N.
     */
    AZ,
    /**
     * Bits per pixel.
     */
    CCD_BITSPERPIXEL,
    /**
     * Compress CCD frame.
     */
    CCD_COMPRESS,
    /**
     * Percentage % of Cooler Power utilized.
     */
    CCD_COOLER_VALUE,
    /**
     * Expose the CCD chip for CCD_EXPOSURE_VALUE seconds.
     */
    CCD_EXPOSURE_VALUE,
    /**
     * Resolution x.
     */
    CCD_MAX_X,
    /**
     * Resolution y.
     */
    CCD_MAX_Y,
    /**
     * CCD pixel size in microns.
     */
    CCD_PIXEL_SIZE,
    /**
     * Pixel size X, microns.
     */
    CCD_PIXEL_SIZE_X,
    /**
     * Pixel size Y, microns.
     */
    CCD_PIXEL_SIZE_Y,
    /**
     * Send raw CCD frame.
     */
    CCD_RAW,
    /**
     * CCD chip temperature in degrees Celsius.
     */
    CCD_TEMPERATURE_VALUE,
    /**
     * CFA X Offset.
     */
    CFA_OFFSET_X,
    /**
     * CFA Y Offset.
     */
    CFA_OFFSET_Y,
    /**
     * CFA Filter type (e.g. RGGB).
     */
    CFA_TYPE,
    /**
     * switch element to establish connection to device.
     */
    CONNECT,
    /**
     * Turn cooler off.
     */
    COOLER_OFF,
    /**
     * Turn cooler on.
     */
    COOLER_ON,
    /**
     * This element represents the declination of the pointing direction.
     */
    DEC,
    /**
     * switch element to establish Disconnect the device.
     */
    DISCONNECT,
    /**
     * Dome radius (m).
     */
    DM_DOME_RADIUS,
    /**
     * Displacement to the east of the mount center (m).
     */
    DM_EAST_DISPLACEMENT,
    /**
     * Displacement to the north of the mount center (m).
     */
    DM_NORTH_DISPLACEMENT,
    /**
     * Distance from the optical axis to the mount center (m).
     */
    DM_OTA_OFFSET,
    /**
     * UP displacement of the mount center (m).
     */
    DM_UP_DISPLACEMENT,
    /**
     * Move dome to DOME_ABSOLUTE_POSITION absolute azimuth angle in degrees.
     */
    DOME_ABSOLUTE_POSITION,
    /**
     * Disable dome slaving.
     */
    DOME_AUTOSYNC_DISABLE,
    /**
     * Enable dome slaving.
     */
    DOME_AUTOSYNC_ENABLE,
    /**
     * Move dome counter clockwise, looking down.
     */
    DOME_CCW,
    /**
     * Move dome Clockwise, looking down.
     */
    DOME_CW,
    /**
     * Go to home position.
     */
    DOME_HOME,
    /**
     * Go to park position.
     */
    DOME_PARK,
    /**
     * Move DOME_RELATIVE_POSITION degrees azimuth in the direction of
     * DOME_MOTION.
     */
    DOME_RELATIVE_POSITION,
    /**
     * Dome shutter width (m).
     */
    DOME_SHUTTER_WIDTH,
    /**
     * Set dome speed in RPM.
     */
    DOME_SPEED_VALUE,
    /**
     * Move the dome in the direction of DOME_MOTION at rate DOME_SPEED for
     * DOME_TIMER_VALUE milliseconds.
     */
    DOME_TIMER_VALUE,
    /**
     * number element of the Site elevation, meters.
     */
    ELEV,
    /**
     * The filter wheel's current slot name.
     */
    FILTER_NAME_VALUE,
    /**
     * The filter wheel's current slot number. Important: Filter numbers start
     * from 1 to N.
     */
    FILTER_SLOT_VALUE,
    /**
     * Absolute position Ticks.
     */
    FOCUS_ABSOLUTE_POSITION,
    /**
     * Focus inward.
     */
    FOCUS_INWARD,
    /**
     * Focus outward.
     */
    FOCUS_OUTWARD,
    /**
     * Move # of ticks in FOCUS_MOTION direction.
     */
    FOCUS_RELATIVE_POSITION,
    /**
     * Set focuser speed to SPEED.
     */
    FOCUS_SPEED_VALUE,
    /**
     * Focus in the direction of FOCUS_MOTION at rate FOCUS_SPEED for
     * FOCUS_TIMER_VALUE milliseconds.
     */
    FOCUS_TIMER_VALUE,
    /**
     * Take a bias frame exposure.
     */
    FRAME_BIAS,
    /**
     * Take a dark frame exposure.
     */
    FRAME_DARK,
    /**
     * Take a flat field frame exposure.
     */
    FRAME_FLAT,
    /**
     * Take a light frame exposure.
     */
    FRAME_LIGHT,
    /**
     * Guide telescope aperture, mm.
     */
    GUIDER_APERTURE,
    /**
     * Guider telescope focal length, mm.
     */
    GUIDER_FOCAL_LENGTH,
    /**
     * Frame height in pixels.
     */
    HEIGHT,
    /**
     * Dome home position in absolute degrees azimuth.
     */
    HOME_POSITION,
    /**
     * Horizontal binning.
     */
    HOR_BIN,
    /**
     * number element of the humidity Percentage %.
     */
    HUMIDITY,
    /**
     * number element of the Site latitude (-90 to +90), degrees +N.
     */
    LAT,
    /**
     * number element of the Site longitude (0 to 360), degrees +E.
     */
    LONG,
    /**
     * number property of the Weather conditions.
     */
    /**
     * number element of the Local sidereal time HH:MM:SS.
     */
    LST,
    /**
     * Move the telescope toward East.
     */
    MOTION_EAST,
    /**
     * Move the telescope toward North.
     */
    MOTION_NORTH,
    /**
     * Move the telescope toward South.
     */
    MOTION_SOUTH,
    /**
     * Move the telescope toward West.
     */
    MOTION_WEST,
    /**
     * property is no General Element.
     */
    NONE,
    /**
     * text element of the UTC offset, in hours +E.
     */
    OFFSET,
    /**
     * Park the telescope to HOME position.
     */
    PARK,
    /**
     * Dome parking position in absolute degrees azimuth.
     */
    PARK_POSITION,
    /**
     * text element of the connection port of to the device.
     */
    PORT,
    /**
     * number element of the pressure in hPa.
     */
    PRESSURE,
    /**
     * This element represents the right ascension of the pointing direction.
     */
    RA,
    /**
     * Reset CCD frame to default X,Y,W, and H settings. Set binning to 1x1.
     */
    RESET,
    /**
     * Close dome shutter.
     */
    SHUTTER_CLOSE,
    /**
     * Open dome shutter.
     */
    SHUTTER_OPEN,
    /**
     * Slew to a coordinate and stop.
     */
    SLEW,
    /**
     * Slow speed.
     */
    SLEW_CENTERING,
    /**
     * Medium speed.
     */
    SLEW_FIND,
    /**
     * 0.5x to 1.0x sidereal rate or slowest possible speed.
     */
    SLEW_GUIDE,
    /**
     * Maximum speed.
     */
    SLEW_MAX,
    /**
     * generic SWICH element.
     */
    SWITCHn,
    /**
     * Accept current coordinate as correct.
     */
    SYNC,
    /**
     * Telescope aperture, mm.
     */
    TELESCOPE_APERTURE,
    /**
     * Telescope focal length, mm.
     */
    TELESCOPE_FOCAL_LENGTH,
    /**
     * number element of the temperature in Kelvin.
     */
    TEMPERATURE,
    /**
     * Guide the scope east for TIMED_GUIDE_E milliseconds.
     */
    TIMED_GUIDE_E,
    /**
     * Guide the scope north for TIMED_GUIDE_N milliseconds.
     */
    TIMED_GUIDE_N,
    /**
     * Guide the scope south for TIMED_GUIDE_S milliseconds.
     */
    TIMED_GUIDE_S,
    /**
     * Guide the scope west for TIMED_GUIDE_W milliseconds.
     */
    TIMED_GUIDE_W,
    /**
     * Slew to a coordinate and track.
     */
    TRACK,
    /**
     * Custom track rate. This element is optional.
     */

    TRACK_CUSTOM,
    /**
     * Track at lunar rate.
     */
    TRACK_LUNAR,
    /**
     * Track at sidereal rate.
     */
    TRACK_SIDEREAL,
    /**
     * Track at solar rate.
     */
    TRACK_SOLAR,
    /**
     * Unpark the telescope.
     */
    UNPARK,
    /**
     * switch element of the Send blob to client and save it locally as well.
     */
    UPLOAD_BOTH,
    /**
     * switch element of the Send BLOB to client.
     */
    UPLOAD_CLIENT,
    /**
     * text element of the Upload directory if the BLOB is saved locally.
     */
    UPLOAD_DIR,
    /**
     * switch element of the Save BLOB locally.
     */
    UPLOAD_LOCAL,
    /**
     * text element of the Prefix used when saving filename.
     */
    UPLOAD_PREFIX,
    /**
     * text element of the UTC time in ISO 8601 format.
     */
    UTC,
    /**
     * Vertical binning.
     */
    VER_BIN,
    /**
     * Frame width in pixels.
     */
    WIDTH,
    /**
     * Left-most pixel position.
     */
    X,
    /**
     * Top-most pixel position.
     */
    Y,

}
