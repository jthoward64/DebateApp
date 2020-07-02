package main.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * <div>Some icons made by <a href="https://www.flaticon.com/authors/freepik" title=
 * "Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title=
 * "Flaticon">www.flaticon.com</a></div>
 *
 * @author Tag Howard
 */
public class DebateAppMain extends Application {
	public static final Version VERSION = new Version("2.0.0");
	public static final FileChooser.ExtensionFilter pngFileFilter = new FileChooser.ExtensionFilter("PNG image",
					"*.png");

	public static final FileChooser.ExtensionFilter saveFileFilter = new FileChooser.ExtensionFilter(
					"DebateApp save file", "*.db8");
	public static DebateAppMain instance;
	public static final FileChooser.ExtensionFilter tiffFileFilter = new FileChooser.ExtensionFilter("TIFF image",
					"*.tiff", "*.tif");
	public static final FileChooser.ExtensionFilter docxFileFilter = new FileChooser.ExtensionFilter("Word document",
					"*.docx");

	public Stage mainStage;

	public static final AppSettings                       settings       = new AppSettings(
					new File(AppUtils.getAppHome()));
	final               SettingsEditor                    settingsEditor = new SettingsEditor(settings);
	final               DebateEvents                      events         = settings.debateEvents;
	final               SimpleObjectProperty<DebateEvent> currentEvent   = new SimpleObjectProperty<>(settings.defaultEvent.getValue());

	//Bottom
	final DebateTimer      mainTimer                  = new DebateTimer(Orientation.HORIZONTAL, "Speech Timer", 0, null);
	final ComboBox<Speech> mainTimerSpeechSelectorBox = new ComboBox<>();
	final HBox mainTimerBox = new HBox(mainTimer, mainTimerSpeechSelectorBox);

	final DebateTimer conPrep = new DebateTimer(Orientation.HORIZONTAL, "Con", 180, null);

	final DebateTimer proPrep = new DebateTimer(Orientation.HORIZONTAL, "Pro", 180, null);

	final BorderPane             bottom                     = new BorderPane(mainTimerBox, null, conPrep, null, proPrep);

	//Center
	final FlowEditor flowEditor = new FlowEditor(settings.defaultLayout.get().getIndex(), currentEvent);

	//Menu
	////File
	//////Save As
	SaveHandler editorSaveHandler = new SaveHandler(flowEditor, null);
	final Action        saveAsAction         = new Action("Save As", e -> {
		Logger.getLogger("DebateApp").info("Started Save As action");
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save");
		chooser.getExtensionFilters().add(saveFileFilter);
		chooser.setSelectedExtensionFilter(saveFileFilter);
		Platform.runLater(() -> {
			File file = chooser.showSaveDialog(mainStage);
			if (file == null) {
				Logger.getLogger("DebateApp").info("Selected file was null, Save As action aborted");
				return;
			}
			editorSaveHandler = new SaveHandler(flowEditor, file);
			try {
				editorSaveHandler.save();
			} catch (IOException ioException) {
				AppUtils.showExceptionDialog(ioException);
			}
		});
	});
	//////Save
	final Action        saveAction           = new Action("Save", e -> {
		Logger.getLogger("DebateApp").info("Started Save action");
		if (editorSaveHandler.getWorkingFile() == null || !editorSaveHandler.getWorkingFile().exists()) {
			Logger.getLogger("DebateApp").info("Save file was null or did not exist, running Save As action instead");
			saveAsAction.handle(e);
		} else {
			try {
				editorSaveHandler.save();
			} catch (IOException ioException) {
				AppUtils.showExceptionDialog(ioException);
			}
		}
	});
	//////Open
	final Action        openAction           = new Action("Open", e -> Platform.runLater(() -> {
		Logger.getLogger("DebateApp").info("Started Open action");
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open");
		chooser.getExtensionFilters().add(saveFileFilter);
		chooser.setSelectedExtensionFilter(saveFileFilter);
		try {
			File file = chooser.showOpenDialog(mainStage);
			if (file == null) {
				Logger.getLogger("DebateApp").info("Selected file was null, Open action aborted");
				return;
			}
			if (editorSaveHandler == null)
				editorSaveHandler = new SaveHandler(flowEditor, file);
			editorSaveHandler.open(file, events);
		} catch(IOException ioException) {
			AppUtils.showExceptionDialog(ioException);
		}
	}));
	//////Export to PNGs
	final ExportHandler editorExportHandler  = new ExportHandler(flowEditor);
	final Action        exportPNGsAction     = new Action("Export to\nmultiple PNGs", e -> Platform.runLater(() -> {
		Logger.getLogger("DebateApp").info("Exporting to multiple PNGs");
		Alert alert = new Alert(Alert.AlertType.INFORMATION,
						"This will save a PNG file for every text box in the current event", ButtonType.OK);
		alert.setTitle("Export to multiple PNGs");
		alert.setHeaderText(null);
		alert.showAndWait();

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Export to multiple PNGs");
		File file = chooser.showDialog(mainStage);
		if (file == null) {
			Logger.getLogger("DebateApp").info("Selected file was null, export aborted");
			return;
		}

		editorExportHandler.saveToImages("png", file);
	}));
	//////Export
	final Action        exportAction         = new Action("Export", e -> Platform.runLater(() -> {
		Logger.getLogger("DebateApp").info("Started combined file export");
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Export");
		chooser.getExtensionFilters().addAll(pngFileFilter, tiffFileFilter, docxFileFilter);
		chooser.setSelectedExtensionFilter(pngFileFilter);
		File file = chooser.showSaveDialog(mainStage);
		if (file == null) {
			Logger.getLogger("DebateApp").info("Selected file was null, export aborted");
			return;
		}
		final File finalFile = file;
		if (chooser.getSelectedExtensionFilter().getExtensions().stream()
						.noneMatch(extension -> finalFile.getPath().toLowerCase()
										.endsWith(extension.substring(1).toLowerCase()))) {
			Logger.getLogger("DebateApp").info("File path does not contain extension, added \"" + chooser.getSelectedExtensionFilter()
							.getExtensions().get(0) + '\"');
			file = new File(file.getPath() + chooser.getSelectedExtensionFilter().getExtensions().get(0));
		}

		try {
			if (chooser.getSelectedExtensionFilter().equals(pngFileFilter)) {
				Logger.getLogger("DebateApp").info("Exporting to Big PNG");
				editorExportHandler.saveToBigPNG(file);
			} else if (chooser.getSelectedExtensionFilter().equals(tiffFileFilter)) {
				Logger.getLogger("DebateApp").info("Exporting to paginated TIFF");
				editorExportHandler.saveToTiff(file);
			} else if (chooser.getSelectedExtensionFilter().equals(docxFileFilter)) {
				Logger.getLogger("DebateApp").info("Exporting to DOCX");
				editorExportHandler.saveToDOCX(file);
			} else {
				Logger.getLogger("DebateApp").warning("Unknown export file type selected, export aborted");
			}
		} catch (IOException | Docx4JException ex) {
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
					e -> flowEditor.currentLayoutProperty().setValue(Layout.RELATED.getIndex()));
	final Action proConLayoutAction  = new Action("Pro/Con layout",
					e -> flowEditor.currentLayoutProperty().setValue(Layout.PRO_CON.getIndex()));
	final Action singleLayoutAction  = new Action("Single speech layout",
					e -> flowEditor.currentLayoutProperty().setValue(Layout.SINGLE.getIndex()));
	final Action allLayoutAction     = new Action("All speeches layout",
					e -> flowEditor.currentLayoutProperty().setValue(Layout.ALL.getIndex()));
	final Action nextPage            = new Action("Next Page", e -> flowEditor.nextPage());
	final Menu   viewMenu            = new Menu("View", null, ActionUtils.createMenuItem(relatedLayoutAction),
					ActionUtils.createMenuItem(proConLayoutAction), ActionUtils.createMenuItem(singleLayoutAction),
					ActionUtils.createMenuItem(allLayoutAction), new SeparatorMenuItem(),
					ActionUtils.createMenuItem(nextPage));

	////Event
	final Action nextEventAction = new Action("Next Event", e -> {
		mainTimerSpeechSelectorBox.getSelectionModel().clearSelection();
		int index = events.getEvents().indexOf(currentEvent.getValue()) + 1;
		if(index>(events.getEvents().size() - 1))
			index = 0;
		currentEvent.setValue(events.getEvents().get(index));
		mainTimerSpeechSelectorBox.setItems(FXCollections.observableArrayList(currentEvent.getValue().getSpeeches()));
	});
	final Action editTimesAction = new Action("Edit times", e -> currentEvent.getValue()
					.setTimesFromString(new SpeechTimesDialog(currentEvent.getValue()).showAndWait().
									orElse(currentEvent.getValue().getDefaultTimes())));
	final Menu   eventMenu       = new Menu("Event", null, ActionUtils.createMenuItem(nextEventAction),
					ActionUtils.createMenuItem(editTimesAction));

	////Links
	final Action openNsdaAction    = new Action("NSDA", e -> AppUtils.openURL("https://www.speechanddebate.org/"));
	final Action openTabroomAction = new Action("Tabroom", e -> AppUtils.openURL("https://www.tabroom.com/"));
	final Action openDriveAction   = new Action("Google Drive",
					e -> AppUtils.openURL("https://drive.google.com/drive/"));
	final Action openDebateRedditAction   = new Action("r/Debate",
					e -> AppUtils.openURL("https://www.reddit.com/r/Debate/"));
	final Menu   linksMenu         = new Menu("Links", null, ActionUtils.createMenuItem(openNsdaAction),
					ActionUtils.createMenuItem(openTabroomAction), ActionUtils.createMenuItem(openDriveAction), ActionUtils.createMenuItem(openDebateRedditAction));

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
	final BorderPane root      = new BorderPane(flowEditor, top, null, bottom, null);
	final Scene      mainScene = new Scene(root);

	public static void main(String[] args) {
		Logger.getLogger("DebateApp").info("Starting with args: " + Arrays.toString(args));
		launch(args);
		Logger.getLogger("DebateApp").info("Launch method has returned");
	}

	@Override public void init() throws Exception {

		settings.load();

		//OS specific code
		final String myOS = System.getProperty("os.name").toLowerCase();
		if (myOS.contains("win")) {//Windows
			Logger.getLogger("DebateApp").info("OS detected as Windows");
			Files.setAttribute(new File(AppUtils.getAppHome()).toPath(), "dos:hidden", true);
			if(System.getProperty("os.name").endsWith("10")) {
				final JMetro jmetro = new JMetro(Style.LIGHT);
				jmetro.setScene(mainScene);
			}
		} else { // Not windows
			if (myOS.contains("mac")) { //macOS
				Logger.getLogger("DebateApp").info("OS detected as Windows");
			}
			else if(myOS.contains("nix") || myOS.contains("nux")) {//Linux or Unix
				Logger.getLogger("DebateApp").info("OS detected as Linux/Unix");
			} else
				Logger.getLogger("DebateApp").info("Unknown OS!");
		}

		//Configure menu
		saveAsAction.setAccelerator(
						new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
		saveAction.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN));
		openAction.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.SHORTCUT_DOWN));
		nextPage.setAccelerator(new KeyCodeCombination(KeyCode.SPACE, KeyCodeCombination.SHORTCUT_DOWN));
		top.setUseSystemMenuBar(true);

		//Configure bottom
		mainTimerSpeechSelectorBox.setItems(FXCollections.observableArrayList(currentEvent.getValue().getSpeeches()));
		mainTimerSpeechSelectorBox.setOnAction(e -> {
			if(mainTimerSpeechSelectorBox.getValue()!=null)
				mainTimer.resetTimer(mainTimerSpeechSelectorBox.getValue().getTimeSeconds());
		});
		mainTimerSpeechSelectorBox.disableProperty().bind(mainTimer.timerRunningProperty);

		mainTimerBox.setAlignment(Pos.CENTER);
		mainTimerBox.setSpacing(5);

		bottom.setBorder(new Border(new BorderStroke(Color.web("748A86"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
	}

	@Override public void start(Stage mainStage) {
		this.mainStage = mainStage;
		mainStage.setScene(mainScene);

		mainStage.setTitle("Debate App");
		mainStage.getIcons().addAll(new Image(getClass().getResourceAsStream("/speaker128.png")),
						new Image(getClass().getResourceAsStream("/speaker64.png")),
						new Image(getClass().getResourceAsStream("/speaker32.png")),
						new Image(getClass().getResourceAsStream("/speaker16.png")));

		Logger.getLogger("DebateApp").info("Successfully loaded icons");

		mainStage.setMinWidth(925);
		mainStage.setMinHeight(400);
		mainStage.setWidth(settings.defaultWidth.getValue());
		mainStage.setHeight(settings.defaultHeight.getValue());

		List<String> args = getParameters().getRaw();
		for(String argument : args) {
			System.out.println(argument);
			File file = new File(argument);
			if(file.exists()) {
				if(editorSaveHandler==null)
					editorSaveHandler = new SaveHandler(flowEditor, file);
				try {
					editorSaveHandler.open(file, events);
				} catch(IOException e) {
					Logger.getLogger("DebateApp").warning("Failed to open " + file.getPath());
				}
			}
		}

		mainStage.show();

		if(AppUtils.firstRun || AppSettings.DEBUGMODE) {
			Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
			infoAlert.setTitle("Info dialog");
			infoAlert.setHeaderText("Welcome to DebateApp");
			infoAlert.setContentText("" +
							"If you haven't already read the readme, read it!\n" +
							"\tTo see the readme and other helpful information, use the\n" +
							"\thelp menu and click \"About\"\n" +
							"Plus, here are a couple of useful tips:\n" +
							"1. Timers for pro/con prep and a main timer for speeches are at the bottom of the window\n" +
							"2. There are lots of ways to save your flows, use the File\n" +
							"\tmenu to see them all\n" +
							"3. DebateApp has multiple layouts and supported events, you\n" +
							"\tcan use the view menu to set your layout and the\n" +
							"\tevent menu to cycle through events\n" +
							"4. If you have an issue or an idea please report it under\n" +
							"\tthe help menu so I can take a look\n" +
							"5. Take a look at the settings menu to customize the app\n\n" +
							"Lastly you should remember the following key bind:\n" +
							"\t\t\tControl (Command) + Space\n" +
							"It will go to the next page of speeches\n");
			infoAlert.showAndWait();
		}

		Platform.runLater(this::checkForUpdate);
	}

	@Override public void stop() throws IOException {
		settings.save();
	}

	public void checkForUpdate() {
		final UpdateChecker[] checker = new UpdateChecker[1];
		Task<Boolean> task = new Task<>() {
			@Override public Boolean call() throws IOException {
				checker[0] = new UpdateChecker(VERSION);
				return checker[0].check();
			}
		};

		Logger.getLogger("DebateApp").info("Checking if latest release is newer than " + VERSION);
		new Thread(task).start();
		task.setOnSucceeded(event -> {
			Logger.getLogger("DebateApp").info("Update Check done");
			try {
				if (task.get()) {
					Logger.getLogger("DebateApp").info("Found new version");
					checker[0].showUpdateAlert();
				} else {
					Logger.getLogger("DebateApp").info("Didn't find new version");
					if(settings.showNoUpdateMessage.get()) {
						Popup noUpdatePopup = new Popup();
						Label topLabel = new Label("No updates found");
						topLabel.setStyle("-fx-background-color: #BAD77A; -fx-font-size: 24;");
						Label bottomLabel = new Label("Click anywhere to continue");
						bottomLabel.setStyle("-fx-text-fill: #748a86; -fx-font-size: 10;");
						VBox vbox = new VBox(topLabel, bottomLabel);
						vbox.setAlignment(Pos.CENTER);
						noUpdatePopup.getContent().setAll(vbox);
						noUpdatePopup.setAutoHide(true);
						noUpdatePopup.getScene().setOnMouseClicked(e -> noUpdatePopup.hide());

						Platform.runLater(() -> {
							noUpdatePopup.show(mainStage);
							noUpdatePopup.setX(mainStage.getX() + (mainStage.getWidth() / 2) - (noUpdatePopup
											.getWidth() / 2));
							noUpdatePopup.setY(mainStage.getY() + (mainStage.getHeight() / 2) - (noUpdatePopup
											.getHeight() / 2));
						});
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				Logger.getLogger("DebateApp").warning("Update Check failed");
			}
		});
	}
}
