package genetic;

import data.DataContainer;
import data.ScoreDataset;
import data.SimpleDataset;
import main.Main;
import phonetics.syllabic.LL_Rhymer;
import tables.MultiTables;

import java.io.IOException;
import java.util.*;

public class GeneticMain {

	public final static Random r = new Random();
	private final static int topIndividualN = 20;
	private final static int offspringN = 100;
	private final static int maxGenerations = 1000;
	public final static double fitnessThreshold = 0.5;
	private final static int rzCorpusSize = 10000;
	public static double temp = 100.00;
	private final static double coolingRate = 0.1;
	public static SimpleDataset data;
	public static ScoreDataset score_data;

	public static void main(String[] args) {
		long startTime = System.nanoTime();

		try {
			Main.main(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//set scoring tables
		MultiTables tables = Main.finalTables;
		Individual.tables = tables;

//		data = DataContainer.rhymeZoneAdvanced;
		score_data = DataContainer.rhymeZoneScoredAdvanced;

		Map<String, Double> values = LL_Rhymer.get100Weights();

		TreeSet<Individual> topIndividuals = new TreeSet<>();
		for (int i = 0; i < topIndividualN; i++) {
			Individual temp = new Individual(values);
			temp.mutate();
//			temp.calculateBinaryFitness();
			temp.classifyByScore();
			topIndividuals.add(temp);
		}

		double bestFitnessYet = 101;
		double bestOfGeneration = 101;
		double generationalTop10Average;
		Individual bestIndividualYet;
		int generation = 0;

//		while (bestOfGeneration > 0.0 && generation < maxGenerations) {
		while (generation < maxGenerations) {
			generation++;
			System.out.println("Generation " + generation + "...");

			//top individuals mate
			List<Individual> allIndividualsOfNewGeneration = mateTopIndividuals(topIndividuals);
			List<Individual> pool = new ArrayList<>(allIndividualsOfNewGeneration);

			//OPTIONAL -- mix parents and new generation
			pool.addAll(topIndividuals);

			//find top topIndividualN individuals of new generation
			topIndividuals = findTopIndividuals(pool);

			generationalTop10Average = generationalAverage(topIndividuals);

			//update highestFitness
			bestOfGeneration = topIndividuals.first().getMean_sq_error();//last for binary F-score

			//update absolutes
			if (bestOfGeneration < bestFitnessYet) {
				bestFitnessYet = bestOfGeneration;
				bestIndividualYet = topIndividuals.first();
				System.out.println("\tNew best individual for " + rzCorpusSize + ": " + bestIndividualYet.getMean_sq_error());
				Map<String,Double> map = bestIndividualYet.getValues();

				System.out.println("\t\tfrontness: " + map.get("frontness"));
				System.out.println("\t\theight: " + map.get("height"));
				System.out.println("\t\troundness: " + map.get("roundness"));
				System.out.println("\t\ttension: " + map.get("tension"));
				System.out.println("\t\tstress: " + map.get("stress") + "\n");

				System.out.println("\t\tmanner: " + map.get("manner"));
				System.out.println("\t\tplace: " + map.get("place"));
				System.out.println("\t\tvoicing: " + map.get("voicing") + "\n");

				System.out.println("\t\tonset: " + map.get("onset"));
				System.out.println("\t\tnucleus: " + map.get("nucleus"));
				System.out.println("\t\tcoda: " + map.get("coda") + "\n");
			}

			System.out.println("\tTemp: " + temp);
			System.out.println("\tAvrg fitness for Gen" + generation + ": " + generationalTop10Average);
			System.out.println("\tBest fitness for Gen" + generation + ": " + bestOfGeneration);
			System.out.println("\tBest fitness of all: " + bestFitnessYet);

			//cool mutation rate
			if (temp > 1.0)
				temp -= coolingRate;
		}
		System.out.println("\tBest individual of final generation: " + topIndividuals.first().getMean_sq_error());
		Map<String,Double> map = topIndividuals.first().getValues();

		System.out.println("\t\tfrontness: " + map.get("frontness"));
		System.out.println("\t\theight: " + map.get("height") + "\n");
		System.out.println("\t\troundness: " + map.get("roundness") + "\n");
		System.out.println("\t\ttension: " + map.get("tension") + "\n");
		System.out.println("\t\tstress: " + map.get("stress") + "\n");

		System.out.println("\t\tmanner: " + map.get("manner"));
		System.out.println("\t\tplace: " + map.get("place"));
		System.out.println("\t\tvoicing: " + map.get("voicing") + "\n");

		System.out.println("\t\tonset: " + map.get("onset"));
		System.out.println("\t\tnucleus: " + map.get("nucleus"));
		System.out.println("\t\tcoda: " + map.get("coda") + "\n");

		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		if (totalTime / 1000000000 > 59) {
			int minutes = (int) (totalTime / 1000000000 / 60);
			int seconds = (int) (totalTime / 1000000000);
			System.out.println("TIME: " + minutes + " minutes " + seconds + " seconds");
		} else
			System.out.println(("TIME: " + (totalTime / 1000000000) % 60 + " seconds"));
	}

	public static List<Individual> mateTopIndividuals(Collection<Individual> topIndividuals) {
		//TODO optimize so only lists come in? or they stay as treesets for sorting?
		List<Individual> result = new ArrayList<>();
		for (int i = 0; i < offspringN; i++) {
			int n1 = r.nextInt(topIndividuals.size());
			int n2 = n1;
			while (n2 == n1) {
				n2 = r.nextInt(topIndividuals.size());
			}
			Individual mater1 = null;
			int j = 0;
			for (Individual individual : topIndividuals) {
				if (j == n1) {
					mater1 = individual;
					break;
				}
				j++;
			}
			Individual mater2 = null;
			j = 0;
			for (Individual individual : topIndividuals) {
				if (j == n2) {
					mater2 = individual;
					break;
				}
				j++;
			}
			Individual child = mater1.crossover(mater2);
			child.mutate();
//			child.calculateBinaryFitness();
			child.classifyByScore();
			result.add(child);
		}
		return result;
	}

	public static TreeSet<Individual> findTopIndividuals(Collection<Individual> allIndividuals) {
		TreeSet<Individual> calculatedIndividuals = new TreeSet<>();
		//sort by fitness, return the top topIndividualN
		for (Individual ind : allIndividuals) {
			if (ind.getMean_sq_error() == 101)
//				ind.calculateBinaryFitness();
				ind.classifyByScore();
			calculatedIndividuals.add(ind);
		}

		TreeSet<Individual> result = new TreeSet<>();
		try {
			for (int i = 0; i < topIndividualN; i++) {
				if (!calculatedIndividuals.isEmpty()) {
					result.add(calculatedIndividuals.first());
					calculatedIndividuals.remove(calculatedIndividuals.first());
				} else break;
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static double generationalAverage(Collection<Individual> inds) {
		double total = 0;
		for (Individual ind : inds) {
			total += ind.getMean_sq_error();
		}
		return total / inds.size();
	}

}
