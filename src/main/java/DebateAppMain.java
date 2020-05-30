package main.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import main.java.controls.FlowEditor;
import main.java.controls.DebateTimer;
import main.java.structures.AppSettings;
import main.java.structures.DebateEvents;
import main.java.structures.Speech;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import java.io.File;
import java.nio.file.Files;

//TODO refactor and add settings to PropertySheet controls **

/**
 * <div>App icon made by <a href="https://www.flaticon.com/authors/freepik" title=
 * "Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title=
 * "Flaticon">www.flaticon.com</a></div>
 *
 * @author Tag Howard
 */
public class DebateAppMain extends Application {

	final AppSettings settings = new AppSettings(new File(System.getProperty("user.home") + File.separator + ".DebateApp"));
	final DebateEvents events = new DebateEvents();

	//Bottom
	final DebateTimer mainTimer = new DebateTimer(Orientation.HORIZONTAL, "Timer", 0);
	final ComboBox<Speech> mainTimerSpeechSelectorBox = new ComboBox<>(); //TODO fill with speeches
	final HBox bottom = new HBox(mainTimer, mainTimerSpeechSelectorBox);

	//Center
	final FlowEditor flowEditor = FlowEditor.parseLayoutString("[h:\"Pro Constructive\"h:\"Con Constructive\"][h:\"Con Constructive\"h:\"Pro Constructive\"]", events.pf);

	final DebateTimer conPrep = new DebateTimer(Orientation.VERTICAL, "Con", 180);
	final VBox right = new VBox(conPrep);

	final DebateTimer proPrep = new DebateTimer(Orientation.VERTICAL, "Pro", 180);
	final VBox left = new VBox();

	final HiddenSidesPane center = new HiddenSidesPane(flowEditor, null, right, null, proPrep);


	//Top TODO add Action objects to these menus
	final Action saveAction = new Action("Save");
	final Action saveAsAction = new Action("Save As");
	final Action openAction = new Action("Open");
	final Action exportPNG = new Action("PNG");
	final Menu export = new Menu("Export", null, ActionUtils.createMenuItem(exportPNG));
	final Menu fileMenu = new Menu("File",null, ActionUtils.createMenuItem(saveAction), ActionUtils.createMenuItem(saveAsAction), ActionUtils.createMenuItem(openAction), export);

	final Action alwaysOnTopAction = new Action("Always on top", e -> toggleAlwaysOnTop());
	final Action nextLayoutAction = new Action("Next Layout");
	final Action nextPage = new Action("Next Page");
	final Menu viewMenu = new Menu("View", null, ActionUtils.createCheckMenuItem(alwaysOnTopAction));

	final Menu eventMenu = new Menu("Event");

	final Action openNsdaAction = new Action("NSDA", e -> AppUtils.openURL("https://www.speechanddebate.org"));
	final Action openTabroomAction = new Action("Tabroom", e -> AppUtils.openURL("https://www.tabroom.com"));
	final Action openDriveAction = new Action("Google Drive", e -> AppUtils.openURL("https://drive.google.com/drive"));
	final Menu linksMenu = new Menu("Links", null, ActionUtils.createMenuItem(openNsdaAction), ActionUtils.createMenuItem(openTabroomAction), ActionUtils.createMenuItem(openDriveAction));

	final Menu helpMenu = new Menu("Help");

	final MenuBar top = new MenuBar(fileMenu, linksMenu, viewMenu, eventMenu, helpMenu);

	//root layout
	final BorderPane root = new BorderPane(center, top, null, bottom, null);
	final Scene           mainScene = new Scene(root);
	Stage mainStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override public void init() throws Exception {
		System.out.println(center.getTriggerDistance());
		//OS specific code
		final String myOS = System.getProperty("os.name").toLowerCase();
		if (myOS.contains("win")) {//Windows
			System.out.println("OS detected as Windows");
		 	Files.setAttribute(settings.propertiesFile.toPath(), "dos:hidden", true);
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

		//Configure menu

		//Configure center
		////Configure left slide-out

		////Configure right slide-out

		////Configure flow editor

		//Configure bottom
		mainTimerSpeechSelectorBox.setOnAction(e -> mainTimer.resetTimer(mainTimerSpeechSelectorBox.getValue().getTimeSeconds()));
		mainTimerSpeechSelectorBox.disableProperty().bind(mainTimer.timerRunningProperty.not());
	}

	@Override public void start(Stage mainStage) {
		this.mainStage = mainStage;
		mainStage.setScene(mainScene);

		mainStage.show();
	}

	private void toggleAlwaysOnTop() {
		Platform.runLater(() -> mainStage.setAlwaysOnTop(!mainStage.isAlwaysOnTop()));
	}
}
