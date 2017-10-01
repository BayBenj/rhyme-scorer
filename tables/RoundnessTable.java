package tables;

import phonetics.Roundness;

import java.io.Serializable;

public class RoundnessTable extends ProbabilityTable implements Serializable {

	public RoundnessTable() {
		super();
	}

	public int getSize() {
		return 2;
	}

	@Override
	public int get_i_size() {
		return getSize();
	}

	@Override
	public int get_j_size() {
		return getSize();
	}

	public void fillCell(Roundness r1, Roundness r2) {
		int x = getCoord(r1);
		int y = getCoord(r2);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public int getCoord(Roundness r) {
		switch (r) {
			case ROUND:
				return round();
			case NOT_ROUND:
				return not_round();
		}
		return -1;
	}

	public String lineName(int i) {
		switch (i) {
			case 0: return "rou";
			case 1: return "not";
		}
		return null;
	}

	public static void printLogLikelihood(RoundnessTable randomMatches, RoundnessTable rhymeMatches) {
		System.out.print("\t");
		for (int i = 0; i < randomMatches.getSize(); i++) {
			System.out.print(randomMatches.lineName(i) + "\t");
		}
		System.out.print("\n");
		for (int i = 0; i < randomMatches.getSize(); i++) {
			System.out.print(randomMatches.lineName(i) + "\t");
			for (int j = 0; j < randomMatches.getSize(); j++) {
				if (i > j) System.out.print("-\t");
//				else System.out.print((computeLogLikelihood((int)(double)randomMatches.get(i).get(j),randomMatches.total(),(int)(double)rhymeMatches.get(i).get(j),rhymeMatches.total())) + "\t");
				else System.out.print(Math.floor((computeLogLikelihood((int)(double)randomMatches.get(i).get(j),randomMatches.total(),(int)(double)rhymeMatches.get(i).get(j),rhymeMatches.total()))* 1000) / 1000 + "\t");
			}
			System.out.print("\n");
		}
	}

	public int round() {
		return 0;
	}

	public int not_round() {
		return 1;
	}

}
