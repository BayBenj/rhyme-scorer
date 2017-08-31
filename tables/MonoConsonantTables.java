package tables;

import java.util.List;

public class MonoConsonantTables {

	public MonoMannerTable mannerTable;
	public MonoPlaceTable placeTable;
	public MonoVoicingTable voicingTable;

	public MonoConsonantTables(MonoMannerTable mannerTable, MonoPlaceTable placeTable, MonoVoicingTable voicingTable) {
		this.mannerTable = mannerTable;
		this.placeTable = placeTable;
		this.voicingTable = voicingTable;
	}

	public static void printLogLikelihoods(MonoConsonantTables randoms, MonoConsonantTables rhymes) {
		System.out.println("\n\nLog-Likelihoods");
		System.out.println("\nManner of articulation");
		MannerTable.printLogLikelihood(randoms.mannerTable, rhymes.mannerTable);
		System.out.println("\nPlace of articulation");
		PlaceTable.printLogLikelihood(randoms.placeTable, rhymes.placeTable);
		System.out.println("\nVoicing");
		VoicingTable.printLogLikelihood(randoms.voicingTable, rhymes.voicingTable);
	}

	public static MonoConsonantTables getLogLikelihoodTables(MonoConsonantTables randoms, MonoConsonantTables rhymes) {
		//manner
		MonoMannerTable ll_mannerTable = new MonoMannerTable();
		for (int i = 0; i < randoms.mannerTable.get_i_size(); i++) {
			List<Double> list = ll_mannerTable.get(i);
			for (int j = 0; j < randoms.mannerTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.mannerTable.cell(i,j), randoms.mannerTable.total(),(int)rhymes.mannerTable.cell(i,j),rhymes.mannerTable.total()));
			}
		}

		//place
		MonoPlaceTable ll_placeTable = new MonoPlaceTable();
		for (int i = 0; i < randoms.placeTable.get_i_size(); i++) {
			List<Double> list = ll_placeTable.get(i);
			for (int j = 0; j < randoms.placeTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.placeTable.cell(i,j), randoms.placeTable.total(),(int)rhymes.placeTable.cell(i,j),rhymes.placeTable.total()));
			}
		}

		//voicing
		MonoVoicingTable ll_voicingTable = new MonoVoicingTable();
		for (int i = 0; i < randoms.voicingTable.get_i_size(); i++) {
			List<Double> list = ll_voicingTable.get(i);
			for (int j = 0; j < randoms.voicingTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.voicingTable.cell(i,j), randoms.voicingTable.total(),(int)rhymes.voicingTable.cell(i,j),rhymes.voicingTable.total()));
			}
		}

		return new MonoConsonantTables(ll_mannerTable, ll_placeTable, ll_voicingTable);
	}

	public void LL_table_printLL() {
		System.out.println("\nManner of articulation:");
		this.mannerTable.printLL();
		System.out.println("\nPlace of articulation:");
		this.placeTable.printLL();
		System.out.println("\nVoicing:");
		this.voicingTable.printLL();
	}

	public void printProbabilities() {
		System.out.println("\n\nProbabilities");
		System.out.println("\nManner of articulation");
		mannerTable.printProbability();
		System.out.println("\nPlace of articulation");
		placeTable.printProbability();
		System.out.println("\nVoicing");
		voicingTable.printProbability();
	}

	public void printCounts() {
		System.out.println("\nManner of articulation:");
		this.mannerTable.print();
		System.out.println("\nPlace of articulation:");
		this.placeTable.print();
		System.out.println("\nVoicing:");
		this.voicingTable.print();
	}

	public void foldTables() {
		this.mannerTable.foldDiagonally();
		this.placeTable.foldDiagonally();
		this.voicingTable.foldDiagonally();
	}

}
