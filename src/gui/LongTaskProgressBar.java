package gui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LongTaskProgressBar {
	private final Stage dialogStage;
	private final ProgressBar progressBar;
	
	public static void executeLongTask(String labelText, Task<?> task, Callable<Void> onSuccess) {
		 new LongTaskProgressBar(labelText, task, onSuccess);
	}
	

	private LongTaskProgressBar(String labelText, Task<?> task, Callable<Void> onSuccess) {
		
		progressBar = new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS);
		dialogStage = new Stage();
		dialogStage.initStyle(StageStyle.UTILITY);
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.APPLICATION_MODAL);

		Label label = new Label();
		label.setText(labelText);

		progressBar.setProgress(-1F);

		HBox hbox = new HBox();
		hbox.setSpacing(5);
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().addAll(progressBar);
		
		progressBar.progressProperty().bind(task.progressProperty());
		task.setOnSucceeded((e) -> {
			dialogStage.hide();
			try {
				onSuccess.call();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		task.setOnFailed((e) -> {
			task.getException().printStackTrace();
		});

		Scene scene = new Scene(hbox);
		dialogStage.setScene(scene);
		dialogStage.show();
		Thread t = new Thread(task);
		t.start();
	}

}