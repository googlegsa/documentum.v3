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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;

import javax.jcr.Value;
import javax.jcr.ValueFormatException;

public class MockDmValue implements IValue {
  private final Value value;

  protected MockDmValue(Value val) {
    value = val;
  }

  @Override
  public String asString() throws RepositoryException {
    try {
      return value.getString();
    } catch (ValueFormatException e) {
      throw new RepositoryException(e);
    } catch (IllegalStateException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public boolean asBoolean() throws RepositoryException {
    try {
      return value.getBoolean();
    } catch (ValueFormatException e) {
      throw new RepositoryException(e);
    } catch (IllegalStateException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public double asDouble() throws RepositoryException {
    try {
      return value.getLong();
    } catch (ValueFormatException e) {
      throw new RepositoryException(e);
    } catch (IllegalStateException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public long asInteger() throws RepositoryException {
    try {
      return value.getLong();
    } catch (ValueFormatException e) {
      throw new RepositoryException(e);
    } catch (IllegalStateException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public ITime asTime() throws RepositoryException {
    try {
      return new MockDmTime(value.getDate().getTime());
    } catch (ValueFormatException e) {
      throw new RepositoryException(e);
    } catch (IllegalStateException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public IId asId() throws RepositoryException {
    try {
      return new MockDmId(value.getString());
    } catch (ValueFormatException e) {
      throw new RepositoryException(e);
    } catch (IllegalStateException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }
}
