/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2007 - 2009 Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.base.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A helper class for a simplified resource-bundle access. This class simply ignores all resource-bundle related errors
 * and prints place-holder strings if a localization key cannot be found.
 *
 * @author David Kincade
 */
public class Messages extends ResourceBundleSupport
{
  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   *
   * @param locale   the locale.
   * @param baseName the baseName of the resource-bundle.
   * @see ResourceBundle#getBundle(String, Locale)
   */
  public Messages(final Locale locale, final String baseName)
  {
    super(locale, baseName);
  }

  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   *
   * @param locale         the locale.
   * @param baseName       the baseName of the resource-bundle.
   * @param resourceBundle a predefined resource-bundle.
   */
  public Messages(final Locale locale, final ResourceBundle resourceBundle, final String baseName)
  {
    super(locale, resourceBundle, baseName);
  }

  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   *
   * @param locale         the locale.
   * @param resourceBundle a predefined resource-bundle.
   */
  public Messages(final Locale locale, final ResourceBundle resourceBundle)
  {
    super(locale, resourceBundle);
  }

  /**
   * Creates a new Messages-collection. The default locale and baseName will be used to create the resource-bundle that
   * backs up this implementation.
   *
   * @param baseName the baseName of the resource-bundle.
   */
  public Messages(final String baseName)
  {
    super(baseName);
  }

  /**
   * Creates a new Messages-collection. The default locale and baseName will be used to create the resource-bundle that
   * backs up this implementation.
   *
   * @param baseName       the baseName of the resource-bundle.
   * @param resourceBundle a predefined resource-bundle.
   */
  public Messages(final ResourceBundle resourceBundle, final String baseName)
  {
    super(resourceBundle, baseName);
  }

  /**
   * Creates a new Messages-collection. The default locale and baseName will be used to create the resource-bundle that
   * backs up this implementation.
   *
   * @param resourceBundle a predefined resource-bundle.
   */
  public Messages(final ResourceBundle resourceBundle)
  {
    super(resourceBundle);
  }

  /**
   * Gets a string for the given key from this resource bundle or one of its parents. If the key is a link, the link is
   * resolved and the referenced string is returned instead. If the given key cannot be resolved, no exception will be
   * thrown and a generic placeholder is used instead.
   *
   * @param key the key for the desired string
   * @return the string for the given key
   * @throws NullPointerException     if <code>key</code> is <code>null</code>
   * @throws MissingResourceException if no object for the given key can be found
   */
  public String getString(final String key)
  {
    try
    {
      return super.strictString(key);
    }
    catch (final MissingResourceException e)
    {
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @return the formated string
   */
  public String getString(final String key, final String param1)
  {
    try
    {
      return formatMessage(key, param1);
    }
    catch (final MissingResourceException e)
    {
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @return the formated string
   */
  public String getString(final String key, final String param1, final String param2)
  {
    try
    {
      return formatMessage(key, param1, param2);
    }
    catch (final MissingResourceException e)
    {
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @return the formated string
   */
  public String getString(final String key, final String param1, final String param2, final String param3)
  {
    try
    {
      return formatMessage(key, new Object[]{param1, param2, param3});
    }
    catch (final MissingResourceException e)
    {
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @param param4 the parameter for the message
   * @return the formated string
   */
  public String getString(final String key,
                          final String param1,
                          final String param2,
                          final String param3,
                          final String param4)
  {
    try
    {
      return formatMessage(key, new Object[]{param1, param2, param3, param4});
    }
    catch (final MissingResourceException e)
    {
      return '!' + key + '!';
    }
  }

  /**
   * Get a formatted error message. The message consists of two parts. The first part is the
   * error numeric Id associated with the key used to identify the message in the resource file.
   * For instance, suppose the error key is MyClass.ERROR_0068_TEST_ERROR. The first
   * part of the error msg would be "0068". The second part of the returned string
   * is simply the <code>msg</code> parameter.
   * <p/>
   * Currently the format is:
   * error key - error msg
   * For instance:
   * "0068 - A test error message."
   * <p/>
   * Currently the format is: error key - error msg For instance: "0069 - You were punched by the donkey."
   *
   * @param key String containing the key that was used to obtain the <code>msg</code> parameter from the resource
   *            file.
   * @param msg String containing the message that was obtained from the resource file using the <code>key</code>
   *            parameter.
   * @return String containing the formatted error message.
   */
  public String formatErrorMessage(final String key, final String msg)
  {
    try
    {
      final int end;
      final int errorMarker = key.indexOf(".ERROR_");
      if (errorMarker < 0)
      {
        end = key.length();
      }
      else
      {
        end = Math.min(errorMarker + ".ERROR_0000".length(), key.length()); //$NON-NLS-1$
      }
      return getString("MESSUTIL.ERROR_FORMAT_MASK", key.substring(0, end), msg); //$NON-NLS-1$
    }
    catch (final Exception e)
    {
      return "!MESSUTIL.ERROR_FORMAT_MASK:" + key + '!';
    }
  }

  /**
   * Get a formatted error message from the resource-bundle. The message consists of two parts. The first part is the
   * error numeric Id associated with the key used to identify the message in the resource file. For instance, suppose
   * the error key is MyClass.ERROR_0069_DONKEY_PUNCH. The first part of the error msg would be "0069". The second part
   * of the returned string is simply the <code>msg</code> parameter.
   * <p/>
   * Currently the format is: error key - error msg For instance: "0069 - You were punched by the donkey."
   *
   * @param key String containing the key that was used to obtain the <code>msg</code> parameter from the resource
   *            file.
   * @return String containing the formatted error message.
   */
  public String getErrorString(final String key)
  {
    return formatErrorMessage(key, getString(key));
  }

  /**
   * Get a parametrized formatted error message from the resource-bundle. The message consists of two parts. The first
   * part is the error numeric Id associated with the key used to identify the message in the resource file. For
   * instance, suppose the error key is MyClass.ERROR_0069_DONKEY_PUNCH. The first part of the error msg would be
   * "0069". The second part of the returned string is simply the <code>msg</code> parameter.
   * <p/>
   * Currently the format is: error key - error msg For instance: "0069 - You were punched by the donkey."
   *
   * @param key    String containing the key that was used to obtain the <code>msg</code> parameter from the resource
   *               file.
   * @param param1 the parameter for the message
   * @return String containing the formatted error message.
   */
  public String getErrorString(final String key, final String param1)
  {
    return formatErrorMessage(key, getString(key, param1));
  }

  /**
   * Get a parametrized formatted error message from the resource-bundle. The message consists of two parts. The first
   * part is the error numeric Id associated with the key used to identify the message in the resource file. For
   * instance, suppose the error key is MyClass.ERROR_0069_DONKEY_PUNCH. The first part of the error msg would be
   * "0069". The second part of the returned string is simply the <code>msg</code> parameter.
   * <p/>
   * Currently the format is: error key - error msg For instance: "0069 - You were punched by the donkey."
   *
   * @param key    String containing the key that was used to obtain the <code>msg</code> parameter from the resource
   *               file.
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @return String containing the formatted error message.
   */
  public String getErrorString(final String key, final String param1, final String param2)
  {
    return formatErrorMessage(key, getString(key, param1, param2));
  }

  /**
   * Get a parametrized formatted error message from the resource-bundle. The message consists of two parts. The first
   * part is the error numeric Id associated with the key used to identify the message in the resource file. For
   * instance, suppose the error key is MyClass.ERROR_0069_DONKEY_PUNCH. The first part of the error msg would be
   * "0069". The second part of the returned string is simply the <code>msg</code> parameter.
   * <p/>
   * Currently the format is: error key - error msg For instance: "0069 - You were punched by the donkey."
   *
   * @param key    String containing the key that was used to obtain the <code>msg</code> parameter from the resource
   *               file.
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @return String containing the formatted error message.
   */
  public String getErrorString(final String key, final String param1, final String param2, final String param3)
  {
    return formatErrorMessage(key, getString(key, param1, param2, param3));
  }

}