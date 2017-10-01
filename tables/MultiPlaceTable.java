package tables;

import java.io.Serializable;

public class MultiPlaceTable extends PlaceTable implements Serializable {

	@Override
	public int get_i_size() {
		return getSize();
	}

	@Override
	public int get_j_size() {
		return getSize() + 3;
	}

}
