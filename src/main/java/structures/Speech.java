package main.java.structures;

public class Speech {
	public final DebateEvent event;
	private final String name;
	private final Side side;
	private final int defaultTimeSeconds;
	private int timeSeconds;

	public Speech(String name, int timeSeconds, Side side, DebateEvent event) {
		this.name = name;
		this.timeSeconds = timeSeconds;
		this.defaultTimeSeconds = timeSeconds;
		this.side = side;
		this.event = event;
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

	public int getDefaultTimeSeconds() {
		return defaultTimeSeconds;
	}

	@Override public String toString() {
		return name;
	}
}
