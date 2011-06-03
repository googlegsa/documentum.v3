// Copyright 2010 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CheckpointTest extends TestCase {
  /** Gets the current date without milliseconds. */
  private static Date now() {
    Calendar nowCalendar = Calendar.getInstance();
    nowCalendar.set(Calendar.MILLISECOND, 0);
    return nowCalendar.getTime();
  }

  private static Date later(Date earlier) {
    return new Date(earlier.getTime() + 1000);
  }

  /** Gets a list of the given size. */
  private static List<String> getWhereClause(int size) {
    List<String> list = new ArrayList<String>(size);
    for (int i = 0; i < size; i++) {
      // TODO: Eventually we will need fake where clause strings here.
      list.add(null);
    }
    return list;
  }

  private static Checkpoint getCheckpoint(int size)
      throws RepositoryException {
    return new Checkpoint(getWhereClause(size));
  }

  private static Checkpoint getCheckpoint(int size, String checkpoint)
      throws RepositoryException {
    return new Checkpoint(getWhereClause(size), checkpoint);
  }

  /** The current date without milliseconds. */
  private static Date NOW = now();

  /** A later date without milliseconds. */
  private static Date LATER = later(NOW);

  public void testCheckpoint_noargs() throws RepositoryException {
    Checkpoint empty = getCheckpoint(0);
    assertNull(empty.toString(), empty.asString());
  }

  public void testCheckpoint_null() throws RepositoryException {
    Checkpoint empty =
        getCheckpoint(0, "{\"uuid\":null,\"lastModified\":null}");
    assertNull(empty.toString(), empty.asString());
  }

  public void testCheckpoint_noindex() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(0, "{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    assertEquals(0, checkpoint.getInsertIndex());
  }

  public void testCheckpoint_index() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(2,
        "{\"uuid\":[\"090000018000e100\"],"
        + "\"lastModified\":[\"2007-01-02 13:58:10\"], \"index\":1}");
    assertEquals(1, checkpoint.getInsertIndex());
  }

  public void testCheckpoint_indexonly() throws RepositoryException {
    try {
      getCheckpoint(2, "{\"index\":1}");
      fail("Expected an invalid checkpoint exception");
    } catch (RepositoryException e) {
    }
  }

  public void testCheckpoint_indextoolarge() throws RepositoryException {
    try {
      getCheckpoint(1, "{\"uuid\":[\"090000018000e100\"],"
          + "\"lastModified\":[\"2007-01-02 13:58:10\"], \"index\":1}");
      fail("Expected an invalid checkpoint exception");
    } catch (RepositoryException e) {
    }
  }

  public void testCheckpoint_noindexnext() throws RepositoryException {
    Checkpoint input = getCheckpoint(45, "{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    assertEquals(0, input.getInsertIndex());
    Checkpoint output = getCheckpoint(45, input.asString());
    assertEquals(1, output.getInsertIndex());
  }

  public void testCheckpoint_emptynoindexnext() throws RepositoryException {
    Checkpoint input = getCheckpoint(0, "{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    assertEquals(0, input.getInsertIndex());
    Checkpoint output = getCheckpoint(0, input.asString());
    assertEquals(0, output.getInsertIndex());
  }

  public void testCheckpoint_singletonnoindexnext() throws RepositoryException {
    Checkpoint input = getCheckpoint(1, "{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    assertEquals(0, input.getInsertIndex());
    Checkpoint output = getCheckpoint(1, input.asString());
    assertEquals(0, output.getInsertIndex());
  }

  public void testCheckpoint_indexnext() throws RepositoryException {
    Checkpoint input = getCheckpoint(3, "{\"uuid\":[\"090000018000e100\"],"
        + "\"lastModified\":[\"2007-01-02 13:58:10\"], \"index\":1}");
    assertEquals(1, input.getInsertIndex());
    Checkpoint output = getCheckpoint(3, input.asString());
    assertEquals(2, output.getInsertIndex());
  }

  public void testCheckpoint_indexnextwrap() throws RepositoryException {
    Checkpoint input = getCheckpoint(3,
        "{\"uuid\":[\"090000018000e100\",\"090000018000e222\"],"
        + "\"lastModified\":[\"2007-01-02 13:58:10\",\"2008-01-02 13:58:10\"],"
        + "\"index\":2}");
    assertEquals(2, input.getInsertIndex());
    Checkpoint output = getCheckpoint(3, input.asString());
    assertEquals(0, output.getInsertIndex());
  }

  public void testCheckpoint_indexnextnull() throws RepositoryException {
    Checkpoint input = getCheckpoint(3,
        "{\"uuid\":[null], \"lastModified\":[null], \"index\":1}");
    assertEquals(1, input.getInsertIndex());
    assertNull(input.getInsertId(), input.getInsertId());
    Checkpoint output = getCheckpoint(3, input.asString());
    assertEquals(2, output.getInsertIndex());
    assertNull(output.getInsertId(), output.getInsertId());

    String str = output.asString();
    assertTrue(str, str.contains("\"index\":0"));
    assertTrue(str, str.contains("\"lastModified\":[null,null,null]"));
    assertTrue(str, str.contains("\"uuid\":[null,null,null]"));
  }

  public void testCheckpoint_elementmissing() throws RepositoryException {
    try {
      getCheckpoint(3, "{\"uuid\":[\"090000018000e100\"],"
        + "\"lastModified\":[\"2007-01-02 13:58:10\"], \"index\":2}");
      fail("Expected an invalid checkpoint exception");
    } catch (RepositoryException e) {
    }
  }

  public void testCheckpoint_migration() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(0,
        "{\"uuidToRemove\":\"090000018000e100\","
        + "\"lastRemoveDate\":\"2007-01-02 13:58:10\"}");

    String str = checkpoint.asString();
    assertTrue(str, str.contains("\"uuidToRemove\":\"090000018000e100\""));
    assertTrue(str, str.contains("\"lastRemoved\":\"2007-01-01 13:58:10\""));
    assertFalse(str, str.contains("\"lastRemoveDate\":"));
  }

  public void testGetInsertId() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(0, "{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    String uuid = checkpoint.getInsertId();
    assertNotNull(uuid);
    assertEquals(uuid, "090000018000e100");
  }

  public void testGetInsertDate() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(0, "{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String modifDate = dateFormat.format(checkpoint.getInsertDate());
    assertNotNull(modifDate);
    assertEquals(modifDate, "2007-01-02 13:58:10");
  }

  public void testSetInsertCheckpoint() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(1);
    checkpoint.setInsertCheckpoint(NOW, "id");
    assertEquals(NOW, checkpoint.getInsertDate());
    assertEquals("id", checkpoint.getInsertId());
  }

  public void testSetDeleteCheckpoint() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(1);
    checkpoint.setDeleteCheckpoint(NOW, "id");
    assertEquals(NOW, checkpoint.getDeleteDate());
    assertEquals("id", checkpoint.getDeleteId());
  }

  public void testRestore() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(1);
    checkpoint.setInsertCheckpoint(NOW, "id");
    checkpoint.setDeleteCheckpoint(NOW, "id");
    String expected = checkpoint.asString();

    assertEquals(NOW, checkpoint.getInsertDate());
    assertEquals("id", checkpoint.getInsertId());
    assertEquals(NOW, checkpoint.getDeleteDate());
    assertEquals("id", checkpoint.getDeleteId());

    assertFalse(NOW + " vs. " + LATER, NOW.equals(LATER));
    checkpoint.setInsertCheckpoint(LATER, "id2");
    assertTrue(checkpoint.hasChanged());
    assertFalse(NOW.toString(), NOW.equals(checkpoint.getInsertDate()));
    assertFalse("id", "id".equals(checkpoint.getInsertId()));
    checkpoint.restore();

    checkpoint.setDeleteCheckpoint(LATER, "id2");
    assertTrue(checkpoint.hasChanged());
    assertFalse(NOW.toString(), NOW.equals(checkpoint.getDeleteDate()));
    assertFalse("id", "id".equals(checkpoint.getDeleteId()));
    checkpoint.restore();

    assertEquals(expected, checkpoint.asString());
  }

  public void testAdvance_one() throws RepositoryException {
    Checkpoint input = getCheckpoint(2, "{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    Checkpoint output = getCheckpoint(2, input.asString());

    input.advance();
    assertEquals(input.asString(), output.asString());
  }

  public void testAdvance_two() throws RepositoryException {
    Checkpoint input = getCheckpoint(2,
        "{\"uuid\":[\"090000018000e100\",\"090000018000e222\"],"
        + "\"lastModified\":[\"2007-01-02 13:58:10\",\"2008-01-02 13:58:10\"],"
        + "\"index\":0}");
    Checkpoint output = getCheckpoint(2, input.asString());

    input.advance();
    assertEquals(input.asString(), output.asString());
  }

  public void testAdvance_restore() throws RepositoryException {
    Checkpoint input = getCheckpoint(2,
        "{\"uuid\":[\"090000018000e100\",\"090000018000e222\"],"
        + "\"lastModified\":[\"2007-01-02 13:58:10\",\"2008-01-02 13:58:10\"],"
        + "\"index\":0}");
    input.setInsertCheckpoint(LATER, "id");
    Checkpoint output = getCheckpoint(2, input.asString());

    input.advance();
    input.restore();
    assertEquals(input.asString(), output.asString());
  }

  public void testAdvance_wrap() throws RepositoryException {
    Checkpoint checkpoint = getCheckpoint(3);
    boolean more = true;
    for (int i = 0; i < 3; i++) {
      assertTrue(more);
      assertEquals("i = " + i, i, checkpoint.getInsertIndex());

      // This is a smoke test; the value is null because that's what
      // this test is setting up. We're just making sure we can
      // retrieve the insert ID without error.
      assertNull("i = " + i, checkpoint.getInsertId());

      more = checkpoint.advance();
    }
    assertFalse(more);
    assertEquals(0, checkpoint.getInsertIndex());
  }

  public void testAsString() throws RepositoryException {
    Checkpoint input = getCheckpoint(1);
    input.setInsertCheckpoint(NOW, "id");
    input.setDeleteCheckpoint(NOW, "id");
    Checkpoint output = getCheckpoint(1, input.asString());
    assertEquals(input.asString(), output.asString());
  }
}
