package main.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import main.java.controls.DebateTimer;
import main.java.controls.FlowEditor;
import main.java.controls.SettingsEditor;
import main.java.structures.AppSettings;
import main.java.structures.DebateEvent;
import main.java.structures.DebateEvents;
import main.java.structures.Speech;
import org.apache.commons.text.StringEscapeUtils;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * <div>App icon made by <a href="https://www.flaticon.com/authors/freepik" title=
 * "Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title=
 * "Flaticon">www.flaticon.com</a></div>
 *
 * @author Tag Howard
 */
public class DebateAppMain extends Application {
	final AppSettings settings = new AppSettings(new File(AppUtils.getAppHome()));
	final SettingsEditor settingsEditor = new SettingsEditor(settings);
	final DebateEvents events = settings.debateEvents;
	final SimpleObjectProperty<DebateEvent> currentEvent = new SimpleObjectProperty<>(events.pf);

	//Bottom
	final DebateTimer mainTimer = new DebateTimer(Orientation.HORIZONTAL, "Timer", 0);
	final ComboBox<Speech> mainTimerSpeechSelectorBox = new ComboBox<>(); //TODO fill with speeches
	final HBox bottom = new HBox(mainTimer, mainTimerSpeechSelectorBox);

	//Center
	final FlowEditor flowEditor = FlowEditor.parseLayoutString("[h:1h:3h:5h:7][h:2h:4h:6h:8]", events.pf, settings);

	final DebateTimer conPrep = new DebateTimer(Orientation.VERTICAL, "Con", 180);
	final VBox right = new VBox(conPrep);


	final DebateTimer proPrep = new DebateTimer(Orientation.VERTICAL, "Pro", 180);
	final VBox left = new VBox(proPrep);

	final HiddenSidesPane center = new HiddenSidesPane(flowEditor, null, right, null, left);


	//Top TODO implement save, open, and export
	final Action saveAction = new Action("Save");
	final Action saveAsAction = new Action("Save As");
	final Action openAction = new Action("Open");
	final Action exportPNG = new Action("PNG");
	final Menu export = new Menu("Export", null, ActionUtils.createMenuItem(exportPNG));
	final Action settingsDialogAction = new Action("Settings", e -> settingsEditor.getDialog().showAndWait());

	final Menu fileMenu = new Menu("File",null, ActionUtils.createMenuItem(saveAction), ActionUtils.createMenuItem(saveAsAction), ActionUtils.createMenuItem(openAction), export, ActionUtils.createMenuItem(settingsDialogAction));

	final Action nextLayoutAction = new Action("Next Layout");
	final Action nextPage = new Action("Next Page", e -> flowEditor.nextPage());
	final Menu viewMenu = new Menu("View", null, ActionUtils.createMenuItem(nextLayoutAction), ActionUtils.createMenuItem(nextPage));

	final Menu eventMenu = new Menu("Event");//TODO add event switching and times editor

	final Action openNsdaAction = new Action("NSDA", e -> AppUtils.openURL("https://www.speechanddebate.org"));
	final Action openTabroomAction = new Action("Tabroom", e -> AppUtils.openURL("https://www.tabroom.com"));
	final Action openDriveAction = new Action("Google Drive", e -> AppUtils.openURL("https://drive.google.com/drive"));
	final Menu linksMenu = new Menu("Links", null, ActionUtils.createMenuItem(openNsdaAction), ActionUtils.createMenuItem(openTabroomAction), ActionUtils.createMenuItem(openDriveAction));

	final Menu helpMenu = new Menu("Help");

	final MenuBar top = new MenuBar(fileMenu, linksMenu, viewMenu, eventMenu, helpMenu); //TODO add keybindings

	//root layout
	final BorderPane root = new BorderPane(center, top, null, bottom, null);
	final Scene           mainScene = new Scene(root);
	Stage mainStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override public void init() throws Exception {

		settings.load();

		//OS specific code
		final String myOS = System.getProperty("os.name").toLowerCase();
		if (myOS.contains("win")) {//Windows
			System.out.println("OS detected as Windows");
		 	Files.setAttribute(new File(AppUtils.getAppHome()).toPath(), "dos:hidden", true);
			if(System.getProperty("os.name").endsWith("10")) {
				final JMetro jmetro = new JMetro(Style.LIGHT);
				jmetro.setScene(mainScene);
			}
		} else { // Not windows
			if (myOS.contains("mac")) { //macOS
				System.out.println("OS detected as Windows");
			}
			else if (myOS.contains("nix") || myOS.contains("nux")) {//Linux or Unix
				System.out.println("OS detected as Linux/Unix");
			}
			else
				System.err.println("Unknown OS!");
		}

		//Backend configuration
		//--------------------
		currentEvent.addListener((observableValue, oldEvent, newEvent) -> {

		});

		//Frontend configuration
		//----------------------

		//Configure menu

		//Configure center
		////Configure left slide-out
		proPrep.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, new CornerRadii(20), Insets.EMPTY)));
		proPrep.getButton().setOnMouseClicked(e -> center.show(Side.LEFT));
		proPrep.getField().setOnMouseClicked(e -> center.show(Side.LEFT));
		left.setOnMouseClicked(e -> center.setPinnedSide(center.getPinnedSide()==null ? Side.LEFT : null));

		////Configure right slide-out
		conPrep.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, new CornerRadii(20), Insets.EMPTY)));
		conPrep.getButton().setOnMouseClicked(e -> center.show(Side.RIGHT));
		conPrep.getField().setOnMouseClicked(e -> center.show(Side.RIGHT));
		right.setOnMouseClicked(e -> center.setPinnedSide(center.getPinnedSide()==null ? Side.RIGHT : null));

		////Configure flow editor

		//Configure bottom
		mainTimerSpeechSelectorBox.setOnAction(e -> mainTimer.resetTimer(mainTimerSpeechSelectorBox.getValue().getTimeSeconds()));
		mainTimerSpeechSelectorBox.disableProperty().bind(mainTimer.timerRunningProperty.not());
	}

	@Override public void start(Stage mainStage) {
		this.mainStage = mainStage;
		mainStage.setScene(mainScene);

		mainStage.setTitle("Debate App");
		mainStage.getIcons().addAll(new Image(getClass().getResourceAsStream("/speaker128.png")),
						new Image(getClass().getResourceAsStream("/speaker64.png")),
						new Image(getClass().getResourceAsStream("/speaker32.png")),
						new Image(getClass().getResourceAsStream("/speaker16.png")));

		mainStage.setMinWidth(850);
		mainStage.setMinHeight(400);
		mainStage.setWidth(settings.defaultWidth.getValue());
		mainStage.setHeight(settings.defaultHeight.getValue());


		mainStage.show();
	}

	@Override public void stop() throws IOException {
		settings.save();
		System.out.println(StringEscapeUtils.unescapeHtml4(flowEditor.editorHashMap.get(currentEvent.get().getSpeeches().get(1)).getHtmlText()));
	}
}
