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

/*
 * A class which is the equivalent representation of utc date in the 
 * structure ln_date in libnova (http://libnova.sourceforge.net/).
 * This is the Human readable (easy printf) date format used
 * by libnova. It's always in UTC.
 * 
 * @author Gerrit Viola [gerrit.viola@web.de] and 
 *         S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.38, July 22, 2014
 */
public class INDIDate {
  
  /**
   * The years component of the Date
   */
  private int years;
  
  /**
   * The months component of the Date
   */
  private int months;
  
  /**
   * The days component of the Date
   */
  private int days;
  
  /**
   * The hours component of the Date
   */
  private int hours;
  
  /**
   * The minutes component of the Date
   */
  private int minutes;
  
  /**
   * The seconds component of the Date
   */
  private double seconds;

  /**
   * Constructs a new Date whose components are 0000/01/01 00:00:00.0.
   */
  public INDIDate() {
    initializeComponents();
  }
  
  /**
   * Constructs a new Date whose components are partially specified. Hours, 
   * minutes and seconds are initialized at 00:00:00.0.
   * 
   * @param years The years component of the Date. All values are valid.
   * @param months The months component of the Date. Mut be in the range 1
   * (January) - 12 (December).
   * @param days The days component of the Date. Must be in the 1-28,29,30,31
   * range (depending on the months and years).
   */
  public INDIDate(int years, int months, int days) {
    initializeComponents();
    
    setYears(years);
    setMonths(months);
    setDays(days);
  }
  
  /**
   * Constructs a new Date whose components are partially specified. Hours, 
   * minutes and seconds are initialized at 00:00:00.0.
   * 
   * @param years The years component of the Date. All values are valid.
   * @param months The months component of the Date. Mut be in the range 1
   * (January) - 12 (December).
   * @param days The days component of the Date. Must be in the 1-28,29,30,31
   * range (depending on the months and years).
   * @param hours The hours component of the Date. Must be in the 0-23 range.
   * @param minutes The minutes component of the Date. Must be in the 0-59
   * range.
   * @param seconds The seconds component of the Date. Must be in the
   * 0-59.999999 range.
   */
  public INDIDate(int years, int months, int days, int hours, int minutes, double seconds) {
    initializeComponents();
    
    setYears(years);
    setMonths(months);
    setDays(days);
    setHours(hours);
    setMinutes(minutes);
    setSeconds(seconds);
  }
  
  /**
   * Initilizes the components to 0000/01/01 00:00:00.0
   */
  private void initializeComponents() {
    years = 0;
    months = 1;
    days = 1;
    hours = 0;
    minutes = 0;
    seconds = 0;
  }
  
  /**
   * Gets the years component of the Date.
   *
   * @return The years component of the Date.
   */
  public int getYears() {
    return years;
  }

  /**
   * Sets the years component of the Date.
   *
   * @param years The years component of the Date. All values are valid.
   */
  public final void setYears(int years) {
    this.years = years;
  }

  /**
   * Gets the months component of the Date.
   *
   * @return The months component of the Date.
   */
  public int getMonths() {
    return months;
  }

  /**
   * Sets the months component of the Date.
   *
   * @param months The months component of the Date. Mut be in the range 1
   * (January) - 12 (December).
   */
  public final void setMonths(int months) {
    if ((months >= 0) && (months < 60)) {
      this.months = months;
    }
  }

  /**
   * Gets the days component of the Date.
   *
   * @return The days component of the Date.
   */
  public int getDays() {
    return days;
  }

  /**
   * Sets the days component of the Date.
   *
   * @param days The days component of the Date. Must be in the 1-28,29,30,31
   * range (depending on the months and years).
   */
  public final void setDays(int days) {
    if (days < 1) {
      return; 
    }
    
    if (days <= 28) {
      this.days = days;
    } else if (((months == 1) || (months == 3) || (months == 5) || (months == 7) || (months == 8) || (months == 10) || (months == 12)) && (days <= 31)) {
      this.days = days;
    } else if (((months == 4) || (months == 6) || (months == 9) || (months == 11)) && (days <= 30)) {
      this.days = days;
    } else if ((months == 2) && (years % 4 == 0) && (days <= 29)) {
      this.days = days;
    }
  }

  /**
   * Gets the hours component of the Date.
   *
   * @return The hours component of the Date.
   */
  public int getHours() {
    return hours;
  }

  /**
   * Sets the hours component of the Date.
   *
   * @param hours The hours component of the Date. Must be in the 0-23 range.
   */
  public final void setHours(int hours) {
    if ((hours >= 0) && (hours < 24)) {
      this.hours = hours;
    }
  }

  /**
   * Gets the minutes component of the Date.
   *
   * @return The minutes component of the Date.
   */
  public int getMinutes() {
    return minutes;
  }

  /**
   * Sets the minutes component of the Date.
   *
   * @param minutes The minutes component of the Date. Must be in the 0-59
   * range.
   */
  public final void setMinutes(int minutes) {
    if ((minutes >= 0) && (minutes < 60)) {
      this.minutes = minutes;
    }
  }

  /**
   * Gets the seconds component of the Date.
   *
   * @return The seconds component of the Date.
   */
  public double getSeconds() {
    return seconds;
  }

  /**
   * Sets the seconds component of the Date.
   *
   * @param seconds The seconds component of the Date. Must be in the
   * 0-59.999999 range.
   */
  public final void setSeconds(double seconds) {
    if ((seconds >= 0) && (seconds < 60)) {
      this.seconds = seconds;
    }
  }

  @Override
  public String toString() {
    return years + "/" + months + "/" + days + " " + hours + ":" + minutes + ":" + seconds;
  }
}
