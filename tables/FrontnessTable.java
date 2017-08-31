package tables;

import phonetics.Frontness;

public class FrontnessTable extends ProbabilityTable {

	public FrontnessTable() {
		super();
	}

	public int getSize() {
		return 3;
	}

	@Override
	public int get_i_size() {
		return getSize();
	}

	@Override
	public int get_j_size() {
		return getSize();
	}

	public void fillCell(Frontness f1, Frontness f2) {
		int x = getCoord(f1);
		int y = getCoord(f2);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public int getCoord(Frontness f) {
		switch (f) {
			case FRONT:
				return front();
			case CENTRAL:
				return central();
			case BACK:
				return back();
		}
		return -1;
	}

	public String lineName(int i) {
		switch (i) {
			case 0: return "fro";
			case 1: return "cen";
			case 2: return "bac";
		}
		return null;
	}

	public static void printLogLikelihood(FrontnessTable randomMatches, FrontnessTable rhymeMatches) {
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

	public int front() {
		return 0;
	}

	public int central() {
		return 1;
	}

	public int back() {
		return 2;
	}

}
