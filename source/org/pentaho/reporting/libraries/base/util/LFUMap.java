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

import java.util.HashMap;
import java.io.Serializable;

/**
 * A Least-Frequently-Used Map.
 * <p/>
 * This is not a real map in the sense of the Java-Collections-API. This is a slimmed down version of a
 * Least-Frequently-Used map with no unnecessary extra stuff like iterators or other costly but rarely used
 * java.util.Collections features. The cache does not accept null-keys, and any attempt to store null-values
 * will yield an error.
 * <p/>
 * To remove a couple of ugly checks and thus improving performance, this map enforces a minimum size of 3 items.
 *
 * @author Thomas Morgner
 */
public class LFUMap implements Serializable, Cloneable
{
  /**
   * A cache map entry class holding both the key and value and acting as member of a linked list.
   */
  private static class MapEntry
  {
    private Object key;
    private Object value;
    private MapEntry previous;
    private MapEntry next;

    /**
     * Creates a new map-entry for the given key and value.
     *
     * @param key the key, never null.
     * @param value the value, never null.
     */
    protected MapEntry(final Object key, final Object value)
    {
      if (key == null)
      {
        throw new NullPointerException();
      }
      if (value == null)
      {
        throw new NullPointerException();
      }
      this.key = key;
      this.value = value;
    }

    /**
     * Returns the entry's key.
     * @return the key.
     */
    public Object getKey()
    {
      return key;
    }

    /**
     * Returns the previous entry in the list or null if this is the first entry.
     * @return the previous entry. 
     */
    public MapEntry getPrevious()
    {
      return previous;
    }

    /**
     * Redefines the previous entry in the list or null if this is the first entry.
     * @param previous the previous entry. 
     */
    public void setPrevious(final MapEntry previous)
    {
      this.previous = previous;
    }

    /**
     * Returns the next entry in the list or null if this is the last entry.
     * @return the next entry. 
     */
    public MapEntry getNext()
    {
      return next;
    }

    /**
     * Redefines the next entry in the list or null if this is the last entry.
     * @param next the next entry.
     */
    public void setNext(final MapEntry next)
    {
      this.next = next;
    }

    /**
     * Returns the current value.
     *
     * @return the value, never null.
     */
    public Object getValue()
    {
      return value;
    }

    /**
     * Redefines the current value.
     *
     * @param value the value, never null.
     */
    public void setValue(final Object value)
    {
      if (value == null)
      {
        throw new NullPointerException();
      }
      this.value = value;
    }
  }

  private HashMap map;
  private MapEntry first;
  private MapEntry last;
  private int cacheSize;

  /**
   * Creates a new LFU-Map with a maximum size of <code>cacheSize</code> entries.
   *
   * @param cacheSize the maximum number of elements this map will be able to store.
   */
  public LFUMap(final int cacheSize)
  {
    // having at least 3 entries saves me a lot of coding and thus gives more performance ..
    this.cacheSize = Math.max(3, cacheSize);
    this.map = new HashMap(cacheSize);
  }

  public void clear()
  {
    this.map.clear();
    this.first = null;
    this.last = null;
  }

  /**
   * Return the entry for the given key. Any successful lookup moves the entry to the top of the list.
   *
   * @param key the lookup key.
   * @return the value stored for the key or null.
   */
  public Object get(final Object key)
  {
    if (key == null)
    {
      throw new NullPointerException();
    }

    if (first == null)
    {
      // the cache is empty, so there is no way how we can have a result
      return null;
    }

    if (first == last)
    {
      // single entry does not even need to hit the cache ..
      if (first.getKey().equals(key))
      {
        return first.getValue();
      }
      return null;
    }

    final MapEntry metrics = (MapEntry) map.get(key);
    if (metrics == null)
    {
      // no such key ..
      return null;
    }

    final MapEntry prev = metrics.getPrevious();
    if (prev == null)
    {
      // already the first value
      return metrics.getValue();
    }

    final MapEntry next = metrics.getNext();
    if (next == null)
    {
      // metrics is last entry
      // prev will be the new last entry 
      prev.setNext(null);
      last = prev;

      metrics.setPrevious(null);
      metrics.setNext(first);
      first.setPrevious(metrics);
      first = metrics;
      return metrics.getValue();
    }

    // in the middle .. remove from the chain
    next.setPrevious(prev);
    prev.setNext(next);

    // and add it at the top ..
    metrics.setPrevious(null);
    metrics.setNext(first);
    first.setPrevious(metrics);
    first = metrics;
    return metrics.getValue();

  }

  /**
   * Puts the given value into the map using the specified non-null key. The new entry is added as first entry in
   * the list of recently used values.
   *
   * @param key the key.
   * @param value the value.
   */
  public void put(final Object key, final Object value)
  {
    if (key == null)
    {
      throw new NullPointerException();
    }

    if (first == null)
    {
      if (value == null)
      {
        return;
      }
      first = new MapEntry(key, value);
      last = first;
      map.put(key, first);
      return;
    }

    if (value == null)
    {
      remove(key);
      return;
    }

    if (first.getKey().equals(key))
    {
      // no need to do actual work ..
      return;
    }

    final MapEntry entry = (MapEntry) map.get(key);
    if (entry == null)
    {
      // check, whether the backend can carry another entry ..
      if ((1 + map.size()) >= cacheSize)
      {
        // remove the last entry
        map.remove(last.getKey());
        final MapEntry previous = last.getPrevious();
        last.setNext(null);
        last.setPrevious(null);

        previous.setNext(null);
        last = previous;
      }

      // now add this entry as first one ..
      final MapEntry cacheEntry = new MapEntry(key, value);
      first.setPrevious(cacheEntry);
      cacheEntry.setNext(first);
      map.put(key, cacheEntry);
      first = cacheEntry;
      return;
    }

    // replace an existing value ..

    entry.setValue(value);
    if (entry == first)
    {
      // already the first one ..
      // should not happen, we have checked that ...
      // map.put(key, entry);
      throw new IllegalStateException("Duplicate return?");
    }

    if (entry == last)
    {
      // prev is now the new last entry ..
      final MapEntry previous = last.getPrevious();
      previous.setNext(null);
      last = previous;

      first.setPrevious(entry);
      entry.setNext(first);
      entry.setPrevious(null);
      first = entry;
      return;
    }

    final MapEntry previous = entry.getPrevious();
    final MapEntry next = entry.getNext();
    // next cannot be null, else 'entry' would be the last entry, and we checked that already ..
    previous.setNext(next);
    next.setPrevious(previous);

    first.setPrevious(entry);
    entry.setNext(first);
    entry.setPrevious(null);
    first = entry;
  }

  /**
   * Removes the entry for the given key.
   *
   * @param key the key for which an entry should be removed.
   */
  public void remove(final Object key)
  {
    if (key == null)
    {
      throw new NullPointerException();
    }

    if (first == null)
    {
      return;
    }

    final MapEntry entry = (MapEntry) map.remove(key);
    if (entry == null)
    {
      return;
    }

    if (entry == first)
    {
      final MapEntry nextEntry = first.getNext();
      if (nextEntry == null)
      {
        first = null;
        last = null;
        entry.setNext(null);
        entry.setPrevious(null);
        return;
      }

      first = nextEntry;
      nextEntry.setPrevious(null);

      entry.setNext(null);
      entry.setPrevious(null);
      return;
    }

    if (entry == last)
    {
      final MapEntry prev = last.getPrevious();
      // prev cannot be null, else first would be the same as last
      prev.setNext(null);
      last = prev;

      entry.setNext(null);
      entry.setPrevious(null);
      return;
    }

    final MapEntry previous = entry.getPrevious();
    final MapEntry next = entry.getNext();
    // next cannot be null, else 'entry' would be the last entry, and we checked that already ..
    previous.setNext(next);
    next.setPrevious(previous);

    entry.setNext(null);
    entry.setPrevious(null);
  }

  /**
   * Returns the number of items in this map.
   *
   * @return the number of items in the map.
   */
  public int size()
  {
    return map.size();
  }

  /**
   * Checks whether this map is empty.
   *
   * @return true, if the map is empty, false otherwise.
   */
  public boolean isEmpty()
  {
    return first == null;
  }

  /**
   * Returns the defined maximum size.
   *
   * @return the defines maximum size.
   */
  public int getMaximumSize()
  {
    return cacheSize;
  }

  /**
   * Validates the map's internal datastructures. There should be no need to call this method manually.
   */
  public void validate()
  {
    if (first == null)
    {
      return;
    }

    if (first.getPrevious() != null)
    {
      throw new IllegalStateException();
    }
    if (this.last.getNext() != null)
    {
      throw new IllegalStateException();
    }

    int counter = 0;
    MapEntry p = null;
    MapEntry entryFromStart = first;
    while (entryFromStart != null)
    {
      if (entryFromStart.getPrevious() != p)
      {
        throw new IllegalStateException();
      }
      p = entryFromStart;
      entryFromStart = entryFromStart.getNext();
      counter += 1;
    }

    if (counter != size())
    {
      throw new IllegalStateException();
    }

    int fromEndCounter = 0;
    MapEntry n = null;
    MapEntry entryFromEnd = this.last;
    while (entryFromEnd != null)
    {
      if (entryFromEnd.getNext() != n)
      {
        throw new IllegalStateException();
      }
      n = entryFromEnd;
      entryFromEnd = entryFromEnd.getPrevious();
      fromEndCounter += 1;
    }

    if (n != first)
    {
      throw new IllegalStateException();
    }

    if (fromEndCounter != size())
    {
      throw new IllegalStateException();
    }

    if (size() > cacheSize)
    {
      throw new IllegalStateException();
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    final LFUMap map = (LFUMap) super.clone();
    map.map = (HashMap) map.clone();
    map.map.clear();
    MapEntry entry = first;
    while (entry != null)
    {
      map.put(entry.getKey(), entry.getValue());
      entry = entry.getNext();
    }
    return map;
  }
}
