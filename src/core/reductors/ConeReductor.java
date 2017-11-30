package core.reductors;

import core.Neuron;
import core.geometry.NetImage;
import core.geometry.Point;
import core.learningfunctions.LearningFunction;
import utils.Utils;

public class ConeReductor implements Reductor {
	
	@Override
	public void reduceLine(Neuron[] neurons, int winnerIndex, int radius, LearningFunction learningFunction, int currentLoop, int allLoops, NetImage input) {
		int startIndex = winnerIndex - radius;
		if (startIndex < 0) {
			startIndex = 0;
		}
		int endIndex = winnerIndex + radius;
		if (endIndex > neurons.length - 1) {
			endIndex = neurons.length - 1;
		}
		
		for (int i = 0; i < startIndex; i++) {
			neurons[i].value = 0;
		}
		
		for (int i = startIndex; i <= endIndex; i++) {
			if (i != winnerIndex) {
				int distance = Math.abs(winnerIndex - i);
				float l1 = radius - distance;
				if (l1 <= 0) {
					neurons[i].value = 0;
				} else {
					neurons[i].value = ((float) distance * l1) / neurons[winnerIndex].value;
				}
			}
		}
		
		for (int i = endIndex + 1; i < neurons.length; i++) {
			neurons[i].value = 0;
		}
		for (int i = 0; i < winnerIndex; i++) {
			Utils.modifyWeights(learningFunction, currentLoop, allLoops, input, neurons[winnerIndex], neurons[i]);
		}
		for (int i = winnerIndex + 1; i < neurons.length; i++) {
			Utils.modifyWeights(learningFunction, currentLoop, allLoops, input, neurons[winnerIndex], neurons[i]);
		}
	}

	@Override
	public void reduceMesh(Neuron[][] neurons, Point winnerIndex, float radius, LearningFunction learningFunction, int currentLoop, int allLoops, NetImage input) {
		Neuron winner = neurons[winnerIndex.x][winnerIndex.y];
		//Nak³adanie sto¿ka
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].length; j++) {
				this.neighbourness(winner, winnerIndex, neurons[i][j], new Point(i, j), radius);
			}
		}
		
		final int neighbournessDistance = (int) Math.floor(radius);
		int startx = Math.max(0, winnerIndex.x - neighbournessDistance);
		int endx =  Math.min(neurons.length - 1, winnerIndex.x + neighbournessDistance);
		int starty = Math.max(0, winnerIndex.y - neighbournessDistance);
		int endy = Math.min(neurons[0].length - 1, winnerIndex.y + neighbournessDistance);
		
		//Modyfikacja wag
		for (int y = starty; y < endy; y++) {
			for (int x = startx; x < endx; x++) {
				if (winnerIndex.x == startx && winnerIndex.y == y) {
					continue;
				}
				try {
					Utils.modifyWeights(learningFunction, currentLoop, allLoops, input, winner, neurons[x][y]);
				} catch (Exception e) {
					System.out.println();
				}
			}
		}
		
		//Normalizacja
		Utils.normalize(winner);
	}

	

	private void neighbourness(Neuron host, Point hostCoords, Neuron neighbour, Point neighbourCoords, float radius) {
		if (hostCoords.equals(neighbourCoords)) {
			return;
		}
		float distance = this.euclidianDistance(hostCoords, neighbourCoords);
		float l1 = radius - distance;
		if (l1 <= 0) {
			neighbour.value = 0;
		} else {
			neighbour.value = (distance * l1) / host.value;
		}
	}
	
	private float euclidianDistance(Point coords1, Point coords2) {
		return (float) Math.sqrt(
			Math.pow(coords1.x - coords2.x, 2) + Math.pow(coords1.y - coords2.y, 2)
		);
	}

	@Override
	public void reduceHex(Neuron[][] neurons, Point winnerIndex, float radius, LearningFunction learningFunction,
			int currentLoop, int allLoops, NetImage input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "Cone";
	}



}
