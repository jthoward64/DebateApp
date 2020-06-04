package main.java.structures;

import java.util.ArrayList;
import java.util.HashMap;

public class DebateEvent { //TODO add and somehow save layout strings to this class (or maybe in settings?)
	private final String                  name;
	private final ArrayList<Speech>       speeches    = new ArrayList<>();
	private final HashMap<String, String> flowLayouts = new HashMap<>();
	private       int                     prepSeconds;

	public DebateEvent(String name, int prepSeconds) {
		this.name = name;
		this.prepSeconds = prepSeconds;
	}

	public void addSpeech(String name, int timeSeconds, Side side) {
		speeches.add(new Speech(name, timeSeconds, side, this));
	}

	public void addLayout(String layoutName, String layout) {
		//TODO validate
		flowLayouts.put(layoutName, layout);
	}

	public String getLayout(String layoutName) {
		return flowLayouts.get(layoutName);
	}

	public String[] getLayouts() {
		return flowLayouts.values().toArray(new String[0]);
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

