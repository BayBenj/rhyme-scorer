package ben_alignment;

import tables.*;
import phonetics.ConsonantPhoneme;
import phonetics.ConsonantPronunciation;

import java.util.ArrayList;
import java.util.List;

public abstract class ConsonantAligner {

	public static Alignment align2ConsonantSequences(List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2, MonoConsonantTables logLikelihoods) {
		double currentScore;
		double[][] table = new double[c1.size() + 1][c2.size() + 1];
		for (int i = 0; i < c1.size() + 1; i++) {
			for (int j = 0; j < c2.size() + 1; j++) {
				if (i == c1.size() - 1 && j == c2.size() - 1) break; //TODO is this right?
				if (i == 0 && j == 0) currentScore = 0;
				else currentScore = table[i][j];

				//TODO is this correct?
				if (i > j) {
					//fill diagonal
					if (i < c1.size() - 1 && j < c2.size() - 1) {
						table[i + 1][j + 1] = currentScore + logLikelihoods.placeTable.get(i + 1).get(j + 1) + logLikelihoods.mannerTable.get(i + 1).get(j + 1) + logLikelihoods.voicingTable.get(i + 1).get(j + 1);
					}

					//fill down
					if (i < c1.size() - 1) {
						table[i + 1][j] = currentScore + logLikelihoods.placeTable.get(i).get(j + 1) + logLikelihoods.mannerTable.get(i).get(j + 1) + logLikelihoods.voicingTable.get(i).get(j + 1);

					}

					//fill right
					if (j < c2.size() - 1) {
						table[i][j + 1] = currentScore + logLikelihoods.placeTable.get(i + 1).get(j) + logLikelihoods.mannerTable.get(i + 1).get(j) + logLikelihoods.voicingTable.get(i + 1).get(j);
					}
				}
				else {
					//fill diagonal
					if (i < c1.size() - 1 && j < c2.size() - 1) {
						table[i + 1][j + 1] = currentScore + logLikelihoods.placeTable.get(i + 1).get(j + 1) + logLikelihoods.mannerTable.get(i + 1).get(j + 1) + logLikelihoods.voicingTable.get(i + 1).get(j + 1);
					}

					//fill down
					if (i < c1.size() - 1) {
						table[i + 1][j] = currentScore + logLikelihoods.placeTable.get(i + 1).get(j) + logLikelihoods.mannerTable.get(i + 1).get(j) + logLikelihoods.voicingTable.get(i + 1).get(j);
					}

					//fill right
					if (j < c2.size() - 1) {
						table[i][j + 1] = currentScore + logLikelihoods.placeTable.get(i).get(j + 1) + logLikelihoods.mannerTable.get(i).get(j + 1) + logLikelihoods.voicingTable.get(i).get(j + 1);
					}
				}

			}
		}
		return backtrack(table, c1, c2);
	}

	public static Alignment backtrack(double[][] table, List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2) {
		ConsonantPronunciation r1 = new ConsonantPronunciation();
		ConsonantPronunciation r2 = new ConsonantPronunciation();
		int i = c1.size() - 1;
		int j = c2.size() - 1;
		while (i > 0 && j > 0) {
			int dir = 0;
			double highestLogLikelihood = Double.MIN_VALUE;
			//look diagonal
			if (table[i - 1][j - 1] > highestLogLikelihood) {
				highestLogLikelihood = table[i - 1][j - 1];
				dir = 0;
			}

			//look up
			if (table[i - 1][j] > highestLogLikelihood) {
				highestLogLikelihood = table[i - 1][j];
				dir = 1;
			}

			//look left
			if (table[i][j - 1] > highestLogLikelihood) {
				highestLogLikelihood = table[i][j - 1];
				dir = -1;
			}

			//backtrack, build alignment backwards
			if (dir == 0) {
				r1.add(0, c1.get(i));
				r2.add(0, c2.get(j));
				i--;
				j--;
			}
			else if (dir == 1) {
				r1.add(0, null);
				r2.add(0, c2.get(j));
				i--;
			}
			else {
				r1.add(0, c1.get(i));
				r2.add(0, null);
				j--;
			}
		}
		return new Alignment(r1, r2);
	}

	public static Alignment greedilyAlign2ConsonantSequences(List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2, MonoConsonantTables monoLLTables) {
		if (c1.isEmpty() && c2.isEmpty()) {
			return new Alignment(new ArrayList<>(), new ArrayList<>());
		}
		//add gaps
		else if (c1.isEmpty() && !c2.isEmpty()) {
			List<ConsonantPhoneme> l1 = new ArrayList<>();
			List<ConsonantPhoneme> l2 = new ArrayList<>();
			for (ConsonantPhoneme c : c2) {
				l1.add(null);
				l2.add(c);
			}
			return new Alignment(l1,l2);
		}
		else if (!c1.isEmpty() && c2.isEmpty()) {
			List<ConsonantPhoneme> l1 = new ArrayList<>();
			List<ConsonantPhoneme> l2 = new ArrayList<>();
			for (ConsonantPhoneme c : c1) {
				l1.add(c);
				l2.add(null);
			}
			return new Alignment(l1,l2);
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
		Alignment currAlignment = new Alignment(l1,l2);

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

}
