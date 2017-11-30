package core.nets;

import java.util.ArrayList;
import java.util.TreeSet;

import core.Neuron;
import core.geometry.NetImage;
import core.geometry.Point;
import core.geometry.Topology;
import utils.Logger;
import utils.Settings;
import utils.Utils;

public class MeshNet extends Net {
	
	private Neuron[][] neurons;

	public MeshNet(NetImage[] feed) {
		super(feed);
	}

	@Override
	public void init() {
		int arraySize = (int) Math.ceil(Math.sqrt(feed.length));
		this.neurons = new Neuron[arraySize][arraySize];
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				neurons[i][j] = new Neuron();
			}
		}
	}

	@Override
	public ArrayList<Point> feed(NetImage image) {
		TreeSet<SortingPoint> winners = new TreeSet<SortingPoint>();
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				neurons[i][j].feed(image.binaryBytes);
				Utils.addWithSizeControl(winners, new SortingPoint(neurons[i][j].value, new Point(i, j)), Settings.WINNERS_QUEUE_SIZE);
			}
		}
		return Utils.convert(winners);
	}
	
	@Override
	public void reduce(NetImage image, ArrayList<Point> winners, int currentLoop) {
		Point winner = this.emergeWinner(winners, image);
		if (winner != null) {
			reductor.reduceMesh(neurons, winner, neighbournessRadius, learningFunction, currentLoop, learningLoops, image);
		} else {
			Logger.log("Something could gone wrong, maybe change radius of neighbours.");
		}
		this.cleanValues();
	}

	@Override
	public Topology getTopology() {
		return Topology.MESH;
	}

	@Override
	public void cleanValues() {
		for (int i = 0; i < this.neurons.length; i++) {
			for (int j = 0; j < this.neurons[i].length; j++) {
				 this.neurons[i][j].value = 0;
			}
		}
	}
	
	@Override
	public String toString() {
		return "Mesh";
	}

}
