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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author peyrona
 */
public class UtilString
{
    /** Default locale decimal separator symbol */
	public static final char cDecimalSep = (new DecimalFormatSymbols()).getMonetaryDecimalSeparator();

    /** Default locale currency symbol */
    public static final String sCurrencySymbol = (new DecimalFormatSymbols()).getCurrencySymbol();

    /** System dependent line separator (property "line.separator") */
    public static final String sEOL = System.getProperty( "line.separator" );

    /** Predefined (standard) Unicode "Record Separator" char */
    public static final char cSeparator = '\u241E';

    /** Predefined (standard) Unicode "Record Separator" String */
    public static final String sSeparator = String.valueOf( cSeparator );

    private static DecimalFormat dfDataUnits  = null;
    private static Pattern       emailPattern = null;

	//------------------------------------------------------------------------//
    private UtilString() {}  // Avoid creation of instances of this class
    //------------------------------------------------------------------------//

    /**
     * Checks if passed String is null, has zero length or all its chars are
     * spaces.
     *
     * @param s String to check.
     * @return true when passed String is null, its length is zero or all its chars are spaces.
     */
    public static boolean isEmpty( final String s )
    {
        return ((s == null) || (s.length() == 0) || (s.trim().length() == 0));
    }

    /**
     * Checks if passed String is not null and length is greater than zero and
     * at least one of its chars is not a space char.
     *
     * @param s String to check.
     * @return true when passed String is not null or length is greater than
     *         zero and at least one of its chars is not a space char.
     */
    public static boolean isNotEmpty( final String s )
    {
        return (! isEmpty( s ));
    }

    public static boolean isEmpty( final Object o )
    {
        if( o == null )
        {
            return true;
        }

        String s = o.toString();

        return ((s.length() == 0) || (s.trim().length() == 0));
    }

    public static boolean isNotEmpty( final Object o )
    {
        return (! isEmpty( o ));
    }

    /**
     * Comprueba que todas las cadenas pasadas como parámetro son vacías.
     * (es decir, con que haya una sóla cadena de las pasadas que sea NO vacía,
     *  este método devuelve false).
     *
     * @param strings
     * @return true si todas las cadenas pasadas son vacías.
     */
    public static boolean areEmpty( final String... strings )
    {
        for( final String s : strings )
        {
            if( (s != null) && (s.trim().length() > 0) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Comprueba que todas las cadenas pasadas como parámetro son NO vacías.
     * (es decir, con que haya una sóla cadena de las pasadas que es vacía, este
     * método devuelve false).
     *
     * @param strings
     * @return true si todas las cadenas pasadas son NO vacías.
     */
    public static boolean areNotEmpty( final String... strings )
    {
        for( final String s : strings )
        {
            if( (s == null) || (s.trim().length() == 0) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns same string trimmed and with the first char capitalized using
     * default locale.
     *
     * @param s
     * @return Same string trimmed and with the first char capitalized.
     */
    public static String capitalize( String s )
    {
        return capitalize( s, Locale.getDefault() );
    }

    /**
     * Returns same string trimmed and with the first char capitalized.
     *
     * @param s
     * @param locale
     * @return Same string trimmed and with the first char capitalized.
     */
    public static String capitalize( String s, Locale locale )
    {
        if( isNotEmpty( s ) )
        {
            s = s.trim().toLowerCase();
            s = s.substring( 0,1 ).toUpperCase( locale ) + s.substring( 1 );
        }

        return s;
    }

    /**
     * A fast way to do uppercase, but works only for ASCII from 'a' to 'z'.
     *
     * @param ch
     * @return
     */
    public static char toUpperCase( char ch )
    {
        return (ch >= 'a' && ch <= 'z' ? ((char) (ch ^ 0x20)) : ch);
    }

    /**
     * A fast way to do lowercase, but works only for ASCII from 'A' to 'Z'.
     *
     * @param ch
     * @return
     */
    public static char toLowerCase( char ch )
    {
        return (ch >= 'A' && ch <= 'Z' ? ((char) (ch ^ 0x20)) : ch);
    }

    public static String removeDuplicatedSpaces( String s )
    {
        return s.replaceAll( "\\s+", " " );
    }

    public static String getOnlyLetters( String s )
    {
        StringBuilder builder = new StringBuilder();

        for( char ch : s.toCharArray() )
        {
            if( Character.isLetter( ch ) )
            {
                builder.append( ch );
            }
        }

        return builder.toString();
    }

    public static String getOnlyLettersAndInnerSpaces( String s )
    {
        StringBuilder builder = new StringBuilder();

        s = s.trim();

        for( char ch : s.toCharArray() )
        {
            if( (ch == ' ') || Character.isLetter( ch ) )
            {
                builder.append( ch );
            }
        }

        return builder.toString();
    }

    /**
     * Recibe una cadena que representa una fecha en formato ANSI (yyyymmdd) y
     * devuelve la fecha representada.
     * <p>
     * La cadena puede tener caracteres de separación, estos serán ignorados.
     * Así por ejemplo, son cadenas válidas: 2015/03/27, 2015-03-27, 2015.03.27.
     *
     * @param sDateANSI
     * @return
     */
    public static Date ansi2Date( String sDateANSI )
    {
        StringBuilder sb = new StringBuilder( 8 );

        for( char c : sDateANSI.trim().toCharArray() )
        {
            if( c >= '0' && c <= '9' )    // Me quedo sólo con los dígitos
            {
                sb.append( c );
            }
        }

        sDateANSI = sb.toString();

        if( sDateANSI.length() == 8 )
        {
            int year  = Integer.parseInt( sDateANSI.substring( 0, 4 ) );
            int month = Integer.parseInt( sDateANSI.substring( 4, 6 ) ) - 1;
            int date  = Integer.parseInt( sDateANSI.substring( 6 ) );

            Calendar cal = Calendar.getInstance();
                     cal.set( year, month, date );

            return cal.getTime();
        }

        return null;
    }

    public static String cleanPhone( String s )
    {
        return s.replaceAll( "[^0-9+]", "" );
    }

    /**
     * Returns the string representation of passed object.
     * <p>
     * Equivalent to ::obj2Str( obj, null, "" )
     * @param obj To be passed to its string representation.
     * @return The string representation of passed object.
     */
    public static String obj2Str( Object obj )
    {
        return obj2Str( obj, null, "" );
    }

    /**
     * Returns the string representation of passed object.
     *
     * @param obj To be passed to its string representation.
     * @param sIfNull What to return if passed object was null.
     * @param sIfEmpty What to return if string representation of passed object was empty.
     * @return The string representation of passed object.
     */
    public static String obj2Str( Object obj, String sIfNull, String sIfEmpty )
    {
        if( obj == null )
        {
            return sIfNull;
        }

        String sObj = obj.toString();

        if( sObj.isEmpty() )
        {
            return sIfEmpty;
        }

        return sObj;
    }

    /**
     * Checks is passed String has meaning chars: letters or digits (tabs,
     * spaces, CR, LF, punctuation, etc does not count).
     *
     * @param s String to check.
     * @return True if passed string contais at least one letter or digit.
     */
    public static boolean isMeaningless( final String s )
    {
        if( s != null )
        {
            for( final char ch : s.toCharArray() )
            {
                if( Character.isLetterOrDigit( ch ) )
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * <code>return (! isMeaningless( s ));</code>
     *
     * @param s
     * @return
     */
    public static boolean isMeaningfull( final String s )
    {
        return (! isMeaningless( s ));
    }

    /**
     * Checks if passed object represents or not a number.
     * <p>
     * Note: uses default locale to check decimal separator character.
     *
     * @param obj
     * @return true if passed object represents a number, false otherwise.
     */
    public static boolean isNotNumeric( final Object obj )
    {
        return (! isNumeric( obj.toString() ));
    }

    /**
     * Checks if passed string represents or not a number.
     * <p>
     * Note: uses default locale to check decimal separator character.
     *
     * @param str
     * @return true if passed string represents a number, false otherwise.
     */
    public static boolean isNotNumeric( String str )
    {
        return (! isNumeric( str ));
    }

    /**
     * Checks if passed object represents or not a number.
     * <p>
     * Note: uses default locale to check decimal separator character.
     *
     * @param obj
     * @return true if passed object represents a number, false otherwise.
     */
    public static boolean isNumeric( final Object obj )
    {
        return (obj == null) ? false : isNumeric( obj.toString() );
    }

    /**
     * Checks if passed string represents or not a number.
     * <p>
     * Note: uses default locale to check decimal separator character.
     *
     * @param str
     * @return true if passed string represents a number, false otherwise.
     */
    public static boolean isNumeric( String str )
    {
        if( str == null )
        {
            return false;
        }

        str = str.trim();

        if( str.length() == 0 )
        {
            return false;
        }

        str = str.replace( ',', '.' );    // In the whole world there are only these 2 options: '.' and ','

        if( countChar( str, '.' ) > 1 )
        {
            return false;
        }

        char ch0 = str.charAt( 0 );

        if( (ch0 != '+') && (ch0 != '-') &&  (ch0 != '.') && (! Character.isDigit( ch0 )) )
        {
            return false;
        }

        char[] aChar = str.toCharArray();

        for( int n = 1; n < aChar.length; n++  )
        {
            if( ! Character.isDigit( aChar[n] ) )
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isInteger( final String str )
    {
        return isNumeric( str )
               &&
               (str.indexOf( '.' ) == -1)
               &&
               (str.indexOf( ',' ) == -1);   // En el mundo sólo hay 2 posibilidades: ',' y '.'
    }

    public static boolean isNotInteger( final String str )
    {
        return ! isInteger( str );
    }

    /**
     * Checks if passed char is the last one in the passed string.
     *
     * <ul>
     * <li>If string is null, returns false
     * <li>If string is empty, returns false
     * <li>Else, returns true if last char of string == passed char
     * </ul>
     *
     * @param s String to check
     * @param c Character to check
     * @return true if passed char is the last one in the passed string.
     */
    public static boolean isLastChar( String s, char c )
    {
        return ((s == null || s.length() == 0) ? false
                                               : s.charAt( s.length() - 1 ) == c);
    }

    public static String leftPad( String string, final char padder, final int length )
    {
        if( string == null )
        {
            string = "";
        }

        if( string.length() < length )
        {
            return UtilString.fill( padder, length - string.length() ) + string;
        }
        else
        {
            return string;
        }
    }

    public static String rightPad( String string, final char padder, final int length )
    {
        if( string == null )
        {
            string = "";
        }

        if( string.length() < length )
        {
            return string + UtilString.fill( padder, length - string.length() );
        }
        else
        {
            return string;
        }
    }

    /**
     * Returns a String with a length of passed parameter and composed only by chars of passed pattern.
     * @param pattern
     * @param length
     * @return A String with a length of passed parameter and composed only by chars of passed pattern.
     */
    public static String fill( final char pattern, final int length )
    {
        final StringBuilder sb = new StringBuilder( length );

        for( int n = 0; n < length; n++ )
        {
            sb.append( pattern );
        }

        return sb.toString();
    }

    public static String fill( final String pattern, int length )
    {
        final StringBuilder sb = new StringBuilder( length );

        length /= pattern.length();

        for( int n = 0; n <  length; n++ )
        {
            sb.append( pattern );
        }

        return sb.toString();
    }

    public static String concat( final String one, final String two )
    {
    	if( one == null )
    	{
    		return two;
    	}
    	else
    	{
    		if( (two == null) || (two.length() == 0) )
    		{
    			return one;
    		}
    		else
    		{
    			return one.concat( two );
    		}
    	}
    }

    /**
     * Formatea cantidades como moneda utilizando para el símbolo monetario el default locale.
     *
     * @param nAmount
     * @return
     */
    public static String formatAsCurrency( final BigDecimal nAmount )
    {
        return formatAsCurrency( nAmount, true );
    }

    /**
     * Formatea cantidades como moneda utilizando para el símbolo monetario el default locale.
     *
     * @param nAmount
     * @param bIncludeSign Incluir o no el símbolo de la moneda.
     * @return
     */
    public static String formatAsCurrency( final BigDecimal nAmount, final boolean bIncludeSign )
    {
        if( bIncludeSign )
        {
            final NumberFormat nfCurrency = NumberFormat.getCurrencyInstance();

            return nfCurrency.format( nAmount );
        }
        else
        {
            final int          nDigits    = Currency.getInstance( Locale.getDefault() ).getDefaultFractionDigits();
            final NumberFormat nfNoSymbol = NumberFormat.getInstance();
                               nfNoSymbol.setMinimumFractionDigits( nDigits );
                               nfNoSymbol.setMaximumFractionDigits( nDigits );

            return nfNoSymbol.format( nAmount );
        }
    }

    /**
     * Format passed number as units of data: bytes, Kb, Mg, etc.
     *
     * @param size To be formatted.
     * @param addLabel true to add the label.
     * @return Passed size formatted and converted into String.
     */
    public static String formatAsDataUnit( final long size, final boolean addLabel )
    {
        if( size <= 0 )
        {
            return "0";
        }

        if( dfDataUnits == null )
        {
            dfDataUnits = new DecimalFormat( "#,##0.#" );
        }

        final int      digitGroups = (int) (Math.log10( size ) / Math.log10( 1024 ) );
        final String[] asDataUnits = new String[] { "B", "KiB", "MiB", "GiB", "TiB" };
              String   ret         = dfDataUnits.format( size / Math.pow( 1024, digitGroups ) );

        if( addLabel )
        {
           ret += " " + asDataUnits[digitGroups];
        }

        return ret;
    }

    /**
     * Corta la cadena a una longitud determinada (añadiendo una elipsis si
     * fuera necesario).
     *
     * @param s
     * @param len Max String length (including "...")
     * @param ellipsysPosition -1 == left, 0 == center, 1 == right
     * @return
     */
    public static String setMaxLength( String s, int len, final int ellipsysPosition )
    {
        if( (s != null) && (len >= 0) )
        {
            final String ellipsys = "...";

            if( s.length() > len )
            {
                len = len - ellipsys.length();
                s   = s.substring( 0, len );

                switch( ellipsysPosition )
                {
                    case -1: s = ellipsys + s; break;
                    case  1: s = s + ellipsys; break;
                    case  0: s = s.substring( 0, s.length()/2 ) + ellipsys + s.substring( (s.length()/2) +1 ); break;
                }
            }
        }

        return s;
    }

    /**
     * Corta la cadena a una longitud determinada.
     *
     * @param s
     * @param len
     * @return
     */
    public static String setMaxLength( String s, final int len )
    {
        if( (s != null) && (len >= 0) )
        {
            if( s.length() > len )
            {
                s = s.substring( 0, len );
            }
        }

        return s;
    }

    /**
     * Reemplaza trozos de una cadena con un formato específico.
     * <p>
     * Ya sé que String.format(...) es mucho más potente, pero también es más
     * lenta.
     * <p>
     * Nota: sólo se reemplaza la primera ocurrencia y la numeración de
     *       parámetros comienza con 1, NO con 0.
     * <p>
     * <pre>
     * Ante una llamada como esta:
     *    setParameters( "Velocidad: {1} {2}", "95", "Km/h" )
     * Devuelve:
     *    "Velocidad: 95 Km/h"
     * </pre>
     * @param template
     * @param params
     * @return
     */
    public static String setParameters( String template, final Object... params )
    {
    	for( int n = 0; n < params.length; n++ )
    	{
    		template = template.replace( "{"+ (n+1) +"}", params[n].toString() );
    	}

    	return template;
    }

    public static int countChar( final String where, final char what )
    {
        int counter = 0;

        if( where != null )
        {
            for( final char ch : where.toCharArray() )
            {
                if( ch == what )
                {
                    counter++;
                }
            }
        }

        return counter;
    }

    public static boolean hasAtLeast( final String where, final char what, final int howMany )
    {
        if( where != null )
        {
            int counter = 0;

            for( final char ch : where.toCharArray() )
            {
                if( ch == what )
                {
                    counter++;

                    if( counter == howMany )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

   	/**
   	 * Validate email.
   	 *
   	 * @param email to validate.
   	 * @return true if email is valid, false otherwise.
   	 */
   	public static boolean isValidEmail( final String email )
       {
           if( emailPattern == null )
           {
               emailPattern = Pattern.compile( "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                                               "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$" );
           }

           final Matcher matcher = emailPattern.matcher( email );

   		return matcher.matches();
   	}
}
