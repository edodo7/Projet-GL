package be.ac.umons.model.levelChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Class that can find all eventually distinct permutations of a given set of objects
 * @param <T>   the type of the objects in the permutation
 */
public class Permutation<T> {

	private final List<T> elements;
	private List<List<T>> permutations;

	public Permutation(List<T> elements, boolean distinct) {
		this.elements = elements;
		permutations = computePermutations(distinct ? getEqualElements() : getElements());
	}

	/**
	 * Get a list of lists containing one element
	 *
	 * @return  a list of lists containing one element
	 */
	List<List<T>> getElements() {
		List<List<T>> equalElements = new ArrayList<>();
		for (int i = 0; i < elements.size(); ++i) {
			List<T> newList = new ArrayList<>();
			newList.add(elements.get(i));
			equalElements.add(newList);
		}
		return equalElements;
	}

	/**
	 * Get a list of lists containing all equal elements
	 *
	 * @return  a list of lists containing all equal elements
	 */
	List<List<T>> getEqualElements() {
		List<List<T>> equalElements = new ArrayList<>();
		for (T obj : elements) {
			boolean foundEqual = false;
			for (int i = 0; i < equalElements.size(); ++i) {
				T equalObj = equalElements.get(i).get(0);
				if (obj.equals(equalObj)) {
					foundEqual = true;
					equalElements.get(i).add(obj);
				}
			}
			if (!foundEqual) {
				List<T> newList = new ArrayList<>();
				newList.add(obj);
				equalElements.add(newList);
			}
		}
		return equalElements;
	}

	/**
	 * Get the indices associated with the given list of lists
	 *
	 * @param equalElements     the list of lists
	 * @return                  the indices
	 */
	int[] getIndices(List<List<T>> equalElements) {
		int[] indices = new int[equalElements.size()];
		Arrays.fill(indices, 0);
		return indices;
	}

	/**
	 * Compute the permutations using {@link #computePermutationsRec(int, List, int[], Stack, List)} and the given elements considered equals
	 *
	 * @param equalElements     the list of lists with all elements considered equals in each list
	 * @return  the permutations
	 */
	private List<List<T>> computePermutations(List<List<T>> equalElements) {
		List<List<T>> perms = new ArrayList<>();
		int[] indices = getIndices(equalElements);
		int n = 0;
		for (List<T> l : equalElements) {
			n += l.size();
		}
		computePermutationsRec(n, equalElements, indices, new Stack<>(), perms);
		return perms;
	}

	/**
	 * Compute the permutations recursively
	 *
	 * @param n                 the number of elements
	 * @param equalElements     the equal elements
	 * @param indices           the indices associated with the equal elements
	 * @param perm              the current state of the permutation
	 * @param perms             the currently calculated permutations
	 */
	private void computePermutationsRec(int n, List<List<T>> equalElements, int[] indices, Stack<T> perm, List<List<T>> perms) {
		if (perm.size() == n) {
			perms.add(new ArrayList<>(perm));
		}
		for (int i = 0; i < equalElements.size(); ++i) {
			if (indices[i] >= equalElements.get(i).size())
				continue;
			++indices[i];
			perm.add(equalElements.get(i).get(indices[i] - 1));
			computePermutationsRec(n, equalElements, indices, perm, perms);
			--indices[i];
			perm.pop();
		}
	}

	public List<List<T>> getPermutations() {
		return permutations;
	}
}