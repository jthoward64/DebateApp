package main.java.structures;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import main.java.AppUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class AppSettings {

	public final File appHome;//These should probably be made user definable eventually
	public final File propertiesFile;

	public final Properties properties = new Properties();

	public final DebateEvents debateEvents = new DebateEvents();

	public final SimpleBooleanProperty saveOnExit = new SimpleBooleanProperty(false);
	public final SimpleBooleanProperty toolbarsVisibleProperty = new SimpleBooleanProperty(true);

	public final SimpleDoubleProperty defaultWidth = new SimpleDoubleProperty();
	public final SimpleDoubleProperty defaultHeight = new SimpleDoubleProperty();

	public final SimpleObjectProperty<DebateEvent> defaultEvent = new SimpleObjectProperty<>(debateEvents.pf);

	public AppSettings(File appHome) {
		this.appHome = appHome;
		this.propertiesFile = new File(this.appHome.getPath() + File.separator + "DebateApp.properties");

		try {
			if(appHome.mkdirs())
				Logger.getLogger("DebateApp").info("\"DebateApp\" directory created in user home");
			if(propertiesFile.createNewFile()) {
				Logger.getLogger("DebateApp").info("Properties file created in \"DebateApp\" directory");
				AppUtils.firstRun = true;
			}

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

		//load save on exit
		saveOnExit.setValue(Boolean.parseBoolean(properties.getProperty("saveOnExit", "true")));

		//load toolbar visibility
		toolbarsVisibleProperty.setValue(Boolean.parseBoolean(properties.getProperty("toolbarsVisible", "true")));

		saveOnExit.setValue(Boolean.parseBoolean(properties.getProperty("saveOnExit", "false")));

		AppUtils.allowSave = true;

		AppUtils.logger.info("Settings loaded from " + propertiesFile.getAbsolutePath());
	}

	public void save() throws IOException {
		AppUtils.allowSave = false;

		if(appHome.mkdirs())
			Logger.getLogger("DebateApp").info("\"DebateApp\" directory created in user home");
		if(propertiesFile.createNewFile())
			Logger.getLogger("DebateApp").info("Properties file created in \"DebateApp\" directory");

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

		//save toolbar visibility
		properties.setProperty("toolbarsVisible", String.valueOf(toolbarsVisibleProperty.get()));

		properties.store(new FileOutputStream(propertiesFile), "Configuration for tajetaje's DebateApp");
		AppUtils.allowSave = true;

		AppUtils.logger.info("Settings saved to " + propertiesFile.getAbsolutePath());
	}
}