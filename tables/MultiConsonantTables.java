package tables;

import java.io.Serializable;
import java.util.List;

public class MultiConsonantTables implements Serializable {

	public MultiMannerTable mannerTable;
	public MultiPlaceTable placeTable;
	public MultiVoicingTable voicingTable;

	public MultiConsonantTables(MultiMannerTable mannerTable, MultiPlaceTable placeTable, MultiVoicingTable voicingTable) {
		this.mannerTable = mannerTable;
		this.placeTable = placeTable;
		this.voicingTable = voicingTable;
	}

	public static void printLogLikelihoods(MultiConsonantTables randoms, MultiConsonantTables rhymes) {
		System.out.println("\n\nLog-Likelihoods");
		System.out.println("\nManner of articulation");
		MannerTable.printLogLikelihood(randoms.mannerTable, rhymes.mannerTable);
		System.out.println("\nPlace of articulation");
		PlaceTable.printLogLikelihood(randoms.placeTable, rhymes.placeTable);
		System.out.println("\nVoicing");
		VoicingTable.printLogLikelihood(randoms.voicingTable, rhymes.voicingTable);
	}

	public static MultiConsonantTables getLogLikelihoodTables(MultiConsonantTables randoms, MultiConsonantTables rhymes) {
		//manner
		MultiMannerTable ll_mannerTable = new MultiMannerTable();
		for (int i = 0; i < randoms.mannerTable.get_i_size(); i++) {
			List<Double> list = ll_mannerTable.get(i);
			for (int j = 0; j < randoms.mannerTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.mannerTable.cell(i,j), randoms.mannerTable.total(),(int)rhymes.mannerTable.cell(i,j),rhymes.mannerTable.total()));
			}
		}

		//place
		MultiPlaceTable ll_placeTable = new MultiPlaceTable();
		for (int i = 0; i < randoms.placeTable.get_i_size(); i++) {
			List<Double> list = ll_placeTable.get(i);
			for (int j = 0; j < randoms.placeTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.placeTable.cell(i,j), randoms.placeTable.total(),(int)rhymes.placeTable.cell(i,j),rhymes.placeTable.total()));
			}
		}

		//voicing
		MultiVoicingTable ll_voicingTable = new MultiVoicingTable();
		for (int i = 0; i < randoms.voicingTable.get_i_size(); i++) {
			List<Double> list = ll_voicingTable.get(i);
			for (int j = 0; j < randoms.voicingTable.get_j_size(); j++) {
				if (i <= j) list.set(j, ProbabilityTable.computeLogLikelihood((int)randoms.voicingTable.cell(i,j), randoms.voicingTable.total(),(int)rhymes.voicingTable.cell(i,j),rhymes.voicingTable.total()));
			}
		}

		return new MultiConsonantTables(ll_mannerTable, ll_placeTable, ll_voicingTable);
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
