package tables;

public abstract class VoicingTable extends ProbabilityTable {

	public int getSize() {
		return 2;
	}

	public void fillCell(boolean v1, boolean v2) {
		int x = getCoord(v1);
		int y = getCoord(v2);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public void fillGapCell(Gap g, boolean b) {
		int x = getCoord(b);
		int y = getGapCoord(g);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public int getCoord(boolean b) {
		if (b) return 0;
		return 1;
	}

	public String lineName(int i) {
		if (i >= getSize()) {
			return gapName(i - getSize());
		}
		switch (i) {
			case 0: return "v";
			case 1: return "no";
		}
		return null;
	}

	public static void printLogLikelihood(VoicingTable randomMatches, VoicingTable rhymeMatches) {
		System.out.print("\t");
		for (int i = 0; i < randomMatches.get_j_size(); i++) {
			System.out.print(randomMatches.lineName(i) + "\t");
		}
		System.out.print("\n");
		for (int i = 0; i < randomMatches.get_i_size(); i++) {
			System.out.print(randomMatches.lineName(i) + "\t");
			for (int j = 0; j < randomMatches.get_j_size(); j++) {
				if (i > j) System.out.print("-\t");
//				else System.out.print((computeLogLikelihood((int)(double)normalMatches.get(i).get(j),normalMatches.total(),(int)(double)rhymeMatches.get(i).get(j),rhymeMatches.total())) + "\t");
				else System.out.print(Math.floor((ProbabilityTable.computeLogLikelihood((int)(double)randomMatches.get(i).get(j),randomMatches.total(),(int)(double)rhymeMatches.get(i).get(j),rhymeMatches.total())) * 1000) / 1000 + "\t");
			}
			System.out.print("\n");
		}
	}

}
