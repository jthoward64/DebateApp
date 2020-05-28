package main.java.structures;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class DebateEvents {
	public final ObservableList<Speech> speeches = FXCollections.observableArrayList();

	public final DebateEvent pf     = new DebateEvent("Public Forum", 180);
	public final DebateEvent ld     = new DebateEvent("Lincoln-Douglas", 240);
	public final DebateEvent policy = new DebateEvent("Policy", 180);

	private final ArrayList<DebateEvent> events = new ArrayList<>();

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
			if(eventName.equals(event.getName()))
				return event;
		}
		return null;
	}

	public ArrayList<DebateEvent> getEvents() {
		return events;
	}
}

