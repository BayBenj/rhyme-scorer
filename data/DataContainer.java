package data;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

import main.*;
import phonetics.*;
import phonetics.syllabic.*;

public abstract class DataContainer {

	private final static String datamuseBase = "https://api.datamuse.com/words?";
	private final static String rhymeZonePerf = datamuseBase + "rel_rhy=";
	private final static String rhymeZoneNear = datamuseBase + "rel_nry=";
	private final static String rhymeZoneSounds = datamuseBase + "sl=";
	public final static int size = 100000;

	public static Dataset rhymeZonePerfRhymes = new Dataset();
	public static Dataset rhymeZoneNearRhymes = new Dataset();
	public static Dataset rhymeZoneSoundsLike = new Dataset();
	public static Dataset hirjeeRapRhymes = new Dataset();
	public static Map<String,WordSyllables> dictionary = new HashMap<>();

	public static void main(String[] args) throws IOException, JSONException {
		Main.setupRootPath();
		setupDict();

		serializeDatasets();
//		deserializeDatasets();
//		cleanDatasets();
	}

	private static void serializeDatasets() throws IOException {
//		rhymeZonePerfRhymes = DataLoader.loadDataset("RZ-perfect", rhymeZonePerf, size);
//		rhymeZoneNearRhymes = DataLoader.loadDataset("RZ-near", rhymeZoneNear, size);
		rhymeZoneSoundsLike = DataLoader.loadDataset("RZ-sounds", rhymeZoneSounds, size);
	}

	private static void deserializeDatasets() {
		rhymeZonePerfRhymes = DataLoader.deserializeRhymes("RZ-perfect", size);
		rhymeZoneNearRhymes = DataLoader.deserializeRhymes("RZ-near", size);
		rhymeZoneSoundsLike = DataLoader.deserializeRhymes("RZ-sounds", size);
	}

	public static void cleanDatasets() {
		rhymeZonePerfRhymes.clean();
		rhymeZoneNearRhymes.clean();
		rhymeZoneSoundsLike.clean();
	}

	public static void setupDict() {
		Map<String,List<WordSyllables>> cmu = Phoneticizer.syllableDict;
		Map<String,List<WordSyllables>> lowerCmu = new HashMap<>();

		//lowercase all CMU keys
		for (Map.Entry<String,List<WordSyllables>> entry : cmu.entrySet()) {
			lowerCmu.put(entry.getKey().toLowerCase(),entry.getValue());
		}

		Set<String> badCmuWords = new HashSet<>();
		for (Map.Entry<String,List<WordSyllables>> entry : lowerCmu.entrySet()) {
			//remove all CMU entries with more than one pronunciation
			if (entry.getValue().size() != 1) {
				badCmuWords.add(entry.getKey());
			}
			//remove all CMU entries with keys with white space or special characters
			else if (entry.getKey().matches(".*[^\\w].*")) {
				badCmuWords.add(entry.getKey());
			}
			else {
//				WordSyllables pronunciation = entry.getValue().get(0);
//				for (Syllable syllable : pronunciation) {
//					//remove all CMU words w/ multi-consonant chains
//					if (syllable.getOnset().size() > 1 || syllable.getCoda().size() > 1) {
//						badCmuWords.add(entry.getKey());
//						break;
//					}
//				}
			}
		}

		//add to dictionary
		for (Map.Entry<String,List<WordSyllables>> entry : lowerCmu.entrySet()) {
			if (!badCmuWords.contains(entry.getKey())) {
				dictionary.put(entry.getKey(),entry.getValue().get(0));
			}
		}

	}

}
