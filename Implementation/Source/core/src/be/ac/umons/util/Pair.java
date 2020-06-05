package be.ac.umons.util;

/**
 * Class representing a couple of two objects
 * @param <T>   the type of the first object
 * @param <S>   the type of the second object
 */
public class Pair<T, S> {
	private T first;
	private S second;

	/**
	 * Constructor
	 * @param first				First element
	 * @param second			Second element
	 */
	public Pair(T first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Returns the first element
	 * @return					The first element
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Updates the first element
	 * @param first				New first element
	 */
	public void setFirst(T first) {
		this.first = first;
	}

	/**
	 * Returns the second element
	 * @return					The second element
	 */
	public S getSecond() {
		return second;
	}

	/**
	 * Updates the second element
	 * @param second				New second element
	 */
	public void setSecond(S second) {
		this.second = second;
	}
}
