package tables;

import java.util.List;

public class VowelTables {

	public HeightTable heightTable;
	public FrontnessTable frontnessTable;
	public RoundnessTable roundnessTable;
	public TensionTable tensionTable;
	public StressTable stressTable;

	public VowelTables(HeightTable heightTable, FrontnessTable frontnessTable, RoundnessTable roundnessTable, TensionTable tensionTable, StressTable stressTable) {
		this.heightTable = heightTable;
		this.frontnessTable = frontnessTable;
		this.roundnessTable = roundnessTable;
		this.tensionTable = tensionTable;
		this.stressTable = stressTable;
	}

	public static void printLogLikelihoods(VowelTables randoms, VowelTables rhymes) {
		System.out.println("\n\nLog-Likelihoods");
		System.out.println("\nHeight");
		HeightTable.printLogLikelihood(randoms.heightTable, rhymes.heightTable);
		System.out.println("\nFrontness");
		FrontnessTable.printLogLikelihood(randoms.frontnessTable, rhymes.frontnessTable);
		System.out.println("\nRoundess");
		RoundnessTable.printLogLikelihood(randoms.roundnessTable, rhymes.roundnessTable);
		System.out.println("\nTension");
		TensionTable.printLogLikelihood(randoms.tensionTable, rhymes.tensionTable);
		System.out.println("\nStress");
		StressTable.printLogLikelihood(randoms.stressTable, rhymes.stressTable);
	}

	public static VowelTables getLogLikelihoodTables(VowelTables randoms, VowelTables rhymes) {
		//height
		HeightTable ll_heightTable = new HeightTable();
		for (int i = 0; i < randoms.heightTable.get_i_size(); i++) {
			List<Double> list = ll_heightTable.get(i);
			for (int j = 0; j < randoms.heightTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.heightTable.cell(i,j), randoms.heightTable.total(),(int)rhymes.heightTable.cell(i,j),rhymes.heightTable.total()));
			}
		}

		//frontness
		FrontnessTable ll_frontnessTable = new FrontnessTable();
		for (int i = 0; i < randoms.frontnessTable.get_i_size(); i++) {
			List<Double> list = ll_frontnessTable.get(i);
			for (int j = 0; j < randoms.frontnessTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.frontnessTable.cell(i,j), randoms.frontnessTable.total(),(int)rhymes.frontnessTable.cell(i,j),rhymes.frontnessTable.total()));
			}
		}

		//roundness
		RoundnessTable ll_roundnessTable = new RoundnessTable();
		for (int i = 0; i < randoms.roundnessTable.get_i_size(); i++) {
			List<Double> list = ll_roundnessTable.get(i);
			for (int j = 0; j < randoms.roundnessTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.roundnessTable.cell(i,j), randoms.roundnessTable.total(),(int)rhymes.roundnessTable.cell(i,j),rhymes.roundnessTable.total()));
			}
		}

		//tension
		TensionTable ll_tensionTable = new TensionTable();
		for (int i = 0; i < randoms.tensionTable.get_i_size(); i++) {
			List<Double> list = ll_tensionTable.get(i);
			for (int j = 0; j < randoms.tensionTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.tensionTable.cell(i,j), randoms.tensionTable.total(),(int)rhymes.tensionTable.cell(i,j),rhymes.tensionTable.total()));
			}
		}

		//stress
		StressTable ll_stressTable = new StressTable();
		for (int i = 0; i < randoms.stressTable.get_i_size(); i++) {
			List<Double> list = ll_stressTable.get(i);
			for (int j = 0; j < randoms.stressTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.stressTable.cell(i,j), randoms.stressTable.total(),(int)rhymes.stressTable.cell(i,j),rhymes.stressTable.total()));
			}
		}

		return new VowelTables(ll_heightTable, ll_frontnessTable, ll_roundnessTable, ll_tensionTable, ll_stressTable);
	}

	public void LL_table_printLL() {
		System.out.println("\nHeight:");
		heightTable.printLL();
		System.out.println("\nFrontness:");
		frontnessTable.printLL();
		System.out.println("\nRoundness:");
		roundnessTable.printLL();
		System.out.println("\nTension:");
		tensionTable.printLL();
		System.out.println("\nStress:");
		stressTable.printLL();
	}

	public void printProbabilities() {
		System.out.println("\n\nProbabilities");
		System.out.println("\nHeight");
		heightTable.printProbability();
		System.out.println("\nFrontness");
		frontnessTable.printProbability();
		System.out.println("\nRoundness");
		roundnessTable.printProbability();
		System.out.println("\nTension");
		tensionTable.printProbability();
		System.out.println("\nStress:");
		stressTable.printProbability();
	}

	public void printCounts() {
		System.out.println("\nHeight:");
		this.heightTable.print();
		System.out.println("\nFrontness:");
		this.frontnessTable.print();
		System.out.println("\nRoundness:");
		this.roundnessTable.print();
		System.out.println("\nTension:");
		this.tensionTable.print();
		System.out.println("\nStress:");
		this.stressTable.print();
	}

	public void foldTables() {
		this.heightTable.foldDiagonally();
		this.frontnessTable.foldDiagonally();
		this.roundnessTable.foldDiagonally();
		this.tensionTable.foldDiagonally();
		this.stressTable.foldDiagonally();
	}

}
