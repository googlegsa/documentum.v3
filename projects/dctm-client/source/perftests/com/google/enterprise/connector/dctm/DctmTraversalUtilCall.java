// Copyright (C) 2006-2009 Google Inc.
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

import com.google.enterprise.connector.pusher.FeedException;
import com.google.enterprise.connector.pusher.PushException;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmTraversalUtilCall {
  public static void main(String[] args) {
    final boolean DFC = true;

    String user, password, clientX, docbase;
    if (DFC) {
      user = "queryUser";
      password = "p@ssw0rd";
      clientX = "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX";
      docbase = "gsadctm";
    } else {
      user = "mark";
      password = "mark";
      clientX = "com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient";
      docbase = "MockRepositoryEventLog7.txt";
    }

    Session session = null;
    Connector connector = null;
    TraversalManager qtm = null;

    connector = new DctmConnector();

    /**
     * Simulation of the setters used by Instance.xml
     */
    ((DctmConnector) connector).setLogin(user);
    ((DctmConnector) connector).setPassword(password);
    ((DctmConnector) connector).setDocbase(docbase);
    ((DctmConnector) connector)
        .setWebtop_display_url("http://swp-vm-wt:8080/webtop/drl/objectId/");
    ((DctmConnector) connector).setClientX(clientX);
    ((DctmConnector) connector)
        .setWhere_clause("and folder('/test_docs',descend)");
    ((DctmConnector) connector).setIs_public("false");
    ((DctmConnector) connector)
        .setIncluded_meta(DmInitialize.included_meta);
    /**
     * End simulation
     */

    try {
      session = (DctmSession) connector.login();
      qtm = (DctmTraversalManager) session.getTraversalManager();
      DctmTraversalUtil.runTraversal(qtm, 10);
    } catch (RepositoryLoginException le) {
      System.out.println("Root Cause : " + le.getCause()
          + " ; Message : " + le.getMessage());
    } catch (RepositoryException re) {
      System.out.println("Root Cause : " + re.getCause()
          + " ; Message : " + re.getMessage());
    } catch (PushException e) {
      System.out.println("Root Cause : " + e.getCause() + " ; Message : "
          + e.getMessage());
    } catch (FeedException e) {
      System.out.println("Root Cause : " + e.getCause() + " ; Message : "
          + e.getMessage());
    }
  }
}
