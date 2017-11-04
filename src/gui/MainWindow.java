package gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.Classifier;
import utils.BinaryImage;
import utils.FeedProvider;
import utils.ImageUtils;
import utils.Settings;

public class MainWindow extends Application {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private ImageView imageToAnalyze;
	private ImageView imageFromAnalysis;
	private Button initializeNetButton;
	private Button learnNetButton;
	private Classifier classifier;
	
	private static final Image NOTHING_IMAGE;
	static {
		Image temp = null;
		try {
			temp = SwingFXUtils.toFXImage(ImageIO.read(new File("./nothing.bmp")), null);
		} catch (Throwable e) {
			temp = null;
		}
		NOTHING_IMAGE = temp;
	}
	
	@Override
	public void init() throws Exception {
		
		classifier = new Classifier();
		
		initializeNetButton = new Button("Initialize net");
		learnNetButton = new Button("Enable learing");
		
		learnNetButton.setOnMouseClicked((e)-> {
			if (!classifier.isNetInitialized()) {
				Alert alert = new Alert(AlertType.ERROR, "Net is not initialized.", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			disableNetButtons();
			LongTaskProgressBar.executeLongTask("Initialize net...", new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					classifier.learn(20);
					return null;
				}
				
			}, () -> {
				enableNetButtons();
				return null;
			});
		});
		initializeNetButton.setOnMouseClicked((e)-> {
			disableNetButtons();
			LongTaskProgressBar.executeLongTask("Initialize net...", new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					classifier.initNet();
					return null;
				}
			}, () -> {
				enableNetButtons();
				return null;
			});
		});
		
		imageToAnalyze = new ImageView(); 
		imageFromAnalysis = new ImageView();
		
		imageFromAnalysis.setPreserveRatio(false);
		imageFromAnalysis.setFitHeight(Settings.IMAGE_SIZE);
		imageFromAnalysis.setFitWidth(Settings.IMAGE_SIZE);
		imageFromAnalysis.setImage(NOTHING_IMAGE);
		
		imageToAnalyze.setPreserveRatio(false);
		imageToAnalyze.setFitHeight(Settings.IMAGE_SIZE);
		imageToAnalyze.setFitWidth(Settings.IMAGE_SIZE);
		imageToAnalyze.setImage(NOTHING_IMAGE);
		
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		VBox mainPane = new VBox(10);
		mainPane.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(1), null)));
		mainPane.setPadding(new Insets(20));
		HBox imagesBox = new HBox(20, 
			imageToAnalyze, 
			imageFromAnalysis
		);
		HBox buttonsBox = new HBox(10, 
			initializeNetButton,
			learnNetButton
		);
		mainPane.getChildren().addAll(imagesBox, buttonsBox);
		
		Scene scene = new Scene(new StackPane(mainPane), 500, 500);
		scene.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			}
		});
		scene.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				
					try {
						if (db.hasFiles()) {
							if (!classifier.isNetInitialized()) {
								Alert alert = new Alert(AlertType.ERROR, "Net is not initialized.", ButtonType.OK);
								alert.showAndWait();
								return;
							}
							File userFile = db.getFiles().get(0);
							imageToAnalyze.setImage(ImageUtils.load(userFile));
							
							File result = classifier.classify(new BinaryImage(FeedProvider.loadAndPreprocessImage(userFile.getAbsolutePath())));
							if (result != null) {
								imageFromAnalysis.setImage(ImageUtils.load(result));
							} else {
								imageFromAnalysis.setImage(NOTHING_IMAGE);
								Alert alert = new Alert(AlertType.ERROR, "Net could not find match.", ButtonType.OK);
								alert.showAndWait();
							}
						}
					} catch (Throwable e) {
						System.out.println("Bad image!");
					} finally {
						event.consume();
					}
			}
		});
		primaryStage.setScene(scene);
		primaryStage.setTitle("Kohonens classifier");
		primaryStage.setWidth(330);
		primaryStage.setHeight(220);
		primaryStage.show();
	}
	
	public void enableNetButtons() {
		this.initializeNetButton.setDisable(false);
		this.learnNetButton.setDisable(false);
	}
	
	public void disableNetButtons() {
		this.initializeNetButton.setDisable(true);
		this.learnNetButton.setDisable(true);
	}
	
	public static void main(String[] args) {
		MainWindow.launch(args);
	}

}
