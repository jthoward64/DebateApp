package main.java.structures;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import main.java.AppUtils;
import main.java.Main;
import org.controlsfx.control.PropertySheet;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class AppSettings {

	public File appHome;//These should probably be made user definable eventually
	public File propertiesFile;

	public final Properties properties = new Properties();

	public final DebateEvents debateEvents = new DebateEvents();

	public final SimpleBooleanProperty saveOnExit = new SimpleBooleanProperty(false);

	public final SimpleDoubleProperty defaultWidth = new SimpleDoubleProperty();
	public final SimpleDoubleProperty defaultHeight = new SimpleDoubleProperty();

	public final SimpleObjectProperty<DebateEvent> defaultEvent = new SimpleObjectProperty<>(debateEvents.pf);

	//TODO move some more hardcoded values here

	public AppSettings(File appHome) {
		this.appHome = appHome;
		this.propertiesFile = new File(this.appHome.getPath() + File.separator + "DebateApp.properties");

		try {
			if (appHome.mkdirs())
				System.out.println("\"DebateApp\" directory created in user home");
			if (propertiesFile.createNewFile())
				System.out.println("Properties file created in \"DebateApp\" directory");

			load();
		} catch (IOException e) {
			AppUtils.showExceptionDialog(e);
		}
	}

	public void load() throws IOException {
		AppUtils.allowSave = false;
		properties.load(new FileInputStream(propertiesFile));

		//load size
		defaultWidth.setValue(Double.parseDouble(properties.getProperty("defaultWidth", String.valueOf(defaultWidth.get()))));
		defaultHeight.setValue(Double.parseDouble(properties.getProperty("defaultHeight", String.valueOf(defaultWidth.get()))));

		//load times
		debateEvents.pf.setTimesFromString(properties.getProperty("pfTimes", "240,240,240,240,180,180,180,120,120,"));
		debateEvents.ld.setTimesFromString(properties.getProperty("ldTimes", "360,420,180,240,360,180,"));
		debateEvents.policy.setTimesFromString(properties.getProperty("policyTimes", "480,480,180,480,480,300,300,300,300,"));

		//load prep times
		debateEvents.pf.setPrepSeconds(Integer.parseInt(properties.getProperty("pfPrep", "180")));
		debateEvents.ld.setPrepSeconds(Integer.parseInt(properties.getProperty("ldPrep", "240")));
		debateEvents.policy.setPrepSeconds(Integer.parseInt(properties.getProperty("policyPrep", "300")));

		//load default event
		defaultEvent.setValue(debateEvents.getEvent(properties.getProperty("defEvent", "Public Forum")));

		saveOnExit.setValue(Boolean.parseBoolean(properties.getProperty("saveOnExit", "false")));

		AppUtils.allowSave = true;
	}

	public void save() throws IOException {
		AppUtils.allowSave = false;

		if(appHome.mkdirs())
			System.out.println("\"DebateApp\" directory created in user home");
		if(propertiesFile.createNewFile())
			System.out.println("Properties file created in \"DebateApp\" directory");

		//save size
		properties.setProperty("defaultWidth", String.valueOf(defaultWidth.get()));
		properties.setProperty("defaultHeight", String.valueOf(defaultHeight.get()));

		//save times
		properties.setProperty("pfTimes", debateEvents.pf.getTimes());
		properties.setProperty("ldTimes", debateEvents.ld.getTimes());
		properties.setProperty("policyTimes", debateEvents.policy.getTimes());

		//save prep times
		properties.setProperty("pfPrep", String.valueOf(debateEvents.pf.getPrepSeconds()));
		properties.setProperty("ldPrep", String.valueOf(debateEvents.ld.getPrepSeconds()));
		properties.setProperty("policyPrep", String.valueOf(debateEvents.policy.getPrepSeconds()));

		//Save default event
		properties.setProperty("defEvent", defaultEvent.get().getName());

		//Save saveOnExit
		properties.setProperty("saveOnExit", String.valueOf(saveOnExit.get()));

		properties.store(new FileOutputStream(propertiesFile), "Configuration for tajetaje's DebateApp");
		AppUtils.allowSave = true;
	}
}