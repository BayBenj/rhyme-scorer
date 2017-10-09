//package genetic;
//
//import data.DataContainer;
//import phonetics.Phoneticizer;
//import phonetics.syllabic.Rhymer;
//import phonetics.syllabic.WordSyllables;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class LL_Individual {
//
//	private static int count = 0;
//
//	private Map<String,Double> values;
//	private double fitness = -1;
//	public int id = 0;
//	private boolean mutated = false;
//	private static final int sampleSize = 1000;
//
//	public LL_Individual() {
//		values.put("frontness", 125.0);//always 100?
//		values.put("height", 87.89145327765064);
//		values.put("roundness", 87.89145327765064);
//		values.put("tension", 87.89145327765064);
//
//		values.put("place_of_articulation", 110.0);//always 100?
//		values.put("manner_of_articulation", 93.74256357399545);
//		values.put("voicing", 27.754425336501495);
//
//		values.put("onset", 5.519934718254886);
//		values.put("nucleus", 60.0);//always 100?
//		values.put("coda", 125.45080735803514);
//
//		values.put("stress", 17.24887448289867);
//
//		values.put("gap_cost", 17.24887448289867);
//
//		id = count++;
//	}
//
//	public LL_Individual(Individual ind) {
//		this.values = ind.getValues();
//		id = count++;
//	}
//
//	public LL_Individual(Map<String, Double> values) {
//		this.values = values;
//		id = count++;
//	}
//
//	public void mutate() {
//		if (!mutated) {
//			int rnd = GeneticMain.r.nextInt(5);
//			if (rnd == 0) {
//				int chosenIndex = GeneticMain.r.nextInt(this.getValues().entrySet().size());
//				int i = 0;
//				for (Map.Entry<String,Double> entry : this.getValues().entrySet()) {
//					if (i == chosenIndex) {
//						entry.setValue(entry.getValue() + ((GeneticMain.r.nextDouble() - 0.5) * GeneticMain.temp));
//						if (entry.getValue() <= 0) {
//							entry.setValue(0d);
//						}
//						break;
//					}
//					i++;
//				}
//			}
//			mutated = true;
//		}
//	}
//
//	public Individual crossover(Individual mate) {
//		Individual offspring = new Individual(this);
//		offspring.setValues(this.getValues());
//		for (Map.Entry<String,Double> entry : this.getValues().entrySet()) {
//			if (GeneticMain.r.nextBoolean()) {
//				offspring.getValues().put(new String(entry.getKey()), new Double(mate.getValues().get(entry.getKey())));
//			}
//		}
//		return offspring;
//	}
//
//	public Map<String, Double> getValues() {
//		return values;
//	}
//
//	public void setValues(Map<String, Double> values) {
//		this.values = values;
//	}
//
//	public double getF_score_fitness() {
//		return fitness;
//	}
//
//	public void setF_score_fitness(double fitness) {
//		this.fitness = fitness;
//	}
//
//	@Override
//	public int compareTo(Individual o) {
//		if (this.fitness > o.fitness) return 1;
//		else if (this.fitness < o.fitness) return -1;
//		else {
//			if (this.id < o.id) return 1;
//			else if (this.id > o.id) return -1;
//			return 0;
//		}
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//
//		Individual that = (Individual) o;
//
//		if (Double.compare(that.getF_score_fitness(), getF_score_fitness()) != 0) return false;
//		if (id != that.id) return false;
//		return getValues().equals(that.getValues());
//	}
//
//	@Override
//	public int hashCode() {
//		int result;
//		long temp;
//		result = getValues().hashCode();
//		temp = Double.doubleToLongBits(getF_score_fitness());
//		result = 31 * result + (int) (temp ^ (temp >>> 32));
//		result = 31 * result + id;
//		return result;
//	}
//
//	Iterator<Map.Entry<String,WordSyllables>> sampleIterator = DataContainer.dictionary.entrySet().iterator();
//	Iterator<String> negIterator = DataContainer.dictionary.keySet().iterator();
//
//	public IndividualResults classifyBinary() {
//		//initialize return values
//		int truePositives = 0;
//		int trueNegatives = 0;
//		int falsePositives = 0;
//		int falseNegatives = 0;
//
//		for (int sampleN = 0; sampleN < sampleSize; sampleN++) {
//			if (!sampleIterator.hasNext()) {
//				sampleIterator = DataContainer.dictionary.entrySet().iterator();
//			}
//			Map.Entry<String,WordSyllables> testDictWord = sampleIterator.next();
//
//			Set<String> positives = GeneticMain.data.get(testDictWord.getKey());//get all words datamuse says rhyme with it removing ones outside of valid cmu dictionary
//
//			Rhymer temp = new Rhymer(this);
//			int tempTruePositives = 0;
//			int tempTrueNegatives = 0;
//			int tempFalsePositives = 0;
//			int tempFalseNegatives = 0;
//			for (String positive : positives) {
//				List<WordSyllables> positivePronunciations = Phoneticizer.getSyllables(positive);
//				if (positivePronunciations == null || positivePronunciations.isEmpty()) continue;
//				WordSyllables positivePronunciation = positivePronunciations.get(0);
//				if (positivePronunciation.isEmpty()) continue;
//				double endSyllablesScore = temp.score2Syllables(positivePronunciation.get(positivePronunciation.size()-1), testDictWord.getValue().get(testDictWord.getValue().size()-1));
//				if (endSyllablesScore >= GeneticMain.fitnessThreshold)
//					tempTruePositives++;
//				else
//					tempFalsePositives++;
//			}
//			for (int i = 0; i < positives.size(); i++) {
//				if (!negIterator.hasNext()) {
//					negIterator = DataContainer.dictionary.keySet().iterator();
//				}
//				String negative = negIterator.next();
//				while (positives.contains(negative)) {
//					if (!negIterator.hasNext()) {
//						negIterator = DataContainer.dictionary.keySet().iterator();
//					}
//					negative = negIterator.next();
//				}
//				List<WordSyllables> negativePronunciations = Phoneticizer.getSyllables(negative);
//				if (negativePronunciations == null || negativePronunciations.isEmpty()) continue;
//				WordSyllables negativePronunciation = negativePronunciations.get(0);
//				if (negativePronunciation.isEmpty()) continue;
//				double endSyllablesScore = temp.score2Syllables(negativePronunciation.get(negativePronunciation.size()-1), testDictWord.getValue().get(testDictWord.getValue().size()-1));
//				if (endSyllablesScore >= GeneticMain.fitnessThreshold)
//					tempFalseNegatives++;
//				else
//					tempTrueNegatives++;
//			}
//			truePositives += tempTruePositives;
//			trueNegatives += tempTrueNegatives;
//			falsePositives += tempFalsePositives;
//			falseNegatives += tempFalseNegatives;
//		}
//		return new IndividualResults(truePositives, trueNegatives, falsePositives, falseNegatives);
//	}
//
//	public double calculateBinaryFitness() {
//		IndividualResults results = this.classifyBinary();
//		double precision = calculatePrecision(results.truePositives, results.falsePositives);
//		double recall = calculateRecall(results.truePositives, results.falseNegatives);
//		double fScore = calculateFScore(precision, recall);
//		this.setF_score_fitness(fScore);
//		return fScore;
//	}
//
//	public static double calculateFScore(double precision, double recall) {
//		return 2 * ((precision * recall) / (precision + recall));
//	}
//
//	public static double calculatePrecision(double truePositives, double falsePositives) {
//		return truePositives / (truePositives + falsePositives);
//	}
//
//	public static double calculateRecall(double truePositives, double falseNegatives) {
//		return truePositives / (truePositives + falseNegatives);
//	}
//
//}
