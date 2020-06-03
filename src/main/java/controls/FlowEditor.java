package main.java.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import main.java.structures.AppSettings;
import main.java.structures.DebateEvent;
import main.java.structures.Speech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.stream.Collectors;

public class FlowEditor extends Pane {
	private final HBox[]                             flowEditorPages;
	private final IntegerProperty                    currentPage   = new SimpleIntegerProperty(0);
	public final  HashMap<Speech, MinimalHTMLEditor> editorHashMap = new HashMap<>();

	private final DoubleExpression editorWidthExpression = new DoubleExpression() {
		@Override public double get() {
			if(flowEditorPages.length>=1)
				return getWidth()/flowEditorPages.length;
			else
				return 0;
		}
		@Override public void addListener(ChangeListener<? super Number> changeListener) {}
		@Override public void removeListener(ChangeListener<? super Number> changeListener) {}
		@Override public void addListener(InvalidationListener invalidationListener) {}
		@Override public void removeListener(InvalidationListener invalidationListener) {}
	};

	private FlowEditor(HBox[] flowEditorPages) {
		this.flowEditorPages = flowEditorPages;
	}

	public void setPage(int pageIndex) {
		getChildren().setAll(flowEditorPages[pageIndex]);
		currentPage.set(pageIndex);
	}

	public void nextPage() {
		if(currentPage.get()==flowEditorPages.length-1)
			setPage(0);
		else
			setPage(currentPage.get()+1);
	}

	/**
	 *
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
	 *                        an actual layout of HTMLEditors, Labels, and Boxes
	 * @param event The event associated with this layout
	 * @throws IllegalFormatException if any part of layoutString is invalid
	 */
	public static FlowEditor parseLayoutString(String layoutString, DebateEvent event, AppSettings settings) throws IllegalFormatException {
		FlowEditor editor = new FlowEditor(new HBox[(int) layoutString.chars().filter(value -> value=='[').count()]);

		StringBuilder layoutStringBuilder = new StringBuilder(layoutString);
		for(int i = 0; i<editor.flowEditorPages.length; i++) {
			if(layoutString.charAt(0)=='[') {
				int endIndex = layoutStringBuilder.indexOf("]");
				editor.flowEditorPages[i] = parseHBoxString(editor, layoutStringBuilder.substring(1, endIndex), event, settings);
				layoutStringBuilder.delete(0, endIndex+1);
			}
		}

		editor.setPage(0);

		return editor;
	}

	/**
	 * Parses the contents of [...] in a layout String for the parseLayoutString method
	 */
	private static HBox parseHBoxString(FlowEditor editor, String hBoxString, DebateEvent event, AppSettings settings) {
		HBox box = new HBox();
		box.prefHeightProperty().bind(editor.heightProperty());
		box.prefWidthProperty().bind(editor.widthProperty());
		StringBuilder hBoxStringBuilder = new StringBuilder(hBoxString);
		while(hBoxStringBuilder.length()>0) {
			if(hBoxStringBuilder.indexOf("h:")==0) {
				hBoxStringBuilder.delete(0,2);
				int endIndex = hBoxStringBuilder.indexOf("h");
				if(endIndex==-1) endIndex=hBoxStringBuilder.length();
				Speech speech = event.getSpeeches().get(Integer.parseInt(hBoxStringBuilder.substring(0, endIndex)));
				MinimalHTMLEditor htmlEditor = new MinimalHTMLEditor();
				htmlEditor.toolbarsVisiblePropertyProperty().bind(settings.toolbarsVisibleProperty);
				Label label = new Label(speech.getName());
				htmlEditor.prefHeightProperty().bind(editor.heightProperty().subtract(label.heightProperty()));
				htmlEditor.prefWidthProperty().bind(editor.editorWidthExpression);
				editor.editorHashMap.put(speech, htmlEditor);
				box.getChildren().add(new VBox(label, htmlEditor));
				hBoxStringBuilder.delete(0, endIndex);
			}
		}
		return box;
	}
}
