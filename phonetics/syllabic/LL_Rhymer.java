package phonetics.syllabic;

import ben_alignment.Alignment;
import ben_alignment.ConsonantAligner;
import data.DataContainer;
import genetic.Individual;
import main.Main;
import phonetics.*;
import tables.MultiTables;
import tables.ProbabilityTable;
import tables.VowelTables;

import java.util.*;

public class LL_Rhymer {

	public final MultiTables ll_tables;

	private final double frontnessWeight;
	private final double heightWeight;
	private final double roundnessWeight;
	private final double tensionWeight;
	private final double stressWeight;

	private final double mannerWeight;
	private final double placeWeight;
	private final double voicingWeight;

	private final double onsetWeight;
	private final double nucleusWeight;
	private final double codaWeight;

	public static void main(String[] args) {
		Main.setupRootPath();
		DataContainer.setupDict();
		MultiTables tables = Main.deserializeTables();
		tables.consonantTables.LL_table_printLL();
		tables.vowelTables.LL_table_printLL();
		test(tables);
//		String word = "berry";
//		Set<String> rhymes = rhymesByThresholds(1.0, 1.0, tables, word);
//		System.out.println("RHYMES W/ " + word);
//		for (String s : rhymes) {
//			System.out.println("\t" + s);
//		}
	}

	public LL_Rhymer(MultiTables ll_tables, double frontnessWeight, double heightWeight, double roundnessWeight, double tensionWeight, double stressWeight, double mannerWeight, double placeWeight, double voicingWeight, double onsetWeight, double nucleusWeight, double codaWeight) {
		this.ll_tables = ll_tables;

		this.frontnessWeight = frontnessWeight;
		this.heightWeight = heightWeight;
		this.roundnessWeight = roundnessWeight;
		this.tensionWeight = tensionWeight;
		this.stressWeight = stressWeight;

		this.mannerWeight = mannerWeight;
		this.placeWeight = placeWeight;
		this.voicingWeight = voicingWeight;

		this.onsetWeight = onsetWeight;
		this.nucleusWeight = nucleusWeight;
		this.codaWeight = codaWeight;
	}

	public LL_Rhymer(MultiTables ll_tables, Individual indiv) {
		this.ll_tables = ll_tables;

		this.frontnessWeight = indiv.getValues().get("frontness");
		this.heightWeight = indiv.getValues().get("height");
		this.roundnessWeight = indiv.getValues().get("roundness");
		this.tensionWeight = indiv.getValues().get("tension");
		this.stressWeight = indiv.getValues().get("stress");

		this.mannerWeight = indiv.getValues().get("manner");
		this.placeWeight = indiv.getValues().get("place");
		this.voicingWeight = indiv.getValues().get("voicing");

		this.onsetWeight = indiv.getValues().get("onset");
		this.nucleusWeight = indiv.getValues().get("nucleus");
		this.codaWeight = indiv.getValues().get("coda");
	}

	public static void test(MultiTables tables) {
		Set<String> words = new HashSet<>();
		words.add("died");
		words.add("room");
		words.add("air");
		words.add("storm");
		words.add("dry");
		words.add("firm");
		words.add("king");
		words.add("away");
		words.add("be");
		words.add("was");
		words.add("fly");
		words.add("buzz");
		words.add("me");
		words.add("then");
		words.add("see");
		int i = 0;
		for (String s1 : words) {
			for (String s2 : words) {
				if (!s1.equals(s2)) {
					oneTest(i, tables, s1, s2);
				}
				i++;
			}
		}

//		oneTest(1, tables, "storm", "away");
//		oneTest(2, tables, "buzz", "was");
//		oneTest(3, tables, "rock", "blot");
//		oneTest(4, tables, "socks", "bricks");
//		oneTest(5, tables, "socks", "blot");
//		oneTest(5, tables, "bricks", "blot");
	}

	public static void oneTest(int n, MultiTables tables, String s1, String s2) {
		System.out.println("\n\nTest " + n);
		List<WordSyllables> w1 = (Phoneticizer.getSyllables(s1));
		List<WordSyllables> w2 = (Phoneticizer.getSyllables(s2));
		double ga_score = LL_Rhymer.score2WordsByGaOptimizedWeights(tables,w1.get(0),w2.get(0));
//		double weightless_score = LL_Rhymer.score2WordsWeightless(tables,w1.get(0),w2.get(0));
//		double experimental_score = LL_Rhymer.score2WordsByExperimentalWeights(tables,w1.get(0),w2.get(0));
		System.out.println("GA-optimized score for f(" + s1 + ", " + s2 + ") = " + ga_score);
//		System.out.println("zero weights score for f(" + s1 + ", " + s2 + ") = " + weightless_score);
//		System.out.println("experimental score for f(" + s1 + ", " + s2 + ") = " + experimental_score);
	}

	public static Set<String> rhymesByThresholds(double min, double max, MultiTables tables, String s) {
		Set<String> result = new HashSet<>();
		WordSyllables w = (Phoneticizer.getSyllables(s)).get(0);
		for (Map.Entry<String, WordSyllables> entry : DataContainer.dictionary.entrySet()) {
			if (w.getStressTail().size() != entry.getValue().getStressTail().size()) continue;
			double score = score2WordsByGaOptimizedWeights(tables, w, entry.getValue());
			if (score >= min && score <= max) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	public static Map<String,Double> getGaOptimizedWeights() {
		Map<String,Double> map = new HashMap<>();

		//As of: Sep 17, 2017 at 3:05pm
		//F score: 0.9600924

		map.put("frontness", 47.447956271080265);
		map.put("height", 151.347951581586);
		map.put("roundness", 65.45199233466151);
		map.put("tension", 118.49472506265411);
		map.put("stress", 70.13142679317251);

		map.put("manner", 80.80378174654354);
		map.put("place", 53.05358882143532);
		map.put("voicing", 209.72933710435296);

		map.put("onset", 40.938284197428366);
		map.put("nucleus", 139.91228974321322);
		map.put("coda", 87.89520831632227);

		return map;
//		return normalizeWeights(map);
	}

	public static Map<String,Double> getExperimentalWeights() {
		Map<String,Double> map = new HashMap<>();

		map.put("frontness", 47.447956271080265);
		map.put("height", 151.347951581586);
		map.put("roundness", 65.45199233466151);
		map.put("tension", 118.49472506265411);
		map.put("stress", 300.0);

		map.put("manner", 80.80378174654354);
		map.put("place", 53.05358882143532);
		map.put("voicing", 25.0);

		map.put("onset", 40.938284197428366);
		map.put("nucleus", 100.0);
		map.put("coda", 87.89520831632227);

		return map;
//		return normalizeWeights(map);
	}

	public static Map<String,Double> getEqualWeights() {
		Map<String,Double> map = new HashMap<>();

		map.put("frontness", 1.0);
		map.put("height", 1.0);
		map.put("roundness", 1.0);
		map.put("tension", 1.0);
		map.put("stress", 1.0);

		map.put("manner", 1.0);
		map.put("place", 1.0);
		map.put("voicing", 1.0);

		map.put("onset", 1.0);
		map.put("nucleus", 1.0);
		map.put("coda", 1.0);

		return map;
//		return normalizeWeights(map);
	}

	public static Map<String,Double> normalizeWeights(Map<String,Double> weights) {
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (Map.Entry<String,Double> entry : weights.entrySet()) {
			double d = entry.getValue();
			if (d > max) {
				max = d;
			}
			if (d < min) {
				min = d;
			}
		}
		for (Map.Entry<String,Double> entry : weights.entrySet()) {
			entry.setValue((entry.getValue() - min) / (max - min));
		}
		return weights;
	}

	public static double score2SyllablesByExperimentalWeights(MultiTables tables, Syllable s1, Syllable s2) {
		Map<String,Double> map = getExperimentalWeights();
		Individual indiv = new Individual(map);
		LL_Rhymer temp = new LL_Rhymer(tables, indiv);
		return temp.score2Syllables(s1,s2);
	}

	public static double score2SyllablesByGaOptimizedWeights(MultiTables tables, Syllable s1, Syllable s2) {
		Map<String,Double> map = getGaOptimizedWeights();
		Individual indiv = new Individual(map);
		LL_Rhymer temp = new LL_Rhymer(tables, indiv);
		return temp.score2Syllables(s1,s2);
	}

	public static double score2SyllablesWeightless(MultiTables tables, Syllable s1, Syllable s2) {
		Map<String,Double> map = getEqualWeights();
		Individual indiv = new Individual(map);
		LL_Rhymer temp = new LL_Rhymer(tables, indiv);
		return temp.score2Syllables(s1,s2);
	}

	public static Double score2WordsByGaOptimizedWeights(MultiTables tables, WordSyllables word1, WordSyllables word2) {
		if (word1 == null || word2 == null || word1.getStressTail() == null || word2.getStressTail() == null ||
				word1.getStressTail().isEmpty() || word2.getStressTail().isEmpty()) return null;
		else if (word1.getStressTail().size() != word2.getStressTail().size()) {
			System.out.println("ERROR: rhyme tails of of unequal length");
			return null;
		}
		else {
			SyllableList stressTail1 = word1.getStressTail();
			SyllableList stressTail2 = word2.getStressTail();
			double total = 0.0;
			for (int i = 0; i < stressTail1.size(); i++) {
				Syllable s1 = stressTail1.get(i);
				Syllable s2 = stressTail2.get(i);
				double syllablesScore;
				if (s1.equals(s2)) syllablesScore = 1.0;
				else syllablesScore = score2SyllablesByGaOptimizedWeights(tables, s1, s2);
				System.out.println("syllablesScore " + i + ": " + syllablesScore);
				total += syllablesScore;
			}
			return new Double(total / (double)stressTail1.size());
		}
	}

	public static Double score2WordsByExperimentalWeights(MultiTables tables, WordSyllables word1, WordSyllables word2) {
		if (word1 == null || word2 == null || word1.getStressTail() == null || word2.getStressTail() == null ||
				word1.getStressTail().isEmpty() || word2.getStressTail().isEmpty()) return null;
		else if (word1.getStressTail().size() != word2.getStressTail().size()) {
			System.out.println("ERROR: rhyme tails of of unequal length");
			return null;
		}
		else {
			SyllableList stressTail1 = word1.getStressTail();
			SyllableList stressTail2 = word2.getStressTail();
			double total = 0.0;
			for (int i = 0; i < stressTail1.size(); i++) {
				Syllable s1 = stressTail1.get(i);
				Syllable s2 = stressTail2.get(i);
				double syllablesScore;
				if (s1.equals(s2)) syllablesScore = 1.0;
				else syllablesScore = score2SyllablesByExperimentalWeights(tables, s1, s2);
				total += syllablesScore;
			}
			return new Double(total / (double)stressTail1.size());
		}
	}

	public static Double score2WordsWeightless(MultiTables tables, WordSyllables word1, WordSyllables word2) {
		if (word1 == null || word2 == null || word1.getStressTail() == null || word2.getStressTail() == null ||
				word1.getStressTail().isEmpty() || word2.getStressTail().isEmpty()) return null;
		else if (word1.getStressTail().size() != word2.getStressTail().size()) {
			System.out.println("ERROR: rhyme tails of of unequal length");
			return null;
		}
		else {
			SyllableList stressTail1 = word1.getStressTail();
			SyllableList stressTail2 = word2.getStressTail();
			double total = 0.0;
			for (int i = 0; i < stressTail1.size(); i++) {
				Syllable s1 = stressTail1.get(i);
				Syllable s2 = stressTail2.get(i);
				double syllablesScore;
				if (s1.equals(s2)) syllablesScore = 1.0;
				else syllablesScore = score2SyllablesWeightless(tables, s1, s2);
				total += syllablesScore;
			}
			return new Double(total / (double)stressTail1.size());
		}
	}

	public Double score2Words(WordSyllables word1, WordSyllables word2) {
		if (word1 == null || word2 == null || word1.getStressTail() == null || word2.getStressTail() == null ||
				word1.getStressTail().isEmpty() || word2.getStressTail().isEmpty()) return null;
		else if (word1.getStressTail().size() != word2.getStressTail().size()) {
			System.out.println("ERROR: rhyme tails of of unequal length");
			return null;
		}
		else {
			SyllableList stressTail1 = word1.getStressTail();
			SyllableList stressTail2 = word2.getStressTail();
			double total = 0.0;
			for (int i = 0; i < stressTail1.size(); i++) {
				Syllable s1 = stressTail1.get(i);
				Syllable s2 = stressTail2.get(i);
				double syllablesScore;
				if (s1.equals(s2)) syllablesScore = 1.0;
				else syllablesScore = score2Syllables(s1, s2);
				total += syllablesScore;
			}
			return new Double(total / (double)stressTail1.size());
		}
	}

	public double score2Syllables(Syllable syl1, Syllable syl2) {
		if (syl1 == null && syl2 == null) return 1.0;
		if (syl1 == null || syl2 == null) return 0.0;
		if (syl1.equals(syl2)) return 1.0;

		double onsetWeight2 = onsetWeight;
		double codaWeight2 = codaWeight;

		//nuclei
		final VowelPhoneme n1 = syl1.getNucleus();
		final VowelPhoneme n2 = syl2.getNucleus();
		final double nucleus = score2Vowels(n1, n2) * nucleusWeight;

		//onsets
		List<ConsonantPhoneme> o1 = syl1.getOnset();
		List<ConsonantPhoneme> o2 = syl2.getOnset();
		double onset;
		if ((o1 == null && o2 == null) || (o1.isEmpty() && o2.isEmpty())) {
			onset = 0;
			onsetWeight2 = 0;
		}
		else if (o1.equals(o2)) {
			onset = 1.0;
		}
		else {
			Alignment onset_align = ConsonantAligner.align2ConsonantSequences(o1, o2, ll_tables.consonantTables, mannerWeight, placeWeight, voicingWeight);
			onset = onset_align.normalizedScore * onsetWeight;
		}

		//codas
		final List<ConsonantPhoneme> c1 = syl1.getCoda();
		final List<ConsonantPhoneme> c2 = syl2.getCoda();
		double coda;
		if ((c1 == null && c2 == null) || (c1.isEmpty() && c2.isEmpty())) {
			coda = 0;
			codaWeight2 = 0;
		}
		else if (c1.equals(c2)) {
			coda = 1.0;
		}
		else {
			final Alignment coda_align = ConsonantAligner.align2ConsonantSequences(c1, c2, ll_tables.consonantTables, mannerWeight, placeWeight, voicingWeight);
			coda = coda_align.normalizedScore * codaWeight;
		}

		System.out.println("onset: " + onset);
		System.out.println("nucleus: " + nucleus);
		System.out.println("coda: " + coda);

		//syllable
		final double weightSum = onsetWeight2 + nucleusWeight + codaWeight2;
		final double syllableScore = (nucleus + onset + coda) / weightSum;
		return syllableScore;
	}

	private double score2Vowels(VowelPhoneme ph1, VowelPhoneme ph2) {
		if (ph1 == null && ph2 == null) return 1.0;
		if (ph1 == null || ph2 == null) return 0;
		if (ph1.equals(ph2)) return 1.0;
		VowelTables tables = ll_tables.vowelTables;
		final int f1 = tables.frontnessTable.getCoord(ph1.phonemeEnum.getFrontness());
		final int h1 = tables.heightTable.getCoord(ph1.phonemeEnum.getHeight());
		final int r1 = tables.roundnessTable.getCoord(ph1.phonemeEnum.getRoundness());
		final int t1 = tables.tensionTable.getCoord(ph1.phonemeEnum.getTension());
		final int s1 = tables.stressTable.getCoord(ph1.stress);

		final int f2 = tables.frontnessTable.getCoord(ph2.phonemeEnum.getFrontness());
		final int h2 = tables.heightTable.getCoord(ph2.phonemeEnum.getHeight());
		final int r2 = tables.roundnessTable.getCoord(ph2.phonemeEnum.getRoundness());
		final int t2 = tables.tensionTable.getCoord(ph2.phonemeEnum.getTension());
		final int s2 = tables.stressTable.getCoord(ph2.stress);

		final double frontness = this.normalizedScore(tables.frontnessTable, f1,f2);
		final double height = this.normalizedScore(tables.frontnessTable, h1,h2);
		final double roundness = this.normalizedScore(tables.frontnessTable, r1,r2);
		final double tension = this.normalizedScore(tables.frontnessTable, t1,t2);
		final double stress = this.normalizedScore(tables.frontnessTable, s1,s2);

		final double scoreSum =
				(frontness * frontnessWeight) +
				(height * heightWeight) +
				(roundness * roundnessWeight) +
				(tension * tensionWeight) +
				(stress * stressWeight);

		final double weightSum = frontnessWeight + heightWeight + roundnessWeight + tensionWeight + stressWeight;
		final double result = scoreSum / weightSum;
		System.out.println("vowel score: " + result);
		return result;
	}

	//TODO: move these to the ProbabiityTable Class
	public double normalizedScore(ProbabilityTable table, int x, int y) {
		return (table.cell(x, y) - this.getMin(table)) / (this.getMax(table) - this.getMin(table));
	}

	private double getMax(ProbabilityTable table) {
		double max = Double.MIN_VALUE;
		for (int i = 0; i < table.get_i_size(); i++) {
			for (int j = 0; j < table.get_j_size(); j++) {
				if (i <= j && table.get(i).get(j) > max) {
					max = table.get(i).get(j);
				}
			}
		}
		return max;
	}

	private double getMin(ProbabilityTable table) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < table.get_i_size(); i++) {
			for (int j = 0; j < table.get_j_size(); j++) {
				if (i <= j && table.get(i).get(j) < min) {
					min = table.get(i).get(j);
				}
			}
		}
		return min;
	}

}
