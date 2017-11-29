package core.reductors;

import core.Neuron;
import core.geometry.NetImage;
import core.geometry.Point;
import core.learningfunctions.LearningFunction;

public interface Reductor {
	
	public void reduceLine(Neuron[] neurons, int winnerIndex, int radius, LearningFunction learningFunction, int currentLoop, int allLoops, NetImage input);
	public void reduceMesh(Neuron[][] neurons, Point winnerIndex, float radius, LearningFunction learningFunction, int currentLoop, int allLoops, NetImage input);
	public void reduceHex(Neuron[][] neurons, Point winnerIndex, float radius, LearningFunction learningFunction, int currentLoop, int allLoops, NetImage input);
	
}
