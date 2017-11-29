/*package deprecated;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import core.Neuron;
import core.geometry.Point;
import core.geometry.Topology;
import utils.NetImage;
import utils.Settings;

public class KohonensNet {
	
	private Neuron[][] neurons;
	
	
	public KohonensNet(Topology topology, int images) {
		neurons = null;
		int arraySize = (int) Math.ceil(Math.sqrt(images));
		neurons = new Neuron[arraySize][arraySize];
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				neurons[i][j] = new Neuron();
			}
		}
	}
	
	

	public ArrayList<Point> feed(NetImage binaryImage) {
		if (binaryImage.binaryBytes.length != Settings.IMAGE_SIZE * Settings.IMAGE_SIZE) {
			System.out.println("Invalid size of feed array");
			return null;
		}
		
		TreeSet<SortingPoint> treeSet = new TreeSet<SortingPoint>();
		
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				neurons[i][j].feed(binaryImage.binaryBytes);
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
	
	public void eraseValues() {
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				neurons[i][j].value = 0;
			}
		}
	}
	
	public float distance(Point coords1, Point coords2) {
		return (float) Math.sqrt(
			Math.pow(coords1.x - coords2.x, 2) + Math.pow(coords1.y - coords2.y, 2)
		);
	}
	
	private static final float NEIGHBOURNESS_RADIUS = 1.41f;
	
	public void neighbourness(Neuron host, Point hostCoords, Neuron neighbour, Point neighbourCoords) {
		if (hostCoords.equals(neighbourCoords)) {
			return;
		}
		float distance = this.distance(hostCoords, neighbourCoords);
		float l1 = NEIGHBOURNESS_RADIUS - distance;
		if (l1 <= 0) {
			neighbour.value = 0;
		} else {
			neighbour.value = (distance * l1) / host.value;
		}
	}
	
	public float learningFunction(int loop, int allLoops) {
		return 1 - ((float) (loop - 1) / (float) allLoops);
	}
	
	public void modifyWithWinner(Point coords, byte[] input, int loop, int allLoops) {
		Neuron winner = this.neurons[coords.x][coords.y];
		//Nak³adanie sto¿ka
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				this.neighbourness(winner, coords, this.neurons[i][j], new Point(i, j));
			}
		}
		
		final int neighbournessDistance = (int) Math.floor(NEIGHBOURNESS_RADIUS);
		int startx = Math.max(0, coords.x - neighbournessDistance);
		int endx =  Math.max(0, coords.x + neighbournessDistance);
		int starty = Math.max(0, coords.y - neighbournessDistance);
		int endy = Math.max(0, coords.y + neighbournessDistance);
		
		//Modyfikacja wag
		for (int y = starty; y < endy; y++) {
			for (int x = startx; x < endx; x++) {
				if (coords.x == startx && coords.y == y) {
					continue;
				}
				Neuron neuron = this.neurons[x][y];
				for (int i = 0; i < neuron.weights.length; i++) {
					neuron.weights[i] += (neuron.value / winner.value) * learningFunction(loop, allLoops) * (input[i] - neuron.weights[i]);
				}
			}
		}
		
		//Normalizacja
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				builder.append("["+neurons[i][j].value+"] ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

}
*/