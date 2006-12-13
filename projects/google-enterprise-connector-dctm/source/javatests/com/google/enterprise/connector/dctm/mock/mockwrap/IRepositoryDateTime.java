package com.google.enterprise.connector.dctm.mock.mockwrap;

import com.google.enterprise.connector.mock.MockRepositoryDateTime;

public interface IRepositoryDateTime extends Comparable{
	public MockRepositoryDateTime getmrDateTime();
	public int getTicks();
}