package main.java.controls;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.HTMLEditorSkin;
import javafx.scene.web.WebView;

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

	public WebView getWebView() {
		return (WebView) ((GridPane) ((HTMLEditorSkin) getSkin()).getChildren().get( 0 )).getChildren().get( 2 );
	}

	public WritableImage snapshot() {
		WebView view = getWebView();
		System.out.println(view.getWidth());
		System.out.println(view.getHeight());
		return view.snapshot(null, new WritableImage((int)view.getWidth(), (int)view.getHeight()));
	}
}