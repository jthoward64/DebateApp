package main.java.structures;

import java.util.ArrayList;

public class DebateEvent {
	private final String            name;
	private final ArrayList<Speech> speeches    = new ArrayList<>();
	private final ArrayList<String> flowLayouts = new ArrayList<>();
	private       int               prepSeconds;

	public DebateEvent(String name, int prepSeconds) {
		this.name = name;
		this.prepSeconds = prepSeconds;
	}

	public void addSpeech(String name, int timeSeconds, Side side) {
		speeches.add(new Speech(name, timeSeconds, side, this));
	}

	public void addLayout(int index, String layout) {
		long numElements = layout.chars().filter(num -> (num == 'h')).count();
		if(speeches.size()-1 >= numElements)
			flowLayouts.add(index, layout);
		else
			throw new IllegalArgumentException("Layout String must have fewer elements than the number of speeches in " + name + " (" + numElements + " > " + (speeches.size()-1) + ")");
	}

	public String[] getLayouts() {
		return flowLayouts.toArray(new String[0]);
	}

	public int getPrepSeconds() {
		return prepSeconds;
	}

	public void setPrepSeconds(int prepSeconds) {
		this.prepSeconds = prepSeconds;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Speech> getSpeeches() {
		return speeches;
	}

	public Speech getSpeech(String name) {
		for(Speech speech : speeches) {
			if(speech.getName().equals(name))
				return speech;
		}
		return null;
	}

	public String getDefaultTimes() {
		StringBuilder times = new StringBuilder();
		for(Speech speech : speeches) {
			times.append(speech.getDefaultTimeSeconds());
			times.append(',');
		}
		return times.toString();
	}

	public String getTimes() {
		StringBuilder times = new StringBuilder();
		for (Speech speech : speeches) {
			times.append(speech.getTimeSeconds());
			times.append(',');
		}
		return times.toString();
	}

	public void setTimesFromString(String times) {
		if (times.chars().filter(ch -> ch == ',').count() == speeches.size()) {
			StringBuilder timesBuffer = new StringBuilder(times);
			for (Speech speech : speeches) {
				speech.setTimeSeconds(Integer.parseInt(timesBuffer.substring(0, timesBuffer.indexOf(","))));
				timesBuffer.delete(0, timesBuffer.indexOf(",") + 1);
			}
		} else
			System.err.println("Wrong number of times for this event");
	}

	@Override
	public String toString() {
		return getName();
	}
}

