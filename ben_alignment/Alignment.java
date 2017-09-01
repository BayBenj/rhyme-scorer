package ben_alignment;

import phonetics.ConsonantPhoneme;

import java.util.List;

public class Alignment {

	public List<ConsonantPhoneme> c1;
	public List<ConsonantPhoneme> c2;
	public int steps; //equal to length of c1 or c2
	public double score;
	public double normalizedScore;
	public double[][] values;
	public BackPointer[][] backPointers;

	public Alignment(List<ConsonantPhoneme> c1, List<ConsonantPhoneme> c2, int steps, double score, double normalizedScore, double[][] values, BackPointer[][] backPointers) {
		this.c1 = c1;
		this.c2 = c2;
		this.steps = steps;
		this.score = score;
		this.normalizedScore = normalizedScore;
		this.values = values;
		this.backPointers = backPointers;
	}

	@Override
	public String toString() {
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		for (int i = 0; i < c1.size(); i++) {
			if (c1.get(i) != null) {
				sb1.append(c1.get(i));
			}
			else {
				sb1.append("_");
			}
			if (i < c1.size() - 1) sb1.append("-");
		}
		for (int i = 0; i < c2.size(); i++) {
			if (c2.get(i) != null) {
				sb2.append(c2.get(i));
			}
			else {
				sb2.append("_");
			}
			if (i < c2.size() - 1) sb2.append("-");
		}
		return sb1.toString() + " / " + sb2.toString();
	}
}
