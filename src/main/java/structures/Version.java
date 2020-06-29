package main.java.structures;

public class Version {
	public final int main;
	public final int feature;
	public final int patch;
	public final boolean isAlpha;
	public final int alphaVersion;

	public Version(int main, int feature, int patch) {
		this.main = main;
		this.feature = feature;
		this.patch = patch;
		this.isAlpha = false;
		this.alphaVersion = -1;
	}

	public Version(int main, int feature, int patch, boolean alpha) {
		this.main = main;
		this.feature = feature;
		this.patch = patch;
		this.isAlpha = alpha;
		this.alphaVersion = -1;
	}

	public Version(int main, int feature, int patch, boolean alpha, int alphaVersion) {
		this.main = main;
		this.feature = feature;
		this.patch = patch;
		this.isAlpha = alpha;
		this.alphaVersion = alphaVersion;
	}

	public Version(String versionString) {
		StringBuilder versionStringBuilder = new StringBuilder(versionString);

		if (versionStringBuilder.toString().contains("-")) {
			if (versionStringBuilder.toString().contains("alpha."))
				alphaVersion = Integer
								.parseInt(versionStringBuilder.substring(versionStringBuilder.lastIndexOf(".") + 1));
			else
				alphaVersion = -1;
			isAlpha = versionStringBuilder.toString().contains("alpha");
			versionStringBuilder.delete(versionStringBuilder.toString().indexOf('-'), versionStringBuilder.length());
		} else {
			isAlpha = false;
			alphaVersion = -1;
		}

		main = Integer.parseInt(versionStringBuilder.substring(0, versionStringBuilder.toString().indexOf('.')));
		feature = Integer.parseInt(versionStringBuilder.substring(versionStringBuilder.toString().indexOf('.') + 1,
						versionStringBuilder.toString().lastIndexOf('.')));
		patch = Integer.parseInt(versionStringBuilder.substring(versionStringBuilder.toString().lastIndexOf('.') + 1));
	}

	@Override public boolean equals(Object o) {
		if (o instanceof Version)
			return (main == ((Version) o).main && feature == ((Version) o).feature && patch == ((Version) o).patch);
		else
			return false;
	}

	@Override public String toString() {
		if (isAlpha)
			if (alphaVersion >= 0)
				return "" + main + '.' + feature + '.' + patch + "-alpha." + alphaVersion;
			else
				return "" + main + '.' + feature + '.' + patch + "-alpha";
		else
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
