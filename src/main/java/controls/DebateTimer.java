package main.java.controls;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.java.AppUtils;
import org.controlsfx.validation.ValidationSupport;

public class DebateTimer extends Region {

	private final Timeline  timerTimeline = new Timeline();
	private final TextField timerField    = new TextField();
	private final Button button = new Button();
	private final Label label = new Label();
	private final Node graphic;

	public final SimpleBooleanProperty timerRunningProperty = new SimpleBooleanProperty(false);

	public final int defaultTime;

	final ValidationSupport validationSupport = new ValidationSupport();

	public DebateTimer(Orientation orientation, String labelText, int defaultTime, Node graphic) {
		Pane box;

		this.graphic = graphic;
		if (orientation.equals(Orientation.VERTICAL)) {
			box = new VBox();
			((VBox) box).setAlignment(Pos.CENTER);
		} else if (orientation.equals(Orientation.HORIZONTAL)) {
			box = new HBox();
			((HBox) box).setAlignment(Pos.CENTER);
		} else
			throw new IllegalArgumentException("Parameter orientation must be either VERTICAL or HORIZONTAL");

		label.setText(labelText);
		label.setGraphic(graphic);

		this.defaultTime=defaultTime;

		validationSupport.registerValidator(timerField, false, AppUtils.timeValidator);
		button.disableProperty().bind(validationSupport.invalidProperty());

		resetTimer(defaultTime);

		box.getChildren().addAll(label, timerField, button);

		getChildren().setAll(box);
	}

	public void resetTimer(int seconds) {
		button.setText("Start");
		timerField.setEditable(true);
		button.setStyle("-fx-background-color: BAD77A;");

		timerField.setText(AppUtils.formatTime(seconds));
		timerTimeline.setCycleCount(Animation.INDEFINITE);

		timerTimeline.getKeyFrames().clear();
		timerTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
			// update timerLabel
			if(AppUtils.unFormatTime(timerField.getText()) < 1)
				endTimer();
			else
				timerField.setText(AppUtils.formatTime(AppUtils.unFormatTime(timerField.getText()) - 1));
		}));


		button.setOnAction(e -> {
			switch(timerTimeline.getStatus()) {
			case RUNNING:
				if(AppUtils.unFormatTime(timerField.getText()) < 1)
					break;
				pauseTimer();
				break;
			case STOPPED:
				if(AppUtils.unFormatTime(timerField.getText()) == 0)
					timerField.setText(AppUtils.formatTime(seconds));
				startTimer();
			case PAUSED:
				resumeTimer();
				break;
			default:
				AppUtils.showExceptionDialog(new IllegalStateException("Timeline is in an unhandled state."));
			}
		});
	}

	public void startTimer() {
		timerRunningProperty.set(true);
		timerField.setEditable(false);
		button.setStyle("-fx-background-color: B1382A;");
		timerTimeline.play();
		button.setText("Pause");
		resumeTimer();
	}

	public void resumeTimer() {
		timerRunningProperty.set(true);
		timerField.setEditable(false);
		button.setStyle("-fx-background-color: B1382A;");
		timerTimeline.play();
		button.setText("Pause");
	}

	public void pauseTimer() {
		timerRunningProperty.set(false);
		timerTimeline.pause();
		button.setStyle("-fx-background-color: BAD77A;");
		button.setText("Resume");
		timerField.setEditable(true);
	}

	public void endTimer() {
		timerRunningProperty.set(false);
		timerTimeline.stop();
		button.setText("Start");
		timerField.setEditable(true);
		((Pane) button.getParent()).setBackground(new Background(
						new BackgroundFill(Color.web("B1382A"), CornerRadii.EMPTY, Insets.EMPTY)));
		button.getParent().setOnMouseClicked(e -> {
			timerField.setText(AppUtils.formatTime(defaultTime));
			((Pane) button.getParent()).setBackground(Background.EMPTY);
			button.getParent().setOnMouseClicked(null);
			button.setStyle("-fx-background-color: BAD77A;");
		});
	}

	public Button getButton() {
		return button;
	}

	public Label getLabel() {
		return label;
	}

	public TextField getField() {
		return timerField;
	}

	public Node getGraphic() {
		return graphic;
	}
}
