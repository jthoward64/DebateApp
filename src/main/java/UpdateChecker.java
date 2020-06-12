package main.java;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import main.java.structures.Version;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
	private final Version current;
	private final Version latestVersion;

	public UpdateChecker(Version current) throws IOException {
		this.current = current;

		URLConnection connection = new URL("https://github.com/tajetaje/DebateApp/releases/latest").openConnection();
		connection.connect();
		InputStream connectionInputStream = connection.getInputStream();
		latestVersion = new Version(connection.getURL().toExternalForm()
						.substring(connection.getURL().toExternalForm().lastIndexOf('/') + 1));

		connectionInputStream.close();
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
