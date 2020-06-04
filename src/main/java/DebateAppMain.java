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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * <div>App icon made by <a href="https://www.flaticon.com/authors/freepik" title=
 * "Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title=
 * "Flaticon">www.flaticon.com</a></div>
 *
 * @author Tag Howard
 * TODO add logging
 */
public class DebateAppMain extends Application {
	Stage mainStage;

	FileChooser.ExtensionFilter saveFileFilter = new FileChooser.ExtensionFilter("DebateApp save file", "*.db8");
	FileChooser.ExtensionFilter pngFileFilter = new FileChooser.ExtensionFilter("PNG image", "*.png");
	FileChooser.ExtensionFilter tiffFileFilter = new FileChooser.ExtensionFilter("TIFF image", "*.tiff", "*.tif");
	FileChooser.ExtensionFilter docxFileFilter = new FileChooser.ExtensionFilter("Word document", "*.docx");

	public static final AppSettings                       settings       = new AppSettings(
					new File(AppUtils.getAppHome()));
	final               SettingsEditor                    settingsEditor = new SettingsEditor(settings);
	final               DebateEvents                      events         = settings.debateEvents;
	final               SimpleObjectProperty<DebateEvent> currentEvent   = new SimpleObjectProperty<>(events.pf);

	//Bottom
	final DebateTimer      mainTimer                  = new DebateTimer(Orientation.HORIZONTAL, "Timer", 0);
	final ComboBox<Speech> mainTimerSpeechSelectorBox = new ComboBox<>(); //TODO fill with speeches
	final HBox             bottom                     = new HBox(mainTimer, mainTimerSpeechSelectorBox);

	//Center
	final FlowEditor flowEditor = new FlowEditor("[h:1h:3h:5h:7][h:2h:4h:6h:8]", events.pf);

	final DebateTimer conPrep = new DebateTimer(Orientation.VERTICAL, "Con", 180);
	final VBox        right   = new VBox(conPrep);

	final DebateTimer proPrep = new DebateTimer(Orientation.VERTICAL, "Pro", 180);
	final VBox        left    = new VBox(proPrep);

	final HiddenSidesPane center = new HiddenSidesPane(flowEditor, null, right, null, left);

	SaveHandler editorSaveHandler = new SaveHandler(flowEditor, null);
	final Action saveAsAction         = new Action("Save As", e -> {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save");
		chooser.getExtensionFilters().add(saveFileFilter);
		chooser.setSelectedExtensionFilter(saveFileFilter);
		System.out.println(chooser.getSelectedExtensionFilter().getExtensions());
		Platform.runLater(() -> {
			editorSaveHandler = new SaveHandler(flowEditor, chooser.showSaveDialog(mainStage));
			try {
				editorSaveHandler.save();
			} catch(IOException ioException) {
				AppUtils.showExceptionDialog(ioException);
			}
		});
	});
	final Action saveAction = new Action("Save", e -> {
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
	final Action openAction           = new Action("Open", e -> Platform.runLater(() -> {
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
	final ExportHandler editorExportHandler = new ExportHandler(flowEditor);
	final Action exportPNGsAction            = new Action("Multiple PNGs", e -> Platform.runLater(() -> {
		//TODO show info message
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Save to PNGs");
		try {
			File file = null;
			while(file==null) {
				file = chooser.showDialog(mainStage);
			}
			editorExportHandler.saveToImages("png", file);
		} catch(IOException ioException) {
			AppUtils.showExceptionDialog(ioException);
		}
	}));
	final Action exportAction = new Action("Export", e -> Platform.runLater(() -> {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Export");
		chooser.getExtensionFilters().addAll(pngFileFilter, tiffFileFilter, docxFileFilter);
		chooser.setSelectedExtensionFilter(pngFileFilter);
		File file = chooser.showSaveDialog(mainStage);
		 if(file == null) return;
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
	final Action settingsDialogAction = new Action("Settings", e -> settingsEditor.getDialog().showAndWait());

	final Menu fileMenu = new Menu("File", null, ActionUtils.createMenuItem(saveAction),
					ActionUtils.createMenuItem(saveAsAction), ActionUtils.createMenuItem(openAction),
					ActionUtils.createMenuItem(exportAction), ActionUtils.createMenuItem(exportPNGsAction),
					ActionUtils.createMenuItem(settingsDialogAction));

	final Action nextLayoutAction = new Action("Next Layout");
	final Action nextPage         = new Action("Next Page", e -> flowEditor.nextPage());
	final Menu   viewMenu         = new Menu("View", null, ActionUtils.createMenuItem(nextLayoutAction),
					ActionUtils.createMenuItem(nextPage));

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

	public static void main(String[] args) {
		launch(args);
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
	}
}
