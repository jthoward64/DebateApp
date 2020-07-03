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
		pf.addSpeech("Pro Constructive", 240, Side.PRO);//0
		pf.addSpeech("Con Constructive", 240, Side.CON);//1
		pf.addSpeech("Pro Rebuttal", 240, Side.PRO);//2
		pf.addSpeech("Con Rebuttal", 240, Side.CON);//3
		pf.addSpeech("Crossfire", 180, Side.NEITHER);//4
		pf.addSpeech("Pro Summary", 180, Side.PRO);//5
		pf.addSpeech("Con Summary", 180, Side.CON);//6
		pf.addSpeech("Pro Final Focus", 120, Side.PRO);//7
		pf.addSpeech("Con Final Focus", 120, Side.CON);//8
		pf.setPrepSeconds(180);
		pf.addLayout(Layout.RELATED.index(), "[h:0h:1][h:2h:3][h:5h:6][h:7h:8]");
		pf.addLayout(Layout.PRO_CON.index(), "[h:0h:2h:5h:7][h:1h:3h:6h:8]");
		pf.addLayout(Layout.SINGLE.index(), "[h:0][h:1][h:2][h:3][h:5][h:6][h:7][h:8]");
		pf.addLayout(Layout.ALL.index(), "[h:0h:1h:2h:3h:5h:6h:7h:8]");

		events.add(ld);
		ld.addSpeech("Aff Constructive", 360, Side.PRO);//0
		ld.addSpeech("Neg Constructive", 420, Side.CON);//1
		ld.addSpeech("Cross-X", 180, Side.NEITHER);//2
		ld.addSpeech("1st Aff Rebuttal", 240, Side.PRO);//3
		ld.addSpeech("Neg Rebuttal", 360, Side.CON);//4
		ld.addSpeech("2nd Aff Rebuttal", 180, Side.PRO);//5
		ld.setPrepSeconds(240);
		ld.addLayout(Layout.RELATED.index(), "[h:0h:1][h:3h:4h:5]");
		ld.addLayout(Layout.PRO_CON.index(), "[h:0h:1h:3][h:4h:5]");
		ld.addLayout(Layout.SINGLE.index(), "[h:0][h:1][h:3][h:4][h:5]");
		ld.addLayout(Layout.ALL.index(), "[h:0h:1h:3h:4h:5]");

		events.add(policy);
		policy.addSpeech("1AC", 480, Side.PRO);//0
		policy.addSpeech("1NC", 480, Side.CON);//1
		policy.addSpeech("Cross-X", 180, Side.NEITHER);//2
		policy.addSpeech("2AC", 480, Side.PRO);//3
		policy.addSpeech("2NC", 480, Side.CON);//4
		policy.addSpeech("1NR", 300, Side.CON);//5
		policy.addSpeech("1AR", 300, Side.PRO);//6
		policy.addSpeech("2NR", 300, Side.CON);//7
		policy.addSpeech("2AR", 300, Side.PRO);//8
		policy.setPrepSeconds(300);
		policy.addLayout(Layout.RELATED.index(), "[h:0h:1h:3h:4][h:5h:6h:7h:8]");
		policy.addLayout(Layout.PRO_CON.index(), "[h:0h:3h:6h:8][h:1h:4h:5h:7]");
		policy.addLayout(Layout.SINGLE.index(), "[h:0][h:1][h:3][h:4][h:5][h:6][h:7][h:8]");
		policy.addLayout(Layout.ALL.index(), "[h:0h:1h:3h:4h:5h:6h:7h:8]");

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

