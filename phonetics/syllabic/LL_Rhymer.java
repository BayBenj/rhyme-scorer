package phonetics.syllabic;

import ben_alignment.Alignment;
import ben_alignment.ConsonantAligner;
import phonetics.*;
import tables.MultiTables;

import java.util.List;

public class LL_Rhymer {

	public final MultiTables ll_tables;

	private final double frontnessWeight;
	private final double heightWeight;
	private final double roundnessWeight;
	private final double tensionWeight;
	private final double stressWeight;

	private final double placeWeight;
	private final double mannerWeight;
	private final double voicingWeight;

	private final double onsetWeight;
	private final double nucleusWeight;
	private final double codaWeight;

	public LL_Rhymer(MultiTables ll_tables, double frontnessWeight, double heightWeight, double roundnessWeight, double tensionWeight, double stressWeight, double placeWeight, double mannerWeight, double voicingWeight, double onsetWeight, double nucleusWeight, double codaWeight) {
		this.ll_tables = ll_tables;
		this.frontnessWeight = frontnessWeight;
		this.heightWeight = heightWeight;
		this.roundnessWeight = roundnessWeight;
		this.tensionWeight = tensionWeight;
		this.stressWeight = stressWeight;
		this.placeWeight = placeWeight;
		this.mannerWeight = mannerWeight;
		this.voicingWeight = voicingWeight;
		this.onsetWeight = onsetWeight;
		this.nucleusWeight = nucleusWeight;
		this.codaWeight = codaWeight;
	}

//	public LL_Rhymer(Individual weights) {
//		Map<String,Double> map = weights.getValues();
//		this.ll_tables = weights.ll_tables;
//		this.frontnessWeight = map.get("frontness");
//		this.heightWeight = map.get("height");
//		this.roundnessWeight = map.get("roundness");
//		this.tensionWeight = map.get("tension");
//		this.stressWeight = map.get("stress");
//		this.placeWeight = map.get("place_of_articulation");
//		this.mannerWeight = map.get("manner_of_articulation");
//		this.voicingWeight = map.get("voicing");
//		this.onsetWeight = map.get("onset");
//		this.nucleusWeight = map.get("nucleus");
//		this.codaWeight = map.get("coda");
//	}

	public static void test(MultiTables tables) {
		System.out.println("\n\nTest 1");
		String s1 = "black";
		String s2 = "white";
		List<WordSyllables> w1 = (Phoneticizer.getSyllables(s1));
		List<WordSyllables> w2 = (Phoneticizer.getSyllables(s2));
		LL_Rhymer temp = new LL_Rhymer(tables,1,1,1,1,1,1,1,1,1,1,1);
		double score = temp.score2Words(w1.get(0),w2.get(0));
		System.out.println("\nScore for f(" + s1 + ", " + s2 + ") = " + score);

//		System.out.println("\n\nTest 2");
//		s1 = "bold";
//		s2 = "fold";
//		w1 = (Phoneticizer.getSyllables(s1));
//		w2 = (Phoneticizer.getSyllables(s2));
//		score = temp.score2Words(w1.get(0),w2.get(0));
//		System.out.println("\nScore for f(" + s1 + ", " + s2 + ") = " + score);
//
//		System.out.println("\n\nTest 3");
//		s1 = "station";
//		s2 = "bacon";
//		w1 = (Phoneticizer.getSyllables(s1));
//		w2 = (Phoneticizer.getSyllables(s2));
//		score = temp.score2Words(w1.get(0),w2.get(0));
//		System.out.println("\nScore for f(" + s1 + ", " + s2 + ") = " + score);

		System.out.println("\n\nTest 2");
		s1 = "strengths";
		s2 = "pranks";
		w1 = (Phoneticizer.getSyllables(s1));
		w2 = (Phoneticizer.getSyllables(s2));
		score = temp.score2Words(w1.get(0),w2.get(0));
		System.out.println("\nScore for f(" + s1 + ", " + s2 + ") = " + score);

	}

//	public static double score2SyllablesByGaOptimizedWeights(Syllable s1, Syllable s2) {
//		LL_Rhymer temp = new LL_Rhymer(100,87.89145327765064,100,93.74256357399545,27.754425336501495,5.519934718254886,100,125.45080735803514,17.24887448289867);
//		return temp.score2Syllables(s1,s2);
//	 /*
//		New best individual: 0.9665289798059271
//		frontness: 100
//		height: 87.89145327765064
//
//		place of articulation: 100
//		manner of articulation: 93.74256357399545
//		voicing: 27.754425336501495
//
//		onset: 5.519934718254886
//		nucleus: 100
//		coda: 125.45080735803514
//
//		stress: 17.24887448289867
//	 */
//	}

	public Double score2Words(WordSyllables word1, WordSyllables word2) {
		if (word1 == null || word2 == null || word1.getRhymeTailFromStress() == null || word2.getRhymeTailFromStress() == null ||
				word1.getRhymeTailFromStress().isEmpty() || word2.getRhymeTailFromStress().isEmpty()) return null;
		else if (word1.getRhymeTailFromStress().size() != word2.getRhymeTailFromStress().size()) {
			System.out.println("ERROR: rhyme tails of of unequal length");
			return null;
		}
		else {
			SyllableList rhymeTail1 = word1.getRhymeTailFromStress();
			SyllableList rhymeTail2 = word2.getRhymeTailFromStress();
			double total = 0.0;
			for (int i = 0; i < rhymeTail1.size(); i++) {
				Syllable s1 = rhymeTail1.get(i);
				Syllable s2 = rhymeTail2.get(i);
				double syllablesScore;
				if (s1.equals(s2)) syllablesScore = 1.0;
				else syllablesScore = score2Syllables(s1, s2);
				total += syllablesScore;
			}
			return new Double(total / (double)rhymeTail1.size());
		}
	}

	public double score2Syllables(Syllable syl1, Syllable syl2) {
		if (syl1 == null && syl2 == null) return 1.0;
		if (syl1 == null || syl2 == null) return 0.0;
		if (syl1.equals(syl2)) return 1.0;

		double onsetWeight2 = onsetWeight;
		double codaWeight2 = codaWeight;

		//nuclei
		VowelPhoneme n1 = syl1.getNucleus();
		VowelPhoneme n2 = syl2.getNucleus();
		double nucleus = score2Vowels(n1, n2) * nucleusWeight;

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
		List<ConsonantPhoneme> c1 = syl1.getCoda();
		List<ConsonantPhoneme> c2 = syl2.getCoda();
		double coda;
		if ((c1 == null && c2 == null) || (c1.isEmpty() && c2.isEmpty())) {
			coda = 0;
			codaWeight2 = 0;
		}
		else if (c1.equals(c2)) {
			coda = 1.0;
		}
		else {
			Alignment coda_align = ConsonantAligner.align2ConsonantSequences(c1, c2, ll_tables.consonantTables, mannerWeight, placeWeight, voicingWeight);
			coda = coda_align.normalizedScore * codaWeight;
		}

		//syllable
		final double weightSum = onsetWeight2 + nucleusWeight + codaWeight2;
		final double syllableScore = (nucleus + onset + coda) / weightSum;
		return syllableScore;
	}

	private double score2Vowels(VowelPhoneme ph1, VowelPhoneme ph2) {
		if (ph1 == null && ph2 == null) return 1.0;
		if (ph1 == null || ph2 == null) return 0;
		if (ph1.equals(ph2)) return 1.0;
		final int f1 = ll_tables.vowelTables.frontnessTable.getCoord(ph1.phonemeEnum.getFrontness());
		final int h1 = ll_tables.vowelTables.heightTable.getCoord(ph1.phonemeEnum.getHeight());
		final int r1 = ll_tables.vowelTables.roundnessTable.getCoord(ph1.phonemeEnum.getRoundness());
		final int t1 = ll_tables.vowelTables.tensionTable.getCoord(ph1.phonemeEnum.getTension());
		final int s1 = ll_tables.vowelTables.stressTable.getCoord(ph1.stress);

		final int f2 = ll_tables.vowelTables.frontnessTable.getCoord(ph2.phonemeEnum.getFrontness());
		final int h2 = ll_tables.vowelTables.heightTable.getCoord(ph2.phonemeEnum.getHeight());
		final int r2 = ll_tables.vowelTables.roundnessTable.getCoord(ph2.phonemeEnum.getRoundness());
		final int t2 = ll_tables.vowelTables.tensionTable.getCoord(ph2.phonemeEnum.getTension());
		final int s2 = ll_tables.vowelTables.stressTable.getCoord(ph2.stress);

		final double scoreSum =
				(ll_tables.vowelTables.frontnessTable.cell(f1,f2) * frontnessWeight) +
				(ll_tables.vowelTables.heightTable.cell(h1,h2) * heightWeight) +
				(ll_tables.vowelTables.roundnessTable.cell(r1,r2) * roundnessWeight) +
				(ll_tables.vowelTables.tensionTable.cell(t1,t2) * tensionWeight) +
				(ll_tables.vowelTables.stressTable.cell(s1,s2) * stressWeight);

		final double weightSum = frontnessWeight + heightWeight + roundnessWeight + tensionWeight + stressWeight;
		final double result = scoreSum / weightSum;
		return result;
	}

//	private double score2Consonants(ConsonantPhoneme ph1, ConsonantPhoneme ph2) {
////		if (ph1 == null && ph2 == null) return 1.0;
////		if (ph1 == null || ph2 == null) return 0;
////		if (ph1 == ph2) return 1.0;
//		int m1 = ll_tables.consonantTables.mannerTable.getCoord(ph1.phonemeEnum.getManner());
//		int p1 = ll_tables.consonantTables.placeTable.getCoord(ph1.phonemeEnum.getPlace());
//		int v1 = ll_tables.consonantTables.voicingTable.getCoord(ph1.phonemeEnum.isVoiced());
//
//		int m2 = ll_tables.consonantTables.mannerTable.getCoord(ph2.phonemeEnum.getManner());
//		int p2 = ll_tables.consonantTables.placeTable.getCoord(ph2.phonemeEnum.getPlace());
//		int v2 = ll_tables.consonantTables.voicingTable.getCoord(ph2.phonemeEnum.isVoiced());
//
//		double result =
//				(ll_tables.consonantTables.mannerTable.cell(m1,m2) * mannerWeight) +
//				(ll_tables.consonantTables.placeTable.cell(p1,p2) * placeWeight) +
//				(ll_tables.consonantTables.voicingTable.cell(v1,v2) * voicingWeight);
//		return result;
//	}
//
//	private double score2Consonants(Gap g, ConsonantPhoneme ph) {
////		if (ph1 == null && ph2 == null) return 1.0;
////		if (ph1 == null || ph2 == null) return 0;
////		if (ph1 == ph2) return 1.0;
//		int m1 = ll_tables.consonantTables.mannerTable.getCoord(ph.phonemeEnum.getManner());
//		int p1 = ll_tables.consonantTables.placeTable.getCoord(ph.phonemeEnum.getPlace());
//		int v1 = ll_tables.consonantTables.voicingTable.getCoord(ph.phonemeEnum.isVoiced());
//
//		int m2 = ll_tables.consonantTables.mannerTable.getGapCoord(g);
//		int p2 = ll_tables.consonantTables.placeTable.getGapCoord(g);
//		int v2 = ll_tables.consonantTables.voicingTable.getGapCoord(g);
//
//		double result =
//				(ll_tables.consonantTables.mannerTable.cell(m1,m2) * mannerWeight) +
//				(ll_tables.consonantTables.placeTable.cell(p1,p2) * placeWeight) +
//				(ll_tables.consonantTables.voicingTable.cell(v1,v2) * voicingWeight);
//		return result;
//	}

}
