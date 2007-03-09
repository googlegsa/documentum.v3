package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.RepositoryException;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DctmQueryTraversalManagerTest test = new DctmQueryTraversalManagerTest();
		try {
			// test.testStartTraversal();
			throw new RepositoryException();
		} catch (RepositoryException e) {
			System.out.println("Probleme");
			e.printStackTrace();
		}

	}

}
