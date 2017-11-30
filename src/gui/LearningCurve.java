package gui;

import core.learningfunctions.HiperbolicLearningFunction;
import core.learningfunctions.LearningFunction;
import core.learningfunctions.LineLearningFunction;
import core.learningfunctions.SquareLearningFunction;

public enum LearningCurve {
	LINE(new LineLearningFunction()),
	HIBERBOLIC(new HiperbolicLearningFunction()),
	SQUARE(new SquareLearningFunction());
	
	private LearningFunction learningFunction;
	private LearningCurve(LearningFunction learningFunction) {
		this.learningFunction = learningFunction;
	}
	public LearningFunction getLearningFunction() {
		return learningFunction;
	}
}
