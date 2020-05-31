package main.java.controls;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.java.AppUtils;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class DebateTimer extends Region {

	private final Timeline  timerTimeline = new Timeline();
	private final TextField timerField    = new TextField();
	private final Button    button        = new Button();

	private boolean timerRunning = false;
	public final BooleanExpression timerRunningProperty = new BooleanExpression() {
		@Override public void addListener(InvalidationListener invalidationListener) {}
		@Override public void removeListener(InvalidationListener invalidationListener) {}
		@Override public void addListener(ChangeListener<? super Boolean> changeListener) {}
		@Override public void removeListener(ChangeListener<? super Boolean> changeListener) {}
		@Override public boolean get() {
			return timerRunning;
		}
	};

	public final int defaultTime;

	ValidationSupport validationSupport = new ValidationSupport();

	public DebateTimer(Orientation orientation, String labelText, int defaultTime) {
		Pane box;
		if (orientation.equals(Orientation.VERTICAL)) {
			box = new VBox();
			((VBox) box).setAlignment(Pos.CENTER);
		} else if (orientation.equals(Orientation.HORIZONTAL)) {
			box = new HBox();
			((HBox) box).setAlignment(Pos.CENTER);
		} else
			throw new IllegalArgumentException("Parameter orientation must be either VERTICAL or HORIZONTAL");

		Label label = new Label(labelText);

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
		button.setStyle("-fx-background-color: lightgreen;");

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
		timerRunning = true;
		timerField.setEditable(false);
		button.setStyle("-fx-background-color: ff5555;");
		timerTimeline.play();
		button.setText("Pause");
		resumeTimer();
	}

	public void resumeTimer() {
		timerRunning = true;
		timerField.setEditable(false);
		button.setStyle("-fx-background-color: ff5555;");
		timerTimeline.play();
		button.setText("Pause");
	}

	public void pauseTimer() {
		timerRunning = false;
		timerTimeline.pause();
		button.setStyle("-fx-background-color: lightgreen;");
		button.setText("Resume");
		timerField.setEditable(true);
	}

	public void endTimer() {
		timerRunning = false;
		timerTimeline.stop();
		button.setText("Start");
		timerField.setEditable(true);
		((Pane) button.getParent()).setBackground(new Background(
						new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		button.getParent().setOnMouseClicked(e -> {
			timerField.setText(AppUtils.formatTime(defaultTime));
			((Pane) button.getParent()).setBackground(Background.EMPTY);
			button.getParent().setOnMouseClicked(null);
			button.setStyle("-fx-background-color: lightgreen;");
		});
	}
}
