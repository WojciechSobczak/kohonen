package core.learningfunctions;

public class LineLearningFunction implements LearningFunction {

	@Override
	public float learningCurve(int currentLoop, int allLoops) {
		return 1 - ((float) (currentLoop - 1) / (float) allLoops);
	}

}
