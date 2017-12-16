package gui;

import java.io.File;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

import core.NetImage;
import core.geometry.Topology;
import core.nets.LineNet;
import core.nets.MeshNet;
import core.nets.Net;
import core.reductors.Reductors;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import utils.FeedProvider;
import utils.FeedProvider.Type;
import utils.ImageUtils;
import utils.Logger;
import utils.Settings;

public class MainWindow extends Application {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private ImageView imageToAnalyze;
	private ImageView imageFromAnalysis;
	private Button learnNetButton;
	private ComboBox<LearningCurve> learningCurvesBox;
	private ComboBox<Topology> topologies;
	private ComboBox<Reductors> reductors;
	private NumericInput radiusInput;
	private NumericInput loopsInput;
	private Net net;
	
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
		
		learnNetButton = new Button("Enable learing");
		learnNetButton.setOnMouseClicked(new EnableLearningClicked());
		
		learningCurvesBox = new ComboBox<>(FXCollections.observableArrayList(LearningCurve.values()));
		learningCurvesBox.getSelectionModel().selectFirst();
		
		topologies = new ComboBox<>(FXCollections.observableArrayList(Topology.values()));
		topologies.getSelectionModel().selectFirst();
		
		reductors = new ComboBox<>(FXCollections.observableArrayList(Reductors.values()));
		reductors.getSelectionModel().selectFirst();
		
		radiusInput = new NumericInput();
		radiusInput.setText("1");
		
		loopsInput = new NumericInput();
		loopsInput.setText("100");
		
		imageFromAnalysis = new ImageView();
		imageFromAnalysis.setPreserveRatio(false);
		imageFromAnalysis.setFitHeight(Settings.IMAGE_SIZE);
		imageFromAnalysis.setFitWidth(Settings.IMAGE_SIZE);
		imageFromAnalysis.setImage(NOTHING_IMAGE);
		
		imageToAnalyze = new ImageView(); 
		imageToAnalyze.setPreserveRatio(false);
		imageToAnalyze.setFitHeight(Settings.IMAGE_SIZE);
		imageToAnalyze.setFitWidth(Settings.IMAGE_SIZE);
		imageToAnalyze.setImage(NOTHING_IMAGE);
		
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		String borderCss = "-fx-border-color: red;\n" +
				"-fx-border-insets: 5;\n" +
				"-fx-border-width: 3;\n" +
				"-fx-border-style: dashed;\n";

		VBox analyzeImageBox = new VBox(0);
		HBox borderedAnalyzedImageBex = new HBox(imageToAnalyze);
		borderedAnalyzedImageBex.setStyle(borderCss);
		analyzeImageBox.getChildren().addAll(
			new Label("Analyzed image: "),
			borderedAnalyzedImageBex
		);
		
		VBox resultImageBox = new VBox(0);
		HBox borderedresultImageBox = new HBox(imageFromAnalysis);
		borderedresultImageBox.setStyle(borderCss);
		resultImageBox.getChildren().addAll(
			new Label("Analysis result image: "),
			borderedresultImageBox
		);
		
		HBox imagesBox = new HBox(20, 
			analyzeImageBox, 
			resultImageBox
		);
		VBox buttonsBox = new VBox(10, 
			new HBox(10, new Label("Learn: "), learnNetButton),
			new HBox(10, new Label("Choose learning curve: "), learningCurvesBox),
			new HBox(10, new Label("Choose net topology: "), topologies),
			new HBox(10, new Label("Choose reductor: "), reductors),
			new HBox(10, new Label("Choose neighbourness radius: "), radiusInput),
			new HBox(10, new Label("Choose amount of learning loops: "), loopsInput)
		);
		
		VBox mainPane = new VBox(10);
		mainPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(1), null)));
		mainPane.setPadding(new Insets(20));
		mainPane.getChildren().addAll(imagesBox, buttonsBox);
		
		Scene scene = new Scene(new StackPane(mainPane), 500, 500);
		scene.setOnDragOver(new DragOver());
		scene.setOnDragDropped(new DragDropped());
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Kohonens classifier");
		primaryStage.setWidth(400);
		primaryStage.setHeight(430);
		primaryStage.show();
	}
	
	public void enableNetButtons() {
		this.learnNetButton.setDisable(false);
	}
	
	public void disableNetButtons() {
		this.learnNetButton.setDisable(true);
	}
	
	private final class EnableLearningClicked implements EventHandler<Event> {
		@Override
		public void handle(Event event) {
			disableNetButtons();
			LongTaskProgressBar.executeLongTask("Initializing net...", new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					HashSet<NetImage> feed = FeedProvider.loadFeed(Type.MANDATORY);
					switch (topologies.getValue()) {
						case LINE:
							net = new LineNet(feed.toArray(new NetImage[0]));
							break;
						case MESH:
							net = new MeshNet(feed.toArray(new NetImage[0]));
							break;
					}
					net.setReductor(reductors.getValue().getReductor());
					net.setNeighbournessRadius(radiusInput.getFloat());
					net.setLearningLoops((int) loopsInput.getFloat());
					net.init();
					net.learn();
					return null;
				}
			}, () -> {
				enableNetButtons();
				return null;
			});
		}
	}

	private final class DragDropped implements EventHandler<DragEvent> {
		@Override
		public void handle(DragEvent event) {
			Dragboard db = event.getDragboard();
			try {
				if (db.hasFiles()) {
					if (db.getFiles().get(0).getCanonicalPath().endsWith(".net")) {
						net = new MeshNet(FeedProvider.loadFeed().toArray(new NetImage[0]));
						net.load(db.getFiles().get(0).getCanonicalPath());
						Alerts.info("Net successfully loaded.");
						return;
					}
					if (net == null) {
						Alerts.error("Net is not initialized");
						return;
					}
					
					File userFile = db.getFiles().get(0);
					Image userImage = ImageUtils.load(userFile);
					if (userImage == null) {
						Alerts.error("Problem with image loading");
						return;
					}
					imageToAnalyze.setImage(userImage);
					
					NetImage result = net.classify(FeedProvider.load(userFile));
					if (result != null) {
						imageFromAnalysis.setImage(result.sourceImage);
					} else {
						imageFromAnalysis.setImage(NOTHING_IMAGE);
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
				Logger.log("Bad image!");
			} finally {
				event.consume();
			}
		}
	}

	private final class DragOver implements EventHandler<DragEvent> {
		@Override
		public void handle(DragEvent event) {
			Dragboard db = event.getDragboard();
			if (db.hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}
		}
	}

}
