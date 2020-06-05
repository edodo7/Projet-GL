package be.ac.umons.model.levelChecker;


import java.util.ArrayList;
import java.util.List;

/**
 * A class that helps to browse the permutations recursively, allowing to mark an object as taken or not
 * and to see which objects are currently available
 *
 * @param <T>   the element type
 */
public class PermutationTree<T> {

	private List<T> elements;
	private List<List<T>> equalElements;
	private int[] indices;

	public PermutationTree(List<T> elements, boolean distinct) {
		this.elements = elements;
		Permutation<T> permutation = new Permutation<>(this.elements, distinct);
		this.equalElements = permutation.getEqualElements();
		this.indices = permutation.getIndices(this.equalElements);
	}

	/**
	 * Mark an object as chosen
	 *
	 * @param i the index of the chosen element, in accordance with the index given by {@link #getChoice()}
	 */
	public void choose(int i) {
		++indices[i];
	}

	/**
	 * Mark an object as no longer chosen
	 * The object must have been chosen before
	 *
	 * @param i the index of the chosen element, in accordance with the index given by {@link #getChoice()}
	 */
	public void reconsider(int i) {
		--indices[i];
	}

	/**
	 * Get the currently available objects
	 *
	 * @return  the currently available objects
	 */
	public List<T> getChoice() {
		List<T> choice = new ArrayList<>();
		for (int i = 0; i < equalElements.size(); ++i) {
			if (indices[i] < equalElements.get(i).size()) {
				choice.add(equalElements.get(i).get(indices[i]));
			} else {
				choice.add(null);
			}
		}
		return choice;
	}
}
