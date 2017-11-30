import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.opencv.core.Core;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import core.geometry.NetImage;
import core.learningfunctions.HiperbolicLearningFunction;
import core.learningfunctions.LearningFunction;
import core.learningfunctions.LineLearningFunction;
import core.learningfunctions.SquareLearningFunction;
import core.nets.LineNet;
import core.nets.MeshNet;
import core.nets.Net;
import core.reductors.ConeReductor;
import core.reductors.Reductor;
import utils.FeedProvider;

public class Tests {
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
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
	
	public static ArrayList<Result> results = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
		Reductor[] reductors = {new ConeReductor()};
		LearningFunction[] learningFunctions = {new LineLearningFunction(), new HiperbolicLearningFunction(), new SquareLearningFunction()};
		float[] radiuses = {1, 2, 3, 4, 5};//, 6, 7, 8, 9, 10};
		int[] loopsAmount = {10, 50, 100, 200, 500, 1000};//, 2000, 5000};
		
		System.out.println("-------TESTS--------");
		System.out.println("Loading feed.");
		HashSet<NetImage> feed = FeedProvider.loadFeed();
		
		for (int i = 0; i < loopsAmount.length; i++) {
			for (int j = 0; j < radiuses.length; j++) {
				for (LearningFunction learningFunction : learningFunctions) {
					for (Reductor reductor : reductors) {
						check(new LineNet(feed.toArray(new NetImage[0])), reductor, learningFunction, radiuses[j], loopsAmount[i], feed);
						check(new MeshNet(feed.toArray(new NetImage[0])), reductor, learningFunction, radiuses[j], loopsAmount[i], feed);
					}
				}
			}
		}
		
		java.util.Collections.sort(results);
		System.out.println();
		System.out.println("RESULTS:");
		for (Result result : results) {
			System.out.println(result);
		}
		
	}
	
	public static void check(Net net, Reductor reductor, LearningFunction learningFunction, float radius, int loops, HashSet<NetImage> feed) {
		net.setReductor(reductor);
		net.setNeighbournessRadius(radius);
		net.setLearningLoops(loops);
		net.init();
		net.learn();
		float feedSize = feed.size();
		float correctQuesses = 0;
		for (NetImage netImage : feed) {
			if (net.classifyNet(netImage).equals(netImage)) {
				correctQuesses++;
			}
		}
		System.out.println("-------------------------------");
		System.out.println("For:");
		System.out.println(" - net: " + net);
		System.out.println(" - loops: " + loops);
		System.out.println(" - radius: " + radius);
		System.out.println(" - learningFunction: " + learningFunction);
		System.out.println(" - reductor: " + reductor);
		System.out.println("Effectiveness  is: " + (correctQuesses / feedSize));
		System.out.println("-------------------------------");
		results.add(new Result(net, (correctQuesses / feedSize), reductor, learningFunction, radius, loops));
	}

}
