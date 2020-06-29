package main.java.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.java.DebateAppMain;
import main.java.structures.DebateEvent;
import main.java.structures.Speech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;

//TODO reimplement
public class FlowEditor extends Pane {
	private final IntegerProperty currentPage = new SimpleIntegerProperty(0);

	private final SimpleStringProperty              layoutStringProperty = new SimpleStringProperty();
	private final SimpleObjectProperty<DebateEvent> eventProperty        = new SimpleObjectProperty<>();

	private final ArrayList<Pane> flowEditorPages = new ArrayList<>();

	private final ArrayList<MinimalHTMLEditor>       orderedEditors  = new ArrayList<>();
	private final ArrayList<Speech>                  orderedSpeeches = new ArrayList<>();
	private final HashMap<Speech, MinimalHTMLEditor> editorHashMap   = new HashMap<>();

	private final DoubleExpression editorWidthExpression = new DoubleExpression() {
		@Override public double get() {
			if(flowEditorPages.get(currentPage.get()).getChildren().size() >= 1)
				return getWidth() / flowEditorPages.get(currentPage.get()).getChildren().size();
			else
				return 0;
		}

		@Override public void addListener(ChangeListener<? super Number> changeListener) {
		}

		@Override public void removeListener(ChangeListener<? super Number> changeListener) {
		}

		@Override public void addListener(InvalidationListener invalidationListener) {
		}

		@Override public void removeListener(InvalidationListener invalidationListener) {
		}
	};

	public FlowEditor(String defaultLayoutString, DebateEvent defaultEvent) {
		parseLayoutString(defaultLayoutString, defaultEvent, new HashMap<>());
	}

	public void setPage(int pageIndex) {
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
		return layoutStringProperty.getValue();
	}

	public int getCurrentPage() {
		return currentPage.get();
	}

	public ArrayList<Pane> getFlowEditorPages() {
		return flowEditorPages;
	}

	public ObjectProperty<DebateEvent> debateEventProperty() {
		return eventProperty;
	}

	public ArrayList<MinimalHTMLEditor> getOrderedEditors() {
		return orderedEditors;
	}

	public ArrayList<Speech> getOrderedSpeeches() {
		return orderedSpeeches;
	}

	public HashMap<Speech, MinimalHTMLEditor> getEditorHashMap() {
		return editorHashMap;
	}

	/**
	 * TODO add a setting for vertical vs horizontal
	 *
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
	 * @param layoutString A string of characters that represents a particular layout for the FlowEditor to parse into
	 *                     an actual layout of HTMLEditors, Labels, and Boxes
	 * @param event        The event associated with this layout
	 * @throws IllegalFormatException if any part of layoutString is invalid
	 */
	public void parseLayoutString(String layoutString, DebateEvent event, HashMap<String, String> defaultText)
					throws IllegalFormatException {
		flowEditorPages.clear();
		orderedEditors.clear();
		orderedSpeeches.clear();
		editorHashMap.clear();

		eventProperty.setValue(event);
		layoutStringProperty.setValue(layoutString);

		StringBuilder layoutStringBuilder = new StringBuilder(layoutString);
		while(layoutStringBuilder.length()>0) {
			if(layoutString.charAt(0)=='[') {
				int endIndex = layoutStringBuilder.indexOf("]");
				flowEditorPages.add(parseBoxString(layoutStringBuilder.substring(1, endIndex), event, defaultText));
				layoutStringBuilder.delete(0, endIndex + 1);
			}
		}

		setPage(0);
	}

	/**
	 * Parses the contents of [...] in a layout String for the parseLayoutString method
	 */
	private HBox parseBoxString(String hBoxString, DebateEvent event, HashMap<String, String> defaultText) {
		HBox box = new HBox();
		box.prefHeightProperty().bind(heightProperty());
		box.prefWidthProperty().bind(widthProperty());
		StringBuilder hBoxStringBuilder = new StringBuilder(hBoxString);
		while(hBoxStringBuilder.length()>0) {
			if(hBoxStringBuilder.indexOf("h:")==0) {
				hBoxStringBuilder.delete(0, 2);
				int endIndex = hBoxStringBuilder.indexOf("h");
				if (endIndex == -1)
					endIndex = hBoxStringBuilder.length();
				Speech speech = event.getSpeeches().get(Integer.parseInt(hBoxStringBuilder.substring(0, endIndex)));
				Label label = new Label(speech.getName());
				MinimalHTMLEditor htmlEditor = new MinimalHTMLEditor(label);
				htmlEditor.toolbarsVisiblePropertyProperty().bind(DebateAppMain.settings.toolbarsVisibleProperty);
				htmlEditor.prefHeightProperty().bind(heightProperty().subtract(label.heightProperty()));
				htmlEditor.prefWidthProperty().bind(editorWidthExpression);
				//				htmlEditor.setHtmlText(defaultText.getOrDefault(speech.getName(), ""));
				editorHashMap.put(speech, htmlEditor);
				orderedEditors.add(htmlEditor);
				orderedSpeeches.add(speech);
				box.getChildren().add(new VBox(label, htmlEditor));
				hBoxStringBuilder.delete(0, endIndex);
			}
		}
		return box;
	}
}
