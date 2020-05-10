package main.java;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashSet;

public class DebateEvents {
	public final ObservableList<Speech> speeches = FXCollections.observableArrayList();

	public final DebateEvent pf = new DebateEvent("Public Forum", 180);
	public final DebateEvent ld = new DebateEvent("Lincoln-Douglas", 240);
	public final DebateEvent policy = new DebateEvent("Policy", 180);

	private ArrayList<DebateEvent> events = new ArrayList<>();

	public DebateEvents() {
		events.add(pf);
		pf.addSpeech("Pro Constructive", 240, Side.PRO);
		pf.addSpeech("Con Constructive", 240, Side.CON);
		pf.addSpeech("Pro Rebuttal", 240, Side.PRO);
		pf.addSpeech("Con Rebuttal", 240, Side.CON);
		pf.addSpeech("Crossfire", 180, Side.NEITHER);
		pf.addSpeech("Pro Summary", 180, Side.PRO);
		pf.addSpeech("Con Summary", 180, Side.CON);
		pf.addSpeech("Pro Final Focus", 120, Side.PRO);
		pf.addSpeech("Con Final Focus", 120, Side.CON);
		pf.setPrepSeconds(180);

		events.add(ld);
		ld.addSpeech("Aff Constructive", 360, Side.PRO);
		ld.addSpeech("Neg Constructive", 420, Side.CON);
		ld.addSpeech("Cross-X", 180, Side.NEITHER);
		ld.addSpeech("1st Aff Rebuttal", 240, Side.PRO);
		ld.addSpeech("Neg Rebuttal", 360, Side.CON);
		ld.addSpeech("2nd Aff Rebuttal", 180, Side.PRO);
		ld.setPrepSeconds(240);

		events.add(policy);
		policy.addSpeech("1AC", 480, Side.PRO);
		policy.addSpeech("1NC", 480, Side.CON);
		policy.addSpeech("Cross-X", 180, Side.NEITHER);
		policy.addSpeech("2AC", 480, Side.PRO);
		policy.addSpeech("2NC", 480, Side.CON);
		policy.addSpeech("1NR", 300, Side.CON);
		policy.addSpeech("1AR", 300, Side.PRO);
		policy.addSpeech("2NR", 300, Side.CON);
		policy.addSpeech("2AR", 300, Side.PRO);
		policy.setPrepSeconds(300);

		speeches.addAll(pf.getSpeeches());
		speeches.addAll(ld.getSpeeches());
		speeches.addAll(policy.getSpeeches());
	}

	public DebateEvent getEvent(String eventName) {
		for(DebateEvent event : events) {
			if (eventName.equals(event.getName()))
				return event;
		}
		return null;
	}

	public ArrayList<DebateEvent> getEvents() {
		return events;
	}
}

class DebateEvent {
	private final String            name;
	private final ArrayList<Speech> speeches = new ArrayList<>();
	private       int               prepSeconds;

	public DebateEvent(String name, int prepSeconds) {
		this.name = name;
		this.prepSeconds = prepSeconds;
	}

	public void addSpeech(String name, int timeSeconds, Side side) {
		speeches.add(new Speech(name, timeSeconds, side, this));
	}

	public Speech getSpeech(int speech) {
		return speeches.get(speech);
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

	public String getTimes() {
		StringBuilder times = new StringBuilder();
		for(Speech speech : speeches){
			times.append(speech.getTimeSeconds());
			times.append(',');
		}
		return times.toString();
	}

	public void setTimesFromString(String times) {
		if(times.chars().filter(ch -> ch == ',').count()==speeches.size()) {
			StringBuilder timesBuffer = new StringBuilder(times);
			for(Speech speech : speeches) {
				speech.setTimeSeconds(Integer.parseInt(timesBuffer.substring(0, timesBuffer.indexOf(","))));
				timesBuffer.delete(0, timesBuffer.indexOf(",") + 1);
			}
		} else System.err.println("Wrong number of times for this event");
	}
}

class Speech {
	private final String name;
	private       int    timeSeconds;
	private final Side side;
	private final DebateEvent event;

	public Speech(String name, int timeSeconds, Side side, DebateEvent event) {
		this.name = name;
		this.timeSeconds = timeSeconds;
		this.side = side;
		this.event = event;
	}

	public DebateEvent getEvent() {
		return event;
	}

	public Side getSide() {
			return side;
	}

	public String getName() {
		return name;
	}

	public int getTimeSeconds() {
		return timeSeconds;
	}

	public void setTimeSeconds(int timeSeconds) {
		this.timeSeconds = timeSeconds;
	}

	@Override public String toString() {
		return name;
	}
}

enum Side {
	PRO, CON, NEITHER
}