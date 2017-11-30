package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import core.Neuron;
import core.geometry.NetImage;
import core.geometry.Point;
import core.learningfunctions.LearningFunction;
import core.nets.Net.SortingPoint;

public class Utils {
	
	public static ArrayList<Point> convert(TreeSet<SortingPoint> sortingPoints) {
		ArrayList<Point> points = new ArrayList<Point>(sortingPoints.size());
		for (SortingPoint sortingPoint : sortingPoints) {
			points.add(sortingPoint.point);
		}
		Collections.reverse(points);
		return points;
	}
	
	public static void addWithSizeControl(TreeSet<SortingPoint> sortingPoints, SortingPoint add, int size) {
		sortingPoints.add(add);
		if (sortingPoints.size() > 10) {
			sortingPoints.pollFirst();
		}
	}

	public static void normalize(Neuron winner) {
		float max = Float.MIN_VALUE;
		for (int i = 0; i < winner.weights.length; i++) {
			if (winner.weights[i] > max) {
				max = winner.weights[i];
			}
		}
		for (int i = 0; i < winner.weights.length; i++) {
			winner.weights[i] /= max;
		}
	}
	
	public static void modifyWeights(LearningFunction learningFunction, int currentLoop, int allLoops, NetImage input, Neuron winner, Neuron neuron) {
		for (int i = 0; i < neuron.weights.length; i++) {
			neuron.weights[i] += (neuron.value / winner.value) * learningFunction.learningCurve(currentLoop, allLoops) * (input.binaryBytes[i] - neuron.weights[i]);
		}
	}

}
