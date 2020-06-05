package be.ac.umons;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class TestUtil {
	public static <T, S> void assertSetEquals(T[] actual, S... expected) {
		TreeSet<T> actualSet = new TreeSet<>(Arrays.asList(actual));
		TreeSet<S> expectedSet = new TreeSet<>(Arrays.asList(expected));
		assertEquals(expectedSet, actualSet);
	}

	public static <T, S> void assertSetEquals(Collection<T> actual, Collection<S> expected) {
		TreeSet<T> actualSet = new TreeSet<>(actual);
		TreeSet<S> expectedSet = new TreeSet<>(expected);
		assertEquals(expectedSet, actualSet);
	}
}
