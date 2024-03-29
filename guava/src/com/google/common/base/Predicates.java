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

package com.google.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * Static utility methods pertaining to {@code Predicate} instances.
 *
 * <p>All methods returns serializable predicates as long as they're given
 * serializable parameters.
 *
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/FunctionalExplained">the
 * use of {@code Predicate}</a>.
 *
 * @author Kevin Bourrillion
 * @since 2.0 (imported from Google Collections Library)
 */
@GwtCompatible(emulated = true)
public final class Predicates {
  private Predicates() {}

  // TODO(kevinb): considering having these implement a VisitablePredicate
  // interface which specifies an accept(PredicateVisitor) method.

  /**
   * Returns a predicate that always evaluates to {@code true}.
   */
  @GwtCompatible(serializable = true)
  public static <T> Predicate<T> alwaysTrue() {
    return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
  }

  /**
   * Returns a predicate that always evaluates to {@code false}.
   */
  @GwtCompatible(serializable = true)
  public static <T> Predicate<T> alwaysFalse() {
    return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the object reference
   * being tested is null.
   */
  @GwtCompatible(serializable = true)
  public static <T> Predicate<T> isNull() {
    return ObjectPredicate.IS_NULL.withNarrowedType();
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the object reference
   * being tested is not null.
   */
  @GwtCompatible(serializable = true)
  public static <T> Predicate<T> notNull() {
    return ObjectPredicate.NOT_NULL.withNarrowedType();
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the given predicate
   * evaluates to {@code false}.
   */
  public static <T> Predicate<T> not(java.util.function.Predicate<T> predicate) {
    return new NotPredicate<T>(predicate);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if each of its
   * components evaluates to {@code true}. The components are evaluated in
   * order, and evaluation will be "short-circuited" as soon as a false
   * predicate is found. It defensively copies the iterable passed in, so future
   * changes to it won't alter the behavior of this predicate. If {@code
   * components} is empty, the returned predicate will always evaluate to {@code
   * true}.
   */
  public static <T> Predicate<T> and(
      Iterable<? extends java.util.function.Predicate<? super T>> components) {
    return new AndPredicate<T>(defensiveCopy(components));
  }

  /**
   * Returns a predicate that evaluates to {@code true} if each of its
   * components evaluates to {@code true}. The components are evaluated in
   * order, and evaluation will be "short-circuited" as soon as a false
   * predicate is found. It defensively copies the array passed in, so future
   * changes to it won't alter the behavior of this predicate. If {@code
   * components} is empty, the returned predicate will always evaluate to {@code
   * true}.
   */
  public static <T> Predicate<T> and(java.util.function.Predicate<? super T>... components) {
    return new AndPredicate<T>(defensiveCopy(components));
  }

  /**
   * Returns a predicate that evaluates to {@code true} if both of its
   * components evaluate to {@code true}. The components are evaluated in
   * order, and evaluation will be "short-circuited" as soon as a false
   * predicate is found.
   */
  public static <T> Predicate<T> and(java.util.function.Predicate<? super T> first,
      java.util.function.Predicate<? super T> second) {
    return new AndPredicate<T>(Predicates.<T>asList(
        checkNotNull(first), checkNotNull(second)));
  }

  /**
   * Returns a predicate that evaluates to {@code true} if any one of its
   * components evaluates to {@code true}. The components are evaluated in
   * order, and evaluation will be "short-circuited" as soon as a
   * true predicate is found. It defensively copies the iterable passed in, so
   * future changes to it won't alter the behavior of this predicate. If {@code
   * components} is empty, the returned predicate will always evaluate to {@code
   * false}.
   */
  public static <T> Predicate<T> or(
      Iterable<? extends java.util.function.Predicate<? super T>> components) {
    return new OrPredicate<T>(defensiveCopy(components));
  }

  /**
   * Returns a predicate that evaluates to {@code true} if any one of its
   * components evaluates to {@code true}. The components are evaluated in
   * order, and evaluation will be "short-circuited" as soon as a
   * true predicate is found. It defensively copies the array passed in, so
   * future changes to it won't alter the behavior of this predicate. If {@code
   * components} is empty, the returned predicate will always evaluate to {@code
   * false}.
   */
  public static <T> Predicate<T> or(java.util.function.Predicate<? super T>... components) {
    return new OrPredicate<T>(defensiveCopy(components));
  }

  /**
   * Returns a predicate that evaluates to {@code true} if either of its
   * components evaluates to {@code true}. The components are evaluated in
   * order, and evaluation will be "short-circuited" as soon as a
   * true predicate is found.
   */
  public static <T> Predicate<T> or(
      java.util.function.Predicate<? super T> first, java.util.function.Predicate<? super T> second) {
    return new OrPredicate<T>(Predicates.<T>asList(
        checkNotNull(first), checkNotNull(second)));
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the object being
   * tested {@code equals()} the given target or both are null.
   */
  public static <T> Predicate<T> equalTo(@Nullable T target) {
    return (target == null)
        ? Predicates.<T>isNull()
        : new IsEqualToPredicate<T>(target);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the object being
   * tested is an instance of the given class. If the object being tested
   * is {@code null} this predicate evaluates to {@code false}.
   *
   * <p>If you want to filter an {@code Iterable} to narrow its type, consider
   * using {@link com.google.common.collect.Iterables#filter(Iterable, Class)}
   * in preference.
   *
   * <p><b>Warning:</b> contrary to the typical assumptions about predicates (as
   * documented at {@link Predicate#apply}), the returned predicate may not be
   * <i>consistent with equals</i>. For example, {@code
   * instanceOf(ArrayList.class)} will yield different results for the two equal
   * instances {@code Lists.newArrayList(1)} and {@code Arrays.asList(1)}.
   */
  @GwtIncompatible("Class.isInstance")
  public static Predicate<Object> instanceOf(Class<?> clazz) {
    return new InstanceOfPredicate(clazz);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the class being
   * tested is assignable from the given class.  The returned predicate
   * does not allow null inputs.
   *
   * @since 10.0
   */
  @GwtIncompatible("Class.isAssignableFrom")
  @Beta
  public static Predicate<Class<?>> assignableFrom(Class<?> clazz) {
    return new AssignableFromPredicate(clazz);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the object reference
   * being tested is a member of the given collection. It does not defensively
   * copy the collection passed in, so future changes to it will alter the
   * behavior of the predicate.
   *
   * <p>This method can technically accept any {@code Collection<?>}, but using
   * a typed collection helps prevent bugs. This approach doesn't block any
   * potential users since it is always possible to use {@code
   * Predicates.<Object>in()}.
   *
   * @param target the collection that may contain the function input
   */
  public static <T> Predicate<T> in(Collection<? extends T> target) {
    return new InPredicate<T>(target);
  }

  /**
   * Returns the composition of a function and a predicate. For every {@code x},
   * the generated predicate returns {@code predicate(function(x))}.
   *
   * @return the composition of the provided function and predicate
   */
  public static <A, B> Predicate<A> compose(
      java.util.function.Predicate<B> predicate, java.util.function.Function<A, ? extends B> function) {
    return new CompositionPredicate<A, B>(predicate, function);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the
   * {@code CharSequence} being tested contains any match for the given
   * regular expression pattern. The test used is equivalent to
   * {@code Pattern.compile(pattern).matcher(arg).find()}
   *
   * @throws java.util.regex.PatternSyntaxException if the pattern is invalid
   * @since 3.0
   */
  @GwtIncompatible(value = "java.util.regex.Pattern")
  public static Predicate<CharSequence> containsPattern(String pattern) {
    return new ContainsPatternPredicate(pattern);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the
   * {@code CharSequence} being tested contains any match for the given
   * regular expression pattern. The test used is equivalent to
   * {@code pattern.matcher(arg).find()}
   *
   * @since 3.0
   */
  @GwtIncompatible(value = "java.util.regex.Pattern")
  public static Predicate<CharSequence> contains(Pattern pattern) {
    return new ContainsPatternPredicate(pattern);
  }

  // End public API, begin private implementation classes.

  // Package private for GWT serialization.
  enum ObjectPredicate implements Predicate<Object> {
    ALWAYS_TRUE {
      @Override public boolean apply(@Nullable Object o) {
        return true;
      }
    },
    ALWAYS_FALSE {
      @Override public boolean apply(@Nullable Object o) {
        return false;
      }
    },
    IS_NULL {
      @Override public boolean apply(@Nullable Object o) {
        return o == null;
      }
    },
    NOT_NULL {
      @Override public boolean apply(@Nullable Object o) {
        return o != null;
      }
    };

    @SuppressWarnings("unchecked") // safe contravariant cast
    <T> Predicate<T> withNarrowedType() {
      return (Predicate<T>) this;
    }
  }

  /** @see Predicates#not(Predicate) */
  private static class NotPredicate<T> implements Predicate<T>, Serializable {
    final java.util.function.Predicate<T> predicate;

    NotPredicate(java.util.function.Predicate<T> predicate) {
      this.predicate = checkNotNull(predicate);
    }
    @Override
    public boolean apply(@Nullable T t) {
      return !predicate.test(t);
    }
    @Override public int hashCode() {
      return ~predicate.hashCode();
    }
    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof NotPredicate) {
        NotPredicate<?> that = (NotPredicate<?>) obj;
        return predicate.equals(that.predicate);
      }
      return false;
    }
    @Override public String toString() {
      return "Not(" + predicate.toString() + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static final Joiner COMMA_JOINER = Joiner.on(",");

  /** @see Predicates#and(Iterable) */
  private static class AndPredicate<T> implements Predicate<T>, Serializable {
    private final List<? extends java.util.function.Predicate<? super T>> components;

    private AndPredicate(List<? extends java.util.function.Predicate<? super T>> components) {
      this.components = components;
    }
    @Override
    public boolean apply(@Nullable T t) {
      // Avoid using the Iterator to avoid generating garbage (issue 820).
      for (int i = 0; i < components.size(); i++) {
        if (!components.get(i).test(t)) {
          return false;
        }
      }
      return true;
    }
    @Override public int hashCode() {
      // add a random number to avoid collisions with OrPredicate
      return components.hashCode() + 0x12472c2c;
    }
    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof AndPredicate) {
        AndPredicate<?> that = (AndPredicate<?>) obj;
        return components.equals(that.components);
      }
      return false;
    }
    @Override public String toString() {
      return "And(" + COMMA_JOINER.join(components) + ")";
    }
    private static final long serialVersionUID = 0;
  }

  /** @see Predicates#or(Iterable) */
  private static class OrPredicate<T> implements Predicate<T>, Serializable {
    private final List<? extends java.util.function.Predicate<? super T>> components;

    private OrPredicate(List<? extends java.util.function.Predicate<? super T>> components) {
      this.components = components;
    }
    @Override
    public boolean apply(@Nullable T t) {
      // Avoid using the Iterator to avoid generating garbage (issue 820).
      for (int i = 0; i < components.size(); i++) {
        if (components.get(i).test(t)) {
          return true;
        }
      }
      return false;
    }
    @Override public int hashCode() {
      // add a random number to avoid collisions with AndPredicate
      return components.hashCode() + 0x053c91cf;
    }
    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof OrPredicate) {
        OrPredicate<?> that = (OrPredicate<?>) obj;
        return components.equals(that.components);
      }
      return false;
    }
    @Override public String toString() {
      return "Or(" + COMMA_JOINER.join(components) + ")";
    }
    private static final long serialVersionUID = 0;
  }

  /** @see Predicates#equalTo(Object) */
  private static class IsEqualToPredicate<T>
      implements Predicate<T>, Serializable {
    private final T target;

    private IsEqualToPredicate(T target) {
      this.target = target;
    }
    @Override
    public boolean apply(T t) {
      return target.equals(t);
    }
    @Override public int hashCode() {
      return target.hashCode();
    }
    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof IsEqualToPredicate) {
        IsEqualToPredicate<?> that = (IsEqualToPredicate<?>) obj;
        return target.equals(that.target);
      }
      return false;
    }
    @Override public String toString() {
      return "IsEqualTo(" + target + ")";
    }
    private static final long serialVersionUID = 0;
  }

  /** @see Predicates#instanceOf(Class) */
  @GwtIncompatible("Class.isInstance")
  private static class InstanceOfPredicate
      implements Predicate<Object>, Serializable {
    private final Class<?> clazz;

    private InstanceOfPredicate(Class<?> clazz) {
      this.clazz = checkNotNull(clazz);
    }
    @Override
    public boolean apply(@Nullable Object o) {
      return clazz.isInstance(o);
    }
    @Override public int hashCode() {
      return clazz.hashCode();
    }
    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof InstanceOfPredicate) {
        InstanceOfPredicate that = (InstanceOfPredicate) obj;
        return clazz == that.clazz;
      }
      return false;
    }
    @Override public String toString() {
      return "IsInstanceOf(" + clazz.getName() + ")";
    }
    private static final long serialVersionUID = 0;
  }

  /** @see Predicates#assignableFrom(Class) */
  @GwtIncompatible("Class.isAssignableFrom")
  private static class AssignableFromPredicate
      implements Predicate<Class<?>>, Serializable {
    private final Class<?> clazz;

    private AssignableFromPredicate(Class<?> clazz) {
      this.clazz = checkNotNull(clazz);
    }
    @Override
    public boolean apply(Class<?> input) {
      return clazz.isAssignableFrom(input);
    }
    @Override public int hashCode() {
      return clazz.hashCode();
    }
    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof AssignableFromPredicate) {
        AssignableFromPredicate that = (AssignableFromPredicate) obj;
        return clazz == that.clazz;
      }
      return false;
    }
    @Override public String toString() {
      return "IsAssignableFrom(" + clazz.getName() + ")";
    }
    private static final long serialVersionUID = 0;
  }

  /** @see Predicates#in(Collection) */
  private static class InPredicate<T> implements Predicate<T>, Serializable {
    private final Collection<?> target;

    private InPredicate(Collection<?> target) {
      this.target = checkNotNull(target);
    }

    @Override
    public boolean apply(@Nullable T t) {
      try {
        return target.contains(t);
      } catch (NullPointerException e) {
        return false;
      } catch (ClassCastException e) {
        return false;
      }
    }

    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof InPredicate) {
        InPredicate<?> that = (InPredicate<?>) obj;
        return target.equals(that.target);
      }
      return false;
    }

    @Override public int hashCode() {
      return target.hashCode();
    }

    @Override public String toString() {
      return "In(" + target + ")";
    }
    private static final long serialVersionUID = 0;
  }

  /** @see Predicates#compose(Predicate, Function) */
  private static class CompositionPredicate<A, B>
      implements Predicate<A>, Serializable {
    final java.util.function.Predicate<B> p;
    final java.util.function.Function<A, ? extends B> f;

    private CompositionPredicate(java.util.function.Predicate<B> p,
        java.util.function.Function<A, ? extends B> f) {
      this.p = checkNotNull(p);
      this.f = checkNotNull(f);
    }

    @Override
    public boolean apply(@Nullable A a) {
      return p.test(f.apply(a));
    }

    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof CompositionPredicate) {
        CompositionPredicate<?, ?> that = (CompositionPredicate<?, ?>) obj;
        return f.equals(that.f) && p.equals(that.p);
      }
      return false;
    }

    @Override public int hashCode() {
      return f.hashCode() ^ p.hashCode();
    }

    @Override public String toString() {
      return p.toString() + "(" + f.toString() + ")";
    }

    private static final long serialVersionUID = 0;
  }

  /**
   * @see Predicates#contains(Pattern)
   * @see Predicates#containsPattern(String)
   */
  @GwtIncompatible("Only used by other GWT-incompatible code.")
  private static class ContainsPatternPredicate
      implements Predicate<CharSequence>, Serializable {
    final Pattern pattern;

    ContainsPatternPredicate(Pattern pattern) {
      this.pattern = checkNotNull(pattern);
    }

    ContainsPatternPredicate(String patternStr) {
      this(Pattern.compile(patternStr));
    }

    @Override
    public boolean apply(CharSequence t) {
      return pattern.matcher(t).find();
    }

    @Override public int hashCode() {
      // Pattern uses Object.hashCode, so we have to reach
      // inside to build a hashCode consistent with equals.

      return Objects.hashCode(pattern.pattern(), pattern.flags());
    }

    @Override public boolean equals(@Nullable Object obj) {
      if (obj instanceof ContainsPatternPredicate) {
        ContainsPatternPredicate that = (ContainsPatternPredicate) obj;

        // Pattern uses Object (identity) equality, so we have to reach
        // inside to compare individual fields.
        return Objects.equal(pattern.pattern(), that.pattern.pattern())
            && Objects.equal(pattern.flags(), that.pattern.flags());
      }
      return false;
    }

    @Override public String toString() {
      return Objects.toStringHelper(this)
          .add("pattern", pattern)
          .add("pattern.flags", Integer.toHexString(pattern.flags()))
          .toString();
    }

    private static final long serialVersionUID = 0;
  }

  private static <T> List<java.util.function.Predicate<? super T>> asList(
      java.util.function.Predicate<? super T> first,
      java.util.function.Predicate<? super T> second) {
    // TODO(kevinb): understand why we still get a warning despite @SafeVarargs!
    return Arrays.<java.util.function.Predicate<? super T>>asList(first, second);
  }

  private static <T> List<T> defensiveCopy(T... array) {
    return defensiveCopy(Arrays.asList(array));
  }

  static <T> List<T> defensiveCopy(Iterable<T> iterable) {
    ArrayList<T> list = new ArrayList<T>();
    for (T element : iterable) {
      list.add(checkNotNull(element));
    }
    return list;
  }
}
