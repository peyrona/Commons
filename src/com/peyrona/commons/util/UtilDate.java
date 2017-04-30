/*
 * Copyright (C) 2015 Francisco José Morero Peyrona. All Rights Reserved.
 *
 * GNU Classpath is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the free
 * Software Foundation; either version 3, or (at your option) any later version.
 *
 * This app is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this software; see the file COPYING.  If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.peyrona.commons.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class UtilDate
{
    //------------------------------------------------------------------------//
    private UtilDate() {}  // Avoid creation of instances of this class
    //------------------------------------------------------------------------//

    /**
     * Return the number of days between doth passed dates.
     * @param date1
     * @param date2
     * @return Number of days between doth passed dates.
     */
    public static long daysBetween( Date date1, Date date2 )
    {
        long diff = Math.abs( date1.getTime() - date2.getTime() );

        return TimeUnit.DAYS.convert( diff, TimeUnit.MILLISECONDS );
    }

    /**
     * Return milliseconds elapse since the begining of today (midnight).
     * @return Milliseconds elapse since the begining of today (midnight).
     */
    public static int elapsedSinceMidnight()
    {
        Calendar cal = Calendar.getInstance();
        long     now = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return (int) (now - cal.getTimeInMillis());
    }

	/**
	 * Devuelve la fecha que corresponde al primer día de la semana
	 * de la fecha pasada como parámetro.
	 *
	 * @param date
	 * @return
	 */
	public static Date beginOfWeek( Date date )
	{
		checkNullity( date );

		Calendar cal = Calendar.getInstance();
                 cal.setTime( date );
                 cal.add( Calendar.DAY_OF_WEEK,
                          cal.getFirstDayOfWeek() - cal.get( Calendar.DAY_OF_WEEK ) );

		return cal.getTime();
	}

    /**
	 * Devuelve la fecha que corresponde al último día de la semana
	 * de la fecha pasada como parámetro.
	 *
	 * @param date
	 * @return
	 */
    public static Date endOfWeek( Date date )
	{
        Calendar cal = Calendar.getInstance();
                 cal.setTime( beginOfWeek( date ) );       // Aquí ya se comprueba la nullity
                 cal.add( Calendar.DAY_OF_YEAR, 6 );

		return cal.getTime();
	}

	/**
	 * Devuelve la fecha que corresponde al primer día del mes para la
	 * fecha pasada como parámetro.
	 *
	 * @param date
	 * @return
	 */
	public static Date beginOfMonth( Date date )
	{
		checkNullity( date );

		Calendar cal = Calendar.getInstance();
		         cal.setTime( date );

		return add( date, -(cal.get( Calendar.DAY_OF_MONTH ) - 1) );
	}

    /**
	 * Devuelve la fecha que corresponde al último día del mes para la
	 * fecha pasada como parámetro.
	 *
	 * @param date
	 * @return
	 */
    public static Date endOfMonth( Date date )
    {
        checkNullity( date );

        Calendar cal = Calendar.getInstance();
                 cal.setTime( date );
                 cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );

        return cal.getTime();
    }

	/**
	 * Suma o resta días a la fecha pasada como parámetro.
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date add( Date date, int days )
	{
		checkNullity( date );

		if( days == 0 )
		{
			return date;
		}

		Calendar cal = Calendar.getInstance();
		         cal.setTime( date );
		         cal.add( Calendar.DATE, days );

		return cal.getTime();
	}

	/**
	 * <p>
	 * Checks if two dates are on the same day ignoring time.
	 * </p>
	 *
	 * @param date1
	 *            the first date, not altered, not null
	 * @param date2
	 *            the second date, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException
	 *             if either date is <code>null</code>
	 */
	public static boolean isSameDay( Date date1, Date date2 )
	{
		checkNullity( date1, date2 );

		Calendar cal1 = Calendar.getInstance();
                 cal1.setTime( date1 );
		Calendar cal2 = Calendar.getInstance();
                 cal2.setTime( date2 );

		return isSameDay( cal1, cal2 );
	}

	/**
	 * <p>
	 * Checks if two calendars represent the same day ignoring time.
	 * </p>
	 *
	 * @param cal1
	 *            the first calendar, not altered, not null
	 * @param cal2
	 *            the second calendar, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException
	 *             if either calendar is <code>null</code>
	 */
	public static boolean isSameDay( Calendar cal1, Calendar cal2 )
	{
		checkNullity( cal1, cal2 );

		return(cal1.get( Calendar.ERA ) == cal2.get( Calendar.ERA )
               &&
               cal1.get( Calendar.YEAR ) == cal2.get( Calendar.YEAR )
               &&
               cal1.get( Calendar.DAY_OF_YEAR ) == cal2.get( Calendar.DAY_OF_YEAR ));
	}

	/**
	 * <p>
	 * Checks if a date is today.
	 * </p>
	 *
	 * @param date
	 *            the date, not altered, not null.
	 * @return true if the date is today.
	 * @throws IllegalArgumentException
	 *             if the date is <code>null</code>
	 */
	public static boolean isToday( Date date )
	{
		return isSameDay( date, Calendar.getInstance().getTime() );
	}

	/**
	 * <p>
	 * Checks if a calendar date is today.
	 * </p>
	 *
	 * @param cal
	 *            the calendar, not altered, not null
	 * @return true if cal date is today
	 * @throws IllegalArgumentException
	 *             if the calendar is <code>null</code>
	 */
	public static boolean isToday( Calendar cal )
	{
		return isSameDay( cal, Calendar.getInstance() );
	}

	/**
	 * <p>
	 * Checks if the first date is before the second date ignoring time.
	 * </p>
	 *
	 * @param date1
	 *            the first date, not altered, not null
	 * @param date2
	 *            the second date, not altered, not null
	 * @return true if the first date day is before the second date day.
	 * @throws IllegalArgumentException
	 *             if the date is <code>null</code>
	 */
	public static boolean isBeforeDay( Date date1, Date date2 )
	{
		checkNullity( date1, date2 );

		Calendar cal1 = Calendar.getInstance();
                 cal1.setTime( date1 );
		Calendar cal2 = Calendar.getInstance();
                 cal2.setTime( date2 );

		return isBeforeDay( cal1, cal2 );
	}

	/**
	 * <p>
	 * Checks if the first calendar date is before the second calendar date
	 * ignoring time.
	 * </p>
	 *
	 * @param cal1
	 *            the first calendar, not altered, not null.
	 * @param cal2
	 *            the second calendar, not altered, not null.
	 * @return true if cal1 date is before cal2 date ignoring time.
	 * @throws IllegalArgumentException
	 *             if either of the calendars are <code>null</code>
	 */
	public static boolean isBeforeDay( Calendar cal1, Calendar cal2 )
	{
		checkNullity( cal1, cal2 );

		if( cal1.get( Calendar.ERA ) < cal2.get( Calendar.ERA ) )   { return true;  }
		if( cal1.get( Calendar.ERA ) > cal2.get( Calendar.ERA ) )   { return false; }
		if( cal1.get( Calendar.YEAR ) < cal2.get( Calendar.YEAR ) ) { return true;  }
		if( cal1.get( Calendar.YEAR ) > cal2.get( Calendar.YEAR ) ) { return false; }

		return cal1.get( Calendar.DAY_OF_YEAR ) < cal2.get( Calendar.DAY_OF_YEAR );
	}

	/**
	 * <p>
	 * Checks if the first date is after the second date ignoring time.
	 * </p>
	 *
	 * @param date1
	 *            the first date, not altered, not null
	 * @param date2
	 *            the second date, not altered, not null
	 * @return true if the first date day is after the second date day.
	 * @throws IllegalArgumentException
	 *             if the date is <code>null</code>
	 */
	public static boolean isAfterDay( Date date1, Date date2 )
	{
		checkNullity( date1, date2 );

		Calendar cal1 = Calendar.getInstance();
                 cal1.setTime( date1 );
		Calendar cal2 = Calendar.getInstance();
                 cal2.setTime( date2 );

		return isAfterDay( cal1, cal2 );
	}

	/**
	 * <p>
	 * Checks if the first calendar date is after the second calendar date
	 * ignoring time.
	 * </p>
	 *
	 * @param cal1
	 *            the first calendar, not altered, not null.
	 * @param cal2
	 *            the second calendar, not altered, not null.
	 * @return true if cal1 date is after cal2 date ignoring time.
	 * @throws IllegalArgumentException
	 *             if either of the calendars are <code>null</code>
	 */
	public static boolean isAfterDay( Calendar cal1, Calendar cal2 )
	{
		checkNullity( cal1, cal2 );

		if( cal1.get( Calendar.ERA ) < cal2.get( Calendar.ERA ) )    { return false; }
		if( cal1.get( Calendar.ERA ) > cal2.get( Calendar.ERA ) )    { return true;  }
		if( cal1.get( Calendar.YEAR ) < cal2.get( Calendar.YEAR ) )  { return false; }
		if( cal1.get( Calendar.YEAR ) > cal2.get( Calendar.YEAR ) )  { return true;  }

		return cal1.get( Calendar.DAY_OF_YEAR ) > cal2.get( Calendar.DAY_OF_YEAR );
	}

	/**
	 * <p>
	 * Checks if a date is after today and within a number of days in the
	 * future.
	 * </p>
	 *
	 * @param date
	 *            the date to check, not altered, not null.
	 * @param days
	 *            the number of days.
	 * @return true if the date day is after today and within days in the future
	 *         .
	 * @throws IllegalArgumentException
	 *             if the date is <code>null</code>
	 */
	public static boolean isWithinDaysFuture( Date date, int days )
	{
		checkNullity( date );

		Calendar cal = Calendar.getInstance();
                 cal.setTime( date );

		return isWithinDaysFuture( cal, days );
	}

	/**
	 * <p>
	 * Checks if a calendar date is after today and within a number of days in
	 * the future.
	 * </p>
	 *
	 * @param cal
	 *            the calendar, not altered, not null
	 * @param days
	 *            the number of days.
	 * @return true if the calendar date day is after today and within days in
	 *         the future .
	 * @throws IllegalArgumentException
	 *             if the calendar is <code>null</code>
	 */
	public static boolean isWithinDaysFuture( Calendar cal, int days )
	{
		checkNullity( cal );

		Calendar today = Calendar.getInstance();
		Calendar future = Calendar.getInstance();
                 future.add( Calendar.DAY_OF_YEAR, days );

		return (isAfterDay( cal, today ) && !isAfterDay( cal, future ));
	}

	/** Returns the given date with the time set to the start of the day.
     * @param date
     * @return  */
	public static Date getStart( Date date )
	{
		return clearTime( date );
	}

    /**
     * Returns the maximum date possible.
     *
     * @return  The maximum date possible.
     */
    public static Date getMaxDate()
    {
        return new Date( Long.MAX_VALUE );
    }

	/** Returns the given date with the time values cleared.
     * @param date
     * @return  */
	public static java.sql.Date clearTime( java.sql.Date date )
    {
        checkNullity( date );

        return new java.sql.Date( clearTime( new Date( date.getTime() ) ).getTime() );
    }

    /**
     * Sets passed date's time to 0.
     *
     * @param date
     * @return Same date as passed with no time.
     */
	public static Date clearTime( Date date )
	{
		checkNullity( date );

		Calendar c = Calendar.getInstance();
                 c.setTime( date );
                 c.set( Calendar.HOUR_OF_DAY, 0 );
                 c.set( Calendar.MINUTE, 0 );
                 c.set( Calendar.SECOND, 0 );
                 c.set( Calendar.MILLISECOND, 0 );

		return c.getTime();
	}

	/**
	 * Determines whether or not a date has any time values (hour, minute,
	 * seconds or millisecondsReturns the given date with the time values
	 * cleared.
	 *
	 * @param date
	 *            The date.
	 * @return true iff the date is not null and any of the date's hour, minute,
	 *         seconds or millisecond values are greater than zero.
	 */
	public static boolean hasTime( Date date )
	{
		if( date == null )
		{
			return false;
		}

		Calendar c = Calendar.getInstance();
		c.setTime( date );

		if( c.get( Calendar.HOUR_OF_DAY ) > 0 )
		{
			return true;
		}

		if( c.get( Calendar.MINUTE ) > 0 )
		{
			return true;
		}

		if( c.get( Calendar.SECOND ) > 0 )
		{
			return true;
		}

		return (c.get( Calendar.MILLISECOND ) > 0);
	}

	/** Returns the given date with time set to the end of the day
     * @param date
     * @return  */
	public static Date getEnd( Date date )
	{
		if( date == null )
		{
			return null;
		}

		Calendar c = Calendar.getInstance();
                 c.setTime( date );
                 c.set( Calendar.HOUR_OF_DAY, 23 );
                 c.set( Calendar.MINUTE, 59 );
                 c.set( Calendar.SECOND, 59 );
                 c.set( Calendar.MILLISECOND, 999 );

		return c.getTime();
	}

	/**
	 * Returns the maximum of two dates. A null date is treated as being less
	 * than any non-null date.
     * @param d1
     * @param d2
     * @return
	 */
	public static Date max( Date d1, Date d2 )
	{
		if( d1 == null && d2 == null ) { return null; }
		if( d1 == null )               { return d2;   }
		if( d2 == null )               { return d1;   }

		return (d1.after( d2 ) ? d1 : d2);
	}

	/**
	 * Returns the minimum of two dates. A null date is treated as being greater
	 * than any non-nu
     * @param d1
     * @param d2
     * @return
	 */
	public static Date min( Date d1, Date d2 )
	{
		if( d1 == null && d2 == null ) { return null; }
		if( d1 == null )               { return d2;   }
		if( d2 == null )               { return d1;   }

		return (d1.before( d2 ) ? d1 : d2);
	}

	//------------------------------------------------------------------------//

	private static void checkNullity( Object... objs )
	{
		for( Object o : objs )
		{
			if( o == null )
			{
				throw new IllegalArgumentException( "Date must not be null" );
			}
		}
	}
}