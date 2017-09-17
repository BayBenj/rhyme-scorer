package ben_alignment;

import tables.*;
import phonetics.ConsonantPhoneme;
import phonetics.ConsonantPronunciation;

import java.util.ArrayList;
import java.util.List;

public abstract class ConsonantAligner {

	public static Alignment align2ConsonantSequences(List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2, MultiConsonantTables ll_tables, double m_w, double p_w, double v_w) {
		Double[][] alignmentTable = new Double[c1.size() + 1][c2.size() + 1];
		BackPointer[][] backPointers = new BackPointer[c1.size() + 1][c2.size() + 1];
		for (int i = 0; i < c1.size() + 1; i++) {
			for (int j = 0; j < c2.size() + 1; j++) {
				if (i == 0 && j == 0) {
					alignmentTable[i][j] = 0.0;
					backPointers[i][j] = null;
					continue;
				}
				if (i == 0 || j == 0) {
					ConsonantPhoneme ph;
					if (i == 0) ph = c2.get(0);
					else ph = c1.get(0);
					int m = ll_tables.mannerTable.getCoord(ph.phonemeEnum.getManner());
					int p = ll_tables.placeTable.getCoord(ph.phonemeEnum.getPlace());
					int v = ll_tables.voicingTable.getCoord(ph.phonemeEnum.isVoiced());
					if (i == 0) {
						alignmentTable[i][j] = weightedSum(ll_tables, m_w, p_w, v_w, Gap.BEG, m, p, v) + alignmentTable[i][j - 1];
						backPointers[i][j] = BackPointer.LEFT;
						continue;
					}
					if (j == 0) {
						alignmentTable[i][j] = weightedSum(ll_tables, m_w, p_w, v_w, Gap.BEG, m, p, v) + alignmentTable[i - 1][j];
						backPointers[i][j] = BackPointer.UP;
						continue;
					}
				}

				ConsonantPhoneme ph1 = c1.get(i - 1);
				ConsonantPhoneme ph2 = c2.get(j - 1);

				int m1 = ll_tables.mannerTable.getCoord(ph1.phonemeEnum.getManner());
				int p1 = ll_tables.placeTable.getCoord(ph1.phonemeEnum.getPlace());
				int v1 = ll_tables.voicingTable.getCoord(ph1.phonemeEnum.isVoiced());

				int m2 = ll_tables.mannerTable.getCoord(ph2.phonemeEnum.getManner());
				int p2 = ll_tables.placeTable.getCoord(ph2.phonemeEnum.getPlace());
				int v2 = ll_tables.voicingTable.getCoord(ph2.phonemeEnum.isVoiced());

				double diag = weightedSum(ll_tables,m_w,p_w,v_w,m1,m2,p1,p2,v1,v2) + alignmentTable[i - 1][j - 1];
				double left = weightedSum(ll_tables,m_w,p_w,v_w,(i == c1.size() - 1 ? Gap.END : Gap.MID),m1,p1,v1) + alignmentTable[i][j - 1];
				double up = weightedSum(ll_tables,m_w,p_w,v_w,(j == c2.size() - 1 ? Gap.END : Gap.MID),m1,p1,v1) + alignmentTable[i - 1][j];

				if (diag >= left) {
					if (diag >= up) {
						alignmentTable[i][j] = diag;
						backPointers[i][j] = BackPointer.DIAG;
					}
					else {
						alignmentTable[i][j] = alignmentTable[i - 1][j] + up;
						backPointers[i][j] = BackPointer.UP;
					}
				}
				else {
					if (left >= up) {
						alignmentTable[i][j] = alignmentTable[i][j - 1] + left;
						backPointers[i][j] = BackPointer.LEFT;
					}
					else {
						alignmentTable[i][j] = alignmentTable[i - 1][j] + up;
						backPointers[i][j] = BackPointer.UP;
					}
				}
			}
		}
		printAlignmentTable(c1,c2,alignmentTable);
		printAlignmentTable(c1,c2,backPointers);
		return backtrack(backPointers, c1, c2, alignmentTable, ll_tables, m_w, p_w, v_w);
	}

	private static void printAlignmentTable(List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2, Object[][] table) {
		for (int i = 0; i < table[0].length; i++) {
			if (i == 0) System.out.print("\t$\t");
			else System.out.print(c2.get(i - 1) + "\t");
		}
		System.out.print("\n");
		for (int i = 0; i < table.length; i++) {
			if (i == 0) System.out.print("$\t");
			else System.out.print(c1.get(i - 1) + "\t");
			for (int j = 0; j < table[i].length; j++) {
				if (table[i][j] instanceof Double) {
					System.out.print(Math.floor(((Double) table[i][j]) * 1000) / 1000 + "\t");
				}
				else {
					if (table[i][j] == BackPointer.LEFT) {
						System.out.print("<-\t");
					}
					else if (table[i][j] == BackPointer.UP) {
						System.out.print("^\t");
					}
					else {
						System.out.print("DIA\t");
					}
				}
			}
			System.out.print("\n");
		}
	}

	private static Alignment backtrack(BackPointer[][] backPointers, List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2, Double[][] alignmentTable, MultiConsonantTables scoringMatrices, double m_w, double p_w, double v_w) {
		double totalNormalizedScores = 0.0;
		ConsonantPronunciation r1 = new ConsonantPronunciation();
		ConsonantPronunciation r2 = new ConsonantPronunciation();
		int i = c1.size();
		int j = c2.size();
		int steps = 0;
		while (i > 0 || j > 0) {
			BackPointer dir = backPointers[i][j];

			double actualScore = 0.0;

			//backtrack, build alignment backwards
			switch (dir) {
				case DIAG:
					double loggedVal = alignmentTable[i][j] - alignmentTable[i - 1][j - 1];
					actualScore = deLog(loggedVal);
					r1.add(0, c1.get(i - 1));
					r2.add(0, c2.get(j - 1));
					i--;
					j--;
					break;
				case UP:
					loggedVal = alignmentTable[i][j] - alignmentTable[i - 1][j];
					actualScore = deLog(loggedVal);
					r1.add(0, c1.get(i - 1));
					r2.add(0, null);
					i--;
					break;
				case LEFT:
					loggedVal = alignmentTable[i][j] - alignmentTable[i][j - 1];
					actualScore = deLog(loggedVal);
					r1.add(0, null);
					r2.add(0, c2.get(j - 1));
					j--;
					break;
				default:
					break;
			}
			double normalizedPairScore;
			if (r1.get(0) == null || r2.get(0) == null) {
				if (r1.get(0) == null) {
					normalizedPairScore = actualScore / optimalPhonemeScore(r2.get(0), scoringMatrices, m_w, p_w, v_w);
				}
				else {
					normalizedPairScore = actualScore / optimalPhonemeScore(r1.get(0), scoringMatrices, m_w, p_w, v_w);
				}
			}
			else {
				normalizedPairScore = ((actualScore / optimalPhonemeScore(r1.get(0), scoringMatrices, m_w, p_w, v_w)) + (actualScore / optimalPhonemeScore(r2.get(0), scoringMatrices, m_w, p_w, v_w))) / 2.0;
			}
			totalNormalizedScores += normalizedPairScore;
			steps++;
		}
		final double score = alignmentTable[c1.size()][c2.size()] / ((double)steps);
		final double normalizedScore = totalNormalizedScores / ((double)steps);
		final Alignment result = new Alignment(r1, r2, steps, score, normalizedScore, alignmentTable, backPointers);
		return result;
	}

	public static Alignment greedilyAlign2ConsonantSequences(List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2, MonoConsonantTables monoLLTables) {
		if (c1.isEmpty() && c2.isEmpty()) {
			return new Alignment(new ArrayList<>(), new ArrayList<>(), -1, -1,-1, null, null);
		}
		//add gaps
		else if (c1.isEmpty() && !c2.isEmpty()) {
			List<ConsonantPhoneme> l1 = new ArrayList<>();
			List<ConsonantPhoneme> l2 = new ArrayList<>();
			for (ConsonantPhoneme c : c2) {
				l1.add(null);
				l2.add(c);
			}
			return new Alignment(l1,l2, c2.size(), -1,-1, null, null);
		}
		else if (!c1.isEmpty() && c2.isEmpty()) {
			List<ConsonantPhoneme> l1 = new ArrayList<>();
			List<ConsonantPhoneme> l2 = new ArrayList<>();
			for (ConsonantPhoneme c : c1) {
				l1.add(c);
				l2.add(null);
			}
			return new Alignment(l1,l2,c1.size(),-1,-1, null, null);
		}

		//find highest-scoring match
		double highestScore = Double.NEGATIVE_INFINITY;
		int high_i = -1;
		int high_j = -1;
		for (int i = 0; i < c1.size(); i++) {
			for (int j = 0; j < c2.size(); j++) {
				int m1 = monoLLTables.mannerTable.getCoord(c1.get(i).getManner());
				int p1 = monoLLTables.placeTable.getCoord(c1.get(i).getPlace());
				int v1 = monoLLTables.voicingTable.getCoord(c1.get(i).isVoiced());

				int m2 = monoLLTables.mannerTable.getCoord(c2.get(j).getManner());
				int p2 = monoLLTables.placeTable.getCoord(c2.get(j).getPlace());
				int v2 = monoLLTables.voicingTable.getCoord(c2.get(j).isVoiced());

				double matchValue = monoLLTables.mannerTable.cell(m1,m2) + monoLLTables.placeTable.cell(p1,p2) + monoLLTables.voicingTable.cell(v1,v2);
				if (matchValue > highestScore) {
					highestScore = matchValue;
					high_i = i;
					high_j = j;
				}
			}
		}
		List<ConsonantPhoneme> l1 = new ArrayList<>();
		List<ConsonantPhoneme> l2 = new ArrayList<>();
		l1.add(c1.get(high_i));
		l2.add(c2.get(high_j));
		Alignment currAlignment = new Alignment(l1,l2, l1.size(), -1,-1, null, null);

		//recurse left
		if (high_i > 0 || high_j > 0) {
			List<ConsonantPhoneme> left1 = new ArrayList<>();
			List<ConsonantPhoneme> left2 = new ArrayList<>();
			if (high_i != 0) {
				left1.addAll(c1.subList(0, high_i));
			}
			if (high_j != 0) {
				left2.addAll(c2.subList(0, high_j));
			}
			Alignment leftAlignment = greedilyAlign2ConsonantSequences(left1, left2, monoLLTables);
			currAlignment.c1.addAll(0, leftAlignment.c1);
			currAlignment.c2.addAll(0, leftAlignment.c2);
		}

		//recurse right
		if (high_i < c1.size() - 1 || high_j < c2.size() - 1) {
			List<ConsonantPhoneme> right1 = new ArrayList<>();
			List<ConsonantPhoneme> right2 = new ArrayList<>();
			if (high_i != c1.size() - 1) {
				right1.addAll(c1.subList(high_i + 1, c1.size()));
			}
			if (high_j != c2.size() - 1) {
				right2.addAll(c2.subList(high_j + 1, c2.size()));
			}
			Alignment rightAlignment = greedilyAlign2ConsonantSequences(right1, right2, monoLLTables);
			currAlignment.c1.addAll(rightAlignment.c1);
			currAlignment.c2.addAll(rightAlignment.c2);
		}

		return currAlignment;
	}

	private static double weightedSum(MultiConsonantTables tables, double mannerWeight, double placeWeight, double voicingWeight, int m1, int m2, int p1, int p2, int v1, int v2) {
		final double scoreSum = (tables.mannerTable.cell(m1,m2) * mannerWeight) + (tables.placeTable.cell(p1,p2) * placeWeight) + (tables.voicingTable.cell(v1,v2) * voicingWeight);
		final double weightSum = mannerWeight + placeWeight + voicingWeight;
		final double result = scoreSum / weightSum;
		return result;
	}

	private static double weightedSum(MultiConsonantTables tables, double mannerWeight, double placeWeight, double voicingWeight, Gap g, int m, int p, int v) {
		final int m2 = tables.mannerTable.getGapCoord(g);
		final int p2 = tables.placeTable.getGapCoord(g);
		final int v2 = tables.voicingTable.getGapCoord(g);

		final double scoreSum = (tables.mannerTable.cell(m,m2) * mannerWeight) + (tables.placeTable.cell(p,p2) * placeWeight) + (tables.voicingTable.cell(v,v2) * voicingWeight);
		final double weightSum = mannerWeight + placeWeight + voicingWeight;
		final double result = scoreSum / weightSum;
		return result;
	}

	private static double optimalPhonemeScore(ConsonantPhoneme cp, MultiConsonantTables tables, double mannerWeight, double placeWeight, double voicingWeight) {
		final int m = tables.mannerTable.getCoord(cp.phonemeEnum.getManner());
		final int p = tables.placeTable.getCoord(cp.phonemeEnum.getPlace());
		final int v = tables.voicingTable.getCoord(cp.phonemeEnum.isVoiced());
		final double result = weightedSum(tables, mannerWeight, placeWeight, voicingWeight, m,m, p,p, v,v);
		final double de_logged = deLog(result);
		return de_logged;
	}

	private static double deLog(double d) {
		return Math.pow(Math.E, d);
	}

}
/*
NORMALIZE GLOBAL ALIGNMENT SCORES!!!
Take out of log space = base^n

For every aligned pair(ph1, ph2):
	pairScore = (actual(ph1, ph2) / optimal ph1) + (actual(ph1, ph2) / optimal ph2) / 2

	actual = diff between current cell and pointed to cell
	optimal = taken from table of phonemes against themselves

Then take the mean of all pairs
 */

/*
	Normalize vowel stuff
	Re-code bad code
	Ensure entire score is normalized
	Switch to marginal probabilities for denominator of LL score, use RZ data instead of CMU iterator
	Search thru serialized data, if all equal coda lengths, scrape again
	Inspect RZ api for new endpoints

 */
