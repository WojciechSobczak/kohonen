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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import core.NetImage;

public class FeedProvider {
	
	public static enum Type {
		MANDATORY, BAN, WARNING, ALL;
	}
	
	public static HashSet<NetImage> loadFeed(Type type) throws IOException {
		Logger.log("Loading files...");
		HashSet<NetImage> binaryImages = new HashSet<NetImage>();
		List<String> paths = new LinkedList<>();
		
		Files.walkFileTree(Paths.get("./feed/"), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes bfa) throws IOException {
				boolean include = true;
				switch (type) {
					case MANDATORY:
						if (!path.toFile().getName().startsWith("nakaz")) {
							include = false;
						}
						break;
					case WARNING:
						if (!path.toFile().getName().startsWith("ostrzezenie")) {
							include = false;
						}
						break;
					case BAN:
						if (!path.toFile().getName().contains("zakaz")) {
							include = false;
						}
						break;
					case ALL:
					default:
						break;
				}
				if (include) {
					binaryImages.add(new NetImage(path.toFile(), ImageUtils.load(path.toFile()), loadAndPreprocessImage(path.toFile().getCanonicalPath())));
				}
				return FileVisitResult.CONTINUE;
			}
		});
		
		Logger.log("Files loaded.");
		return binaryImages;
	}
	
	public static HashMap<String, List<NetImage>> loadBrokenFeed() throws IOException {
		HashMap<String, List<NetImage>> binaryImages = new HashMap<String, List<NetImage>>();
		Files.walkFileTree(Paths.get("./brokenfeed/"), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes bfa) throws IOException {
				String folderName = path.toFile().getParentFile().getName();
				if (!binaryImages.containsKey(folderName)) {
					binaryImages.put(folderName, new ArrayList<>());
				}
				
				binaryImages.get(folderName).add(new NetImage(path.toFile(), ImageUtils.load(path.toFile()), loadAndPreprocessImage(path.toFile().getCanonicalPath())));
				return FileVisitResult.CONTINUE;
			}
		});
		for (Entry<String, List<NetImage>> entry : binaryImages.entrySet()) {
			entry.getValue().sort(new Comparator<NetImage>() {
				@Override
				public int compare(NetImage o1, NetImage o2) {
					return o1.fileName.compareTo(o2.fileName);
				}
			});
		}
		Logger.log("Files loaded.");
		return binaryImages;
	}
	
	public static HashSet<NetImage> loadFeed() throws IOException {
		return loadFeed(Type.ALL);
	}
	
	/**
	 * Tests method, can be erased
	 * @return
	 */
	public static Mat getOne() {
		return loadAndPreprocessImage("./feed/nakazu/nakaz (1).png");
	}
	
	public static NetImage load(File file) throws IOException {
		return new NetImage(file, ImageUtils.load(file), loadAndPreprocessImage(file.getCanonicalPath()));
	}
	
	public static Mat preprocessImage(Mat image) {
		Imgproc.resize(image, image, new Size(Settings.IMAGE_SIZE, Settings.IMAGE_SIZE));
		Mat greyImage = new Mat(image.size(), image.type());
		Imgproc.cvtColor(image, greyImage, Imgproc.COLOR_BGR2GRAY);
		double lowerTreshold = 1;
		double higherTreshold = 3;
		Mat detectedEdges = new Mat();
		Imgproc.Canny(greyImage, detectedEdges, lowerTreshold, higherTreshold);
		
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(detectedEdges, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		return detectedEdges;
	}
	
	public static Mat loadAndPreprocessImage(String path) {
		Logger.log("Loading: " + path);
		return preprocessImage(Imgcodecs.imread(path));
	}

}
