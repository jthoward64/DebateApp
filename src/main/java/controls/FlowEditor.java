package main.java.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import main.java.DebateAppMain;
import main.java.structures.DebateEvent;
import main.java.structures.Layout;
import main.java.structures.Speech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FlowEditor extends Pane {
	private final IntegerProperty currentPage = new SimpleIntegerProperty(0);

	private final SimpleIntegerProperty currentLayout;
	private final SimpleObjectProperty<DebateEvent> currentEvent;

	private final ArrayList<Pane> flowEditorPages = new ArrayList<>();

	private final ArrayList<Pair<Speech, MinimalHTMLEditor>>       orderedEditorSpeechPairs  = new ArrayList<>();
	private final HashMap<Speech, MinimalHTMLEditor> editorHashMap   = new HashMap<>();

	private final DoubleExpression editorWidthExpression = new DoubleExpression() {
		@Override public double get() {
			if(flowEditorPages.get(currentPage.get()).getChildren().size() >= 1)
				return getWidth() / flowEditorPages.get(currentPage.get()).getChildren().size();
			else
				return 0;
		}

		@Override public void addListener(ChangeListener<? super Number> changeListener) {
			widthProperty().addListener(changeListener);
		}

		@Override public void removeListener(ChangeListener<? super Number> changeListener) {
			widthProperty().removeListener(changeListener);
		}

		@Override public void addListener(InvalidationListener invalidationListener) {
			widthProperty().addListener(invalidationListener);
		}

		@Override public void removeListener(InvalidationListener invalidationListener) {
			widthProperty().removeListener(invalidationListener);
		}
	};

	public FlowEditor(int defaultLayout, SimpleObjectProperty<DebateEvent> currentEvent) {
		currentLayout = new SimpleIntegerProperty(defaultLayout);
		this.currentEvent = currentEvent;

		currentLayout.addListener((observable, oldValue, newValue) -> {
			Logger.getLogger("DebateApp").info("Layout changed from " + Layout.getByIndex((Integer) oldValue) +
							" to " + Layout.getByIndex((Integer) newValue));
			refreshLayout();
		});
		currentEvent.addListener(((observable, oldValue, newValue) -> {
			Logger.getLogger("DebateApp").info("Event changed from " + oldValue +
							" to " + newValue);
			refreshLayout();
		}));

		refreshLayout();
	}

	public void setPage(int pageIndex) {
		Logger.getLogger("DebateApp").info("Page switching from " + currentPage.get() + " to " + pageIndex);
		getChildren().setAll(flowEditorPages.get(pageIndex));
		currentPage.set(pageIndex);
	}

	public void nextPage() {
		if(currentPage.get()==flowEditorPages.size() - 1)
			setPage(0);
		else
			setPage(currentPage.get() + 1);
	}

	public String getLayoutString() {
		return currentEvent.get().getLayouts()[currentLayout.get()];
	}

	public int getCurrentPage() {
		return currentPage.get();
	}

	public ArrayList<Pane> getFlowEditorPages() {
		return flowEditorPages;
	}

	public ObjectProperty<DebateEvent> debateEventProperty() {
		return currentEvent;
	}

	public SimpleIntegerProperty currentLayoutProperty() {
		return currentLayout;
	}

	public List<MinimalHTMLEditor> getOrderedEditors() {
		return orderedEditorSpeechPairs.stream().map(Pair::getValue).collect(Collectors.toList());
	}

	public List<Speech> getOrderedSpeeches() {
		return orderedEditorSpeechPairs.stream().map(Pair::getKey).collect(Collectors.toList());
	}

	public HashMap<Speech, MinimalHTMLEditor> getEditorHashMap() {
		return editorHashMap;
	}

	/**
	 * <p>
	 * List of valid schema and their meanings:<br>
	 * <table style="width:100%">
	 *   <tr>
	 *     <td>h:index</td>
	 *     <td>An HTMLEditors associated with a speech of index "index", "index" MUST match a speech at that index in the speeches arraylist as defined in the event</td>
	 *   </tr>
	 *   <tr>
	 *     <td>[...]</td>
	 *     <td>An HBox that should contain HTMLEditors</td>
	 *   </tr>
	 * </table>
	 * <br>
	 * For example <code>"[h:"Pro Constructive"h:"Con Constructive"][h:"Con Constructive"h:"Pro Constructive"]"</code> would generate
	 * two HBoxes, one with pro first and the other with con first<br>
	 *
	 * @throws IllegalFormatException if any part of layoutString is invalid
	 */
	public void refreshLayout() {
		Logger.getLogger("DebateApp").entering("FlowEditor", "refreshLayout");

		flowEditorPages.clear();
		orderedEditorSpeechPairs.clear();

		StringBuilder layoutStringBuilder = new StringBuilder(getLayoutString());
		while(layoutStringBuilder.length()>0) {
			if(getLayoutString().charAt(0)=='[') {
				int endIndex = layoutStringBuilder.indexOf("]");
				flowEditorPages.add(parseBoxString(layoutStringBuilder.substring(1, endIndex), debateEventProperty().get()));
				layoutStringBuilder.delete(0, endIndex + 1);
			}
		}

		setPage(0);

		Logger.getLogger("DebateApp").exiting("FlowEditor", "refreshLayout");
	}

	/**
	 * Parses the contents of [...] in a layout String for the parseLayoutString method
	 */
	private Pane parseBoxString(String boxString, DebateEvent event) {
		Logger.getLogger("DebateApp").entering("FlowEditor", "parseBoxString", new Object[]{boxString, event});
		Pane box = new HBox();
		box.prefHeightProperty().bind(heightProperty());
		box.prefWidthProperty().bind(widthProperty());
		StringBuilder hBoxStringBuilder = new StringBuilder(boxString);
		while(hBoxStringBuilder.length()>0) {
			if(hBoxStringBuilder.indexOf("h:")==0) {
				hBoxStringBuilder.delete(0, 2);
				int endIndex = hBoxStringBuilder.indexOf("h");
				if(endIndex==-1)
					endIndex = hBoxStringBuilder.length();
				Speech speech = event.getSpeeches().get(Integer.parseInt(hBoxStringBuilder.substring(0, endIndex)));
				if(!editorHashMap.containsKey(speech)) {
					Label label = new Label(speech.getName());
					MinimalHTMLEditor htmlEditor = new MinimalHTMLEditor(label);
					htmlEditor.toolbarsVisiblePropertyProperty().bind(DebateAppMain.settings.toolbarsVisibleProperty);
					htmlEditor.prefHeightProperty().bind(heightProperty().subtract(label.heightProperty()));
					htmlEditor.prefWidthProperty().bind(editorWidthExpression);
					editorHashMap.put(speech, htmlEditor);
				}
				orderedEditorSpeechPairs.add(new Pair<>(speech, editorHashMap.get(speech)));
				VBox column = new VBox(editorHashMap.get(speech).getEditorLabel(), editorHashMap.get(speech));
				column.setMinWidth(225);
				box.getChildren().add(column);
				hBoxStringBuilder.delete(0, endIndex);
			}
		}

		ScrollPane scrollPane = new ScrollPane(box);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		Logger.getLogger("DebateApp").exiting("FlowEditor", "parseBoxString", scrollPane);

		return new StackPane(scrollPane);
	}
}
