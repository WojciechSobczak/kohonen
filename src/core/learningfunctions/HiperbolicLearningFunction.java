package core.learningfunctions;

public class HiperbolicLearningFunction implements LearningFunction {

	@Override
	public float learningCurve(int currentLoop, int allLoops) {
		return 1 / (float) currentLoop;
	}
	
	@Override
	public String toString() {
		return "Hiperbolic";
	}

}
