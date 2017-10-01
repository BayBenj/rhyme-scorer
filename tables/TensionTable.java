package tables;

import phonetics.Tension;

import java.io.Serializable;

public class TensionTable extends ProbabilityTable implements Serializable {

	public TensionTable() {
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

	public void fillCell(Tension t1, Tension t2) {
		int x = getCoord(t1);
		int y = getCoord(t2);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public int getCoord(Tension t) {
		switch (t) {
			case TENSE:
				return tense();
			case LAX:
				return lax();
		}
		return -1;
	}

	public String lineName(int i) {
		switch (i) {
			case 0: return "tns";
			case 1: return "lax";
		}
		return null;
	}

	public static void printLogLikelihood(TensionTable randomMatches, TensionTable rhymeMatches) {
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

	public int tense() {
		return 0;
	}

	public int lax() {
		return 1;
	}

}
