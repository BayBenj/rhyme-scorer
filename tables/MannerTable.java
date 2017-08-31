package tables;

import phonetics.MannerOfArticulation;

public abstract class MannerTable extends ProbabilityTable {

	public MannerTable() {
		super();
	}

	public int getSize() {
		return 7;
	}

	public void fillCell(MannerOfArticulation m1, MannerOfArticulation m2) {
		int x = getCoord(m1);
		int y = getCoord(m2);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public void fillGapCell(Gap g, MannerOfArticulation m) {
		int x = getCoord(m);
		int y = getGapCoord(g);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public int getCoord(MannerOfArticulation m) {
		switch (m) {
			case AFFRICATE:
				return affricate();
			case ASPIRATE:
				return aspirate();
			case FRICATIVE:
				return fricative();
			case LIQUID:
				return liquid();
			case NASAL:
				return nasal();
			case SEMIVOWEL:
				return semivowel();
			case STOP:
				return stop();
		}
		return -1;
	}

	public String lineName(int i) {
		if (i >= getSize()) {
			return gapName(i - getSize());
		}
		switch (i) {
			case 0: return "aff";
			case 1: return "asp";
			case 2: return "fri";
			case 3: return "liq";
			case 4: return "nas";
			case 5: return "sem";
			case 6: return "sto";
		}
		return null;
	}

	public static void printLogLikelihood(MannerTable randomMatches, MannerTable rhymeMatches) {
		System.out.print("\t");
		for (int i = 0; i < randomMatches.get_j_size(); i++) {
			System.out.print(randomMatches.lineName(i) + "\t");
		}
		System.out.print("\n");
		for (int i = 0; i < randomMatches.get_i_size(); i++) {
			System.out.print(randomMatches.lineName(i) + "\t");
			for (int j = 0; j < randomMatches.get_j_size(); j++) {
				if (i > j) System.out.print("-\t");
//				else System.out.print((computeLogLikelihood((int)(double)randomMatches.get(i).get(j),randomMatches.total(),(int)(double)rhymeMatches.get(i).get(j),rhymeMatches.total())) + "\t");
				else System.out.print(Math.floor((computeLogLikelihood((int)(double)randomMatches.get(i).get(j),randomMatches.total(),(int)(double)rhymeMatches.get(i).get(j),rhymeMatches.total()))* 1000) / 1000 + "\t");
			}
			System.out.print("\n");
		}
	}

	public int affricate() {
		return 0;
	}

	public int aspirate() {
		return 1;
	}

	public int fricative() {
		return 2;
	}

	public int liquid() {
		return 3;
	}

	public int nasal() {
		return 4;
	}

	public int semivowel() {
		return 5;
	}

	public int stop() {
		return 6;
	}

}

/*
	AFFRICATE,
	ASPIRATE,
	FRICATIVE,
	LIQUID,
	NASAL,
	SEMIVOWEL,
	STOP

 */
