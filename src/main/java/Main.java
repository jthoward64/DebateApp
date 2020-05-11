package main.java;

import com.opencsv.CSVWriter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * <div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title=
 * "Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title=
 * "Flaticon">www.flaticon.com</a></div>
 *
 * @author Tag Howard
 */
public class Main extends Application {
	public static final int version = 100; //e.g. v1.2.3 = 123
	final DebateEvents events = new DebateEvents();
	final File appHome = new File(System.getProperty("user.home") + File.separator + "DebateApp");
	final File propertiesFile = new File(appHome.getPath() + File.separator + "DebateApp.properties");
	// Pro
	final Pair<TextField, Timeline> proTimerPair = new Pair<>(new TextField("3:00"), new Timeline());
	final VBox proPrep = new VBox(new Label("Pro"), proTimerPair.getKey(), new Button("Start"));
	final HBox proFlow = new HBox();
	final ArrayList<String> proSpeechNames = new ArrayList<>();
	final ArrayList<TextArea> proSpeechTextAreas = new ArrayList<>();
	// Con
	final Pair<TextField, Timeline> conTimerPair = new Pair<>(new TextField("3:00"), new Timeline());
	final VBox conPrep = new VBox(new Label("Con"), conTimerPair.getKey(), new Button("Start"));
	final HBox conFlow = new HBox();
	final ArrayList<String> conSpeechNames = new ArrayList<>();
	final ArrayList<TextArea> conSpeechTextAreas = new ArrayList<>();
	// Main timer
	final Pair<TextField, Timeline> bottomTimerPair = new Pair<>(new TextField("0:00"), new Timeline());
	final ComboBox<Speech> timeSelect = new ComboBox<>();
	final HBox bottom = new HBox(new Label("Speech Timer "), bottomTimerPair.getKey(), new Button("Start"), timeSelect);
	// Menu
	final MenuBar menuBar = new MenuBar(new Menu("View", null, //0
					new CheckMenuItem("Always visible")), //-0

					new Menu("Flow", null, //1
									new MenuItem("Pro"), //-0
									new MenuItem("Con"), //-1
									new MenuItem("Save")), //-2

					new Menu("Links", null, //2
									new MenuItem("Tabroom"),  //-0
									new MenuItem("NSDA"), //-1
									new MenuItem("GDrive")), //-2

					new Menu("Settings", null, //3
									new Menu("Event", null,//-0
													new MenuItem("Public Forum"), //--0
													new MenuItem("Lincoln-Douglas"), //--1
													new MenuItem("Policy"), //--2
													new SeparatorMenuItem(), //-3
													new MenuItem("Set as Default")), //--4
									new Menu("Custom Times"), //-1 (Filled dynamically)
									new SeparatorMenuItem(), // -2
									new CheckMenuItem("Save on Exit"), //-3
									new SeparatorMenuItem(), //-4
									new MenuItem("Set default window size") //-5
					));
	final BooleanProperty saveOnExit = ((CheckMenuItem) (menuBar.getMenus().get(3).getItems().get(3)))
					.selectedProperty();
	// Utility
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	final UnaryOperator<Change> timeFilter = change -> {
		if (change.getControlNewText().matches("[0-9]?[0-9]?:[0-9]?[0-9]?"))
			return change;
		return null;
	};
	final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	final Date today = new Date();
	final BorderPane root = new BorderPane();
	final Scene scene = new Scene(root);
	double defWidth = 1150;
	double defHeight = 600;
	DebateEvent defEvent = events.pf;
	// Main
	Stage primaryStage;
	final Properties properties = loadProps();
	DebateEvent currentEvent = switchEvent(defEvent);

	public static void main(final String[] args) {
		launch(args);
	}

	public String formatTime(final int seconds) {
		if ((seconds % 60) >= 10)
			return (seconds / 60) + ":" + (seconds % 60);
		else
			return (seconds / 60) + ":0" + (seconds % 60);
	}

	//Add more properties
	private Properties loadProps() {
		Properties props = new Properties();

		try {
			if (appHome.mkdirs())
				System.out.println("\"DebateApp\" directory created in user home");
			if (propertiesFile.createNewFile())
				System.out.println("Properties file created in \"DebateApp\" directory");

			props.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			showExceptionDialog(e, false);
		}

		//load size
		defWidth = Double.parseDouble(props.getProperty("defaultWidth", String.valueOf(defWidth)));
		defHeight = Double.parseDouble(props.getProperty("defaultHeight", String.valueOf(defHeight)));
		if (primaryStage != null) {
			primaryStage.setWidth(defWidth);
			primaryStage.setHeight(defHeight);
		}

		//load times
		events.pf.setTimesFromString(props.getProperty("pfTimes", "240,240,240,240,180,180,180,120,120,"));
		events.ld.setTimesFromString(props.getProperty("ldTimes", "360,420,180,240,360,180,"));
		events.policy.setTimesFromString(props.getProperty("policyTimes", "480,480,180,480,480,300,300,300,300,"));

		//load prep times
		events.pf.setPrepSeconds(Integer.parseInt(props.getProperty("pfPrep", "180")));
		events.ld.setPrepSeconds(Integer.parseInt(props.getProperty("ldPrep", "240")));
		events.policy.setPrepSeconds(Integer.parseInt(props.getProperty("policyPrep", "300")));

		//load default event
		defEvent = events.getEvent(props.getProperty("defEvent", "Public Forum"));

		Platform.runLater(() -> saveOnExit.setValue(Boolean.parseBoolean(props.getProperty("saveOnExit", "true"))));

		return props;
	}

	private void saveProps() throws IOException {
		if (appHome.mkdirs())
			System.out.println("\"DebateApp\" directory created in user home");
		if (propertiesFile.createNewFile())
			System.out.println("Properties file created in \"DebateApp\" directory");

		//save size
		properties.setProperty("defaultWidth", String.valueOf(defWidth));
		properties.setProperty("defaultHeight", String.valueOf(defHeight));

		//save times
		properties.setProperty("pfTimes", events.pf.getTimes());
		properties.setProperty("ldTimes", events.ld.getTimes());
		properties.setProperty("policyTimes", events.policy.getTimes());

		//save prep times
		properties.setProperty("pfPrep", String.valueOf(events.pf.getPrepSeconds()));
		properties.setProperty("ldPrep", String.valueOf(events.ld.getPrepSeconds()));
		properties.setProperty("policyPrep", String.valueOf(events.policy.getPrepSeconds()));

		//Save default event
		properties.setProperty("defEvent", defEvent.getName());

		//Save saveOnExit
		properties.setProperty("saveOnExit", String.valueOf(saveOnExit.get()));

		properties.store(new FileOutputStream(propertiesFile), "DebateApp configuration file");
	}

	@Override public void init() {
		loadProps();

		{//Add JMetro if windows
			if (System.getProperty("os.name").endsWith("10")) {
				final JMetro jmetro = new JMetro(Style.LIGHT);
				jmetro.setScene(scene);
			}
		}

		directoryChooser.setInitialDirectory(appHome);

		//		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		final Insets margin = new Insets(10.0);

		{//Fill root
			root.setTop(menuBar);

			root.setBottom(bottom);
			BorderPane.setMargin(bottom, margin);
			BorderPane.setAlignment(bottom, Pos.CENTER);

			root.setCenter(proFlow);
			proPrep.getChildren().get(0).setStyle("-fx-font-weight: bold;");
			conPrep.getChildren().get(0).setStyle("-fx-font-weight: normal;");
			BorderPane.setMargin(proFlow, margin);
			BorderPane.setMargin(conFlow, margin);

			root.setLeft(proPrep);
			BorderPane.setMargin(proPrep, margin);

			root.setRight(conPrep);
			BorderPane.setMargin(conPrep, margin);

			root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		}

		timeSelect.disableProperty().bind(bottomTimerPair.getKey().editableProperty().not());

		Platform.runLater(() -> menuBar.getMenus().get(0).getItems().get(0).setOnAction(e -> primaryStage
						.setAlwaysOnTop(((CheckMenuItem) (menuBar.getMenus().get(0).getItems().get(0))).isSelected())));

		{//Switch to pro
			menuBar.getMenus().get(1).getItems().get(0).setOnAction(e -> {
				root.setCenter(proFlow);
				proPrep.getChildren().get(0).setStyle("-fx-font-weight: bold;");
				conPrep.getChildren().get(0).setStyle("-fx-font-weight: normal;");
			});
		}

		{//Switch to con
			menuBar.getMenus().get(1).getItems().get(1).setOnAction(e -> {
				root.setCenter(conFlow);
				proPrep.getChildren().get(0).setStyle("-fx-font-weight: normal;");
				conPrep.getChildren().get(0).setStyle("-fx-font-weight: bold;");
			});
		}

		{
			menuBar.getMenus().get(1).getItems().get(2).setOnAction(e -> saveFlow());
		}

		{
			menuBar.getMenus().get(2).getItems().get(0).setOnAction(e -> {//Open tabroom
				try {
					openURL("www.tabroom.com");
				} catch (IOException | URISyntaxException ex) {
					showExceptionDialog(ex, true);
				}
			});
			menuBar.getMenus().get(2).getItems().get(1).setOnAction(e -> {//Open NSDA
				try {
					openURL("www.speechanddebate.org");
				} catch (IOException | URISyntaxException ex) {
					showExceptionDialog(ex, true);
				}
			});
			menuBar.getMenus().get(2).getItems().get(2).setOnAction(e -> {//Open GDrive
				try {
					openURL("drive.google.com");
				} catch (IOException | URISyntaxException ex) {
					showExceptionDialog(ex, true);
				}
			});
		}

		{
			((Menu) (menuBar.getMenus().get(3).getItems().get(0))).getItems().get(0)
							.setOnAction(e -> switchEvent(events.pf));// Switch to PF
			((Menu) (menuBar.getMenus().get(3).getItems().get(0))).getItems().get(1)
							.setOnAction(e -> switchEvent(events.ld));//Switch to LD
			((Menu) (menuBar.getMenus().get(3).getItems().get(0))).getItems().get(2)
							.setOnAction(e -> switchEvent(events.policy));//Switch to Policy
			((Menu) (menuBar.getMenus().get(3).getItems().get(0))).getItems().get(4)
							.setOnAction(e -> defEvent = currentEvent);//Set default event

			menuBar.getMenus().get(3).getItems().get(5).setOnAction(e -> {//Set default size
				defWidth = primaryStage.getWidth();
				defHeight = primaryStage.getHeight();
			});

			timeSelect.setOnAction(e -> resetTimer(timeSelect.getValue().getTimeSeconds(), bottomTimerPair.getKey(),
							bottomTimerPair.getValue(), //Set timer to contents of speech ComboBox
							(Button) bottom.getChildren().get(2)));
		}

		{//Set key binds
			KeyCombination saveCombo = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN);
			Runnable saveRunnable = this::saveFlow;
			scene.getAccelerators().put(saveCombo, saveRunnable);

			KeyCombination switchFlowCombo = new KeyCodeCombination(KeyCode.SPACE, KeyCodeCombination.CONTROL_DOWN);
			Runnable switchFlowRunnable = () -> {
				if (root.getCenter().equals(proFlow)) {
					root.setCenter(conFlow);
					proPrep.getChildren().get(0).setStyle("-fx-font-weight: normal;");
					conPrep.getChildren().get(0).setStyle("-fx-font-weight: bold;");

				} else {
					root.setCenter(proFlow);
					proPrep.getChildren().get(0).setStyle("-fx-font-weight: bold;");
					conPrep.getChildren().get(0).setStyle("-fx-font-weight: normal;");
				}
			};

			scene.getAccelerators().put(switchFlowCombo, switchFlowRunnable);

			KeyCombination switchEventCombo = new KeyCodeCombination(KeyCode.SPACE, KeyCodeCombination.CONTROL_DOWN,
							KeyCodeCombination.SHIFT_DOWN);
			Runnable switchEventRunnable = () -> switchEvent(events.getEvents()
							.get(events.getEvents().indexOf(events.getEvent(currentEvent.getName()))+1));

			scene.getAccelerators().put(switchEventCombo, switchEventRunnable);
		}
	}

	@Override public void start(final Stage primaryStage) {
		this.primaryStage = primaryStage;

		primaryStage.setScene(scene);

		primaryStage.getIcons().addAll(new Image(getClass().getResourceAsStream("/speaker128.png")),
						new Image(getClass().getResourceAsStream("/speaker64.png")),
						new Image(getClass().getResourceAsStream("/speaker32.png")),
						new Image(getClass().getResourceAsStream("/speaker16.png")));

		primaryStage.setMinWidth(1150);
		primaryStage.setMinHeight(600);
		primaryStage.setWidth(defWidth);
		primaryStage.setHeight(defHeight);

		try {
			checkForUpdate();
		} catch (IOException e) {
			e.printStackTrace();
		}

		primaryStage.show();
	}

	@Override public void stop() throws Exception {
		saveProps();
		if (saveOnExit.get())
			saveFlow();
	}

	private void buildFlowEditor(HBox textParent, ArrayList<String> namesList, ArrayList<TextArea> textAreas,
					Side side) {
		textParent.getChildren().clear();
		textParent.prefHeightProperty().unbind();
		textParent.maxHeightProperty().unbind();
		namesList.clear();
		textAreas.clear();

		ArrayList<Speech> collectedSpeeches = currentEvent.getSpeeches().stream()
						.filter((Speech speech) -> speech.getSide().equals(side))
						.collect(Collectors.toCollection(ArrayList::new));

		for (Speech collectedSpeech : collectedSpeeches) {
			Label label = new Label(collectedSpeech.getName());
			namesList.add(collectedSpeech.getName());
			TextArea textArea = new TextArea();
			textAreas.add(textArea);
			label.prefWidthProperty().bind(textArea.widthProperty());
			textParent.getChildren().add(new VBox(label, textArea));

			textParent.prefHeightProperty().bindBidirectional(textArea.prefHeightProperty());
		}

		textParent.maxHeightProperty().bind(root.heightProperty());
		textParent.prefHeightProperty().bind(root.heightProperty().subtract(menuBar.heightProperty())
						.subtract(bottom.heightProperty()));
	}

	private DebateEvent switchEvent(DebateEvent event) {
		currentEvent = event;
		buildFlowEditor(proFlow, proSpeechNames, proSpeechTextAreas, Side.PRO);
		buildFlowEditor(conFlow, conSpeechNames, conSpeechTextAreas, Side.CON);
		resetTimer(event.getPrepSeconds(), proTimerPair.getKey(), proTimerPair.getValue(),
						((Button) proPrep.getChildren().get(2)));
		resetTimer(event.getPrepSeconds(), conTimerPair.getKey(), conTimerPair.getValue(),
						((Button) conPrep.getChildren().get(2)));
		timeSelect.getItems().setAll(event.getSpeeches());

		return event;
	}

	/**
	 * Opens the given url using the client's default browser <br>
	 * Supports <i>macOS</i>, <i>Windows</i>, and most <i>Linux</i> distros
	 *
	 * @param url the page to be opened
	 * @throws IOException        if the method fails to run the browser
	 * @throws URISyntaxException if the url is malformed
	 * @since 1.0.0
	 */
	private void openURL(final String url) throws IOException, URISyntaxException {
		final String myOS = System.getProperty("os.name").toLowerCase();

		if (Desktop.isDesktopSupported()) { // Probably Windows
			final Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(url));
		} else { // Definitely Non-windows
			final Runtime runtime = Runtime.getRuntime();
			if (myOS.contains("mac"))
				runtime.exec("open " + url);
			else if (myOS.contains("nix") || myOS.contains("nux"))
				runtime.exec("xdg-open " + url);
		}
	}

	public void resetTimer(final int seconds, final TextField field, final Timeline timeline, final Button button) {
		final String defaultText = "Start";
		button.setText(defaultText);
		field.setEditable(true);
		field.setTextFormatter(new TextFormatter<Integer>(timeFilter));

		button.setStyle("-fx-background-color: lightgreen;");

		// update timerLabel
		field.setText(formatTime(seconds));
		timeline.setCycleCount(Animation.INDEFINITE);
		// KeyFrame event handler
		timeline.getKeyFrames().clear();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
			// update timerLabel
			field.setText(formatTime(unFormatTime(field.getText()) - 1));
			if (unFormatTime(field.getText()) <= 0) {
				timeline.stop();
				button.setText(defaultText);
				field.setEditable(true);
				((Pane) button.getParent()).setBackground(new Background(
								new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
				button.getParent().setOnMouseClicked(e -> {
					field.setText(formatTime(seconds));
					((Pane) button.getParent()).setBackground(Background.EMPTY);
					button.getParent().setOnMouseClicked(null);
					button.setStyle("-fx-background-color: lightgreen;");
				});
			}
		}));

		button.setOnAction(e -> {
			switch (timeline.getStatus()) {
			case RUNNING:
				if (unFormatTime(field.getText()) < 1)
					break;
				timeline.pause();
				button.setStyle("-fx-background-color: lightgreen;");
				button.setText("Resume");
				field.setEditable(true);
				break;
			case STOPPED:
				if (unFormatTime(field.getText()) == 0)
					field.setText(formatTime(seconds));
				field.setEditable(false);
				button.setStyle("-fx-background-color: ff5555;");
				timeline.play();
				button.setText("Pause");
			case PAUSED:
				field.setEditable(false);
				button.setStyle("-fx-background-color: ff5555;");
				timeline.play();
				button.setText("Pause");
				break;
			default:
				showExceptionDialog(new IllegalStateException("Timeline is in an unhandled state."), true);
			}
		});
	}

	public void saveFlow() {
		boolean screenshot = false;
		boolean csvFile = false;

		TextInputDialog namePrompt = new TextInputDialog();
		namePrompt.setTitle("Name your file");
		namePrompt.setHeaderText(namePrompt.getTitle());
		namePrompt.setContentText(
						"Choose a meaningful name for your round (e.g. your opponents team code) so that you can refer back to the round in the future.");
		String fileName = namePrompt.showAndWait().orElse("NO_CUSTOM_NAME");

		if (fileName.equals("NO_CUSTOM_NAME"))
			return;

		Alert howToSavePrompt = new Alert(AlertType.INFORMATION, "How do you want to save?",
						new ButtonType("Screenshot", ButtonData.OTHER), new ButtonType("Both", ButtonData.OTHER),
						new ButtonType("CSV File", ButtonData.OTHER), ButtonType.CANCEL);
		String howToSave = howToSavePrompt.showAndWait().orElse(ButtonType.CLOSE).getText();
		switch (howToSave) {
		case "Screenshot":
			screenshot = true;
			break;
		case "Both":
			screenshot = true;
			csvFile = true;
			break;
		case "CSV File":
			csvFile = true;
			break;
		default:
			return;
		}

		final File directory = directoryChooser.showDialog(primaryStage);

		if (directory == null)
			return;

		try {
			if (csvFile) {
				final File flowFile = new File(directory.getPath() + File.separator + "Flow " + dateFormat
								.format(today) + " " + fileName + ".csv");
				if (!flowFile.createNewFile()) {
					Alert fileConflictAlert = new Alert(AlertType.ERROR);
					fileConflictAlert.setContentText("The file you tried to create already exists");
					fileConflictAlert.showAndWait();
				}
				final CSVWriter w = new CSVWriter(new FileWriter(flowFile));

				String[] namesArray = new String[proSpeechNames.size() + conSpeechNames.size()];
				int i;
				for (i = 0; i < proSpeechNames.size(); i++) {
					namesArray[i] = proSpeechNames.get(i);
				}
				for (; i < proSpeechNames.size() + conSpeechNames.size(); i++) {
					namesArray[i] = conSpeechNames.get(i);
				}

				String[] flowArray = new String[proSpeechTextAreas.size() + conSpeechNames.size()];
				int j;
				for (j = 0; j < proSpeechTextAreas.size(); j++) {
					flowArray[i] = proSpeechTextAreas.get(i).getText();
				}
				for (; j < proSpeechTextAreas.size() + conSpeechNames.size(); j++) {
					flowArray[i] = conSpeechTextAreas.get(i).getText();
				}

				w.writeAll(Collections.singletonList(namesArray));
				w.writeAll(Collections.singletonList(flowArray));

				if (w.checkError()) {
					final Alert errorMessage = new Alert(AlertType.ERROR, "There was an error while saving the file",
									ButtonType.OK);
					errorMessage.showAndWait();
				}
				w.flush();
				w.close();
			}
			if (screenshot) {
				final WritableImage writableProImage = proFlow.snapshot(new SnapshotParameters(), null);
				final File proFlowFile = new File(directory.getPath() + File.separator + "Pro Flow " + dateFormat
								.format(today) + " " + fileName + ".png");
				if (!proFlowFile.createNewFile()) {
					Alert fileConflictAlert = new Alert(AlertType.ERROR);
					fileConflictAlert.setContentText("The file you tried to create already exists");
					fileConflictAlert.showAndWait();
				}
				ImageIO.write(SwingFXUtils.fromFXImage(writableProImage, null), "png", proFlowFile);

				final WritableImage writableConImage = conFlow.snapshot(new SnapshotParameters(), null);
				final File conFlowFile = new File(directory.getPath() + File.separator + "Con Flow " + dateFormat
								.format(today) + " " + fileName + ".png");
				if (!conFlowFile.createNewFile()) {
					Alert fileConflictAlert = new Alert(AlertType.ERROR);
					fileConflictAlert.setContentText("The file you tried to create already exists");
					fileConflictAlert.showAndWait();
				}
				ImageIO.write(SwingFXUtils.fromFXImage(writableConImage, null), "png", conFlowFile);
			}
		} catch (final IOException e) {
			showExceptionDialog(e, false);
		}

	}

	public void showExceptionDialog(Exception exception, boolean allowSave) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");
		alert.setHeaderText("DebateApp has experienced an issue");
		alert.setContentText(allowSave ?
						"To prevent unstable operation the program will close.\nIf you would like to try to save your work press \"Save\"" :
						"To prevent unstable operation the program will close.");

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);

		ButtonType saveButtonType = new ButtonType("Save");

		alert.getButtonTypes().setAll(allowSave ? saveButtonType : null, ButtonType.CLOSE);

		if (alert.showAndWait().orElse(ButtonType.CLOSE).equals(saveButtonType)) {
			saveFlow();
		}

		Platform.exit();
	}

	public void checkForUpdate() throws IOException {

		URLConnection connection = new URL("https://github.com/tajetaje/DebateApp/releases/latest").openConnection();
		connection.connect();
		InputStream connectionInputStream = connection.getInputStream();
		StringBuilder latestVersion = new StringBuilder();
		latestVersion.append(connection.getURL().toExternalForm()
						.substring(connection.getURL().toExternalForm().lastIndexOf('/') + 1));
		for (int i = latestVersion.length() - 1; i > 0; i--) {
			if (latestVersion.charAt(i) == '.')
				latestVersion.deleteCharAt(i);
		}
		int latestVersionNumber = Integer.parseInt(latestVersion.toString());

		connectionInputStream.close();

		if (latestVersionNumber > version) {
			Alert newVersionAlert = new Alert(AlertType.INFORMATION);
			newVersionAlert.setHeaderText("A new version is available");
			newVersionAlert.setTitle("Update message");
			newVersionAlert.setContentText("Would you like to go to the download page now?");
			newVersionAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
			System.out.println("test");
			if (newVersionAlert.showAndWait().orElse(ButtonType.NO).getButtonData().equals(ButtonData.YES)) {
				try {
					openURL("https://github.com/tajetaje/DebateApp/releases/latest");
				} catch (IOException | URISyntaxException e) {
					showExceptionDialog(e, true);
					}
			}
		}
	}

	public int unFormatTime(final String time) {
		int seconds;
		if (time.endsWith(":"))
			seconds = 0;
		else
			seconds = Integer.parseInt(time.substring(time.indexOf(':') + 1));
		int minutes;
		if (time.startsWith(":"))
			minutes = 0;
		else
			minutes = Integer.parseInt(time.substring(0, time.indexOf(':')));
		seconds += 60 * minutes;
		return seconds;
	}
}
