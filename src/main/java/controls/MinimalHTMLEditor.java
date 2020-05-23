package main.java.controls;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.web.HTMLEditor;

import java.util.ArrayList;
import java.util.HashSet;

public class MinimalHTMLEditor extends HTMLEditor {
	public MinimalHTMLEditor() {
		super();
		minimalizeHtmlEditor(this);
	}

	public static void minimalizeHtmlEditor(final HTMLEditor editor) {
		editor.setVisible(false);
		Platform.runLater(() -> {
			ToolBar toolBar1 = (ToolBar) editor.lookup(".top-toolbar");
			ToolBar toolBar2 = (ToolBar) editor.lookup(".bottom-toolbar");

			HashSet<Node> nodesToKeep = new HashSet<>();

			nodesToKeep.add(editor.lookup(".html-editor-numbers"));
			nodesToKeep.add(editor.lookup(".html-editor-bullets"));

			nodesToKeep.add(editor.lookup(".html-editor-foreground"));
			nodesToKeep.add(editor.lookup(".html-editor-background"));

			nodesToKeep.add(editor.lookup(".html-editor-bold"));
			nodesToKeep.add(editor.lookup(".html-editor-italics"));
			nodesToKeep.add(editor.lookup(".html-editor-underline"));
			nodesToKeep.add(editor.lookup(".html-editor-strike"));

			toolBar1.getItems().removeIf(n -> !nodesToKeep.contains(n));
			toolBar2.getItems().removeIf(n -> !nodesToKeep.contains(n));

			ArrayList<Node> toCopy = new ArrayList<>();
			toCopy.addAll(toolBar2.getItems());
			toolBar2.getItems().clear();
			toolBar1.getItems().addAll(toCopy);

			toolBar2.setVisible(false);
			toolBar2.setManaged(false);

			editor.setVisible(true);
		});
	}
}
