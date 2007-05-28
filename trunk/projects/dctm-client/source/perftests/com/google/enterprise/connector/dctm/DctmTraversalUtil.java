package com.google.enterprise.connector.dctm;

import java.util.Iterator;

import com.google.enterprise.connector.pusher.DocPusher;
import com.google.enterprise.connector.pusher.GsaFeedConnection;

import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.PropertyMapList;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;


public class DctmTraversalUtil {

	public static void runTraversal(
			TraversalManager queryTraversalManager, int batchHint)
			throws RepositoryException {

		DctmTraversalManager dctmTM = (DctmTraversalManager) queryTraversalManager;
		dctmTM.setBatchHint(batchHint);
		System.out.println(batchHint);

		PropertyMapList resultSet = dctmTM.startTraversal();
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
		if (resultSet == null) {
			// in this test program, we will stop in this situation. The real
			// connector manager might wait for a while, then try again
			return;
		}

		DocPusher push = new DocPusher(new GsaFeedConnection("8.6.46.39",
				19900));
//		DocPusher push = new DocPusher(new GsaFeedConnection("swp-gsa-demo",
//				19900));

		while (true) {
			int counter = 0;

			PropertyMap pm = null;
			for (Iterator iter = resultSet.iterator(); iter.hasNext();) {
				pm = (PropertyMap) iter.next();
				counter++;

				if (counter == batchHint) {
					System.out.println("counter == batchhint !!!!");
					// this test program only takes batchHint results from each
					// resultSet. The real connector manager may take fewer -
					// for
					// example, if it receives a shutdown request

					break;
				}
				Iterator iteri = pm.getProperties();
				int k = 0;
				while (iteri.hasNext()) {
					iteri.next();
					k++;
				}
				System.out.println("counter " + counter + " " + k);
				System.out.println(pm.getProperty(SpiConstants.PROPNAME_DISPLAYURL).getValue().getString());
				push.take(pm, "dctm");

			}

			if (counter == 0) {
				// this test program stops if it receives zero results in a
				// resultSet.
				// the real connector Manager might wait a while, then try again
				break;
			}
			if (pm == null) {
				System.out.println("pm null");
			}
			String checkPointString = dctmTM.checkpoint(pm);

			resultSet = dctmTM.resumeTraversal(checkPointString);

			// the real connector manager will call checkpoint (as here) as soon
			// as possible after processing the last property map it wants to
			// process.
			// It would then store the checkpoint string it received in
			// persistent
			// store.
			// Unlike here, it might not then immediately turn around and call
			// resumeTraversal. For example, it may have received a shutdown
			// command,
			// so it won't call resumeTraversal again until it starts up again.
			// Or, it may be running this connector on a schedule and there may
			// be a
			// scheduled pause.
		}
	}

}
