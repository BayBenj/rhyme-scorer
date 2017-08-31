package tables;

public class MultiTables {

	public MultiConsonantTables consonantTables;
	public VowelTables vowelTables;

	public MultiTables(MultiConsonantTables consonantTables, VowelTables vowelTables) {
		this.consonantTables = consonantTables;
		this.vowelTables = vowelTables;
	}

	public void foldAll() {
		consonantTables.foldTables();
		vowelTables.foldTables();
	}
}
