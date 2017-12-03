package core.nets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import core.NetImage;
import core.geometry.Point;
import core.geometry.Topology;
import core.learningfunctions.LearningFunction;
import core.learningfunctions.LineLearningFunction;
import core.reductors.ConeReductor;
import core.reductors.Reductor;
import javafx.scene.image.Image;

public abstract class Net {
	
	protected HashMap<Point, NetImage> queses;
	protected float neighbournessRadius = 1;
	protected Reductor reductor;
	protected LearningFunction learningFunction;
	protected NetImage[] feed;
	protected int learningLoops;
	
	public abstract void offload(String path) throws FileNotFoundException, IOException;
	public abstract void load(String path) throws FileNotFoundException, IOException;
	
	public static class SortingPoint implements Comparable<SortingPoint> {
		public float value;
		public Point point;
		public SortingPoint(float value, Point point) {
			this.value = value;
			this.point = point;
		}
		@Override
		public int compareTo(SortingPoint s) {
			return Float.compare(value, s.value);
		}
		@Override
		public String toString() {
			return "SortingPoint [value=" + value + ", point=" + point + "]";
		}
		
	}
	
	public Net(NetImage[] feed) {
		this.queses = new HashMap<>();
		this.reductor = new ConeReductor();
		this.learningFunction = new LineLearningFunction();
		this.feed = feed;
	}
	
	public void learn() {
		ArrayList<NetImage> netImages = new ArrayList<NetImage>(Arrays.asList(feed));
		for (int i = 0; i < learningLoops; i++) {
			for (NetImage netImage : netImages) {
				ArrayList<Point> winners = this.feed(netImage);
				this.reduce(netImage, winners, i);
			}
			if (i != learningLoops - 1) {
				this.queses.clear();
			}
			Collections.shuffle(netImages);
		}
	}
	
	
	/**
	 * initalizing required elements of the net
	 */
	public abstract void init();
	
	/**
	 * feeds net and returns list of winner points
	 * @param image
	 */
	public abstract ArrayList<Point> feed(NetImage image);
	
	
	/**
	 * Making the reduction
	 * @param image
	 * @param winner
	 * @param currentLoop
	 */
	public abstract void reduce(NetImage image, ArrayList<Point> winners, int currentLoop);
	
	/**
	 * giving image based on learning
	 * @param binaryImage
	 * @return
	 */
	public NetImage classify(NetImage binaryImage) {
		ArrayList<Point> winners = this.feed(binaryImage);
		return this.queses.get(winners.get(0));
	}
	
	public NetImage classifyNet(NetImage binaryImage) {
		ArrayList<Point> winners = this.feed(binaryImage);
		return this.queses.get(winners.get(0));
	}
	
	public abstract Topology getTopology();
	public abstract void cleanValues();
	
	/**
	 * Method will emerge winner and add it to queses map
	 * @param winners
	 * @param netImage
	 * @return
	 */
	protected Point emergeWinner(ArrayList<Point> winners, NetImage netImage) {
		for (Point point : winners) {
			if (!queses.containsKey(point)) {
				this.queses.put(point, netImage);
				return point;
			}
		}
		return null;
	}
	
	public float getNeighbournessRadius() {
		return neighbournessRadius;
	}
	public void setNeighbournessRadius(float neighbournessRadius) {
		this.neighbournessRadius = neighbournessRadius;
	}
	public Reductor getReductor() {
		return reductor;
	}
	public void setReductor(Reductor reductor) {
		this.reductor = reductor;
	}
	public LearningFunction getLearningFunction() {
		return learningFunction;
	}
	public void setLearningFunction(LearningFunction learningFunction) {
		this.learningFunction = learningFunction;
	}

	public int getLearningLoops() {
		return learningLoops;
	}

	public void setLearningLoops(int learningLoops) {
		this.learningLoops = learningLoops;
	}

}
