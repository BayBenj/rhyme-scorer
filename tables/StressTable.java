package tables;

public class StressTable extends ProbabilityTable {

	public StressTable() {
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

	public void fillCell(Stress s1, Stress s2) {
		int x = getCoord(s1);
		int y = getCoord(s2);
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public void fillCell(int s1, int s2) {
		int x;
		int y;
		switch (s1) {
			case 1:
				x = primary();
				break;
			case 2:
				x = secondary();
				break;
			default:
				x = none();
		}
		switch (s2) {
			case 1:
				y = primary();
				break;
			case 2:
				y = secondary();
				break;
			default:
				y = none();
		}
		Double cell = this.get(x).get(y);
		if (cell == null || cell <= 0)
			this.get(x).set(y, 1.0);
		else
			this.get(x).set(y, cell + 1.0);
	}

	public int getCoord(Stress s) {
		switch (s) {
			case PRI:
				return primary();
			case SEC:
				return secondary();
			case NUL:
				return none();
		}
		return -1;
	}

	public String lineName(int i) {
		switch (i) {
			case 0: return "pri";
			case 1: return "sec";
			case 2: return "nul";
		}
		return null;
	}

	public static void printLogLikelihood(StressTable randomMatches, StressTable rhymeMatches) {
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

	public int primary() {
		return 0;
	}

	public int secondary() {
		return 1;
	}

	public int none() {
		return 2;
	}

}
