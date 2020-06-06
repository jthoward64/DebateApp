package main.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import main.java.controls.DebateTimer;
import main.java.controls.FlowEditor;
import main.java.controls.SettingsEditor;
import main.java.controls.SpeechTimesDialog;
import main.java.structures.*;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <div>Some icons made by <a href="https://www.flaticon.com/authors/freepik" title=
 * "Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title=
 * "Flaticon">www.flaticon.com</a></div>
 *
 * @author Tag Howard
 *
 */
public class DebateAppMain extends Application {
	public static       DebateAppMain instance;
	public static final Version       VERSION = new Version(2, 0, 0);

	public static final FileChooser.ExtensionFilter saveFileFilter = new FileChooser.ExtensionFilter(
					"DebateApp save file", "*.db8");
	public static final FileChooser.ExtensionFilter pngFileFilter  = new FileChooser.ExtensionFilter("PNG image",
					"*.png");
	public static final FileChooser.ExtensionFilter tiffFileFilter = new FileChooser.ExtensionFilter("TIFF image",
					"*.tiff", "*.tif");
	public static final FileChooser.ExtensionFilter docxFileFilter = new FileChooser.ExtensionFilter("Word document",
					"*.docx");

	Stage mainStage;

	public static final AppSettings                       settings       = new AppSettings(
					new File(AppUtils.getAppHome()));
	final               SettingsEditor                    settingsEditor = new SettingsEditor(settings);
	final               DebateEvents                      events         = settings.debateEvents;
	final               SimpleObjectProperty<DebateEvent> currentEvent   = new SimpleObjectProperty<>(settings.defaultEvent.getValue());

	//Bottom
	final DebateTimer      mainTimer                  = new DebateTimer(Orientation.HORIZONTAL, "Timer", 0, null);
	final ComboBox<Speech> mainTimerSpeechSelectorBox = new ComboBox<>();
	final HBox             bottom                     = new HBox(mainTimer, mainTimerSpeechSelectorBox);

	//Center
	final FlowEditor flowEditor = new FlowEditor(currentEvent.getValue().getLayouts()[Layouts.RELATED],
					currentEvent.getValue());

	final DebateTimer conPrep = new DebateTimer(Orientation.VERTICAL, "Con", 180, new Button(null,new ImageView(new Image(getClass().getResourceAsStream("/pin16.png")))));
	final VBox        right   = new VBox(conPrep);

	final DebateTimer proPrep = new DebateTimer(Orientation.VERTICAL, "Pro", 180, new Button(null,new ImageView(new Image(getClass().getResourceAsStream("/pin16.png")))));
	final VBox        left    = new VBox(proPrep);

	final HiddenSidesPane center = new HiddenSidesPane(flowEditor, null, right, null, left);

	//Menu
	////File
	//////Save As
	SaveHandler editorSaveHandler = new SaveHandler(flowEditor, null);
	final Action        saveAsAction         = new Action("Save As", e -> {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save");
		chooser.getExtensionFilters().add(saveFileFilter);
		chooser.setSelectedExtensionFilter(saveFileFilter);
		Platform.runLater(() -> {
			editorSaveHandler = new SaveHandler(flowEditor, chooser.showSaveDialog(mainStage));
			try {
				editorSaveHandler.save();
			} catch(IOException ioException) {
				AppUtils.showExceptionDialog(ioException);
			}
		});
	});
	//////Save
	final Action        saveAction           = new Action("Save", e -> {
		if(editorSaveHandler.getWorkingFile()==null)
			saveAsAction.handle(e);
		else {
			try {
				editorSaveHandler.save();
			} catch(IOException ioException) {
				AppUtils.showExceptionDialog(ioException);
			}
		}
	});
	//////Open
	final Action        openAction           = new Action("Open", e -> Platform.runLater(() -> {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open");
		chooser.getExtensionFilters().add(saveFileFilter);
		chooser.setSelectedExtensionFilter(saveFileFilter);
		try {
			File file = chooser.showOpenDialog(mainStage);
			if(editorSaveHandler==null)
				editorSaveHandler = new SaveHandler(flowEditor, file);
			editorSaveHandler.open(file, events);
		} catch(IOException ioException) {
			AppUtils.showExceptionDialog(ioException);
		}
	}));
	//////Export to PNGs
	final ExportHandler editorExportHandler  = new ExportHandler(flowEditor);
	final Action        exportPNGsAction     = new Action("Export to\nmultiple PNGs", e -> Platform.runLater(() -> {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "This will save a PNG file for every text box in the current event", ButtonType.OK);
		alert.setTitle("Export to multiple PNGs");
		alert.setHeaderText(null);
		alert.showAndWait();

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Export to multiple PNGs");
		File file = chooser.showDialog(mainStage);
		if(file==null)
			return;

		try {
			editorExportHandler.saveToImages("png", file);
		} catch(IOException ioException) {
			AppUtils.showExceptionDialog(ioException);
		}
	}));
	//////Export
	final Action        exportAction         = new Action("Export", e -> Platform.runLater(() -> {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Export");
		chooser.getExtensionFilters().addAll(pngFileFilter, tiffFileFilter); //TODO add docx
		chooser.setSelectedExtensionFilter(pngFileFilter);
		File file = chooser.showSaveDialog(mainStage);
		if(file==null)
			return;

		try {
			if(chooser.getSelectedExtensionFilter().equals(pngFileFilter)) {
				editorExportHandler.saveToBigPNG(file);
			} else if(chooser.getSelectedExtensionFilter().equals(tiffFileFilter)) {
				editorExportHandler.saveToTiff(file);
			} else if(chooser.getSelectedExtensionFilter().equals(docxFileFilter)) {
				editorExportHandler.saveToDOCX(file);
			}
		} catch(IOException ex) {
			AppUtils.showExceptionDialog(ex);
		}
	}));
	//////Settings
	final Action        settingsDialogAction = new Action("Settings", e -> settingsEditor.getDialog().showAndWait());

	final Menu fileMenu = new Menu("File", null, ActionUtils.createMenuItem(saveAction),
					ActionUtils.createMenuItem(saveAsAction), ActionUtils.createMenuItem(openAction), new SeparatorMenuItem(),
					ActionUtils.createMenuItem(exportAction), ActionUtils.createMenuItem(exportPNGsAction), new SeparatorMenuItem(),
					ActionUtils.createMenuItem(settingsDialogAction));

	////View
	final Action relatedLayoutAction = new Action("Related speeches layout",
					e -> flowEditor.parseLayoutString(currentEvent.getValue().getLayouts()[Layouts.RELATED],
									currentEvent.getValue(), new HashMap<>()));
	final Action proConLayoutAction  = new Action("Pro/Con layout",
					e -> flowEditor.parseLayoutString(currentEvent.getValue().getLayouts()[Layouts.PRO_CON],
									currentEvent.getValue(), new HashMap<>()));
	final Action singleLayoutAction  = new Action("Single speech layout",
					e -> flowEditor.parseLayoutString(currentEvent.getValue().getLayouts()[Layouts.SINGLE],
									currentEvent.getValue(), new HashMap<>()));
	final Action allLayoutAction     = new Action("Pro/Con layout",
					e -> flowEditor.parseLayoutString(currentEvent.getValue().getLayouts()[Layouts.ALL],
									currentEvent.getValue(), new HashMap<>()));
	final Action nextPage            = new Action("Next Page", e -> flowEditor.nextPage());
	final Menu   viewMenu            = new Menu("View", null, ActionUtils.createMenuItem(relatedLayoutAction),
					ActionUtils.createMenuItem(proConLayoutAction), ActionUtils.createMenuItem(singleLayoutAction),
					ActionUtils.createMenuItem(allLayoutAction), new SeparatorMenuItem(),
					ActionUtils.createMenuItem(nextPage));

	////Event
	final Action nextEventAction = new Action("Next Event", e -> {
		Alert warningAlert = new Alert(Alert.AlertType.WARNING, "This will clear your flow, are you sure you want to continue?", ButtonType.YES, ButtonType.NO);
		if(warningAlert.showAndWait().orElse(ButtonType.NO).equals(ButtonType.YES)) {
			mainTimerSpeechSelectorBox.getSelectionModel().clearSelection();
			int index = events.getEvents().indexOf(currentEvent.getValue()) + 1;
			if(index>(events.getEvents().size() - 1))
				index = 0;
			currentEvent.setValue(events.getEvents().get(index));
			mainTimerSpeechSelectorBox.setItems(FXCollections.observableArrayList(currentEvent.getValue().getSpeeches()));
			flowEditor.parseLayoutString(currentEvent.getValue().getLayouts()[0], currentEvent.getValue(), new HashMap<>());
		}
	});
	final Action editTimesAction = new Action("Edit times", e -> currentEvent.getValue()
					.setTimesFromString(new SpeechTimesDialog(currentEvent.getValue()).showAndWait().
									orElse(currentEvent.getValue().getDefaultTimes())));
	final Menu   eventMenu       = new Menu("Event", null, ActionUtils.createMenuItem(nextEventAction),
					ActionUtils.createMenuItem(editTimesAction));

	////Links
	final Action openNsdaAction    = new Action("NSDA", e -> AppUtils.openURL("https://www.speechanddebate.org"));
	final Action openTabroomAction = new Action("Tabroom", e -> AppUtils.openURL("https://www.tabroom.com"));
	final Action openDriveAction   = new Action("Google Drive",
					e -> AppUtils.openURL("https://drive.google.com/drive"));
	final Menu   linksMenu         = new Menu("Links", null, ActionUtils.createMenuItem(openNsdaAction),
					ActionUtils.createMenuItem(openTabroomAction), ActionUtils.createMenuItem(openDriveAction));

	////Help
	final Action aboutAction          = new Action("About", e -> {
		Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION,
						"" + "DebateApp is made by Tajetaje\n" + "You are currently using version " + VERSION + "\nWould you like to visit the github page?",
						ButtonType.YES, ButtonType.NO);
		aboutAlert.setHeaderText("About Debate App");
		aboutAlert.setTitle("About");
		if(aboutAlert.showAndWait().orElse(ButtonType.NO).equals(ButtonType.YES)) {
			AppUtils.openURL("https://github.com/tajetaje/DebateApp");
		}
	});
	final Action reportAction         = new Action("Report an issue",
					e -> AppUtils.openURL("https://github.com/tajetaje/DebateApp/issues/new"));
	final Action checkForUpdateAction = new Action("Check for update", e -> AppUtils.openURL("https://github.com/tajetaje/DebateApp/releases/latest"));
	final Menu   helpMenu             = new Menu("Help", null, ActionUtils.createMenuItem(aboutAction),
					ActionUtils.createMenuItem(reportAction), ActionUtils.createMenuItem(checkForUpdateAction));

	final MenuBar top = new MenuBar(fileMenu, linksMenu, viewMenu, eventMenu, helpMenu);

	//root layout
	final BorderPane root      = new BorderPane(center, top, null, bottom, null);
	final Scene      mainScene = new Scene(root);

	public DebateAppMain() {
		instance = this;
	}

	public static void main(String[] args) {
		AppUtils.logger.info("Starting with args: " + Arrays.toString(args));
		launch(args);
		AppUtils.logger.info("Launch method has returned");
	}

	@Override public void init() throws Exception {

		settings.load();

		//OS specific code
		final String myOS = System.getProperty("os.name").toLowerCase();
		if (myOS.contains("win")) {//Windows
			AppUtils.logger.info("OS detected as Windows");
			Files.setAttribute(new File(AppUtils.getAppHome()).toPath(), "dos:hidden", true);
			if(System.getProperty("os.name").endsWith("10")) {
				final JMetro jmetro = new JMetro(Style.LIGHT);
				jmetro.setScene(mainScene);
			}
		} else { // Not windows
			if (myOS.contains("mac")) { //macOS
				AppUtils.logger.info("OS detected as Windows");
			}
			else if(myOS.contains("nix") || myOS.contains("nux")) {//Linux or Unix
				AppUtils.logger.info("OS detected as Linux/Unix");
			} else
				AppUtils.logger.info("Unknown OS!");
		}

		//Backend configuration
		//--------------------

		//Frontend configuration
		//----------------------

		//Configure menu
		saveAsAction.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
		saveAction.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN));
		openAction.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.SHORTCUT_DOWN));
		nextPage.setAccelerator(new KeyCodeCombination(KeyCode.SPACE, KeyCodeCombination.SHORTCUT_DOWN));

		//Configure center
		center.setTriggerDistance(24);

		////Configure left slide-out
		proPrep.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, new CornerRadii(20), Insets.EMPTY)));
		proPrep.getButton().setOnMouseClicked(e -> center.show(Side.LEFT));
		proPrep.getField().setOnMouseClicked(e -> center.show(Side.LEFT));
		proPrep.getLabel().setOnMouseClicked(e -> center.show(Side.LEFT));

		////Configure right slide-out
		conPrep.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, new CornerRadii(20), Insets.EMPTY)));
		conPrep.getButton().setOnMouseClicked(e -> center.show(Side.RIGHT));
		conPrep.getField().setOnMouseClicked(e -> center.show(Side.RIGHT));
		conPrep.getLabel().setOnMouseClicked(e -> center.show(Side.RIGHT));

		////Configure flow editor

		//Configure bottom
		mainTimerSpeechSelectorBox.setItems(FXCollections.observableArrayList(currentEvent.getValue().getSpeeches()));
		mainTimerSpeechSelectorBox.setOnAction(e -> {
			if(mainTimerSpeechSelectorBox.getValue() != null)
				mainTimer.resetTimer(mainTimerSpeechSelectorBox.getValue().getTimeSeconds());
		});
		mainTimerSpeechSelectorBox.disableProperty().bind(mainTimer.timerRunningProperty);
	}

	@Override public void start(Stage mainStage) {
		this.mainStage = mainStage;
		mainStage.setScene(mainScene);

		mainStage.setTitle("Debate App");
		mainStage.getIcons().addAll(new Image(getClass().getResourceAsStream("/speaker128.png")),
						new Image(getClass().getResourceAsStream("/speaker64.png")),
						new Image(getClass().getResourceAsStream("/speaker32.png")),
						new Image(getClass().getResourceAsStream("/speaker16.png")));

		AppUtils.logger.info("Successfully loaded icons");

		mainStage.setMinWidth(850);
		mainStage.setMinHeight(400);
		mainStage.setWidth(settings.defaultWidth.getValue());
		mainStage.setHeight(settings.defaultHeight.getValue());

		mainStage.show();

		if(AppUtils.firstRun) {
			Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
			infoAlert.setTitle("Info dialog");
			infoAlert.setHeaderText("Welcome to DebateApp");
			infoAlert.setContentText("" +
							"If you haven't already read the readme, read it!\n" +
							"\tTo see the readme and other helpful information, use the\n" +
							"\thelp menu and click \"About\"\n" +
							"Plus, here are a couple of useful tips:\n" +
							"1. Move your mouse to a side of the window to see prep timers\n" +
							"2. There are lots of ways to save your flows, use the File\n" +
							"\tmenu to see them all\n" +
							"3. DebateApp has multiple layouts and supported events, you\n" +
							"\tcan use the view menu to set your layout and the\n" +
							"event menu to cycle through events\n" +
							"4. If you have an issue or an idea please report it under\n" +
							"\tthe help menu so I can take a look\n" +
							"5. Take a look at the settings menu to customize the app\n\n" +
							"Lastly you should remember the following key bind:\n" +
							"\t\t\tControl (Command) + Space\n" +
							"It will go to the next page of speeches" +
							"Enjoy!");
			infoAlert.showAndWait();
		}
	}

	@Override public void stop() throws IOException {
		settings.save();
	}

	//TODO fix
	public void checkForUpdate() {
		try {
			AppUtils.logger.info("Checking if latest release is newer than " + VERSION);
			UpdateChecker checker = new UpdateChecker(VERSION);
			if(checker.check()) {
				AppUtils.logger.info("Found new version");
				checker.showUpdateAlert();
			} else {
				AppUtils.logger.info("Didn't find new version");
				Popup noUpdatePopup = new Popup();
				Label topLabel = new Label("No updates found");
				topLabel.setStyle("-fx-background-color: #008000; -fx-font-size: 24;");
				Label bottomLabel = new Label("Click anywhere to continue");
				bottomLabel.setStyle("-fx-font-size: 10;");
				VBox vbox = new VBox(topLabel, bottomLabel);
				vbox.setAlignment(Pos.CENTER);
				noUpdatePopup.getContent().setAll(vbox);
				noUpdatePopup.setAutoHide(true);
				Platform.runLater(() -> noUpdatePopup.show(mainStage));
			}
		} catch (IOException e) {
			AppUtils.logger.warning("Update Check failed");
			AppUtils.showExceptionDialog(e);
		}
	}
}
