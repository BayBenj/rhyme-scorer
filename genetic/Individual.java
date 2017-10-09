package genetic;

import data.DataContainer;
import phonetics.Phoneticizer;
import phonetics.syllabic.LL_Rhymer;
import phonetics.syllabic.WordSyllables;
import tables.MultiTables;
import utils.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Individual implements Comparable<Individual> {

	private static int count = 0;
	public static MultiTables tables;

	private Map<String,Double> values;
	private double f_score_fitness = -1;
	private double mean_sq_error = 101;
	public int id = 0;
	private boolean mutated = false;
	private static final int sampleSize = 1000;

	public Individual(Map<String, Double> values) {
		this.values = values;
		id = count++;
	}

	public Individual(Individual ind) {
		this.values = ind.getValues();
		id = count++;
	}

	public void mutate() {
		if (!mutated) {
//			int rnd = GeneticMain.r.nextInt(5);
//			if (rnd == 0) {
				int chosenIndex = GeneticMain.r.nextInt(this.getValues().entrySet().size());
				int i = 0;
				for (Map.Entry<String,Double> entry : this.getValues().entrySet()) {
					if (i == chosenIndex) {
						entry.setValue(entry.getValue() + ((GeneticMain.r.nextDouble() - 0.5) * GeneticMain.temp));
						if (entry.getValue() <= 0) {
							entry.setValue(0d);
						}
						break;
					}
					i++;
				}
//			}
			mutated = true;
		}
	}

	public Individual crossover(Individual mate) {
		Individual offspring = new Individual(this);
		offspring.setValues(this.getValues());
		for (Map.Entry<String,Double> entry : this.getValues().entrySet()) {
			if (GeneticMain.r.nextBoolean()) {
				offspring.getValues().put(new String(entry.getKey()), new Double(mate.getValues().get(entry.getKey())));
			}
		}
		return offspring;
	}

	public Map<String, Double> getValues() {
		return values;
	}

	public void setValues(Map<String, Double> values) {
		this.values = values;
	}

	public double getF_score_fitness() {
		return f_score_fitness;
	}

	public void setF_score_fitness(double f_score_fitness) {
		this.f_score_fitness = f_score_fitness;
	}

	public double getMean_sq_error() {
		return mean_sq_error;
	}

	public void setMean_sq_error(double mean_sq_error) {
		this.mean_sq_error = mean_sq_error;
	}

	@Override
	public int compareTo(Individual o) {
		if (this.f_score_fitness > o.f_score_fitness) return 1;
		else if (this.f_score_fitness < o.f_score_fitness) return -1;
		else {
			if (this.id < o.id) return 1;
			else if (this.id > o.id) return -1;
			return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Individual that = (Individual) o;

		if (Double.compare(that.getF_score_fitness(), getF_score_fitness()) != 0) return false;
		if (id != that.id) return false;
		return getValues().equals(that.getValues());
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = getValues().hashCode();
		temp = Double.doubleToLongBits(getF_score_fitness());
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + id;
		return result;
	}

	Iterator<Map.Entry<String,WordSyllables>> sampleIterator = DataContainer.dictionary.entrySet().iterator();
	Iterator<String> negIterator = DataContainer.dictionary.keySet().iterator();

	public IndividualBinaryResults classifyBinary() {
		//initialize return values
		int truePositives = 0;
		int trueNegatives = 0;
		int falsePositives = 0;
		int falseNegatives = 0;

		for (int sampleN = 0; sampleN < sampleSize; sampleN++) {
			if (!sampleIterator.hasNext()) {
				sampleIterator = DataContainer.dictionary.entrySet().iterator();
			}
			Map.Entry<String,WordSyllables> testDictWord = sampleIterator.next();

			Set<String> positives = GeneticMain.data.get(testDictWord.getKey());//get all words datamuse says rhyme with it removing ones outside of valid cmu dictionary TODO fix???
			while (positives == null) {
				if (!sampleIterator.hasNext()) {
					sampleIterator = DataContainer.dictionary.entrySet().iterator();
				}
				testDictWord = sampleIterator.next();

				positives = GeneticMain.data.get(testDictWord.getKey());
			}

			LL_Rhymer temp = new LL_Rhymer(tables, this);
			int tempTruePositives = 0;
			int tempTrueNegatives = 0;
			int tempFalsePositives = 0;
			int tempFalseNegatives = 0;
			for (String positive : positives) {
				List<WordSyllables> positivePronunciations = Phoneticizer.getSyllables(positive);
				if (positivePronunciations == null || positivePronunciations.isEmpty()) continue;
				WordSyllables positivePronunciation = positivePronunciations.get(0);
				if (positivePronunciation.isEmpty()) continue;
				double endSyllablesScore = temp.score2Syllables(positivePronunciation.get(positivePronunciation.size()-1), testDictWord.getValue().get(testDictWord.getValue().size()-1));
				if (endSyllablesScore >= GeneticMain.fitnessThreshold)
					tempTruePositives++;
				else
					tempFalsePositives++;
			}
			for (int i = 0; i < positives.size(); i++) {
				if (!negIterator.hasNext()) {
					negIterator = DataContainer.dictionary.keySet().iterator();
				}
				String negative = negIterator.next();
				while (positives.contains(negative)) {
					if (!negIterator.hasNext()) {
						negIterator = DataContainer.dictionary.keySet().iterator();
					}
					negative = negIterator.next();
				}
				List<WordSyllables> negativePronunciations = Phoneticizer.getSyllables(negative);
				if (negativePronunciations == null || negativePronunciations.isEmpty()) continue;
				WordSyllables negativePronunciation = negativePronunciations.get(0);
				if (negativePronunciation.isEmpty()) continue;
				double endSyllablesScore = temp.score2Syllables(negativePronunciation.get(negativePronunciation.size()-1), testDictWord.getValue().get(testDictWord.getValue().size()-1));
				if (endSyllablesScore >= GeneticMain.fitnessThreshold)
					tempFalseNegatives++;
				else
					tempTrueNegatives++;
			}
			truePositives += tempTruePositives;
			trueNegatives += tempTrueNegatives;
			falsePositives += tempFalsePositives;
			falseNegatives += tempFalseNegatives;
		}
		return new IndividualBinaryResults(truePositives, trueNegatives, falsePositives, falseNegatives);
	}

	public double calculateBinaryFitness() {
		IndividualBinaryResults results = this.classifyBinary();
		double precision = calculatePrecision(results.truePositives, results.falsePositives);
		double recall = calculateRecall(results.truePositives, results.falseNegatives);
		double fScore = calculateFScore(precision, recall);
		this.setF_score_fitness(fScore);
		return fScore;
	}

	public static double calculateFScore(double precision, double recall) {
		return 2 * ((precision * recall) / (precision + recall));
	}

	public static double calculatePrecision(double truePositives, double falsePositives) {
		return truePositives / (truePositives + falsePositives);
	}

	public static double calculateRecall(double truePositives, double falseNegatives) {
		return truePositives / (truePositives + falseNegatives);
	}

	public double classifyByScore() {
		double error_sum = 0;
		int total_iterations = 0;
		for (int sampleN = 0; sampleN < sampleSize; sampleN++) {
			if (!sampleIterator.hasNext()) {
				sampleIterator = DataContainer.dictionary.entrySet().iterator();
			}
			Map.Entry<String, WordSyllables> testDictWord = sampleIterator.next();
			Set<Pair<String, Integer>> positives = GeneticMain.score_data.get(testDictWord.getKey());
			while (positives == null) {
				if (!sampleIterator.hasNext()) {
					sampleIterator = DataContainer.dictionary.entrySet().iterator();
				}
				testDictWord = sampleIterator.next();
				positives = GeneticMain.score_data.get(testDictWord.getKey());
			}

			LL_Rhymer temp = new LL_Rhymer(tables, this);
			for (Pair<String, Integer> positive : positives) {
				double rzScore = positive.getSecond() / 100.0;
				List<WordSyllables> positivePronunciations = Phoneticizer.getSyllables(positive.getFirst());
				if (positivePronunciations == null || positivePronunciations.isEmpty()) continue;
				WordSyllables positivePronunciation = positivePronunciations.get(0);
				if (positivePronunciation.isEmpty()) continue;
				double endSyllablesScore = temp.score2Syllables(positivePronunciation.get(positivePronunciation.size() - 1), testDictWord.getValue().get(testDictWord.getValue().size() - 1));//TODO change to entire rhyme tail?????
				error_sum += calculate1SqError(rzScore, endSyllablesScore);
				total_iterations++;
			}
		}

		double result = calculateMeanSqError(error_sum, total_iterations);
		return result;
	}

	public static double calculate1SqError(double rzScore, double indivScore) {
		return Math.pow(rzScore - indivScore, 2);
	}

	public double calculateMeanSqError(double error_sum, int iterations) {
		double mse = error_sum / ((double) iterations);
		this.setMean_sq_error(mse);
		return mse;
	}

}

/*
@Look at average of the top 10
@balance positive:negative ratio
#Make sure The function is the function that solves the problem I care about
#Consider: Let the system learn whether something should be positive or negative
@Ensure no individual calls mutate twice
@Fix stress variable, change 1 to 2 and 2 to 1 (verify this is correct)
@> Only do last syllables
> Add single-point crossover, where there are 2 kids
> Make crossover and mutation rate anneal a bit
@> Lower mutation rate
@> only mutate 1 gene
> pick a normal distribution centered on the value
> Cool via F-score
> Do weighted chance to go on to reproduce, like tournaments or something
@> Randomly select 1000 words from CMU dictionary
 */


/*
> Keep track of worst F-score
 */






// @>> Do negatives differently
// @>> Don't actually be random (try it)
