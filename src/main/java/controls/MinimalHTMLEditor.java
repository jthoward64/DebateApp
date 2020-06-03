package main.java.controls;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.scene.web.HTMLEditor;

import java.util.List;

public class MinimalHTMLEditor extends HTMLEditor {
	private final SimpleBooleanProperty toolbarsVisibleProperty = new SimpleBooleanProperty(true);
	public MinimalHTMLEditor() {
		lookup(".top-toolbar").managedProperty().bind(toolbarsVisibleProperty);
		lookup(".top-toolbar").visibleProperty().bind(toolbarsVisibleProperty);

		lookup(".bottom-toolbar").managedProperty().bind(toolbarsVisibleProperty);
		lookup(".bottom-toolbar").visibleProperty().bind(toolbarsVisibleProperty);
	}

	public SimpleBooleanProperty toolbarsVisiblePropertyProperty() {
		return toolbarsVisibleProperty;
	}
}