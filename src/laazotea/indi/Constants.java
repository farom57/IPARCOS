/*
 *  This file is part of INDI for Java.
 * 
 *  INDI for Java is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 * 
 *  INDI for Java is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with INDI for Java.  If not, see 
 *  <http://www.gnu.org/licenses/>.
 */
package laazotea.indi;

/**
 * A class representing a some Constants and convenience functions to deal with
 * them used in several parts of the INDI for Java libraries.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.31, April 10, 2012
 */
public class Constants {

  /**
   * Possible Light State Values.
   */
  public enum LightStates {

    /**
     * Idle State
     */
    IDLE,
    /**
     * Ok State
     */
    OK,
    /**
     * Busy State
     */
    BUSY,
    /**
     * Alert State
     */
    ALERT
  };

  /**
   * Parses a Light State
   *
   * @param state a string representation of the Light State to be parsed
   * ("Alert" or "Busy" or "Ok" or "Idle").
   * @return The parsed Light State
   * @throws IllegalArgumentException if the
   * <code>state</code> is not a valid one.
   */
  public static LightStates parseLightState(String state) throws IllegalArgumentException {
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
   * @param state The string to check
   * @return
   * <code>true</code> if it corresponds to a valid LightState.
   * <code>false</code> otherwise.
   */
  public static boolean isValidLightState(String state) {
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
   * @param lightState The Light State
   * @return A String representation of the Light State
   */
  public static String getLightStateAsString(LightStates lightState) {
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
     * Off Status
     */
    OFF,
    /**
     * On Status
     */
    ON
  };

  /**
   * Parses a Switch Status.
   *
   * @param status a string representation of the Switch Status to be parsed
   * ("Off" or "On").
   * @return The parsed Switch Status
   * @throws IllegalArgumentException if the
   * <code>status</code> is not a valid one.
   */
  public static SwitchStatus parseSwitchStatus(String status) throws IllegalArgumentException {
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
   * @param status The string to check
   * @return
   * <code>true</code> if it corresponds to a valid SwitchStatus.
   * <code>false</code> otherwise.
   */
  public static boolean isValidSwitchStatus(String status) {
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
   * @param switchStatus The Switch Status
   * @return A String representation of the Switch Status
   */
  public static String getSwitchStatusAsString(SwitchStatus switchStatus) {
    if (switchStatus == SwitchStatus.ON) {
      return "On";
    } else if (switchStatus == SwitchStatus.OFF) {
      return "Off";
    }

    return "";
  }

  /**
   * Possible perimssions for a INDI Property
   */
  public enum PropertyPermissions {

    /**
     * Read Only
     */
    RO,
    /**
     * Read Write
     */
    RW,
    /**
     * Write Only
     */
    WO
  };

  /**
   * Parses a Property Permission
   *
   * @param permission a string representation of the Property Permission to be
   * parsed ("ro" or "rw" or "wo").
   * @return The parsed Property Permission
   * @throws IllegalArgumentException if the
   * <code>permission</code> is not a valid one.
   */
  public static PropertyPermissions parsePropertyPermission(String permission) throws IllegalArgumentException {
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
   * @param permission The string to check
   * @return
   * <code>true</code> if it corresponds to a valid PropertyPermission.
   * <code>false</code> otherwise.
   */
  public static boolean isValidPropertyPermission(String permission) {
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
   * @param permission The Property Permission
   * @return A String representation of the Property Permission.
   */
  public static String getPropertyPermissionAsString(PropertyPermissions permission) {
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
   * Possible States for a INDI Property
   */
  public enum PropertyStates {

    /**
     * Idle
     */
    IDLE,
    /**
     * Ok
     */
    OK,
    /**
     * Busy
     */
    BUSY,
    /**
     * Alert
     */
    ALERT
  };

  /**
   * Parses a Property State.
   *
   * @param state a string representation of the Property State to be parsed
   * ("Alert" or "Busy" or "Ok" or "Idle").
   * @return The parsed Property State
   * @throws IllegalArgumentException if the
   * <code>state</code> is not a valid one.
   */
  public static PropertyStates parsePropertyState(String state) throws IllegalArgumentException {
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
   * @param state The string to check
   * @return
   * <code>true</code> if it corresponds to a valid PropertyState.
   * <code>false</code> otherwise.
   */
  public static boolean isValidPropertyState(String state) {
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
   * @param propertyState The Property State
   * @return A String representation of the Property State
   */
  public static String getPropertyStateAsString(PropertyStates propertyState) {
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
   * Parses a Switch Rule
   *
   * @param rule a string representation of the Switch Rule to be parsed
   * ("OneOfMany" or "AtMostOne" or "AnyOfMany").
   * @return The Switch Rule
   * @throws IllegalArgumentException if the
   * <code>rule</code> is not a valid one.
   */
  public static SwitchRules parseSwitchRule(String rule) throws IllegalArgumentException {
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
   * @param rule The string to check
   * @return
   * <code>true</code> if it corresponds to a valid SwitchRule.
   * <code>false</code> otherwise.
   */
  public static boolean isValidSwitchRule(String rule) {
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
   * @param rule The Switch Rule
   * @return A String representation of the Switch Rule.
   */
  public static String getSwitchRuleAsString(SwitchRules rule) {
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
     * Never (no BLOB values are sent)
     */
    NEVER,
    /**
     * Also (every value is sent)
     */
    ALSO,
    /**
     * Only (just the BLOB values are sent)
     */
    ONLY
  };

  /**
   * Parses a BLOB Enable
   *
   * @param BLOBEnable a string representation of the BLOB Enable to be parsed
   * ("Never" or "Also" or "Only").
   * @return The BLOB Enable
   * @throws IllegalArgumentException if the
   * <code>BLOBEnable</code> is not a valid one.
   */
  public static BLOBEnables parseBLOBEnable(String BLOBEnable) throws IllegalArgumentException {
    if (BLOBEnable.compareTo("Never") == 0) {
      return BLOBEnables.NEVER;
    } else if (BLOBEnable.compareTo("Also") == 0) {
      return BLOBEnables.ALSO;
    } else if (BLOBEnable.compareTo("Only") == 0) {
      return BLOBEnables.ONLY;
    }

    throw new IllegalArgumentException("Invalid BLOBEnable String: '" + BLOBEnable + "'");
  }

  /**
   * Checks if a string corresponds to a valid BLOBEnable.
   *
   * @param BLOBEnable The string to check
   * @return
   * <code>true</code> if it corresponds to a valid BLOBEnable.
   * <code>false</code> otherwise.
   */
  public static boolean isValidBLOBEnable(String BLOBEnable) {
    try {
      parseBLOBEnable(BLOBEnable);
    } catch (IllegalArgumentException e) {
      return false;
    }

    return true;
  }

  /**
   * Gets a String representation of the BLOB Enable.
   *
   * @param BLOBEnable The BLOBEnable
   * @return A String representation of the BLOB Enable.
   */
  public static String getBLOBEnableAsString(BLOBEnables BLOBEnable) {
    if (BLOBEnable == BLOBEnables.NEVER) {
      return "Never";
    } else if (BLOBEnable == BLOBEnables.ALSO) {
      return "Also";
    } else if (BLOBEnable == BLOBEnables.ONLY) {
      return "Only";
    }

    return "";
  }
}
