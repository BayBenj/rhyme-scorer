package tables;

public class MonoVoicingTable extends VoicingTable {
	@Override
	public int get_i_size() {
		return getSize();
	}

	@Override
	public int get_j_size() {
		return getSize();
	}
}
