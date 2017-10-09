package data;

import phonetics.syllabic.WordSyllables;
import java.util.*;

public class SimpleDataset extends HashMap<String,Set<String>> {

	private Map<String,WordSyllables> dictionary = DataContainer.dictionary;

	public void clean() {
		//clean Dataset's rhymes that aren't in dictionary
		for (Map.Entry<String,Set<String>> entry : this.entrySet()) {
			Set<String> originals = entry.getValue();
			originals.retainAll(dictionary.keySet());
			this.put(entry.getKey(), originals);
		}

		//clean Rhyme Zone keys
		Set<String> badDatasetWords = new HashSet<>();
		for (Map.Entry<String,Set<String>> entry : this.entrySet()) {
			//remove all rhyme zone entries w/ 0 rhymes
			if (entry.getValue() == null || entry.getValue().isEmpty()) {
				badDatasetWords.add(entry.getKey());
			}
			//remove rhyme zone entries w/ keys that aren't in CMU dict
			else if (!dictionary.containsKey(entry.getKey().toLowerCase())) {
				badDatasetWords.add(entry.getKey());
			}
			else {
				Set<String> goodEntryRhymes = new HashSet<>();
				for (String w : entry.getValue()) {
					if (!w.matches(".*\\s.*") && dictionary.containsKey(w))
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
