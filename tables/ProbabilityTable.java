package tables;

import java.util.ArrayList;

public abstract class ProbabilityTable extends ArrayList<ArrayList<Double>> {

	public ProbabilityTable() {
		super();
		for (int i = 0; i < this.get_i_size(); i++) {
			this.add(i, new ArrayList<>());
			for (int j = 0; j < this.get_j_size(); j++) {
				this.get(i).add(j, 0.0);
			}
		}

	}

	public void foldDiagonally() {
		for (int i = 1; i < this.get_i_size(); i++) {
			for (int j = 0; j < this.get_j_size(); j++) {
				if (i > j) {
					this.get(j).set(i, this.get(j).get(i) + this.get(i).get(j));
					this.get(i).set(j, 0.0);
				}
			}
		}
	}

	public void print() {
		System.out.print("\t");
		for (int i = 0; i < this.get_j_size(); i++) {
			System.out.print(this.lineName(i) + "\t");
		}
		System.out.print("\n");
		for (int i = 0; i < this.get_i_size(); i++) {
			System.out.print(this.lineName(i) + "\t");
			for (int j = 0; j < this.get_j_size(); j++) {
				if (i > j) System.out.print("-\t");
				else System.out.print(((int)(double)(this.get(i).get(j)))+ "\t");
			}
			System.out.print("\n");
		}
	}

	public void printLL() {
		System.out.print("\t");
		for (int i = 0; i < this.get_j_size(); i++) {
			System.out.print(this.lineName(i) + "\t");
		}
		System.out.print("\n");
		for (int i = 0; i < this.get_i_size(); i++) {
			System.out.print(this.lineName(i) + "\t");
			for (int j = 0; j < this.get_j_size(); j++) {
				if (i > j) System.out.print("-\t");
				else System.out.print(Math.floor((this.get(i).get(j)) * 1000) / 1000 + "\t");
			}
			System.out.print("\n");
		}
	}

	public void printProbability() {
		System.out.print("\t");
		for (int i = 0; i < this.get_j_size(); i++) {
			System.out.print(this.lineName(i) + "\t");
		}
		System.out.print("\n");
		for (int i = 0; i < this.get_i_size(); i++) {
			System.out.print(this.lineName(i) + "\t");
			for (int j = 0; j < this.get_j_size(); j++) {
				if (i > j) System.out.print("-\t");
				else System.out.print(Math.floor(this.get(i).get(j) / this.total() * 1000) / 1000 + "\t");
			}
			System.out.print("\n");
		}
	}

	public static double computeLogLikelihood(int randomMatches, int randomTotal, int rhymeMatches, int rhymeTotal) {
		if (randomMatches == 0 || rhymeMatches == 0) return 0;
//		double result = ((double)rhymeMatches/(double)rhymeTotal) / ((double)randomMatches/(double)randomTotal);
		double result = Math.log(((double)rhymeMatches/(double)rhymeTotal) / ((double)randomMatches/(double)randomTotal));
		return result;
	}

	public int total() {
		int result = 0;
		for (int i = 0; i < this.get_i_size(); i++) {
			for (int j = 0; j < this.get_j_size(); j++) {
				result += this.get(i).get(j);
			}
		}
		return result;
	}

	public double cell(int i, int j) {
		if (i > j) return this.get(j).get(i);
		return this.get(i).get(j);
	}

	public int getGapCoord(Gap g) {
		switch (g) {
			case BEG: return getSize() + 0;
			case MID: return getSize() + 1;
			case END: return getSize() + 2;
		}
		return -1;
	}

	public String gapName(int i) {
		switch (i) {
			case 0: return "b_g";
			case 1: return "m_g";
			case 2: return "e_g";
		}
		return null;
	}

	public abstract int get_i_size();
	public abstract int get_j_size();
	public abstract int getSize();
	public abstract String lineName(int i);

}
