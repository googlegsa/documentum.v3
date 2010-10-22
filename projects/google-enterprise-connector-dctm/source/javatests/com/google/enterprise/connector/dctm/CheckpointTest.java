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
import java.util.Calendar;
import java.util.Date;

public class CheckpointTest extends TestCase {
  /** The current date without milliseconds. */
  private Date now;

  @Override
  protected void setUp() {
    Calendar nowCalendar = Calendar.getInstance();
    nowCalendar.set(Calendar.MILLISECOND, 0);
    now = nowCalendar.getTime();
  }

  public void testNullCheckpoint() throws RepositoryException {
    Checkpoint empty = new Checkpoint();
    assertNull(empty.toString(), empty.asString());
  }

  public void testGetInsertId() throws RepositoryException {
    Checkpoint checkpoint = new Checkpoint("{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    String uuid = checkpoint.getInsertId();
    assertNotNull(uuid);
    assertEquals(uuid, "090000018000e100");
  }

  public void testGetInsertDate() throws RepositoryException {
    Checkpoint checkpoint = new Checkpoint("{\"uuid\":\"090000018000e100\","
        + "\"lastModified\":\"2007-01-02 13:58:10\"}");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String modifDate = dateFormat.format(checkpoint.getInsertDate());
    assertNotNull(modifDate);
    assertEquals(modifDate, "2007-01-02 13:58:10");
  }

  public void testSetInsertCheckpoint() throws RepositoryException {
    Checkpoint checkpoint = new Checkpoint();
    checkpoint.setInsertCheckpoint(now, "id");
    assertEquals(now, checkpoint.getInsertDate());
    assertEquals("id", checkpoint.getInsertId());
  }

  public void testSetDeleteCheckpoint() throws RepositoryException {
    Checkpoint checkpoint = new Checkpoint();
    checkpoint.setDeleteCheckpoint(now, "id");
    assertEquals(now, checkpoint.getDeleteDate());
    assertEquals("id", checkpoint.getDeleteId());
  }

  public void testAsString() throws RepositoryException {
    Checkpoint input = new Checkpoint();
    input.setInsertCheckpoint(now, "id");
    input.setDeleteCheckpoint(now, "id");
    Checkpoint output = new Checkpoint(input.asString());
    assertEquals(input.asString(), output.asString());
  }

  public void testRestore() throws RepositoryException {
    Checkpoint checkpoint = new Checkpoint();
    checkpoint.setInsertCheckpoint(now, "id");
    checkpoint.setDeleteCheckpoint(now, "id");
    String expected = checkpoint.asString();

    assertEquals(now, checkpoint.getInsertDate());
    assertEquals("id", checkpoint.getInsertId());
    assertEquals(now, checkpoint.getDeleteDate());
    assertEquals("id", checkpoint.getDeleteId());

    Date later = new Date(now.getTime() + 1000);
    assertFalse(now + " vs. " + later, now.equals(later));
    checkpoint.setInsertCheckpoint(later, "id2");
    assertTrue(checkpoint.hasChanged());
    assertFalse(now.toString(), now.equals(checkpoint.getInsertDate()));
    assertFalse("id", "id".equals(checkpoint.getInsertId()));
    checkpoint.restore();

    checkpoint.setDeleteCheckpoint(later, "id2");
    assertTrue(checkpoint.hasChanged());
    assertFalse(now.toString(), now.equals(checkpoint.getDeleteDate()));
    assertFalse("id", "id".equals(checkpoint.getDeleteId()));
    checkpoint.restore();

    assertEquals(expected, checkpoint.asString());
  }
}
