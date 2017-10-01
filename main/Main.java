package main;

import java.io.*;
import java.util.*;

import data.DataContainer;
import data.DataLoader;
import data.Dataset;
import data.HttpInterface;
import org.json.JSONException;
import org.json.JSONObject;
import phonetics.ConsonantPhoneme;
import phonetics.Phoneticizer;
import phonetics.VowelPhoneme;
import phonetics.syllabic.LL_Rhymer;
import phonetics.syllabic.Syllable;
import phonetics.syllabic.SyllableList;
import phonetics.syllabic.WordSyllables;
import ben_alignment.*;
import tables.*;

public abstract class Main {

	public static String rootPath;
	public static MultiTables finalTables;

	public static void main(String[] args) throws IOException {
		setupRootPath();
		DataContainer.setupDict();

		DataContainer.rhymeZoneAdvanced = DataLoader.deserializeRhymes("RZ-adv", DataContainer.size);
		DataContainer.rhymeZoneAdvanced.clean();

		DataContainer.setupRzDict(DataContainer.rhymeZoneAdvanced);
		Dataset randomRzMatches = getRandomRzMatches();

		//monophonemic
		System.out.println("\n\nMONOPHONEMIC (consonants and vowels)");
		MonoTables randoms = findMonoCountsOnly(randomRzMatches);
		MonoTables rhymes = findMonoCountsOnly(DataContainer.rhymeZoneAdvanced);
		randoms.foldAll();
		rhymes.foldAll();

		System.out.println("\n\nVOWELS");
//		System.out.println("\n\nNear rhyme counts: " + rhymes.vowelTables.heightTable.total() + " total matches");
//		rhymes.vowelTables.printCounts();
//		System.out.println("\n\nRandom counts: " + randoms.vowelTables.frontnessTable.total() + " total matches");
//		randoms.vowelTables.printCounts();
		VowelTables mono_ll_vowel_tables = VowelTables.getLogLikelihoodTables(randoms.vowelTables, rhymes.vowelTables);
		mono_ll_vowel_tables.LL_table_printLL();

		System.out.println("\n\nCONSONANTS");
//		System.out.println("\n\nNear rhyme counts: " + rhymes.consonantTables.mannerTable.total() + " total matches");
//		rhymes.consonantTables.printCounts();
//		System.out.println("\n\nRandom counts: " + randoms.consonantTables.mannerTable.total() + " total matches");
//		randoms.consonantTables.printCounts();
		MonoConsonantTables mono_ll_consonant_tables = MonoConsonantTables.getLogLikelihoodTables(randoms.consonantTables, rhymes.consonantTables);
		mono_ll_consonant_tables.LL_table_printLL();

		//multiphonemic
		System.out.println("\n\nMULTIPHONEMIC (consonants only)");
		MultiTables randoms2 = findAllCounts(randomRzMatches, mono_ll_consonant_tables);
		MultiTables rhymes2 = findAllCounts(DataContainer.rhymeZoneAdvanced, mono_ll_consonant_tables);
		randoms2.foldAll();
		rhymes2.foldAll();

		System.out.println("\n\nNear rhyme counts: " + rhymes2.consonantTables.mannerTable.total() + " total matches");
		rhymes2.consonantTables.printCounts();
		System.out.println("\n\nRandom counts: " + randoms2.consonantTables.mannerTable.total() + " total matches");
		randoms2.consonantTables.printCounts();
		MultiConsonantTables multi_ll_tables = MultiConsonantTables.getLogLikelihoodTables(randoms2.consonantTables, rhymes2.consonantTables);
		multi_ll_tables.LL_table_printLL();

		finalTables = new MultiTables(multi_ll_tables, mono_ll_vowel_tables);

		serializeTables(finalTables);

		LL_Rhymer.test(finalTables);
	}

	public static void serializeTables(MultiTables tables) {
		System.out.print("Serializing tables...");
		try {
			FileOutputStream fileOut = new FileOutputStream(Main.rootPath + "data/tables/ll_tables.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(tables);
			out.close();
			fileOut.close();
			System.out.println("Serialized tables saved in data/tables/ll_tables.ser");
		}
		catch(IOException i) {
			i.printStackTrace();
		}
	}

	public static MultiTables deserializeTables() {
		System.out.print("Deserializing tables...");
		MultiTables result = null;
		try {
			FileInputStream fileIn = new FileInputStream(Main.rootPath + "data/tables/ll_tables.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			result = (MultiTables) in.readObject();
			in.close();
			fileIn.close();
			System.out.println("done!");
		}
		catch(IOException i) {
			i.printStackTrace();
		}
		catch(ClassNotFoundException c) {
			System.out.println("class not found");
			c.printStackTrace();
		}
		return result;
	}

	public static Dataset getRandomRzMatches() {
		Dataset result = new Dataset();
		Iterator<String> iterator = DataContainer.RZdictionary.iterator();
		for (String s1 : DataContainer.RZdictionary) {
			String s2;
			for (int i = 0; i < 300; i++) {
				Set<String> set = new HashSet<>();
				if (!iterator.hasNext()) {
					iterator = DataContainer.RZdictionary.iterator();
				}
				s2 = iterator.next();
				while (s1.equals(s2) || s2 == null || s2.isEmpty() || s2 == "") {
					if (!iterator.hasNext()) {
						iterator = DataContainer.RZdictionary.iterator();
					}
					s2 = iterator.next();
				}
				if (result.get(s1) == null) {
					set.add(s2);
					result.put(s1, set);
				} else {
					Set<String> oldSet = result.get(s1);
					oldSet.add(s2);
					result.put(s1, oldSet);
				}
			}
		}
		return result;
	}

	public static MonoTables findMonoCountsOnly(Dataset dataset) {
		MonoConsonantTables consonantResult = new MonoConsonantTables(new MonoMannerTable(), new MonoPlaceTable(), new MonoVoicingTable());
		VowelTables vowelResult = new VowelTables(new HeightTable(), new FrontnessTable(), new RoundnessTable(), new TensionTable(), new StressTable());
		for (Map.Entry<String,Set<String>> entry : dataset.entrySet()) {
			WordSyllables r1 = DataContainer.dictionary.get(entry.getKey());
			for (String s : entry.getValue()) {
				WordSyllables r2 = DataContainer.dictionary.get(s);
				if (r1 != null && r2 != null && r1.last() != null && r2.last() != null && (r1.last().getCoda() != null || r2.last().getCoda() != null)) {

					//monophonemic end codas
					if (r1.last().getCoda() != null && r2.last().getCoda() != null && r1.last().getCoda().size() == 1 && r2.last().getCoda().size() == 1)
						consonantResult = manageConsonantMatch(consonantResult, r1.last().getCoda().get(0),r2.last().getCoda().get(0));

					//end nuclei
					vowelResult = manageVowelMatches(vowelResult, r1.last().getNucleus(),r2.last().getNucleus());
				}

				//do counts of middle onsets and codas and nuclei
				if (r1.getRhymeTailFromStress().size() == r2.getRhymeTailFromStress().size() && r1.getRhymeTailFromStress().size() > 1) {
					SyllableList rt1 = r1.getRhymeTailFromStress();
					SyllableList rt2 = r2.getRhymeTailFromStress();
					//monophonemic onsets in rhyme tail
					for (int i = 0; i < rt1.size(); i++) {
						Syllable syl1 = rt1.get(i);
						Syllable syl2 = rt2.get(i);
						if (syl1 == null || !syl1.hasOnset() || syl1.getOnset().size() != 1 || syl2 == null || !syl2.hasOnset() || syl2.getOnset().size() != 1) continue;
						consonantResult = manageConsonantMatch(consonantResult, syl1.getOnset().get(0),syl2.getOnset().get(0));
					}

					//monophonemic codas in rhyme tail (excluding final syllable)
					for (int i = 0; i < rt1.size(); i++) {
						Syllable syl1 = rt1.get(i);
						Syllable syl2 = rt2.get(i);
						if (i == rt1.size() - 1 || syl1 == null || !syl1.hasCoda() || syl1.getCoda().size() != 1 || syl2 == null || !syl2.hasCoda() || syl2.getCoda().size() != 1) continue;
						consonantResult = manageConsonantMatch(consonantResult, syl1.getCoda().get(0),syl2.getCoda().get(0));
					}

					//nuclei in rhyme tail (excluding final syllable)
					for (int i = 0; i < rt1.size(); i++) {
						Syllable syl1 = rt1.get(i);
						Syllable syl2 = rt2.get(i);
						if (i == rt1.size() - 1 || syl1 == null || !syl1.hasNucleus() || syl2 == null || !syl2.hasNucleus()) continue;
						vowelResult = manageVowelMatches(vowelResult, syl1.getNucleus(),syl2.getNucleus());
					}
				}
			}
		}
		return new MonoTables(consonantResult, vowelResult);
	}

	public static MultiTables findAllCounts(Dataset dataset, MonoConsonantTables monoLLTables) {
		MultiConsonantTables consonantResult = new MultiConsonantTables(new MultiMannerTable(), new MultiPlaceTable(), new MultiVoicingTable());
		VowelTables vowelResult = new VowelTables(new HeightTable(), new FrontnessTable(), new RoundnessTable(), new TensionTable(), new StressTable());
		for (Map.Entry<String,Set<String>> entry : dataset.entrySet()) {
			WordSyllables r1 = DataContainer.dictionary.get(entry.getKey());
			for (String s : entry.getValue()) {
				WordSyllables r2 = DataContainer.dictionary.get(s);
				if (r1 != null && r2 != null && r1.last() != null && r2.last() != null && (r1.last().getCoda() != null || r2.last().getCoda() != null)) {

					//monophonemic end codas
					if (r1.last().getCoda() != null && r2.last().getCoda() != null && r1.last().getCoda().size() == 1 && r2.last().getCoda().size() == 1)
						consonantResult = manageConsonantMatch(consonantResult, r1.last().getCoda().get(0),r2.last().getCoda().get(0));

					//end nuclei
					vowelResult = manageVowelMatches(vowelResult, r1.last().getNucleus(),r2.last().getNucleus());

					//multiphonemic end codas
					if (r1.last().getCoda().size() > 1 || r2.last().getCoda().size() > 1) {
						Alignment alignment = ConsonantAligner.greedilyAlign2ConsonantSequences(r1.last().getCoda(), r2.last().getCoda(), monoLLTables);
						consonantResult = manageConsonantMatches(consonantResult, alignment);
					}
				}

				//do counts of middle onsets and codas and nuclei
				if (r1.getRhymeTailFromStress().size() == r2.getRhymeTailFromStress().size() && r1.getRhymeTailFromStress().size() > 1) {
					SyllableList rt1 = r1.getRhymeTailFromStress();
					SyllableList rt2 = r2.getRhymeTailFromStress();
					//monophonemic onsets in rhyme tail
					for (int i = 0; i < rt1.size(); i++) {
						Syllable syl1 = rt1.get(i);
						Syllable syl2 = rt2.get(i);
						if (syl1 == null || !syl1.hasOnset() || syl1.getOnset().size() != 1 || syl2 == null || !syl2.hasOnset() || syl2.getOnset().size() != 1) continue;
						consonantResult = manageConsonantMatch(consonantResult, syl1.getOnset().get(0),syl2.getOnset().get(0));
					}

					//monophonemic codas in rhyme tail (excluding final syllable)
					for (int i = 0; i < rt1.size(); i++) {
						Syllable syl1 = rt1.get(i);
						Syllable syl2 = rt2.get(i);
						if (i == rt1.size() - 1 || syl1 == null || !syl1.hasCoda() || syl1.getCoda().size() != 1 || syl2 == null || !syl2.hasCoda() || syl2.getCoda().size() != 1) continue;
						consonantResult = manageConsonantMatch(consonantResult, syl1.getCoda().get(0),syl2.getCoda().get(0));
					}

					//nuclei in rhyme tail (excluding final syllable)
					for (int i = 0; i < rt1.size(); i++) {
						Syllable syl1 = rt1.get(i);
						Syllable syl2 = rt2.get(i);
						if (i == rt1.size() - 1 || syl1 == null || !syl1.hasNucleus() || syl2 == null || !syl2.hasNucleus()) continue;
						vowelResult = manageVowelMatches(vowelResult, syl1.getNucleus(),syl2.getNucleus());
					}

					//multiphonemic onsets in rhyme tail
					for (int i = 0; i < rt1.size(); i++) {
						Syllable syl1 = rt1.get(i);
						Syllable syl2 = rt2.get(i);
						if (syl1 == null || !syl1.hasOnset() || syl1.getOnset().size() <= 1 || syl2 == null || !syl2.hasOnset() || syl2.getOnset().size() <= 1) continue;
						Alignment alignment = ConsonantAligner.greedilyAlign2ConsonantSequences(syl1.getOnset(), syl2.getOnset(), monoLLTables);
						consonantResult = manageConsonantMatches(consonantResult, alignment);
					}

					//multiphonemic codas in rhyme tail (excluding final syllable)
					for (int i = 0; i < rt1.size(); i++) {
						Syllable syl1 = rt1.get(i);
						Syllable syl2 = rt2.get(i);
						if (i == rt1.size() - 1 || syl1 == null || !syl1.hasCoda() || syl1.getCoda().size() != 1 || syl2 == null || !syl2.hasCoda() || syl2.getCoda().size() != 1) continue;
						Alignment alignment = ConsonantAligner.greedilyAlign2ConsonantSequences(syl1.getCoda(), syl2.getCoda(), monoLLTables);
						consonantResult = manageConsonantMatches(consonantResult, alignment);
					}
				}
			}
		}
		return new MultiTables(consonantResult, vowelResult);
	}

	public static MonoConsonantTables manageConsonantMatch(MonoConsonantTables tables, ConsonantPhoneme r1consonant, ConsonantPhoneme r2consonant) {
		tables.mannerTable.fillCell(r1consonant.phonemeEnum.getManner(),r2consonant.phonemeEnum.getManner());
		tables.placeTable.fillCell(r1consonant.phonemeEnum.getPlace(),r2consonant.phonemeEnum.getPlace());
		tables.voicingTable.fillCell(r1consonant.phonemeEnum.isVoiced(),r2consonant.phonemeEnum.isVoiced());
		return tables;
	}

	public static MultiConsonantTables manageConsonantMatch(MultiConsonantTables tables, ConsonantPhoneme r1consonant, ConsonantPhoneme r2consonant) {
		tables.mannerTable.fillCell(r1consonant.phonemeEnum.getManner(),r2consonant.phonemeEnum.getManner());
		tables.placeTable.fillCell(r1consonant.phonemeEnum.getPlace(),r2consonant.phonemeEnum.getPlace());
		tables.voicingTable.fillCell(r1consonant.phonemeEnum.isVoiced(),r2consonant.phonemeEnum.isVoiced());
		return tables;
	}

	public static MultiConsonantTables manageConsonantMatches(MultiConsonantTables tables, Alignment alignment) {
		List<ConsonantPhoneme> r1consonants = alignment.c1;
		List<ConsonantPhoneme> r2consonants = alignment.c2;
		for (int i = 0; i < r1consonants.size(); i++) {

			//double null, no gap
			if (r1consonants.get(i) == null && r2consonants.get(i) == null) continue;

			//beginning gap
			else if (i == 0 && (r1consonants.get(i) == null || r2consonants.get(i) == null)) {
				if (r1consonants.get(i) == null) {
					tables.mannerTable.fillGapCell(Gap.BEG, r2consonants.get(i).phonemeEnum.getManner());
					tables.placeTable.fillGapCell(Gap.BEG, r2consonants.get(i).phonemeEnum.getPlace());
					tables.voicingTable.fillGapCell(Gap.BEG, r2consonants.get(i).phonemeEnum.isVoiced());
				}
				else {
					tables.mannerTable.fillGapCell(Gap.BEG, r1consonants.get(i).phonemeEnum.getManner());
					tables.placeTable.fillGapCell(Gap.BEG, r1consonants.get(i).phonemeEnum.getPlace());
					tables.voicingTable.fillGapCell(Gap.BEG, r1consonants.get(i).phonemeEnum.isVoiced());
				}
			}
			//end gap
			else if (i == r1consonants.size() - 1 && (r1consonants.get(i) == null || r2consonants.get(i) == null)) {
				if (r1consonants.get(i) == null) {
					tables.mannerTable.fillGapCell(Gap.END, r2consonants.get(i).phonemeEnum.getManner());
					tables.placeTable.fillGapCell(Gap.END, r2consonants.get(i).phonemeEnum.getPlace());
					tables.voicingTable.fillGapCell(Gap.END, r2consonants.get(i).phonemeEnum.isVoiced());
				}
				else {
					tables.mannerTable.fillGapCell(Gap.END, r1consonants.get(i).phonemeEnum.getManner());
					tables.placeTable.fillGapCell(Gap.END, r1consonants.get(i).phonemeEnum.getPlace());
					tables.voicingTable.fillGapCell(Gap.END, r1consonants.get(i).phonemeEnum.isVoiced());
				}
			}
			//middle gap
			else if (r1consonants.get(i) == null || r2consonants.get(i) == null) {
				if (r1consonants.get(i) == null) {
					tables.mannerTable.fillGapCell(Gap.MID, r2consonants.get(i).phonemeEnum.getManner());
					tables.placeTable.fillGapCell(Gap.MID, r2consonants.get(i).phonemeEnum.getPlace());
					tables.voicingTable.fillGapCell(Gap.MID, r2consonants.get(i).phonemeEnum.isVoiced());
				}
				else {
					tables.mannerTable.fillGapCell(Gap.MID, r1consonants.get(i).phonemeEnum.getManner());
					tables.placeTable.fillGapCell(Gap.MID, r1consonants.get(i).phonemeEnum.getPlace());
					tables.voicingTable.fillGapCell(Gap.MID, r1consonants.get(i).phonemeEnum.isVoiced());
				}
			}
			else {
				tables.mannerTable.fillCell(r1consonants.get(i).phonemeEnum.getManner(), r2consonants.get(i).phonemeEnum.getManner());
				tables.placeTable.fillCell(r1consonants.get(i).phonemeEnum.getPlace(), r2consonants.get(i).phonemeEnum.getPlace());
				tables.voicingTable.fillCell(r1consonants.get(i).phonemeEnum.isVoiced(), r2consonants.get(i).phonemeEnum.isVoiced());
			}
		}
		return tables;
	}

	public static VowelTables manageVowelMatches(VowelTables tables, VowelPhoneme r1, VowelPhoneme r2) {
		tables.heightTable.fillCell(r1.phonemeEnum.getHeight(),r2.phonemeEnum.getHeight());
		tables.frontnessTable.fillCell(r1.phonemeEnum.getFrontness(),r2.phonemeEnum.getFrontness());
		tables.roundnessTable.fillCell(r1.phonemeEnum.getRoundness(),r2.phonemeEnum.getRoundness());
		tables.tensionTable.fillCell(r1.phonemeEnum.getTension(),r2.phonemeEnum.getTension());
		tables.stressTable.fillCell(r1.stress,r2.stress);
		return tables;
	}

	public static void setupRootPath() {
		//Set the root path of Lyrist in U
		final File currentDirFile = new File("");
		Main.rootPath = currentDirFile.getAbsolutePath() + "/";
	}

//	public double computeFScoreForRhymingFunction(Dataset dataset, RhymeFunction rhymeFunction) {
//
//	}

	public double computeFitness(int truePositives, int falsePositives, int falseNegatives) {
		double precision = computePrecision(truePositives, falsePositives);
		double recall = computeRecall(truePositives, falseNegatives);
		double fScore = computeFScore(precision, recall);
		return fScore;
	}

	public static double computeFScore(double precision, double recall) {
		return 2 * ((precision * recall) / (precision + recall));
	}

	public static double computePrecision(double truePositives, double falsePositives) {
		return truePositives / (truePositives + falsePositives);
	}

	public static double computeRecall(double truePositives, double falseNegatives) {
		return truePositives / (truePositives + falseNegatives);
	}

}

/*
Steps
1. Compute log-likelihood tables for monophonemic voicing, manner, and place

2. Greedily align all codas (including multiphonemic) to get aligned phonemes using the scoring matrix computed in step 1.
	By "greedily align" this means simply to take the best matching phoneme pair based on linguistic features using the
	scoring matrices from step 1; then take the next best; and so on, ensuring that the order of phonemes is maintained
	in the pairwise association. This does not require a gap cost to perform this alignment.

3. Using the multiphonemic alignments from step 2, *update the linguistic scoring matrices from step 1 with log-likelihoods
from the newly aligned phonemes (including gaps).
 */
