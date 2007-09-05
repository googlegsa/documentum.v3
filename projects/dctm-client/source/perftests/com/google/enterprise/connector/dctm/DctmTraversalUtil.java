package com.google.enterprise.connector.dctm;

import java.util.Iterator;

import com.google.enterprise.connector.pusher.DocPusher;
import com.google.enterprise.connector.pusher.GsaFeedConnection;
import com.google.enterprise.connector.pusher.PushException;

import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmTraversalUtil {

	public static void runTraversal(TraversalManager queryTraversalManager,
			int batchHint) throws RepositoryException, PushException {

		DctmTraversalManager dctmTM = (DctmTraversalManager) queryTraversalManager;
		dctmTM.setBatchHint(batchHint);
		System.out.println(batchHint);

		DocumentList documentList = dctmTM.startTraversal();
		// int nb=resultSet.size();
		// System.out.println("nb vaut "+nb);
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

		DocPusher push = new DocPusher(
				new GsaFeedConnection("8.6.49.36", 19900));
		// DocPusher push = new DocPusher(new GsaFeedConnection("swp-gsa-demo",
		// 19900));
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
					// for
					// example, if it receives a shutdown request

					break;
				}
				Iterator iteri = pm.getPropertyNames().iterator();
				int k = 0;
				while (iteri.hasNext()) {
					iteri.next();
					k++;
				}
				System.out.println("counter " + counter + " " + k);
				System.out.println(pm.findProperty(
						SpiConstants.PROPNAME_DISPLAYURL).nextValue());
				push.take(pm, "dctm");

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
