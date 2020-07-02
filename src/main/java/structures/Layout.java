package main.java.structures;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Layout {
	RELATED(0), PRO_CON(1), SINGLE(2), ALL(3);

	private final int index;

	Layout(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public String toString() {
		//Splits the enum name on '_' and converts each word to title case, then joins them with ' '
		return Arrays.stream(name().split("_"))
						.map(s -> s = (s.length()>1 ? s.charAt(0) + s.substring(1).toLowerCase() : s))
						.collect(Collectors.joining(" "));
	}
}
