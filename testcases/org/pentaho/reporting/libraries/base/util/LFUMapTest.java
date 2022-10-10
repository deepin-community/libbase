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

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class LFUMapTest extends TestCase
{
  public LFUMapTest()
  {
  }

  public LFUMapTest(final String s)
  {
    super(s);
  }

  public void testAdd ()
  {
    final LFUMap lfuMap = new LFUMap(10);
    lfuMap.put("1", "1");
    lfuMap.validate();
    lfuMap.put("2", "2");
    lfuMap.validate();
    lfuMap.put("3", "3");
    lfuMap.validate();
    lfuMap.put("4", "4");
    lfuMap.validate();
    lfuMap.put("1", "5");
    lfuMap.validate();
    lfuMap.put("3", "6");
    lfuMap.validate();
    lfuMap.put("4", "7");
    lfuMap.validate();
    lfuMap.put("2", "8");
    lfuMap.validate();

    DebugLog.logHere();
  }

  public void testAdd2 ()
  {
    final LFUMap lfuMap = new LFUMap(10);
    lfuMap.put("1", "1");
    lfuMap.validate();
    lfuMap.put("2", "2");
    lfuMap.validate();
    lfuMap.put("3", "3");
    lfuMap.validate();
    lfuMap.put("4", "4");
    lfuMap.validate();
    lfuMap.put("1", "5");
    lfuMap.validate();
    lfuMap.put("3", "6");
    lfuMap.validate();
    lfuMap.put("4", "7");
    lfuMap.validate();
    lfuMap.put("a2", "8");
    lfuMap.validate();
    lfuMap.put("a4", "4");
    lfuMap.validate();
    lfuMap.put("a1", "5");
    lfuMap.validate();
    lfuMap.put("a3", "6");
    lfuMap.validate();
    lfuMap.put("b4", "7");
    lfuMap.validate();
    lfuMap.put("b4", "4");
    lfuMap.validate();
    lfuMap.put("b1", "5");
    lfuMap.validate();
    lfuMap.put("b3", "6");
    lfuMap.validate();
    lfuMap.put("c4", "7");
    lfuMap.validate();

    lfuMap.get("a2");
    lfuMap.validate();
    lfuMap.get("a4");
    lfuMap.validate();
    lfuMap.get("a1");
    lfuMap.validate();
    lfuMap.get("a3");
    lfuMap.validate();
    lfuMap.get("b4");
    lfuMap.validate();
    lfuMap.get("b4");
    lfuMap.validate();

  }

}
