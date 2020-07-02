package main.java;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import main.java.structures.Version;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class UpdateChecker {
	private final Version current;
	private final Version latestVersion;

	public UpdateChecker(Version current) throws IOException {
		this.current = current;

		Logger.getLogger("DebateApp").info("Checking version " + current + " against GitHub");
		URLConnection connection = new URL("https://github.com/tajetaje/DebateApp/releases/latest").openConnection();
		connection.connect();
		Logger.getLogger("DebateApp").info("Connection requested");

		InputStream connectionInputStream = connection.getInputStream();
		Logger.getLogger("DebateApp").info("Latest version url is " + connection.getURL().toExternalForm());
		latestVersion = new Version(connection.getURL().toExternalForm()
						.substring(connection.getURL().toExternalForm().lastIndexOf('/') + 1));
		connectionInputStream.close();

		Logger.getLogger("DebateApp").info("GitHub reports " + latestVersion + " is latest");
	}

	public boolean check() {
		return latestVersion.greaterThan(current);
	}

	public void showUpdateAlert() {
		Alert newVersionAlert = new Alert(Alert.AlertType.INFORMATION);
		newVersionAlert.setHeaderText("A new version is available");
		newVersionAlert.setTitle("Update message");
		newVersionAlert.setContentText("Would you like to go to the download page for version " + latestVersion + " now?");
		newVersionAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		if(newVersionAlert.showAndWait().orElse(ButtonType.NO).getButtonData().equals(ButtonBar.ButtonData.YES)) {
			AppUtils.openURL("https://github.com/tajetaje/DebateApp/releases/latest");
		}
	}
}
