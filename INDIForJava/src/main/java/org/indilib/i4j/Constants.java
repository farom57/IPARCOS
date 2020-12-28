package org.indilib.i4j;

/*
 * #%L
 * INDI for Java Base Library
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
 * A class representing a some Constants and convenience functions to deal with
 * them used in several parts of the INDI for Java libraries.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public final class Constants {

    /**
     * The default port for the INDI protocol.
     */
    public static final int INDI_DEFAULT_PORT = 7624;

    /**
     * Default size for buffers.
     */
    public static final int BUFFER_SIZE = 1000000;

    /**
     * The number of milliseconds used in the dinamic wait for property methods.
     */
    public static final int WAITING_INTERVAL = 500;

    /**
     * Milliseconds in a second.
     */
    public static final int MILLISECONDS_IN_A_SECOND = 1000;

    /**
     * A private constructor to avoid instantiating this utility class.
     */
    private Constants() {
    }

    /**
     * Possible Light State Values.
     */
    public enum LightStates {

        /**
         * Idle State.
         */
        IDLE,
        /**
         * Ok State.
         */
        OK,
        /**
         * Busy State.
         */
        BUSY,
        /**
         * Alert State.
         */
        ALERT
    };

    /**
     * Parses a Light State.
     * 
     * @param state
     *            a string representation of the Light State to be parsed
     *            ("Alert" or "Busy" or "Ok" or "Idle").
     * @return The parsed Light State
     */
    public static LightStates parseLightState(final String state) {
        if (state.compareTo("Alert") == 0) {
            return LightStates.ALERT;
        } else if (state.compareTo("Busy") == 0) {
            return LightStates.BUSY;
        } else if (state.compareTo("Ok") == 0) {
            return LightStates.OK;
        } else if (state.compareTo("Idle") == 0) {
            return LightStates.IDLE;
        }

        throw new IllegalArgumentException("Invalid LightState String: '" + state + "'");
    }

    /**
     * Checks if a string corresponds to a valid LightState.
     * 
     * @param state
     *            The string to check
     * @return <code>true</code> if it corresponds to a valid LightState.
     *         <code>false</code> otherwise.
     */
    public static boolean isValidLightState(final String state) {
        try {
            parseLightState(state);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    /**
     * Gets a String representation of the Light State.
     * 
     * @param lightState
     *            The Light State
     * @return A String representation of the Light State
     */
    public static String getLightStateAsString(final LightStates lightState) {
        if (lightState == LightStates.ALERT) {
            return "Alert";
        } else if (lightState == LightStates.BUSY) {
            return "Busy";
        } else if (lightState == LightStates.OK) {
            return "Ok";
        } else if (lightState == LightStates.IDLE) {
            return "Idle";
        }

        return "";
    }

    /**
     * Possible Switch Status Values.
     */
    public enum SwitchStatus {

        /**
         * Off Status.
         */
        OFF,
        /**
         * On Status.
         */
        ON
    };

    /**
     * Parses a Switch Status.
     * 
     * @param status
     *            a string representation of the Switch Status to be parsed
     *            ("Off" or "On").
     * @return The parsed Switch Status
     */
    public static SwitchStatus parseSwitchStatus(final String status) {
        if (status.compareTo("Off") == 0) {
            return SwitchStatus.OFF;
        } else if (status.compareTo("On") == 0) {
            return SwitchStatus.ON;
        }

        throw new IllegalArgumentException("Invalid SwitchStatus String: '" + status + "'");
    }

    /**
     * Checks if a string corresponds to a valid SwitchStatus.
     * 
     * @param status
     *            The string to check
     * @return <code>true</code> if it corresponds to a valid SwitchStatus.
     *         <code>false</code> otherwise.
     */
    public static boolean isValidSwitchStatus(final String status) {
        try {
            parseSwitchStatus(status);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    /**
     * Gets a String representation of the Switch Status.
     * 
     * @param switchStatus
     *            The Switch Status
     * @return A String representation of the Switch Status
     */
    public static String getSwitchStatusAsString(final SwitchStatus switchStatus) {
        if (switchStatus == SwitchStatus.ON) {
            return "On";
        } else if (switchStatus == SwitchStatus.OFF) {
            return "Off";
        }

        return "";
    }

    /**
     * Possible perimssions for a INDI Property.
     */
    public enum PropertyPermissions {

        /**
         * Read Only.
         */
        RO,
        /**
         * Read Write.
         */
        RW,
        /**
         * Write Only.
         */
        WO
    };

    /**
     * Parses a Property Permission.
     * 
     * @param permission
     *            a string representation of the Property Permission to be
     *            parsed ("ro" or "rw" or "wo").
     * @return The parsed Property Permission
     */
    public static PropertyPermissions parsePropertyPermission(final String permission) {
        if (permission.compareTo("ro") == 0) {
            return PropertyPermissions.RO;
        } else if (permission.compareTo("rw") == 0) {
            return PropertyPermissions.RW;
        } else if (permission.compareTo("wo") == 0) {
            return PropertyPermissions.WO;
        }

        throw new IllegalArgumentException("Invalid PropertyPermissions String: '" + permission + "'");
    }

    /**
     * Checks if a string corresponds to a valid PropertyPermission.
     * 
     * @param permission
     *            The string to check
     * @return <code>true</code> if it corresponds to a valid
     *         PropertyPermission. <code>false</code> otherwise.
     */
    public static boolean isValidPropertyPermission(final String permission) {
        try {
            parsePropertyPermission(permission);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    /**
     * Gets a String representation of the Property Permission.
     * 
     * @param permission
     *            The Property Permission
     * @return A String representation of the Property Permission.
     */
    public static String getPropertyPermissionAsString(final PropertyPermissions permission) {
        if (permission == PropertyPermissions.RO) {
            return "ro";
        } else if (permission == PropertyPermissions.RW) {
            return "rw";
        } else if (permission == PropertyPermissions.WO) {
            return "wo";
        }

        return "";
    }

    /**
     * Possible States for a INDI Property.
     */
    public enum PropertyStates {

        /**
         * Idle.
         */
        IDLE,
        /**
         * Ok.
         */
        OK,
        /**
         * Busy.
         */
        BUSY,
        /**
         * Alert.
         */
        ALERT
    };

    /**
     * Parses a Property State.
     * 
     * @param state
     *            a string representation of the Property State to be parsed
     *            ("Alert" or "Busy" or "Ok" or "Idle").
     * @return The parsed Property State
     */
    public static PropertyStates parsePropertyState(final String state) {
        if (state.compareTo("Alert") == 0) {
            return PropertyStates.ALERT;
        } else if (state.compareTo("Busy") == 0) {
            return PropertyStates.BUSY;
        } else if (state.compareTo("Ok") == 0) {
            return PropertyStates.OK;
        } else if (state.compareTo("Idle") == 0) {
            return PropertyStates.IDLE;
        }

        throw new IllegalArgumentException("Invalid PropertyState String: '" + state + "'");
    }

    /**
     * Checks if a string corresponds to a valid PropertyState.
     * 
     * @param state
     *            The string to check
     * @return <code>true</code> if it corresponds to a valid PropertyState.
     *         <code>false</code> otherwise.
     */
    public static boolean isValidPropertyState(final String state) {
        try {
            parsePropertyState(state);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    /**
     * Gets a String representation of the Property State.
     * 
     * @param propertyState
     *            The Property State
     * @return A String representation of the Property State
     */
    public static String getPropertyStateAsString(final PropertyStates propertyState) {
        if (propertyState == PropertyStates.ALERT) {
            return "Alert";
        } else if (propertyState == PropertyStates.BUSY) {
            return "Busy";
        } else if (propertyState == PropertyStates.OK) {
            return "Ok";
        } else if (propertyState == PropertyStates.IDLE) {
            return "Idle";
        }

        return "";
    }

    /**
     * Possible selection rules for a Switch Property.
     */
    public enum SwitchRules {

        /**
         * One of many (one and just one).
         */
        ONE_OF_MANY,
        /**
         * At most one (zero or one).
         */
        AT_MOST_ONE,
        /**
         * Any of many (zero or more).
         */
        ANY_OF_MANY
    };

    /**
     * Parses a Switch Rule.
     * 
     * @param rule
     *            a string representation of the Switch Rule to be parsed
     *            ("OneOfMany" or "AtMostOne" or "AnyOfMany").
     * @return The Switch Rule
     */
    public static SwitchRules parseSwitchRule(final String rule) {
        if (rule.compareTo("OneOfMany") == 0) {
            return SwitchRules.ONE_OF_MANY;
        } else if (rule.compareTo("AtMostOne") == 0) {
            return SwitchRules.AT_MOST_ONE;
        } else if (rule.compareTo("AnyOfMany") == 0) {
            return SwitchRules.ANY_OF_MANY;
        }

        throw new IllegalArgumentException("Invalid SwitchRules String: '" + rule + "'");
    }

    /**
     * Checks if a string corresponds to a valid SwitchRule.
     * 
     * @param rule
     *            The string to check
     * @return <code>true</code> if it corresponds to a valid SwitchRule.
     *         <code>false</code> otherwise.
     */
    public static boolean isValidSwitchRule(final String rule) {
        try {
            parseSwitchRule(rule);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    /**
     * Gets a String representation of the Switch Rule.
     * 
     * @param rule
     *            The Switch Rule
     * @return A String representation of the Switch Rule.
     */
    public static String getSwitchRuleAsString(final SwitchRules rule) {
        if (rule == SwitchRules.ONE_OF_MANY) {
            return "OneOfMany";
        } else if (rule == SwitchRules.AT_MOST_ONE) {
            return "AtMostOne";
        } else if (rule == SwitchRules.ANY_OF_MANY) {
            return "AnyOfMany";
        }

        return "";
    }

    /**
     * Possible selection rules for a Switch Property.
     */
    public enum BLOBEnables {

        /**
         * Never (no BLOB values are sent).
         */
        NEVER,
        /**
         * Also (every value is sent).
         */
        ALSO,
        /**
         * Only (just the BLOB values are sent).
         */
        ONLY
    };

    /**
     * Parses a BLOB Enable.
     * 
     * @param blobEnable
     *            a string representation of the BLOB Enable to be parsed
     *            ("Never" or "Also" or "Only").
     * @return The BLOB Enable
     */
    public static BLOBEnables parseBLOBEnable(final String blobEnable) {
        if (blobEnable.compareTo("Never") == 0) {
            return BLOBEnables.NEVER;
        } else if (blobEnable.compareTo("Also") == 0) {
            return BLOBEnables.ALSO;
        } else if (blobEnable.compareTo("Only") == 0) {
            return BLOBEnables.ONLY;
        }

        throw new IllegalArgumentException("Invalid BLOBEnable String: '" + blobEnable + "'");
    }

    /**
     * Checks if a string corresponds to a valid blobEnable.
     * 
     * @param blobEnable
     *            The string to check
     * @return <code>true</code> if it corresponds to a valid blobEnable.
     *         <code>false</code> otherwise.
     */
    public static boolean isValidBLOBEnable(final String blobEnable) {
        try {
            parseBLOBEnable(blobEnable);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    /**
     * Gets a String representation of the BLOB Enable.
     * 
     * @param blobEnable
     *            The blobEnable
     * @return A String representation of the BLOB Enable.
     */
    public static String getBLOBEnableAsString(final BLOBEnables blobEnable) {
        if (blobEnable == BLOBEnables.NEVER) {
            return "Never";
        } else if (blobEnable == BLOBEnables.ALSO) {
            return "Also";
        } else if (blobEnable == BLOBEnables.ONLY) {
            return "Only";
        }

        return "";
    }
}
