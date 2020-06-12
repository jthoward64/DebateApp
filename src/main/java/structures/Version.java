package main.java.structures;

public class Version {
	public final int main;
	public final int feature;
	public final int patch;

	public Version(int main, int feature, int patch) {
		this.main = main;
		this.feature = feature;
		this.patch = patch;
	}

	public Version(String versionString) {
		main = Integer.parseInt(versionString.substring(0, versionString.indexOf('.')));
		feature = Integer.parseInt(versionString
						.substring(versionString.indexOf('.') + 1, versionString.lastIndexOf('.')));
		patch = Integer.parseInt(versionString.substring(versionString.lastIndexOf('.') + 1));
	}

	@Override public boolean equals(Object o) {
		if (o instanceof Version)
			return (main == ((Version) o).main && feature == ((Version) o).feature && patch == ((Version) o).patch);
		else
			return false;
	}

	@Override public String toString() {
		return "" + main + '.' + feature + '.' + patch;
	}

	public boolean greaterThan(Version v) {
		if(main>v.main)
			return true;
		else if(main<v.main)
			return false;
		else if(feature>v.feature)
			return true;
		else if(feature<v.feature)
			return false;
		else
			return patch>v.patch;
	}
}
