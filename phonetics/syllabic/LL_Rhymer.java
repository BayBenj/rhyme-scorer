package phonetics.syllabic;

import ben_alignment.Alignment;
import ben_alignment.ConsonantAligner;
import genetic.Individual;
import main.Main;
import phonetics.*;
import tables.Gap;
import tables.MultiConsonantTables;
import tables.MultiTables;
import utils.Utils;

import java.util.List;
import java.util.Map;

public class LL_Rhymer {

	public final MultiTables ll_tables;

	private final double frontnessWeight;
	private final double heightWeight;
	private final double roundnessWeight;
	private final double tensionWeight;
	private final double stressWeight;
	private final double placeOfArticulationWeight;
	private final double mannerOfArticulationWeight;
	private final double voicingWeight;
	private final double onsetWeight;
	private final double nucleusWeight;
	private final double codaWeight;

	public LL_Rhymer(MultiTables ll_tables, double frontnessWeight, double heightWeight, double roundnessWeight, double tensionWeight, double stressWeight, double placeOfArticulationWeight, double mannerOfArticulationWeight, double voicingWeight, double onsetWeight, double nucleusWeight, double codaWeight) {
		this.ll_tables = ll_tables;
		this.frontnessWeight = frontnessWeight;
		this.heightWeight = heightWeight;
		this.roundnessWeight = roundnessWeight;
		this.tensionWeight = tensionWeight;
		this.stressWeight = stressWeight;
		this.placeOfArticulationWeight = placeOfArticulationWeight;
		this.mannerOfArticulationWeight = mannerOfArticulationWeight;
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
//		this.placeOfArticulationWeight = map.get("place_of_articulation");
//		this.mannerOfArticulationWeight = map.get("manner_of_articulation");
//		this.voicingWeight = map.get("voicing");
//		this.onsetWeight = map.get("onset");
//		this.nucleusWeight = map.get("nucleus");
//		this.codaWeight = map.get("coda");
//	}

	public static void test(MultiTables tables) {
		String s1 = "hold";
		String s2 = "molds";
		List<WordSyllables> w1 = (Phoneticizer.getSyllables(s1));
		List<WordSyllables> w2 = (Phoneticizer.getSyllables(s2));
		LL_Rhymer temp = new LL_Rhymer(tables,1,1,1,1,1,1,1,1,1,1,1);
		double score = temp.score2Words(w1.get(0),w2.get(0));
		System.out.println("\n\nScore for f(" + s1 + ", " + s2 + ") = " + score);
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
		else if (word1.getRhymeTailFromStress().size() != word2.getRhymeTailFromStress().size()) return null;
		else {
			double total = 0;
			for (int i = 0; i < word1.getRhymeTailFromStress().size(); i++) {
				Syllable s1 = word1.get(i);
				Syllable s2 = word2.get(i);
				total += score2Syllables(s1, s2);
			}
			return new Double(total / (double)word1.getRhymeTailFromStress().size());
		}
	}

	public double score2Syllables(Syllable syl1, Syllable syl2) {
		if (syl1 == null && syl2 == null) return 1.0;
		if (syl1 == null || syl2 == null) return 0;
		if (syl1.equals(syl2)) return 1.0;

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
		}
		else {
			Alignment onset_align = align2ConsonantSequences(o1, o2, ll_tables.consonantTables);
			onset = onset_align.normalizedScore * onsetWeight;
		}

		//codas
		List<ConsonantPhoneme> c1 = syl1.getCoda();
		List<ConsonantPhoneme> c2 = syl2.getCoda();
		Alignment coda_align = align2ConsonantSequences(c1, c2, ll_tables.consonantTables);
		double coda = coda_align.normalizedScore * codaWeight;

		//syllable
		double syllable = nucleus + onset + coda;
		return syllable;
	}

	private double score2Vowels(VowelPhoneme ph1, VowelPhoneme ph2) {
//		if (ph1 == null && ph2 == null) return 1.0;
//		if (ph1 == null || ph2 == null) return 0;
//		if (ph1 == ph2) return 1.0;
		int f1 = ll_tables.vowelTables.frontnessTable.getCoord(ph1.phonemeEnum.getFrontness());
		int h1 = ll_tables.vowelTables.heightTable.getCoord(ph1.phonemeEnum.getHeight());
		int r1 = ll_tables.vowelTables.roundnessTable.getCoord(ph1.phonemeEnum.getRoundness());
		int t1 = ll_tables.vowelTables.tensionTable.getCoord(ph1.phonemeEnum.getTension());
		int s1 = ll_tables.vowelTables.stressTable.getCoord(ph1.stress);

		int f2 = ll_tables.vowelTables.frontnessTable.getCoord(ph2.phonemeEnum.getFrontness());
		int h2 = ll_tables.vowelTables.heightTable.getCoord(ph2.phonemeEnum.getHeight());
		int r2 = ll_tables.vowelTables.roundnessTable.getCoord(ph2.phonemeEnum.getRoundness());
		int t2 = ll_tables.vowelTables.tensionTable.getCoord(ph2.phonemeEnum.getTension());
		int s2 = ll_tables.vowelTables.stressTable.getCoord(ph2.stress);

		double result =
				(ll_tables.vowelTables.frontnessTable.cell(f1,f2) * frontnessWeight) +
				(ll_tables.vowelTables.heightTable.cell(h1,h2) * heightWeight) +
				(ll_tables.vowelTables.roundnessTable.cell(r1,r2) * roundnessWeight) +
				(ll_tables.vowelTables.tensionTable.cell(t1,t2) * tensionWeight) +
				(ll_tables.vowelTables.stressTable.cell(s1,s2) * stressWeight);
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
//				(ll_tables.consonantTables.mannerTable.cell(m1,m2) * mannerOfArticulationWeight) +
//				(ll_tables.consonantTables.placeTable.cell(p1,p2) * placeOfArticulationWeight) +
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
//				(ll_tables.consonantTables.mannerTable.cell(m1,m2) * mannerOfArticulationWeight) +
//				(ll_tables.consonantTables.placeTable.cell(p1,p2) * placeOfArticulationWeight) +
//				(ll_tables.consonantTables.voicingTable.cell(v1,v2) * voicingWeight);
//		return result;
//	}

	public Alignment align2ConsonantSequences(List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2, MultiConsonantTables scoringTables) {
		Alignment result = ConsonantAligner.align2ConsonantSequences(c1, c2, scoringTables, mannerOfArticulationWeight, placeOfArticulationWeight, voicingWeight);
		return result;
	}

}
