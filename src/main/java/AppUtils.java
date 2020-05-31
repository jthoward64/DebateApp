package main.java;

import javafx.application.Platform;
import javafx.beans.binding.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AppUtils {

	public static boolean allowSave = true;
	public static Validator<String> timeValidator = Validator.createRegexValidator("Invalid time", "[0-9]{1,2}:[0-9]{1,2}",
	Severity.ERROR);

	/**
	 * Opens the given url using the client's default browser <br>
	 * Supports <i>macOS</i>, <i>Windows</i>, and most <i>Linux</i> distros
	 *
	 * @param url the page to be opened
	 * @since 1.0.0
	 */
	public static void openURL(final String url) {
		final String myOS = System.getProperty("os.name").toLowerCase();
		final Runtime runtime = Runtime.getRuntime();
		try {
			if(myOS.contains("win"))
				runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
			else if(myOS.contains("mac"))
				runtime.exec("open " + url);
			else if(myOS.contains("nix") || myOS.contains("nux"))
				runtime.exec("xdg-open " + url);
			else
				System.err.println("Unknown OS!");
		} catch(IOException e) {
			showExceptionDialog(e);
		}
	}

	public static IntegerBinding remainderProperty(IntegerBinding value, int divisor) {
		return Bindings.createIntegerBinding(() -> value.get() % divisor, value);
	}

	public static String formatTime(final int seconds) {
		if ((seconds % 60) >= 10)
			return (seconds / 60) + ":" + (seconds % 60);
		else
			return (seconds / 60) + ":0" + (seconds % 60);
	}

	public static int unFormatTime(final String time) {
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

	public static void showExceptionDialog(Exception exception) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
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

		if(allowSave)
			alert.getButtonTypes().setAll(saveButtonType, ButtonType.CLOSE);
		else
			alert.getButtonTypes().setAll(ButtonType.CLOSE);

		if (alert.showAndWait().orElse(ButtonType.CLOSE).equals(saveButtonType)) {
			System.out.println("Placeholder"); //TODO implement
		}

		Platform.exit();
	}

	public static String getAppHome() {
		final String myOS = System.getProperty("os.name").toLowerCase();
			if(myOS.contains("mac") || myOS.contains("nix") || myOS.contains("nux"))
				return System.getProperty("user.home") + File.separator + ".DebateApp";
			else
				return System.getProperty("user.home") + File.separator + "DebateApp";

	}
}
