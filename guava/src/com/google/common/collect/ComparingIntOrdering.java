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
import java.util.function.ToIntFunction;

import javax.annotation.Nullable;

/**
 * An ordering that orders elements by applying an order to the result of a
 * function on those elements.
 */
@GwtCompatible(serializable = true)
final class ComparingIntOrdering<T> extends Ordering<T> implements Serializable {
  final ToIntFunction<? super T> function;

  ComparingIntOrdering(ToIntFunction<? super T> function) {
    this.function = checkNotNull(function);
  }

  @Override public int compare(T left, T right) {
    return Integer.compare(function.applyAsInt(left), function.applyAsInt(right));
  }

  @Override public boolean equals(@Nullable Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof ComparingIntOrdering) {
      ComparingIntOrdering<?> that = (ComparingIntOrdering<?>) object;
      return this.function.equals(that.function);
    }
    return false;
  }

  @Override public int hashCode() {
    return function.hashCode() ^ 0x890D6382; // meaningless
  }

  @Override public String toString() {
    return "comparingInt(" + function + ")";
  }

  private static final long serialVersionUID = 0;
}
