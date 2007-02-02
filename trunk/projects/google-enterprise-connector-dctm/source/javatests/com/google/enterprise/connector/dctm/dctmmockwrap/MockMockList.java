package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.mock.MockRepositoryPropertyList;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

/**
 * @author jpasquon This list will be utilized only by the MockJcrQueryResult
 *         (which constructor takes a List as parameter) Only Iterator is called
 *         and only next and hasNext are called on this parameter. The most
 *         important thing to ensure is that the Object returned by next can be
 *         cast as a MockRepositoryDocument.
 */
public class MockMockList implements List {

	private Set set = new HashSet(1, 1);

	protected MockMockList(String[] ids, ISessionManager sessionManager)
			throws LoginException, RepositoryException {
		String claimant = sessionManager.getIdentity(
				sessionManager.getDocbaseName()).getUser();
		MockRepositoryDocumentStore store = ((MockDmSession) sessionManager
				.getSession(sessionManager.getDocbaseName())).getStore();
		for (int j = 0; j < ids.length; j++) {
			MockRepositoryDocument doc = store.getDocByID(ids[j]);// if no
																	// 'content'
																	// defined,
																	// doc==null
			if (doc != null) {
				MockRepositoryPropertyList pl = doc.getProplist();
				MockRepositoryProperty p = pl.getProperty("acl");
				if (p != null) {// If doc contains acls
					String[] acl = p.getValues();
					for (int i = 0; i < acl.length; i++) {
						if (claimant.equals(acl[i])) {
							set.add(doc);
						}
					}
				} else if (pl.getProperty("google:ispublic") != null) {
					if (pl.getProperty("google:ispublic").getValue().equals(
							"true")) {
						set.add(doc);
					}
				}
			}
		}
	}

	public Iterator iterator() {
		return set.iterator();
	}

	public int size() {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();

	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public Object get(int arg0) {
		throw new UnsupportedOperationException();
	}

	public Object remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	public void add(int arg0, Object arg1) {
		throw new UnsupportedOperationException();

	}

	public int indexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public int lastIndexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean add(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int arg0, Collection arg1) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public List subList(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	public ListIterator listIterator() {
		throw new UnsupportedOperationException();
	}

	public ListIterator listIterator(int arg0) {
		throw new UnsupportedOperationException();
	}

	public Object set(int arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	public Object[] toArray(Object[] arg0) {
		throw new UnsupportedOperationException();
	}

}
