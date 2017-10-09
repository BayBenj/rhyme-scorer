package data;

import phonetics.syllabic.WordSyllables;
import utils.Pair;

import java.io.Serializable;
import java.util.*;

public class ScoreDataset extends HashMap<String, Set<Pair<String, Integer>>> {

	private Map<String,WordSyllables> dictionary = DataContainer.dictionary;

	public void clean() {
		//clean Dataset's rhymes that aren't in dictionary
		for (Map.Entry<String, Set<Pair<String, Integer>>> entry : this.entrySet()) {
			Set<Pair<String, Integer>> originals = entry.getValue();
			Set<Pair<String, Integer>> bads = new HashSet<>();
			for (Pair<String, Integer> pair : originals) {
				if (!dictionary.keySet().contains(pair.getFirst())) {
					bads.add(pair);
				}
			}
			originals.removeAll(bads);
			this.put(entry.getKey(), originals);
		}

		//clean Rhyme Zone keys
		Set<String> badDatasetWords = new HashSet<>();
		for (Map.Entry<String, Set<Pair<String, Integer>>> entry : this.entrySet()) {
			//remove all rhyme zone entries w/ 0 rhymes
			if (entry.getValue() == null || entry.getValue().isEmpty()) {
				badDatasetWords.add(entry.getKey());
			}
			//remove rhyme zone entries w/ keys that aren't in CMU dict
			else if (!dictionary.containsKey(entry.getKey().toLowerCase())) {
				badDatasetWords.add(entry.getKey());
			}
			else {
				Set<Pair<String, Integer>> goodEntryRhymes = new HashSet<>();
				for (Pair<String, Integer> w : entry.getValue()) {
					if (!w.getFirst().matches(".*\\s.*") && dictionary.containsKey(w.getFirst()))
						goodEntryRhymes.add(w);
				}
				entry.setValue(goodEntryRhymes);
			}
		}
		this.keySet().removeAll(badDatasetWords);
		this.keySet().retainAll(dictionary.keySet());
	}

}
/*
Datasets:
Hirjee Rap
Rhyme zone perfect
Rhyme zone near
Rhyme zone sounds like?
 */
