package be.ac.umons;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.ac.umons.model.levelChecker.Permutation;
import be.ac.umons.model.levelChecker.PermutationTree;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class TestPermutation {

	private PermutationTree<Integer> permutationTree;
	private ArrayList<Integer> elements;

	@Before
	public void init() {
		elements = new ArrayList<>();
		elements.add(1);
		elements.add(2);
		elements.add(2);
		permutationTree = new PermutationTree<>(elements, true);
	}

	@Test
	public void testPermutation() {

		Integer[][] expected = new Integer[][]{{1, 2, 2}, {1, 2, 2}, {2, 1, 2}, {2, 2, 1}, {2, 1, 2}, {2, 2, 1}};
		List<List<Integer>> expectedList = new ArrayList<>();
		for (Integer[] perm : expected) {
			expectedList.add(Arrays.asList(perm));
		}

		assertEquals(expectedList, new Permutation<>(elements, false).getPermutations());
	}

	@Test
	public void testDistinctPermutation() {
		Integer[][] expected = new Integer[][]{{1, 2, 2}, {2, 1, 2}, {2, 2, 1}};
		List<List<Integer>> expectedList = new ArrayList<>();
		for (Integer[] perm : expected) {
			expectedList.add(Arrays.asList(perm));
		}

		assertEquals(expectedList, new Permutation<>(elements, true).getPermutations());
	}

	@Test
	public void testPermutationTree() {
		ArrayList<Integer> expected = new ArrayList<>();
		expected.add(1);
		expected.add(2);
		assertEquals(expected, permutationTree.getChoice());
		permutationTree.choose(0);
		expected.set(0, null);
		assertEquals(expected, permutationTree.getChoice());
		permutationTree.reconsider(0);
		expected.set(0, 1);
		permutationTree.choose(1);
		assertEquals(expected, permutationTree.getChoice());
	}
}
