package main.java.controls;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.HTMLEditorSkin;
import javafx.scene.web.WebView;

public class MinimalHTMLEditor extends HTMLEditor {
	private final Label editorLabel;

	private final SimpleBooleanProperty toolbarsVisibleProperty = new SimpleBooleanProperty(true);

	public MinimalHTMLEditor(Label editorLabel) {
		this.editorLabel = editorLabel;

		lookup(".top-toolbar").managedProperty().bind(toolbarsVisibleProperty);
		lookup(".top-toolbar").visibleProperty().bind(toolbarsVisibleProperty);

		lookup(".bottom-toolbar").managedProperty().bind(toolbarsVisibleProperty);
		lookup(".bottom-toolbar").visibleProperty().bind(toolbarsVisibleProperty);
	}

	public SimpleBooleanProperty toolbarsVisiblePropertyProperty() {
		return toolbarsVisibleProperty;
	}

	public WebView getWebView() {
		return (WebView) ((GridPane) ((HTMLEditorSkin) getSkin()).getChildren().get(0)).getChildren().get(2);
	}

	public Label getEditorLabel() {
		return editorLabel;
	}
}