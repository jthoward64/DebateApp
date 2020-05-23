package main.java.structures;

import main.java.AppUtils;
import main.java.Main;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class AppSettings {

	File propertiesFile;

	public final Properties properties = new Properties();

	public final HashMap<String, Boolean> boolProperties = new HashMap<>();// b_...
	public final HashMap<String, Integer> intProperties = new HashMap<>();// i_...
	public final HashMap<String, Double> doubleProperties = new HashMap<>();// d_...
	public final HashMap<String, String> stringProperties = new HashMap<>();// s_...
	public final HashMap<String, Enum> enumProperties = new HashMap<>();// e_...

	public AppSettings(File propertiesFile) {
		this.propertiesFile = propertiesFile;

		try {
			if (Main.appHome.mkdirs())
				System.out.println("\"DebateApp\" directory created in user home");
			if (propertiesFile.createNewFile())
				System.out.println("Properties file created in \"DebateApp\" directory");

			load();
		} catch (IOException e) {
			AppUtils.showExceptionDialog(e);
		}
	}

	public void load() throws IOException {
		properties.load(new FileInputStream(propertiesFile));
		properties.forEach( (k, v) -> {
			String key = (String) k;

			if(key.startsWith("b_")) {
				boolProperties.put(key.substring(key.indexOf('_')-1), Boolean.valueOf((String) v));
			}
			else if(key.startsWith("i_")) {
				intProperties.put(key.substring(key.indexOf('_')-1), Integer.valueOf((String) v));
			}
			else if(key.startsWith("d_")) {
				doubleProperties.put(key.substring(key.indexOf('_')-1), Double.valueOf((String) v));
			}
			else if(key.startsWith("s_")) {
				stringProperties.put(key.substring(key.indexOf('_')-1), (String) v);
			}
			else if(key.startsWith("e_")) {//TODO find some way to load enums dynamically
				key=key.substring(key.indexOf('_')-1);
				if(key.startsWith("Side_")) {
					enumProperties.put(key.substring(key.indexOf('_')-1), Side.valueOf((String) v));
				}
				else {
					System.err.println("Invalid enum data in properties file at: "+key+": "+ v);
					System.out.println("Invalid enum data will be ignored on next save");
				}
			}
			else {
				System.err.println("Invalid data in properties file at: "+key+": "+ v);
				System.out.println("Invalid data will be ignored on next save");
			}
		});
	}

	public void save() throws IOException {

		boolProperties.forEach( (k, v) -> {
			String key= "b_"+ k;
			String value = v.toString();
			properties.put(key, value);
		});
		intProperties.forEach( (k, v) -> {
			String key= "n_"+ k;
			String value = v.toString();
			properties.put(key, value);
		});
		doubleProperties.forEach( (k, v) -> {
			String key= "n_"+ k;
			String value = v.toString();
			properties.put(key, value);
		});
		stringProperties.forEach( (k, value) -> {
			String key= "s_"+ k;
			properties.put(key, value);
		});
		enumProperties.forEach( (k, v) -> {
			String key= "e_" + v.getClass().getSimpleName() + "_" + k;
			String value = v.toString();
			properties.put(key, value);
		});

		properties.store(new FileOutputStream(propertiesFile), "Configuration for tajetaje's DebateApp");
	}
}