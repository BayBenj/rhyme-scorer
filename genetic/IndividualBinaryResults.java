package genetic;

public class IndividualBinaryResults {
	public double truePositives = -1;
	public double trueNegatives = -1;
	public double falsePositives = -1;
	public double falseNegatives = -1;

	public IndividualBinaryResults(double truePositives, double trueNegatives, double falsePositives, double falseNegatives) {
		this.truePositives = truePositives;
		this.trueNegatives = trueNegatives;
		this.falsePositives = falsePositives;
		this.falseNegatives = falseNegatives;
	}
}
