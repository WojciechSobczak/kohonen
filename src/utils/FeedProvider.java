package utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FeedProvider {
	
	public static HashMap<BinaryImage, File> loadFeed() throws IOException {
		System.out.println("Loading files...");
		HashMap<BinaryImage, File> binaryImages = new  HashMap<BinaryImage, File>();
		List<String> paths = new LinkedList<>();
		Files.walkFileTree(Paths.get("./feed/"), new SimpleFileVisitor<Path>() {
			private int i = 0;
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes bfa) throws IOException {
				paths.add(path.toFile().getCanonicalPath());
				if (++i == 19) {
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}
		});
		
		try {
			for (String string : paths) {
				binaryImages.put(new BinaryImage(loadAndPreprocessImage(string)), new File(string));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Files loaded.");
		return binaryImages;
	}
	
	/**
	 * Tests method, can be erased
	 * @return
	 */
	public static Mat getOne() {
		return loadAndPreprocessImage("./feed/nakazu/nakaz (1).png");
	}
	
	
	public static Mat loadAndPreprocessImage(String path) {
		System.out.println("Loading: " + path);
		Mat image = Imgcodecs.imread(path);
		Imgproc.resize(image, image, new Size(Settings.IMAGE_SIZE, Settings.IMAGE_SIZE));
		Mat greyImage = new Mat(image.size(), image.type());
		Imgproc.cvtColor(image, greyImage, Imgproc.COLOR_BGR2GRAY);
		//Imgproc.blur(greyImage, greyImage, new Size(3, 3));
		
		double lowerTreshold = 1;
		double higherTreshold = 3;
		Mat detectedEdges = new Mat();
		Imgproc.Canny(greyImage, detectedEdges, lowerTreshold, higherTreshold);
		
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(detectedEdges, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		return detectedEdges;
	}

}
