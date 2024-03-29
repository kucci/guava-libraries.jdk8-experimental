/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * Implementation of {@link ImmutableSet} with two or more elements.
 *
 * @author Kevin Bourrillion
 */
@GwtCompatible(serializable = true, emulated = true)
@SuppressWarnings("serial") // uses writeReplace(), not default serialization
final class RegularImmutableSet<E> extends ImmutableSet<E> {
  private final Object[] elements;
  // the same elements in hashed positions (plus nulls)
  @VisibleForTesting final transient Object[] table;
  // 'and' with an int to get a valid table index.
  private final transient int mask;
  private final transient int hashCode;

  RegularImmutableSet(
      Object[] elements, int hashCode, Object[] table, int mask) {
    this.elements = elements;
    this.table = table;
    this.mask = mask;
    this.hashCode = hashCode;
  }

  @Override public boolean contains(Object target) {
    if (target == null) {
      return false;
    }
    for (int i = Hashing.smear(target.hashCode()); true; i++) {
      Object candidate = table[i & mask];
      if (candidate == null) {
        return false;
      }
      if (candidate.equals(target)) {
        return true;
      }
    }
  }

  @Override
  public int size() {
    return elements.length;
  }

  @SuppressWarnings("unchecked") // all elements are E's
  @Override
  public UnmodifiableIterator<E> iterator() {
    return (UnmodifiableIterator<E>) Iterators.forArray(elements);
  }

  @Override public Spliterator<E> spliterator() {
    return Spliterators.spliterator(elements,
        Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.DISTINCT);
  }

  // stream() and parallelStream() get efficient implementations for free from spliterator()

  @Override
  int copyIntoArray(Object[] dst, int offset) {
    System.arraycopy(elements, 0, dst, offset, elements.length);
    return offset + elements.length;
  }

  @Override
  ImmutableList<E> createAsList() {
    return new RegularImmutableAsList<E>(this, elements);
  }

  @Override
  public void forEach(Consumer<? super E> consumer) {
    asList().forEach(consumer);
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override public int hashCode() {
    return hashCode;
  }

  @Override boolean isHashCodeFast() {
    return true;
  }
}
