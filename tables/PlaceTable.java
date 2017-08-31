package tables;

import phonetics.PlaceOfArticulation;

public abstract class PlaceTable extends ProbabilityTable {

	public PlaceTable() {
		super();
	}

	public void fillCell(PlaceOfArticulation p1, PlaceOfArticulation p2) {
		int x = getCoord(p1);
		int y = getCoord(p2);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public void fillGapCell(Gap g, PlaceOfArticulation m) {
		int x = getCoord(m);
		int y = getGapCoord(g);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public int getSize() {
		return 7;
	}

	public int getCoord(PlaceOfArticulation p) {
		switch (p) {
			case BILABIAL:
				return bilabial();
			case LABIODENTAL:
				return labiodental();
			case INTERDENTAL:
				return interdental();
			case ALVEOLAR:
				return alveolar();
			case PALATAL:
				return palatal();
			case VELAR:
				return velar();
			case GLOTTAL:
				return glottal();
		}
		return -1;
	}

	public String lineName(int i) {
		if (i >= getSize()) {
			return gapName(i - getSize());
		}
		switch (i) {
			case 0: return "bil";
			case 1: return "lab";
			case 2: return "int";
			case 3: return "alv";
			case 4: return "pal";
			case 5: return "vel";
			case 6: return "glo";
		}
		return null;
	}

	public static void printLogLikelihood(PlaceTable randomMatches, PlaceTable rhymeMatches) {
		System.out.print("\t");
		for (int i = 0; i < randomMatches.get_j_size(); i++) {
			System.out.print(randomMatches.lineName(i) + "\t");
		}
		System.out.print("\n");
		for (int i = 0; i < randomMatches.get_i_size(); i++) {
			System.out.print(randomMatches.lineName(i) + "\t");
			for (int j = 0; j < randomMatches.get_j_size(); j++) {
				if (i > j) System.out.print("-\t");
				else System.out.print(Math.floor((computeLogLikelihood((int)(double)randomMatches.get(i).get(j),randomMatches.total(),(int)(double)rhymeMatches.get(i).get(j),rhymeMatches.total())) * 1000) / 1000 + "\t");
			}
			System.out.print("\n");
		}
	}

	public int bilabial() {
		return 0;
	}

	public int labiodental() {
		return 1;
	}

	public int interdental() {
		return 2;
	}

	public int alveolar() {
		return 3;
	}

	public int palatal() {
		return 4;
	}

	public int velar() {
		return 5;
	}

	public int glottal() {
		return 6;
	}

}
/*
	BILABIAL,
	LABIODENTAL,
	INTERDENTAL,
	ALVEOLAR,
	PALATAL,
	VELAR,
	GLOTTAL
 */
