package main.java.controls;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class DebateTimer extends Region {
	final Pane box;

	final Label label;
	final TextField timerField = new TextField();
	final Button button = new Button();

	ValidationSupport validationSupport = new ValidationSupport();

	public DebateTimer(Orientation orientation, String labelText) {
		//		timerField.setOnAction(e -> System.out.println(AppUtils.unFormattedTimeProperty(
		//						(StringBinding) timerField.textProperty().concat(""))));

		if (orientation.equals(Orientation.VERTICAL)) {
			box = new VBox();
			((VBox) box).setAlignment(Pos.CENTER);
		} else if (orientation.equals(Orientation.HORIZONTAL)) {
			box = new HBox();
			((HBox) box).setAlignment(Pos.CENTER);
		} else
			throw new IllegalArgumentException("Parameter orientation must be either VERTICAL or HORIZONTAL");

		this.label = new Label(labelText);

		validationSupport.registerValidator(timerField, true,
						Validator.createRegexValidator("Invalid time", "[0-9]?[0-9][0-9]?:[0-9]?[0-9][0-9]?",
										Severity.ERROR));

		this.getChildren().setAll(box);
		box.getChildren().addAll(label, timerField, button);
	}

	public void resetTimer(int seconds) {
		button.setText("Start");
	}

	public void startTimer() {

	}

	public void resumeTimer() {

	}

	public void pauseTimer() {

	}

	//	public void resetrTimer() {
	//		final String defaultText = "Start";
	//		button.setText(defaultText);
	//		field.setEditable(true);
	//		field.setTextFormatter(new TextFormatter<Integer>(timeFilter));
	//		field.textProperty().addListener((obs, oldText, newText) -> {
	//			if(newText.matches("[0-9]?[0-9][0-9]?:[0-9]?[0-9][0-9]?")) {
	//				field.setStyle(null);
	//			} else {
	//				field.setStyle("-fx-background-color: indianred;");
	//			}
	//		});
	//
	//		button.setStyle("-fx-background-color: lightgreen;");
	//
	//		// update timerLabel
	//		field.setText(AppUtils.formatTime(seconds));
	//		timeline.setCycleCount(Animation.INDEFINITE);
	//		// KeyFrame event handler
	//		timeline.getKeyFrames().clear();
	//		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
	//			// update timerLabel
	//			field.setText(AppUtils.formatTime(AppUtils.unFormatTime(field.getText()) - 1));
	//			if(AppUtils.unFormatTime(field.getText()) <= 0) {
	//				timeline.stop();
	//				button.setText(defaultText);
	//				field.setEditable(true);
	//				((Pane) button.getParent()).setBackground(new Background(
	//								new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
	//				button.getParent().setOnMouseClicked(e -> {
	//					field.setText(AppUtils.formatTime(seconds));
	//					((Pane) button.getParent()).setBackground(Background.EMPTY);
	//					button.getParent().setOnMouseClicked(null);
	//					button.setStyle("-fx-background-color: lightgreen;");
	//				});
	//			}
	//		}));
	//
	//		button.setOnAction(e -> {
	//			switch(timeline.getStatus()) {
	//			case RUNNING:
	//				if(AppUtils.unFormatTime(field.getText()) < 1)
	//					break;
	//				timeline.pause();
	//				button.setStyle("-fx-background-color: lightgreen;");
	//				button.setText("Resume");
	//				field.setEditable(true);
	//				break;
	//			case STOPPED:
	//				if(AppUtils.unFormatTime(field.getText()) == 0)
	//					field.setText(AppUtils.formatTime(seconds));
	//				field.setEditable(false);
	//				button.setStyle("-fx-background-color: ff5555;");
	//				timeline.play();
	//				button.setText("Pause");
	//			case PAUSED:
	//				field.setEditable(false);
	//				button.setStyle("-fx-background-color: ff5555;");
	//				timeline.play();
	//				button.setText("Pause");
	//				break;
	//			default:
	//				showExceptionDialog(new IllegalStateException("Timeline is in an unhandled state."), true);
	//			}
	//	s	});
	//	}
}
