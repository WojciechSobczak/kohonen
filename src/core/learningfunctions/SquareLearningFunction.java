package core.learningfunctions;

public class SquareLearningFunction implements LearningFunction {

	@Override
	public float learningCurve(int currentLoop, int allLoops) {
		return (float) -Math.pow(((float) currentLoop / (float) allLoops), 2) + 1;
	}
	
	@Override
	public String toString() {
		return "Square";
	}

}
