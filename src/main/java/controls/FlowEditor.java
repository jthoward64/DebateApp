package main.java.controls;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.java.structures.DebateEvent;
import main.java.structures.Speech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.stream.Collectors;

public class FlowEditor extends Pane {
	private final HBox[] flowEditorPages;
	private int currentPage = 0;
	public final HashMap<Speech, MinimalHTMLEditor> editorHashMap = new HashMap<>();

	private FlowEditor(HBox[] flowEditorPages) {
		this.flowEditorPages = flowEditorPages;
	}

	public void setPage(int pageIndex) {
		getChildren().setAll(flowEditorPages[pageIndex]);
		currentPage = pageIndex;
	}

	public void nextPage() {
		if(currentPage==flowEditorPages.length-1)
			setPage(0);
		else
			setPage(currentPage+1);
	}

	/**
	 *
	 * List of valid schema and their meanings:<br>
	 * <table style="width:100%">
	 *   <tr>
	 *     <td>h:"Name"</td>
	 *     <td>An HTMLEditors associated with a speech called Name, name MUST match a speech in event</td>
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
	public static FlowEditor parseLayoutString(String layoutString, DebateEvent event) throws IllegalFormatException {
		FlowEditor editor = new FlowEditor(new HBox[(int) layoutString.chars().filter(value -> value=='[').count()]);
		System.out.println(editor.flowEditorPages.length);

		StringBuilder layoutStringBuilder = new StringBuilder(layoutString);
		for(int i = 0; i<editor.flowEditorPages.length; i++) {
			if(layoutString.charAt(0)=='[') {
				int endIndex = layoutStringBuilder.indexOf("]");
				editor.flowEditorPages[i] = parseHBoxString(editor, layoutStringBuilder.substring(1, endIndex), event);
				layoutStringBuilder.delete(0, endIndex+1);
			}
		}

		editor.setPage(0);

		return editor;
	}

	/**
	 * Parses the contents of [...] in a layout String for the parseLayoutString method
	 */
	private static HBox parseHBoxString(FlowEditor editor, String hBoxString, DebateEvent event) {
		HBox box = new HBox();
		StringBuilder hBoxStringBuilder = new StringBuilder(hBoxString);
		while(hBoxStringBuilder.length()>0) {
			if(hBoxStringBuilder.indexOf("h:\"")==0) {
				hBoxStringBuilder.delete(0,3);
				System.out.println(hBoxStringBuilder.toString());
				int endIndex = hBoxStringBuilder.indexOf("\"");
				Speech speech = event.getSpeeches().stream().filter(e -> e.getName().equalsIgnoreCase(hBoxStringBuilder.substring(0, endIndex))).collect(
								Collectors.toList()).get(0);
				MinimalHTMLEditor htmlEditor = new MinimalHTMLEditor();
				editor.editorHashMap.put(speech, htmlEditor);
				box.getChildren().add(new VBox(new Label(speech.getName()), htmlEditor));
				hBoxStringBuilder.delete(0, endIndex+1);
			}
		}
		return box;
	}
}
