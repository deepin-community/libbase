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

package org.pentaho.reporting.libraries.base.versioning;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A utility class for reading versioning information from a Manifest file.
 *
 * @author Thomas Morgner
 */
public class VersionHelper
{
  private String version;
  private String title;
  private String productId;
  private String releaseMilestone;
  private String releaseMinor;
  private String releaseMajor;
  private String releaseCandidateToken;
  private String releaseNumber;
  private String releaseBuildNumber;
  private ProjectInformation projectInformation;

  /**
   * Loads the versioning information for the given project-information structure using the project information's
   * internal name as lookup key.
   *
   * @param projectInformation the project we load information for.
   */
  public VersionHelper(final ProjectInformation projectInformation)
  {
    if (projectInformation == null)
    {
      throw new NullPointerException();
    }

    this.projectInformation = projectInformation;

    final ClassLoader loader = projectInformation.getClass().getClassLoader();
    try
    {
      final Enumeration resources = loader.getResources("META-INF/MANIFEST.MF");
      while (resources.hasMoreElements())
      {
        final URL url = (URL) resources.nextElement();
        final InputStream inputStream = url.openStream();
        try
        {
          if (init(inputStream))
          {
            return;
          }
        }
        finally
        {
          inputStream.close();
        }
      }

    }
    catch (Exception e)
    {
      // Ignore; Maybe log.
    }
  }

  /**
   * Initializes the instance, reaading the properties from the input stream.
   *
   * @param input the input stream.
   * @return true, if the manifest contains version information about this library, false otherwise. 
   */
  private boolean init(final InputStream input)
  {
    if (input == null)
    {
      return false;
    }

    try
    {
      final Manifest props = new Manifest(input);
      final Attributes attr = getAttributes(props, projectInformation.getInternalName());
      final String maybeTitle = getValue(attr, "Implementation-ProductID", null);
      if (ObjectUtilities.equal(projectInformation.getInternalName(), maybeTitle) == false)
      {
        return false;
      }

      title = getValue(attr, "Implementation-Title", maybeTitle);
      releaseMajor = getValue(attr, "Release-Major-Number", "0");
      releaseMinor = getValue(attr, "Release-Minor-Number", "0");
      releaseMilestone = getValue(attr, "Release-Milestone-Number", "0");
      releaseCandidateToken = getValue(attr, "Release-Candidate-Token", "");
      releaseBuildNumber = getValue(attr, "Release-Build-Number", "");
      releaseNumber = getValue(attr, "Release-Number", "");
      if (releaseNumber.length() == 0)
      {
        releaseNumber = createReleaseVersion();
      }
      version = getValue(attr, "Implementation-Version", "");
      if (version.length() == 0)
      {
        version = createVersion();
      }

      productId = maybeTitle;
      if (productId.length() == 0)
      {
        productId = createProductId();
      }

      return true;
    }
    catch (final Exception e)
    {
      return false;
    }
  }

  /**
   * Looks up the attributes for the given module specified by <code>name</code> in the given Manifest.
   *
   * @param props the manifest where to search for the attributes.
   * @param name the name of the module.
   * @return the attributes for the module or the main attributes if the jar contains no such module.
   */
  private Attributes getAttributes(final Manifest props, final String name)
  {
    final Attributes attributes = props.getAttributes(name);
    if (attributes == null)
    {
      return props.getMainAttributes();
    }
    return attributes;
  }

  /**
   * Looks up a single value in the given attribute collection using the given key. If the key is not contained in
   * the attributes, this method returns the default value specified as parameter.
   *
   * @param attrs the attributes where to lookup the key.
   * @param name the name of the key to use for the lookup.
   * @param defaultValue the default value to return in case the attributes contain no such key.
   * @return the value from the attributes or the default values.
   */
  private String getValue(final Attributes attrs, final String name, final String defaultValue)
  {
    final String value = attrs.getValue(name);
    if (value == null)
    {
      return defaultValue;
    }
    return value.trim();
  }

  /**
   * Creates a product-id string, which is the implementation title plus the optional version information.
   *
   * @return the product id string.
   */
  private String createProductId()
  {
    if (version.trim().length() == 0)
    {
      return title;
    }
    return title + '-' + version;
  }

  /**
   * Creates a version string using only the major, minor and milestone version information.
   *
   * @return the version.
   */
  private String createVersion()
  {
    final StringBuffer buffer = new StringBuffer(50);
    buffer.append(releaseMajor);
    buffer.append('.');
    buffer.append(releaseMinor);
    buffer.append('.');
    buffer.append(releaseMilestone);
    return buffer.toString();
  }

  /**
   * Creates a version string using the major, minor and milestone version information and the build number.
   *
   * @return the release version.
   */
  private String createReleaseVersion()
  {
    final StringBuffer buffer = new StringBuffer(50);
    buffer.append(releaseMajor);
    buffer.append('.');
    buffer.append(releaseMinor);
    buffer.append('.');
    buffer.append(releaseMilestone);
    if (releaseCandidateToken.length() > 0)
    {
      buffer.append('-');
      buffer.append(releaseCandidateToken);
    }
    if (releaseBuildNumber.length() > 0)
    {
      buffer.append(" (Build ");
      buffer.append(releaseBuildNumber);
      buffer.append(')');
    }
    return buffer.toString();
  }

  /**
   * Returns the full version string as computed by createVersion().
   *
   * @return the version string.
   * @see #createVersion() 
   */
  public String getVersion()
  {
    return version;
  }

  /**
   * Returns the implementation title as specified in the manifest.
   *
   * @return the implementation title.
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * Returns the product id as computed by createProductId().
   *
   * @return the product id.
   * @see #createProductId() 
   */
  public String getProductId()
  {
    return productId;
  }

  /**
   * Returns the release milestone number.
   *
   * @return the milestone number.
   */
  public String getReleaseMilestone()
  {
    return releaseMilestone;
  }

  /**
   * Returns the release minor number.
   *
   * @return the minor version number.
   */
  public String getReleaseMinor()
  {
    return releaseMinor;
  }

  /**
   * Returns the release major number.
   *
   * @return the major version number.
   */
  public String getReleaseMajor()
  {
    return releaseMajor;
  }

  /**
   * Returns the release candidate token.
   *
   * @return the candidate token.
   */
  public String getReleaseCandidateToken()
  {
    return releaseCandidateToken;
  }

  /**
   * Returns the release number.
   *
   * @return the release number.
   */
  public String getReleaseNumber()
  {
    return releaseNumber;
  }

  /**
   * Returns the release build number.
   *
   * @return the build-number).
   */
  public String getReleaseBuildNumber()
  {
    return releaseBuildNumber;
  }
}
