package main.java;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.structures.AppSettings;

import java.io.File;

//TODO add alternative layouts for flows (maybe a SpreadsheetView?) *
//TODO test prep times using HiddenSidesPane *
//TODO refactor and add settings to PropertySheet controls **

/**
 * <div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title=
 * "Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title=
 * "Flaticon">www.flaticon.com</a></div>
 *
 * @author Tag Howard
 */
public class DebateAppMain extends Application {

	public final static File         appHome        = new File(System.getProperty("user.home") + File.separator + "DebateApp");
	final               File         propertiesFile = new File(appHome.getPath() + File.separator + "DebateApp.properties");

	public static void main(String[] args) {
		launch(args);
	}

	@Override public void init() throws Exception {
		AppSettings settings = new AppSettings(propertiesFile);
		settings.properties.list(System.out);
		System.out.println();
		settings.load();
		settings.properties.list(System.out);
		System.out.println();
		settings.save();;
		settings.properties.list(System.out);
	}

	@Override public void start(Stage primaryStage) {
		primaryStage.show();
	}
}
