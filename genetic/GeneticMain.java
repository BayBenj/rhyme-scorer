package genetic;

import data.DataContainer;
import data.Dataset;
import main.Main;
import tables.MultiTables;

import java.io.IOException;
import java.util.*;

public class GeneticMain {

	public final static Random r = new Random();
	private final static int topIndividualN = 20;
	private final static int offspringN = 100;
	private final static int maxGenerations = 10000;
	public final static double fitnessThreshold = 0.5;
	private final static int rzCorpusSize = 10000;
	public static double temp = 1.00;
	private final static double coolingRate = 0.001;
	public static Dataset data;

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

		data = DataContainer.rhymeZoneAdvanced;

		Map<String, Double> values = new HashMap<>();

//		New best individual for 100: 0.9106901217861975
//
// 		frontness: 125.85064944028905
//		height: 66.31743773925848
//
//		place_of_articulation: 108.18860203149124
//		manner_of_articulation: 98.01522428944162
//		voicing: 15.508342021886682
//
//		onset: 20.284123584145814
//		nucleus: 60.65245740265866
//		coda: 127.75846797910884
//
//		stress: 0.6026471314006965

//		//values.put("frontness", 125.0);//always 100
//		values.put("height", 00.0);
//
//		//values.put("place_of_articulation", 110.0);//always 100
//		values.put("manner_of_articulation", 000.0);
//		values.put("voicing", 00.0);
//
//		values.put("onset", 00.0);
//		//values.put("nucleus", 60.0);//always 100
//		values.put("coda", 000.0);
//
//		values.put("stress", 00.0);

		values.put("frontness", 100.0);
		values.put("height", 100.0);
		values.put("roundness", 100.0);
		values.put("tension", 100.0);
		values.put("stress", 100.0);

		values.put("manner", 100.0);
		values.put("place", 100.0);
		values.put("voicing", 100.0);

		values.put("onset", 100.0);
		values.put("nucleus", 100.0);
		values.put("coda", 100.0);

		TreeSet<Individual> topIndividuals = new TreeSet<>();
		for (int i = 0; i < topIndividualN; i++) {
			Individual temp = new Individual(values);
			temp.mutate();
			temp.calculateFitness();
			topIndividuals.add(temp);
		}

		double bestFitnessYet = -1;
		double bestOfGeneration = -1;
		double generationalTop10Average;
		Individual bestIndividualYet;
		int generation = 0;

		while (bestOfGeneration < 1.0 && generation < maxGenerations) {
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
			bestOfGeneration = topIndividuals.last().getFitness();

			//update absolutes
			if (bestOfGeneration > bestFitnessYet) {
				bestFitnessYet = bestOfGeneration;
				bestIndividualYet = topIndividuals.last();
				System.out.println("\tNew best individual for " + rzCorpusSize + ": " + bestIndividualYet.getFitness());
				Map<String,Double> map = bestIndividualYet.getValues();

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
			}

			System.out.println("\tTemp: " + temp);
			System.out.println("\tAvrg fitness for Gen" + generation + ": " + generationalTop10Average);
			System.out.println("\tBest fitness for Gen" + generation + ": " + bestOfGeneration);
			System.out.println("\tBest fitness of all: " + bestFitnessYet);

			//cool mutation rate
			if (temp > 1.0)
				temp -= coolingRate;
		}
		System.out.println("\tBest individual of final generation: " + topIndividuals.last().getFitness());
		Map<String,Double> map = topIndividuals.last().getValues();

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
			child.calculateFitness();
			result.add(child);
		}
		return result;
	}

	public static TreeSet<Individual> findTopIndividuals(Collection<Individual> allIndividuals) {
		TreeSet<Individual> calculatedIndividuals = new TreeSet<>();
		//sort by fitness, return the top topIndividualN
		for (Individual ind : allIndividuals) {
			if (ind.getFitness() == -1)
				ind.calculateFitness();
			calculatedIndividuals.add(ind);
		}

		TreeSet<Individual> result = new TreeSet<>();
		try {
			for (int i = 0; i < topIndividualN; i++) {
				if (!calculatedIndividuals.isEmpty()) {
					result.add(calculatedIndividuals.last());
					calculatedIndividuals.remove(calculatedIndividuals.last());
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
			total += ind.getFitness();
		}
		return total / inds.size();
	}

}
