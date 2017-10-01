package tables;

import java.io.Serializable;

public class MultiVoicingTable extends VoicingTable implements Serializable {

	@Override
	public int get_i_size() {
		return getSize();
	}

	@Override
	public int get_j_size() {
		return getSize() + 3;
	}

}
