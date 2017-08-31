package tables;

public class MonoTables {

	public MonoConsonantTables consonantTables;
	public VowelTables vowelTables;

	public MonoTables(MonoConsonantTables consonantTables, VowelTables vowelTables) {
		this.consonantTables = consonantTables;
		this.vowelTables = vowelTables;
	}

	public void foldAll() {
		consonantTables.foldTables();
		vowelTables.foldTables();
	}
}
