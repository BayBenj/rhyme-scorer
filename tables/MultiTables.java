package tables;

import java.io.Serializable;

public class MultiTables implements Serializable {

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
