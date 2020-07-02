package main.java.controls;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import main.java.AppUtils;
import main.java.structures.AppSettings;
import main.java.structures.DebateEvent;
import main.java.structures.Layout;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

public class SettingsEditor extends PropertySheet {

	private final Dialog<Void> dialog = new Dialog<>();

	public SettingsEditor(AppSettings settings) {

		setMode(Mode.NAME);
		setModeSwitcherVisible(false);
		setSearchBoxVisible(false);

		getItems().add(buildBooleanItem("Save on Exit", "Automatically save every time the app closes",
						settings.saveOnExit));
		getItems().add(buildBooleanItem("Show editor toolbars", "Display the toolbar above the flow editor",
						settings.toolbarsVisibleProperty));
		getItems().add(buildBooleanItem("Show \"No Update\" message",
						"Display a message on startup if the installed version of DebateApp is the latest",
						settings.showNoUpdateMessage));

		getItems().add(buildDoubleItem("Default Height", "Width the app defaults to when it opens (1 for auto)",
						settings.defaultHeight));
		getItems().add(buildDoubleItem("Default Width", "Height the app defaults to when it opens (1 for auto)",
						settings.defaultWidth));

		getItems().add(buildEnumItem("Default layout", "The layout to use when opening the app", settings.defaultLayout,
						Layout.class));

		getItems().add(new Item() {
			@Override public Class<?> getType() {
				return DebateEvent.class;
			}

			@Override public String getCategory() {
				return "General";
			}

			@Override public String getName() {
				return "Default Event";
			}

			@Override public String getDescription() {
				return "Select the event to show by default when opening the app";
			}

			@Override public Object getValue() {
				return settings.defaultEvent.getValue();
			}

			@Override public void setValue(Object value) {
				settings.defaultEvent.setValue((DebateEvent) value);
			}

			@Override public Optional<ObservableValue<?>> getObservableValue() {
				return Optional.of(settings.defaultEvent);
			}
		});
		setPropertyEditorFactory(new CustomPropertyEditorFactory(settings));

		//Set up dialog
		dialog.getDialogPane().setContent(this);
		dialog.setContentText(null);
		dialog.setHeaderText(null);
		dialog.setTitle("Settings");
		dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
		dialog.setResizable(true);
		dialog.getDialogPane().setMinWidth(400);//350 is the width of a row in the editor, may need to tweak this later
		dialog.getDialogPane().setMinHeight(42 * getItems()
						.size());//Aside from the meaning of life the universe and everything, 42 happens to be the height of a single row
	}

	private PropertySheet.Item buildIntegerItem(String name, String description, SimpleIntegerProperty property){
		return new Item() {
			@Override public Class<?> getType() {
				return Integer.class;
			}

			@Override public String getCategory() {
				return "General";
			}

			@Override public String getName() {
				return name;
			}

			@Override public String getDescription() {
				return description;
			}

			@Override public Object getValue() {
				return property.getValue();
			}

			@Override public void setValue(Object value) {
				AppUtils.logger.info(
								"Value of " + getName() + " updated from \"" + getValue() + "\" to \" " + value + '\"');
				property.setValue((Integer) value);
			}

			@Override public Optional<ObservableValue<?>> getObservableValue() {
				return Optional.of(property);
			}
		};
	}

	private PropertySheet.Item buildDoubleItem(String name, String description, SimpleDoubleProperty property){
		return new Item() {
			@Override public Class<?> getType() {
				return Double.class;
			}

			@Override public String getCategory() {
				return "General";
			}

			@Override public String getName() {
				return name;
			}

			@Override public String getDescription() {
				return description;
			}

			@Override public Object getValue() {
				return property.getValue();
			}

			@Override public void setValue(Object value) {
				AppUtils.logger.info(
								"Value of " + getName() + " updated from \"" + getValue() + "\" to \" " + value + '\"');
				property.setValue((Double) value);
			}

			@Override public Optional<ObservableValue<?>> getObservableValue() {
				return Optional.of(property);
			}
		};
	}

	private PropertySheet.Item buildBooleanItem(String name, String description, SimpleBooleanProperty property){
		return new Item() {
			@Override public Class<?> getType() {
				return Boolean.class;
			}

			@Override public String getCategory() {
				return "General";
			}

			@Override public String getName() {
				return name;
			}

			@Override public String getDescription() {
				return description;
			}

			@Override public Object getValue() {
				return property.getValue();
			}

			@Override public void setValue(Object value) {
				AppUtils.logger.info(
								"Value of " + getName() + " updated from \"" + getValue() + "\" to \" " + value + '\"');
				property.setValue((Boolean) value);
			}

			@Override public Optional<ObservableValue<?>> getObservableValue() {
				return Optional.of(property);
			}
		};
	}

	private PropertySheet.Item buildStringItem(String name, String description, SimpleStringProperty property){
		return new Item() {
			@Override public Class<?> getType() {
				return String.class;
			}

			@Override public String getCategory() {
				return "General";
			}

			@Override public String getName() {
				return name;
			}

			@Override public String getDescription() {
				return description;
			}

			@Override public Object getValue() {
				return property.getValue();
			}

			@Override public void setValue(Object value) {
				AppUtils.logger.info(
								"Value of " + getName() + " updated from \"" + getValue() + "\" to \" " + value + '\"');
				property.setValue((String) value);
			}

			@Override public Optional<ObservableValue<?>> getObservableValue() {
				return Optional.of(property);
			}
		};
	}

	private <T extends Enum<T>> PropertySheet.Item buildEnumItem(String name, String description, SimpleObjectProperty<T> property, Class<T> enumType){
		return new Item() {
			@Override public Class<?> getType() {
				return enumType;
			}

			@Override public String getCategory() {
				return "General";
			}

			@Override public String getName() {
				return name;
			}

			@Override public String getDescription() {
				return description;
			}

			@Override public Object getValue() {
				return property.getValue();
			}

			@Override public void setValue(Object value) {
				AppUtils.logger.info(
								"Value of " + getName() + " updated from \"" + getValue() + "\" to \" " + value + '\"');
				property.setValue(enumType.cast(value));
			}

			@Override public Optional<ObservableValue<?>> getObservableValue() {
				return Optional.of(property);
			}
		};
	}

	public Dialog<Void> getDialog() {
		return dialog;
	}
}

class CustomPropertyEditorFactory extends DefaultPropertyEditorFactory {
	private final AppSettings settings;
	public CustomPropertyEditorFactory(AppSettings settings) {
		this.settings = settings;
	}

	@Override public PropertyEditor<?> call(PropertySheet.Item item) {
		Class<?> type = item.getType();

		if (item.getPropertyEditorClass().isPresent()) {
			Optional<PropertyEditor<?>> ed = Editors.createCustomEditor(item);
			if (ed.isPresent()) return ed.get();
		}

		if(type == DebateEvent.class) {
			return Editors.createChoiceEditor(item, settings.debateEvents.getEvents());
		}

		if (/*type != null &&*/ type == String.class) {
			return Editors.createTextEditor(item);
		}

		if (/*type != null &&*/ type.getSuperclass() == Number.class) {
			return Editors.createNumericEditor(item);
		}

		if (/*type != null &&*/(type == boolean.class || type == Boolean.class)) {
			return Editors.createCheckEditor(item);
		}

		if (/*type != null &&*/type == LocalDate.class) {
			return Editors.createDateEditor(item);
		}

		if (/*type != null &&*/type == Color.class || type == Paint.class) {
			return Editors.createColorEditor(item);
		}

		if (type.isEnum()) {
			return Editors.createChoiceEditor(item, Arrays.<Object>asList(type.getEnumConstants()));
		}

		if (/*type != null &&*/type == Font.class) {
			return Editors.createFontEditor(item);
		}

		return null;
	}
}