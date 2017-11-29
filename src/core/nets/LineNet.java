package core.nets;

import java.util.ArrayList;
import java.util.TreeSet;

import core.Neuron;
import core.geometry.NetImage;
import core.geometry.Point;
import core.geometry.Topology;
import utils.Settings;
import utils.Utils;

public class LineNet extends Net {

	public Neuron[] neurons;
	
	public LineNet(NetImage[] feed) {
		super(feed);
	}

	@Override
	public ArrayList<Point> feed(NetImage image) {
		TreeSet<SortingPoint> winners = new TreeSet<SortingPoint>();
		for (int i = 0; i < neurons.length; i++) {
			this.neurons[i].feed(image.binaryBytes);
			Utils.addWithSizeControl(winners, new SortingPoint(neurons[i].value, new Point(i, 0)), Settings.WINNERS_QUEUE_SIZE);
		}
		return Utils.convert(winners);
	}

	@Override
	public void init() {
		this.neurons = new Neuron[this.feed.length];
		for (int i = 0; i < neurons.length; i++) {
			this.neurons[i] = new Neuron();
		}
	}

	@Override
	public Topology getTopology() {
		return Topology.LINE;
	}

	@Override
	public void cleanValues() {
		for (int i = 0; i < neurons.length; i++) {
			this.neurons[i].value = 0;
		}
	}

	@Override
	public void reduce(NetImage image, ArrayList<Point> winners, int currentLoop) {
		Point winner = this.emergeWinner(winners, image);
		if (winner != null) {
			reductor.reduceLine(neurons, winner.x, (int) neighbournessRadius, learningFunction, currentLoop, learningLoops, image);
		} else {
			System.out.println("Something could gone wrong, maybe change radius of neighbours.");
		}
		this.cleanValues();
	}


}
