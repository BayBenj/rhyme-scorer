package tables;

import phonetics.Height;

public class HeightTable extends ProbabilityTable {

	public HeightTable() {
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

	public void fillCell(Height h1, Height h2) {
		int x = getCoord(h1);
		int y = getCoord(h2);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public int getCoord(Height h) {
		switch (h) {
			case HIGH:
				return high();
			case MID:
				return mid();
			case LOW:
				return low();
		}
		return -1;
	}

	public String lineName(int i) {
		switch (i) {
			case 0: return "hi";
			case 1: return "mid";
			case 2: return "low";
		}
		return null;
	}

	public static void printLogLikelihood(HeightTable randomMatches, HeightTable rhymeMatches) {
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

	public int high() {
		return 0;
	}

	public int mid() {
		return 1;
	}


	public int low() {
		return 2;
	}

}
