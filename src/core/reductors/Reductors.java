package core.reductors;

public enum Reductors {
	CONE(new ConeReductor());
	
	
	private Reductor reductor;
	private Reductors(Reductor reductor) {
		this.reductor = reductor;
	}
	
	public Reductor getReductor() {
		return this.reductor;
	}
}
