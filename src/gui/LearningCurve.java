package gui;

import core.learningfunctions.LearningFunction;
import core.learningfunctions.LineLearningFunction;

public enum LearningCurve {
	LINE(new LineLearningFunction());
	
	private LearningFunction learningFunction;
	private LearningCurve(LearningFunction learningFunction) {
		this.learningFunction = learningFunction;
	}
	public LearningFunction getLearningFunction() {
		return learningFunction;
	}
}
