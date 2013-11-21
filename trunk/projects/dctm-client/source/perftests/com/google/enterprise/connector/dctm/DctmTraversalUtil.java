// Copyright 2007 Google Inc.
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

import com.google.enterprise.connector.pusher.DocPusher;
import com.google.enterprise.connector.pusher.FeedException;
import com.google.enterprise.connector.pusher.GsaFeedConnection;
import com.google.enterprise.connector.pusher.PushException;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.traversal.FileSizeLimitInfo;
import com.google.enterprise.connector.util.filter.DocumentFilterChain;

import java.net.MalformedURLException;

public class DctmTraversalUtil {
  public static void runTraversal(TraversalManager queryTraversalManager,
      int batchHint) throws RepositoryException, PushException, FeedException {
    DctmTraversalManager dctmTM = (DctmTraversalManager) queryTraversalManager;
    dctmTM.setBatchHint(batchHint);
    System.out.println(batchHint);

    DocumentList documentList = dctmTM.startTraversal();
    // int nb = resultSet.size();
    // System.out.println("nb vaut " + nb);
    // The real connector manager will not always start from the beginning.
    // It will start from the beginning if it receives an explicit admin
    // command to do so, or if it thinks that it has never run this
    // connector
    // before. It decides whether it has run a connector before by storing
    // every checkpoint it receives from
    // the connector. If it can find no stored checkpoint, it assumes that
    // it has never run this connector before and starts from the beginning,
    // as here.
    if (documentList == null) {
      // in this test program, we will stop in this situation. The real
      // connector manager might wait for a while, then try again
      return;
    }

    DocPusher push = null;
    try {
      push = new DocPusher(new GsaFeedConnection(null, "gogol", 19900, -1),
          "dctm", new FileSizeLimitInfo(), new DocumentFilterChain(), null);

    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    int counter = 0;

    while (true) {
      counter = 0;
      Document pm = null;
      while ((pm = documentList.nextDocument()) != null) {
        System.out.println("pm change");
        counter++;

        if (counter == batchHint) {
          System.out.println("counter == batchhint !!!!");
          // this test program only takes batchHint results from each
          // resultSet. The real connector manager may take fewer -
          // for example, if it receives a shutdown request
          break;
        }
        int k = pm.getPropertyNames().size();
        System.out.println("counter " + counter + ",  num properties " + k);
        System.out.println(pm.findProperty(
            SpiConstants.PROPNAME_DISPLAYURL).nextValue());
        push.take(pm, null);
      }
      String checkpoint = "";
      if (counter != 0) {
        System.out.println("appel checkpoint");
        checkpoint = documentList.checkpoint();
        System.out.println("appel checkpoint " + checkpoint);
      }
      dctmTM.resumeTraversal(checkpoint);
    }
  }
}
