package core.nets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import core.NetImage;
import core.Neuron;
import core.geometry.Point;
import core.geometry.Topology;
import utils.FileUtils;
import utils.Logger;
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
			Logger.log("Something could gone wrong, maybe change radius of neighbours.");
		}
		this.cleanValues();
	}
	
	@Override
	public String toString() {
		return "Line";
	}

	@Override
	public void offload(String path) throws FileNotFoundException, IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(FileUtils.createFile(path)));
		objectOutputStream.writeObject(neurons);
		objectOutputStream.writeObject(queses);
		objectOutputStream.close();
	}

	@Override
	public void load(String path) throws FileNotFoundException, IOException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(path)));
		try {
			neurons = (Neuron[]) objectInputStream.readObject();
			queses = (HashMap<Point, NetImage>) objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		objectInputStream.close();
	}


}
