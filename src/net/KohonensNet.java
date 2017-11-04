package net;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import utils.BinaryImage;
import utils.Point;
import utils.Settings;

public class KohonensNet {
	
	private Neuron[][] neurons;
	public KohonensNet() {
		neurons = null;
		neurons = new Neuron[Settings.IMAGE_SIZE][Settings.IMAGE_SIZE];
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				neurons[i][j] = new Neuron();
			}
		}
	}
	
	private class SortingPoint implements Comparable<SortingPoint>{
		float value;
		Point point;
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

	public ArrayList<Point> feed(BinaryImage binaryImage) {
		if (binaryImage.bytes.length != Settings.IMAGE_SIZE * Settings.IMAGE_SIZE) {
			System.out.println("Invalid size of feed array");
			return null;
		}
		
		TreeSet<SortingPoint> treeSet = new TreeSet<SortingPoint>();
		
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				neurons[i][j].feed(binaryImage.bytes);
				treeSet.add(new SortingPoint(neurons[i][j].value, new Point(i, j)));
				if (treeSet.size() > 10) {
					treeSet.pollFirst();
				}
			}
		}
		
		ArrayList<Point> points = new ArrayList<Point>(treeSet.size());
		for (SortingPoint sortingPoint : treeSet) {
			points.add(sortingPoint.point);
		}
		Collections.reverse(points);
		return points;
	}
	
	public void modifyWinners(Point coords, int loop, int allLoops) {
		neurons[coords.x][coords.y].modifyAsWinner(loop, allLoops);
	}
	
	public void eraseValues() {
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				neurons[i][j].value = 0;
			}
		}
	}
	
	public Point classify(BinaryImage binaryImage) {
		return new Point(0, 0);
	}

}
