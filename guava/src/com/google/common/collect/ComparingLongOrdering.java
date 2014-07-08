/*
 * Copyright (C) 2013 The Guava Authors
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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.GwtCompatible;

import java.io.Serializable;
import java.util.function.ToLongFunction;

import javax.annotation.Nullable;

/**
 * An ordering that orders elements by applying an order to the result of a
 * function on those elements.
 */
@GwtCompatible(serializable = true)
final class ComparingLongOrdering<T> extends Ordering<T> implements Serializable {
  final ToLongFunction<? super T> function;

  ComparingLongOrdering(ToLongFunction<? super T> function) {
    this.function = checkNotNull(function);
  }

  @Override public int compare(T left, T right) {
    return Long.compare(function.applyAsLong(left), function.applyAsLong(right));
  }

  @Override public boolean equals(@Nullable Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof ComparingLongOrdering) {
      ComparingLongOrdering<?> that = (ComparingLongOrdering<?>) object;
      return this.function.equals(that.function);
    }
    return false;
  }

  @Override public int hashCode() {
    return function.hashCode() ^ 0x413A8AAC; // meaningless
  }

  @Override public String toString() {
    return "comparingLong(" + function + ")";
  }

  private static final long serialVersionUID = 0;
}
