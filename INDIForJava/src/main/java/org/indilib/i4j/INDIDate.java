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
 * A class which is the equivalent representation of utc date in the structure
 * ln_date in libnova (http://libnova.sourceforge.net/). This is the Human
 * readable (easy printf) date format used by libnova. It's always in UTC.
 * 
 * @author Gerrit Viola [gerrit.viola at web.de] and S. Alonso (Zerjillo)
 *         [zerjioi at ugr.es]
 */
public class INDIDate {

    /**
     * The years component of the Date.
     */
    private int years;

    /**
     * The months component of the Date.
     */
    private int months;

    /**
     * The days component of the Date.
     */
    private int days;

    /**
     * The hours component of the Date.
     */
    private int hours;

    /**
     * The minutes component of the Date.
     */
    private int minutes;

    /**
     * The seconds component of the Date.
     */
    private double seconds;

    /**
     * January month number.
     */
    private static final int JANUARY = 1;

    /**
     * February month number.
     */
    private static final int FEBRUARY = 2;

    /**
     * March month number.
     */
    private static final int MARCH = 3;

    /**
     * April month number.
     */
    private static final int APRIL = 4;

    /**
     * May month number.
     */
    private static final int MAY = 5;

    /**
     * June month number.
     */
    private static final int JUNE = 6;

    /**
     * July month number.
     */
    private static final int JULY = 7;

    /**
     * August month number.
     */
    private static final int AUGUST = 8;

    /**
     * September month number.
     */
    private static final int SEPTEMBER = 9;

    /**
     * October month number.
     */
    private static final int OCTOBER = 10;

    /**
     * November month number.
     */
    private static final int NOVEMBER = 11;

    /**
     * December month number.
     */
    private static final int DECEMBER = 12;

    /**
     * The number of days in January, March, May, July, August, October,
     * December.
     */
    private static final int DAYS_LONG_MONTH = 31;

    /**
     * The number of days in April, June, September, November.
     */
    private static final int DAYS_SHORT_MONTH = 30;

    /**
     * The number of days in February (on a non Leap Year).
     */
    private static final int DAYS_FEBRUARY = 28;

    /**
     * The number of days in February (on a Leap Year).
     */
    private static final int DAYS_FEBRUARY_LEAP_YEAR = 29;

    /**
     * Number of years that have to pass for the next Leap Year.
     */
    private static final int YEARS_FOR_LEAP_YEAR = 4;

    /**
     * The number of horus in a day.
     */
    private static final int HOURS_PER_DAY = 24;

    /**
     * The number of minutes in a hour.
     */
    private static final int MINUTES_PER_HOUR = 60;

    /**
     * The number of seconds in a minute.
     */
    private static final int SECONDS_PER_MINUTE = 60;

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
     * @param years
     *            The years component of the Date. All values are valid.
     * @param months
     *            The months component of the Date. Mut be in the range 1
     *            (January) - 12 (December).
     * @param days
     *            The days component of the Date. Must be in the 1-28,29,30,31
     *            range (depending on the months and years).
     */
    public INDIDate(final int years, final int months, final int days) {
        initializeComponents();

        setYears(years);
        setMonths(months);
        setDays(days);
    }

    /**
     * Constructs a new Date whose components are partially specified. Hours,
     * minutes and seconds are initialized at 00:00:00.0.
     * 
     * @param years
     *            The years component of the Date. All values are valid.
     * @param months
     *            The months component of the Date. Mut be in the range 1
     *            (January) - 12 (December).
     * @param days
     *            The days component of the Date. Must be in the 1-28,29,30,31
     *            range (depending on the months and years).
     * @param hours
     *            The hours component of the Date. Must be in the 0-23 range.
     * @param minutes
     *            The minutes component of the Date. Must be in the 0-59 range.
     * @param seconds
     *            The seconds component of the Date. Must be in the 0-59.999999
     *            range.
     */
    public INDIDate(final int years, final int months, final int days, final int hours, final int minutes, final double seconds) {
        initializeComponents();

        setYears(years);
        setMonths(months);
        setDays(days);
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
    }

    /**
     * Initilizes the components to "0000/01/01 00:00:00.0".
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
    public final int getYears() {
        return years;
    }

    /**
     * Sets the years component of the Date.
     * 
     * @param years
     *            The years component of the Date. All values are valid.
     */
    public final void setYears(final int years) {
        this.years = years;
    }

    /**
     * Gets the months component of the Date.
     * 
     * @return The months component of the Date.
     */
    public final int getMonths() {
        return months;
    }

    /**
     * Sets the months component of the Date.
     * 
     * @param months
     *            The months component of the Date. Must be in the range 1
     *            (January) - 12 (December).
     */
    public final void setMonths(final int months) {
        if (months >= JANUARY && months <= DECEMBER) {
            this.months = months;
        }
    }

    /**
     * Gets the days component of the Date.
     * 
     * @return The days component of the Date.
     */
    public final int getDays() {
        return days;
    }

    /**
     * Sets the days component of the Date.
     * 
     * @param days
     *            The days component of the Date. Must be in the 1-28,29,30,31
     *            range (depending on the months and years).
     */
    public final void setDays(final int days) {
        if (days < 1) {
            return;
        }

        if (days <= DAYS_FEBRUARY) {
            this.days = days;
        } else if ((months == JANUARY || months == MARCH || months == MAY || months == JULY || months == AUGUST || months == OCTOBER || months == DECEMBER)
                && days <= DAYS_LONG_MONTH) {
            this.days = days;
        } else if ((months == APRIL || months == JUNE || months == SEPTEMBER || months == NOVEMBER) && days <= DAYS_SHORT_MONTH) {
            this.days = days;
        } else if (months == FEBRUARY && years % YEARS_FOR_LEAP_YEAR == 0 && days <= DAYS_FEBRUARY_LEAP_YEAR) {
            this.days = days;
        }
    }

    /**
     * Gets the hours component of the Date.
     * 
     * @return The hours component of the Date.
     */
    public final int getHours() {
        return hours;
    }

    /**
     * Sets the hours component of the Date.
     * 
     * @param hours
     *            The hours component of the Date. Must be in the 0-23 range.
     */
    public final void setHours(final int hours) {
        if (hours >= 0 && hours < HOURS_PER_DAY) {
            this.hours = hours;
        }
    }

    /**
     * Gets the minutes component of the Date.
     * 
     * @return The minutes component of the Date.
     */
    public final int getMinutes() {
        return minutes;
    }

    /**
     * Sets the minutes component of the Date.
     * 
     * @param minutes
     *            The minutes component of the Date. Must be in the 0-59 range.
     */
    public final void setMinutes(final int minutes) {
        if (minutes >= 0 && minutes < MINUTES_PER_HOUR) {
            this.minutes = minutes;
        }
    }

    /**
     * Gets the seconds component of the Date.
     * 
     * @return The seconds component of the Date.
     */
    public final double getSeconds() {
        return seconds;
    }

    /**
     * Sets the seconds component of the Date.
     * 
     * @param seconds
     *            The seconds component of the Date. Must be in the 0-59.999999
     *            range.
     */
    public final void setSeconds(final double seconds) {
        if (seconds >= 0 && seconds < SECONDS_PER_MINUTE) {
            this.seconds = seconds;
        }
    }

    @Override
    public final String toString() {
        return years + "/" + months + "/" + days + " " + hours + ":" + minutes + ":" + seconds;
    }
}
