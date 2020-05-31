package main.java.controls;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import main.java.AppUtils;
import main.java.structures.DebateEvent;
import main.java.structures.Speech;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.util.ArrayList;

public class SpeechTimesDialog extends Dialog<String> {
	ArrayList<TextField> speechFields = new ArrayList<>();

	Region topSpacer = new Region();
	Region bottomSpacer = new Region();

	GridPane speechesGrid = new GridPane();
	VBox mainBox = new VBox(new Label("Fields in red are invalid and will not be saved"), topSpacer,
					new Separator(Orientation.HORIZONTAL), bottomSpacer, speechesGrid);

	ButtonType acceptButtonType = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
	ButtonType revertButtonType = new ButtonType("Revert", ButtonBar.ButtonData.CANCEL_CLOSE);
	ButtonType defaultButtonType =  new ButtonType("Default", ButtonBar.ButtonData.OTHER);
	ValidationSupport validationSupport = new ValidationSupport();

	public SpeechTimesDialog(DebateEvent debateEvent) {
		super();

		buildDialog(debateEvent);
	}

	private void buildDialog(DebateEvent debateEvent) {

		//Create dialog
		getDialogPane().getButtonTypes().setAll(acceptButtonType, revertButtonType, defaultButtonType);
		speechesGrid.setHgap(5);
		topSpacer.setPrefHeight(10);
		bottomSpacer.setPrefHeight(5);
		setHeaderText("Customize speech times");
		getDialogPane().setContent(mainBox);

		//fill the speeches box
		for(int i=0; i<debateEvent.getSpeeches().size(); i++) {
			Label label = new Label("Length of " + debateEvent.getSpeeches().get(i).getName());
			TextField field = new TextField();
			speechFields.add(i, field);
			field.setText(AppUtils.formatTime(debateEvent.getSpeeches().get(i).getTimeSeconds()));
			validationSupport.registerValidator(field, false, AppUtils.timeValidator);
			speechesGrid.add(label, 0, i);
			speechesGrid.add(field, 1, i);
		}

		getDialogPane().lookupButton(acceptButtonType).disableProperty().bind(validationSupport.invalidProperty());

		setResultConverter(dialogButton -> {
			//Cancel or something else
			if(acceptButtonType.equals(dialogButton)) { //Accept new times
				StringBuilder times = new StringBuilder();
				for(TextField speechField : speechFields) {
					times.append(AppUtils.unFormatTime(speechField.getText()));
					times.append(',');
				}
				return String.valueOf(times);
			} else if(defaultButtonType.equals(dialogButton)) { //Return to defaults
				return debateEvent.getDefaultTimes();
			}
			return debateEvent.getTimes(); //If neither of those, then give the original times (e.g. canceled or closed)
		});
	}
}
