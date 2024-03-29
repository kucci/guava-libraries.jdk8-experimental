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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * An ordering that orders elements by applying an order to the result of a
 * function on those elements.
 */
@GwtCompatible(serializable = true)
final class ComparingOrdering<F, T>
    extends Ordering<F> implements Serializable {
  final Function<? super F, ? extends T> function;
  final Comparator<? super T> comparator;

  ComparingOrdering(
      Function<? super F, ? extends T> function, Comparator<? super T> comparator) {
    this.function = checkNotNull(function);
    this.comparator = checkNotNull(comparator);
  }

  @Override public int compare(F left, F right) {
    return comparator.compare(function.apply(left), function.apply(right));
  }

  @Override public boolean equals(@Nullable Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof ComparingOrdering) {
      ComparingOrdering<?, ?> that = (ComparingOrdering<?, ?>) object;
      return this.function.equals(that.function)
          && this.comparator.equals(that.comparator);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(function, comparator);
  }

  @Override public String toString() {
    return "comparing(" + function + ", " + comparator + ")";
  }

  private static final long serialVersionUID = 0;
}
