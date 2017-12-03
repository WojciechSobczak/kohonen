import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import core.NetImage;
import core.learningfunctions.HiperbolicLearningFunction;
import core.learningfunctions.LearningFunction;
import core.learningfunctions.LineLearningFunction;
import core.learningfunctions.SquareLearningFunction;
import core.nets.LineNet;
import core.nets.MeshNet;
import core.nets.Net;
import core.reductors.ConeReductor;
import core.reductors.Reductor;
import javafx.scene.image.Image;
import utils.FeedProvider;

public class Tests {
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	public static final String NET_OFFLOAD = "C:\\Users\\Lama\\Desktop\\best.net";
	
	public static class Result implements Comparable<Result> {
		float res;
		Reductor reductor;
		LearningFunction learningFunction;
		float radius;
		int loopsAmount;
		Net net;
		
		@Override
		public int compareTo(Result o) {
			return Float.compare(o.res, res);
		}
		
		public Result(Net net, float res, Reductor reductor, LearningFunction learningFunctiom, float radiuse, int loopsAmount) {
			super();
			this.net = net;
			this.res = res;
			this.reductor = reductor;
			this.learningFunction = learningFunctiom;
			this.radius = radiuse;
			this.loopsAmount = loopsAmount;
		}
		
		@Override
		public String toString() {
			return "Effectivness: " + res + ", Net: " + net + ", Reductor: " + reductor + ", Function: " + learningFunction + ", Radius: " + radius + ", Loops: " + loopsAmount;
		}
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		HashSet<NetImage> feed = FeedProvider.loadFeed(FeedProvider.Type.MANDATORY);
		Reductor[] reductors = {new ConeReductor()};
		LearningFunction[] learningFunctions = {new LineLearningFunction(), new HiperbolicLearningFunction(), new SquareLearningFunction()};
		float[] radiuses = {1};//, 2, 3, 4, 5};//, 6, 7, 8, 9, 10};
		int[] loops = {10, 50};//, 100, 200, 500, 1000};//, 2000, 5000};
		Net[] nets = {new LineNet(feed.toArray(new NetImage[0])), new MeshNet(feed.toArray(new NetImage[0]))};
		System.out.println("------- PARAMETERS TESTS --------");
		ArrayList<Result> results = parametersOptimizer(feed, reductors, learningFunctions, radiuses, loops, nets);
		Net bestNet = results.get(0).net;
		bestNet.offload(NET_OFFLOAD);
		//Net bestNet = new MeshNet(feed.toArray(new NetImage[0]));
		//bestNet.load(NET_OFFLOAD);
		System.out.println("------- BLUR TESTS --------");
		blurTest(feed, bestNet);
		System.out.println("------- DIRTY TESTS --------");
		dirtyTest(bestNet);
	}
	
	private static void dirtyTest(Net bestNet) throws IOException {
		HashMap<String, List<NetImage>> brokenFeed = FeedProvider.loadBrokenFeed();
		HashSet<NetImage> mandatories = FeedProvider.loadFeed(FeedProvider.Type.MANDATORY);
		String[] categories = {"Snowball", "Random text", "Snow", "Confusing element"};
		float[] results = new float[brokenFeed.entrySet().iterator().next().getValue().size()]; 
		for (NetImage netImage : mandatories) {
			List<NetImage> images = brokenFeed.get(netImage.fileName);
			int brokenIndex = 0;
			for (NetImage brokenImage : images) {
				NetImage classifiedImage = bestNet.classify(brokenImage);
				if (classifiedImage != null && classifiedImage.equals(netImage)) {
					results[brokenIndex]++;
				}
				brokenIndex++;
			}
		}
		
		for (int i = 0; i < categories.length; i++) {
			System.out.println(categories[i] + " effectivness:" + (results[i] / mandatories.size()));
		}
	}

	private static void blurTest(HashSet<NetImage> feed, Net bestNet) {
		for (int j = 1; j <= 10; j++) {
			float correctQuesses = 0;
			for (NetImage netImage : feed) {
				Mat mat = FeedProvider.loadAndPreprocessImage(netImage.canonicalPath);
				Imgproc.blur(mat, mat, new Size(j, j));
				NetImage blurred = new NetImage(new File(netImage.canonicalPath), netImage.sourceImage, mat);
				NetImage classifiedImage = bestNet.classify(blurred);
				if (classifiedImage != null && classifiedImage.equals(netImage)) {
					correctQuesses++;
				}
			}
			System.out.println("For blur matrix size: " + j + ", effectivness: " + (correctQuesses / feed.size()));
		}
	}

	private static ArrayList<Result> parametersOptimizer(HashSet<NetImage> feed, Reductor[] reductors, LearningFunction[] learningFunctions, float[] radiuses, int[] loops, Net[] nets) {
		ArrayList<Result> results = new ArrayList<>();
		for (int i = 0; i < loops.length; i++) {
			for (int j = 0; j < radiuses.length; j++) {
				for (LearningFunction learningFunction : learningFunctions) {
					for (Reductor reductor : reductors) {
						for (Net net : nets) {
							net.setReductor(reductor);
							net.setNeighbournessRadius(radiuses[j]);
							net.setLearningLoops(loops[i]);
							net.init();
							net.learn();
							float correctQuesses = 0;
							for (NetImage netImage : feed) {
								if (net.classifyNet(netImage).equals(netImage)) {
									correctQuesses++;
								}
							}
							System.out.println("-------------------------------");
							System.out.println("For:");
							System.out.println(" - net: " + net);
							System.out.println(" - loops: " + loops[i]);
							System.out.println(" - radius: " + radiuses[j]);
							System.out.println(" - learningFunction: " + learningFunction);
							System.out.println(" - reductor: " + reductor);
							System.out.println("Effectiveness  is: " + (correctQuesses / feed.size()));
							System.out.println("-------------------------------");
							results.add(new Result(net, (correctQuesses / feed.size()), reductor, learningFunction, radiuses[j], loops[i]));
						}
					}
				}
			}
		}
		
		Collections.sort(results);
		System.out.println();
		System.out.println("RESULTS:");
		for (Result result : results) {
			System.out.println(result);
		}
		return results;
	}
	
}
