package main.java.structures;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum Layout {
	RELATED(0), PRO_CON(1), SINGLE(2), ALL(3);

	private final int index;

	Layout(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

	/**
	 * Retrieves the Layout of the specified index.
	 *
	 * @param indexToCheck A valid Layout index, as specified in Layout.
	 * @return The first Layout that matches the specified index or null if no such Layout exists
	 */
	public static Layout getByIndex(int indexToCheck) {
		for(Layout value : values()) {
			if (value.index() == indexToCheck)
				return value;
		}
		return null;
	}

	/**
	 * Return a user-friendly name of this enum found by splitting the enum name on every '_' and converts each word to title case, then joins them with ' '
	 */
	public String toString() {
		return Arrays.stream(name().split("_"))
						.map(s -> s = (s.length()>1 ? s.charAt(0) + s.substring(1).toLowerCase() : s))
						.collect(Collectors.joining(" "));
	}
}
