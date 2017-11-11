package net;
import java.util.Random;

import utils.Settings;

public class Neuron {
	
	public float value;
	float[] weights = new float[Settings.IMAGE_SIZE * Settings.IMAGE_SIZE];
	
	public Neuron() {
		Random random = new Random(System.nanoTime());
		for (int i = 0; i < weights.length; i++) {
			weights[i] = random.nextFloat();
		}
	}
	/**
	 * bytes have to have 0 or 1 values
	 * @param bytes
	 */
	public void feed(byte[] bytes) {
		this.value = 0;
		for (int i = 0; i < bytes.length; i++) {
			this.value += bytes[i] * weights[i];
		}
	}

}
