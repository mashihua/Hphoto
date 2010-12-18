/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hbase;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.hbase.HColumnDescriptor.CompressionType;
import org.apache.hadoop.hbase.util.Writables;
import org.apache.hadoop.io.Text;

/**
 * Tests user specifiable time stamps putting, getting and scanning.  Also
 * tests same in presence of deletes.  Test cores are written so can be
 * run against an HRegion and against an HTable: i.e. both local and remote.
 */
public class TestTimestamp extends HBaseTestCase {
  private static final long T0 = 10L;
  private static final long T1 = 100L;
  private static final long T2 = 200L;
  
  private static final String COLUMN_NAME = "contents:";
  
  private static final Text COLUMN = new Text(COLUMN_NAME);
  private static final Text[] COLUMNS = {COLUMN};
  private static final Text ROW = new Text("row");
  
  // When creating column descriptor, how many versions of a cell to allow.
  private static final int VERSIONS = 3;

  /**
   * Test that delete works according to description in <a
   * href="https://issues.apache.org/jira/browse/HADOOP-1784">hadoop-1784</a>.
   * @throws IOException
   */
  public void testDelete() throws IOException {
    final HRegion r = createRegion();
    try {
     doTestDelete(new HRegionIncommon(r), new FlushCache() {
      public void flushcache() throws IOException {
        r.flushcache(false);
      }
     });
    } finally {
      r.close();
      r.getLog().closeAndDelete();
    }
  }

  /**
   * Test scanning against different timestamps.
   * @throws IOException
   */
  public void testTimestampScanning() throws IOException {
    final HRegion r = createRegion();
    try {
      doTestTimestampScanning(new HRegionIncommon(r), new FlushCache() {
        public void flushcache() throws IOException {
          r.flushcache(false);
        }
       });
    } finally {
      r.close();
      r.getLog().closeAndDelete();
    }
  }

  /**
   * Basic test of timestamps.
   * Do the above tests from client side.
   * @throws IOException
   */
  public void testTimestamps() throws IOException {
    final MiniHBaseCluster cluster = new MiniHBaseCluster(this.conf, 1);
    try {
      HTable t = createTable();
      Incommon incommon = new HTableIncommon(t);
      doTestDelete(incommon, new FlushCache() {
        public void flushcache() throws IOException {
          cluster.flushcache();
        }
       });
      
      // Perhaps drop and readd the table between tests so the former does
      // not pollute this latter?  Or put into separate tests.
      doTestTimestampScanning(incommon, new FlushCache() {
        public void flushcache() throws IOException {
          cluster.flushcache();
        }
       });
    } catch (Exception e) {
      cluster.shutdown();
    }
  }
  
  /*
   * Run test that delete works according to description in <a
   * href="https://issues.apache.org/jira/browse/HADOOP-1784">hadoop-1784</a>.
   * @param incommon
   * @param flusher
   * @throws IOException
   */
  private void doTestDelete(final Incommon incommon, FlushCache flusher)
  throws IOException {
    // Add values at various timestamps (Values are timestampes as bytes).
    put(incommon, T0);
    put(incommon, T1);
    put(incommon, T2);
    put(incommon);
    // Verify that returned versions match passed timestamps.
    assertVersions(incommon, new long [] {HConstants.LATEST_TIMESTAMP, T2, T1});
    // If I delete w/o specifying a timestamp, this means I'm deleting the
    // latest.
    delete(incommon);
    // Verify that I get back T2 through T1 -- that the latest version has
    // been deleted.
    assertVersions(incommon, new long [] {T2, T1, T0});
    
    // Flush everything out to disk and then retry
    flusher.flushcache();
    assertVersions(incommon, new long [] {T2, T1, T0});
    
    // Now add, back a latest so I can test remove other than the latest.
    put(incommon);
    assertVersions(incommon, new long [] {HConstants.LATEST_TIMESTAMP, T2, T1});
    delete(incommon, T2);
    assertVersions(incommon, new long [] {HConstants.LATEST_TIMESTAMP, T1, T0});
    // Flush everything out to disk and then retry
    flusher.flushcache();
    assertVersions(incommon, new long [] {HConstants.LATEST_TIMESTAMP, T1, T0});
    
    // Now try deleting all from T2 back inclusive (We first need to add T2
    // back into the mix and to make things a little interesting, delete and
    // then readd T1.
    put(incommon, T2);
    delete(incommon, T1);
    put(incommon, T1);
    incommon.deleteAll(ROW, COLUMN, T2);
    // Should only be current value in set.  Assert this is so
    assertOnlyLatest(incommon, HConstants.LATEST_TIMESTAMP);
    
    // Flush everything out to disk and then redo above tests
    flusher.flushcache();
    assertOnlyLatest(incommon, HConstants.LATEST_TIMESTAMP);
  }
  
  private void assertOnlyLatest(final Incommon incommon,
      final long currentTime)
  throws IOException {
    byte [][] bytesBytes = incommon.get(ROW, COLUMN, 3/*Ask for too much*/);
    assertEquals(1, bytesBytes.length);
    long time = Writables.bytesToLong(bytesBytes[0]);
    assertEquals(time, currentTime);
    assertNull(incommon.get(ROW, COLUMN, T1, 3 /*Too many*/));
    assertTrue(assertScanContentTimestamp(incommon, T1) == 0);
  }
  
  /*
   * Assert that returned versions match passed in timestamps and that results
   * are returned in the right order.  Assert that values when converted to
   * longs match the corresponding passed timestamp.
   * @param r
   * @param tss
   * @throws IOException
   */
  private void assertVersions(final Incommon incommon, final long [] tss)
  throws IOException {
    // Assert that 'latest' is what we expect.
    byte [] bytes = incommon.get(ROW, COLUMN);
    assertEquals(Writables.bytesToLong(bytes), tss[0]);
    // Now assert that if we ask for multiple versions, that they come out in
    // order.
    byte [][] bytesBytes = incommon.get(ROW, COLUMN, tss.length);
    assertEquals(bytesBytes.length, tss.length);
    for (int i = 0; i < bytesBytes.length; i++) {
      long ts = Writables.bytesToLong(bytesBytes[i]);
      assertEquals(ts, tss[i]);
    }
    // Specify a timestamp get multiple versions.
    bytesBytes = incommon.get(ROW, COLUMN, tss[0], bytesBytes.length - 1);
    for (int i = 1; i < bytesBytes.length; i++) {
      long ts = Writables.bytesToLong(bytesBytes[i]);
      assertEquals(ts, tss[i]);
    }
    // Test scanner returns expected version
    assertScanContentTimestamp(incommon, tss[0]);
  }
  
  /*
   * Run test scanning different timestamps.
   * @param incommon
   * @param flusher
   * @throws IOException
   */
  private void doTestTimestampScanning(final Incommon incommon,
      final FlushCache flusher)
  throws IOException {
    // Add a couple of values for three different timestamps.
    put(incommon, T0);
    put(incommon, T1);
    put(incommon, HConstants.LATEST_TIMESTAMP);
    // Get count of latest items.
    int count = assertScanContentTimestamp(incommon,
      HConstants.LATEST_TIMESTAMP);
    // Assert I get same count when I scan at each timestamp.
    assertEquals(count, assertScanContentTimestamp(incommon, T0));
    assertEquals(count, assertScanContentTimestamp(incommon, T1));
    // Flush everything out to disk and then retry
    flusher.flushcache();
    assertEquals(count, assertScanContentTimestamp(incommon, T0));
    assertEquals(count, assertScanContentTimestamp(incommon, T1));
  }
  
  /*
   * Assert that the scan returns only values < timestamp. 
   * @param r
   * @param ts
   * @return Count of items scanned.
   * @throws IOException
   */
  private int assertScanContentTimestamp(final Incommon in, final long ts)
  throws IOException {
    HScannerInterface scanner =
      in.getScanner(COLUMNS, HConstants.EMPTY_START_ROW, ts);
    int count = 0;
    try {
      HStoreKey key = new HStoreKey();
      TreeMap<Text, byte []>value = new TreeMap<Text, byte[]>();
      while (scanner.next(key, value)) {
        assertTrue(key.getTimestamp() <= ts);
        // Content matches the key or HConstants.LATEST_TIMESTAMP.
        // (Key does not match content if we 'put' with LATEST_TIMESTAMP).
        long l = Writables.bytesToLong(value.get(COLUMN));
        assertTrue(key.getTimestamp() == l ||
          HConstants.LATEST_TIMESTAMP == l);
        count++;
        value.clear();
      }
    } finally {
      scanner.close(); 
    }
    return count;
  }
  
  private void put(final Incommon loader, final long ts)
  throws IOException {
    put(loader, Writables.longToBytes(ts), ts);
  }
  
  private void put(final Incommon loader)
  throws IOException {
    long ts = HConstants.LATEST_TIMESTAMP;
    put(loader, Writables.longToBytes(ts), ts);
  }
  
  /*
   * Put values.
   * @param loader
   * @param bytes
   * @param ts
   * @throws IOException
   */
  private void put(final Incommon loader, final byte [] bytes,
    final long ts)
  throws IOException {
    long lockid = loader.startBatchUpdate(ROW);
    loader.put(lockid, COLUMN, bytes);
    if (ts == HConstants.LATEST_TIMESTAMP) {
      loader.commit(lockid);
    } else {
      loader.commit(lockid, ts);
    }
  }
  
  private void delete(final Incommon loader) throws IOException {
    delete(loader, HConstants.LATEST_TIMESTAMP);
  }

  private void delete(final Incommon loader, final long ts) throws IOException {
    long lockid = loader.startBatchUpdate(ROW);
    loader.delete(lockid, COLUMN);
    if (ts == HConstants.LATEST_TIMESTAMP) {
      loader.commit(lockid);
    } else {
      loader.commit(lockid, ts);
    }
  }
  
  /* 
   * Create a table named TABLE_NAME.
   * @return An instance of an HTable connected to the created table.
   * @throws IOException
   */
  private HTable createTable() throws IOException {
    HTableDescriptor desc = new HTableDescriptor(getName());
    desc.addFamily(new HColumnDescriptor(COLUMN_NAME));
    HBaseAdmin admin = new HBaseAdmin(conf);
    admin.createTable(desc);
    return new HTable(conf, new Text(getName()));
  }
  
  private HRegion createRegion() throws IOException {
    HLog hlog = new HLog(this.localFs, this.testDir, this.conf);
    HTableDescriptor htd = createTableDescriptor(getName());
    htd.addFamily(new HColumnDescriptor(COLUMN, VERSIONS,
      CompressionType.NONE, false, Integer.MAX_VALUE, null));
    HRegionInfo hri = new HRegionInfo(1, htd, null, null);
    return new HRegion(testDir, hlog, this.localFs, this.conf, hri, null);
  }
}