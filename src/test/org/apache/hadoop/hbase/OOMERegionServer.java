/**
 * Copyright 2007 The Apache Software Foundation
 *
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
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.io.BatchUpdate;
import org.apache.hadoop.io.Text;

/**
 * A region server that will OOME.
 * Everytime {@link #batchUpdate(Text, long, BatchUpdate)} is called, we add
 * keep around a reference to the batch.  Use this class to test OOME extremes.
 * Needs to be started manually as in
 * <code>${HBASE_HOME}/bin/hbase ./bin/hbase org.apache.hadoop.hbase.OOMERegionServer start</code>.
 */
public class OOMERegionServer extends HRegionServer {
  private List<BatchUpdate> retainer = new ArrayList<BatchUpdate>();

  public OOMERegionServer(Configuration conf) throws IOException {
    super(conf);
  }

  public OOMERegionServer(HServerAddress address, Configuration conf)
  throws IOException {
    super(address, conf);
  }
  
  @Override
  public void batchUpdate(Text regionName, long timestamp, BatchUpdate b)
      throws IOException {
    super.batchUpdate(regionName, timestamp, b);
    for (int i = 0; i < 30; i++) {
      // Add the batch update 30 times to bring on the OOME faster.
      this.retainer.add(b);
    }
  }
  
  public static void main(String[] args) {
    HRegionServer.doMain(args, OOMERegionServer.class);
  }
}