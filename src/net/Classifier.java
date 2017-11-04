package net;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import utils.BinaryImage;
import utils.FeedProvider;
import utils.Point;

public class Classifier {
	
	public KohonensNet kohonensNet;
	public HashMap<Point, File> classified;
	
	public Classifier() {
		this.classified = new HashMap<>();
	}
	
	
	public void initNet() {
		this.kohonensNet = new KohonensNet();
	}
	
	public void learn(int loops) throws IOException {
		this.classified = new HashMap<>();
		HashMap<BinaryImage, File> loadedFeedMap = FeedProvider.loadFeed();
		ArrayList<BinaryImage> feedList = new ArrayList<>(loadedFeedMap.keySet());
		
		for (int i = 0; i < loops; i++) {
			System.out.println((i + 1) + " learing loop of " + loops);
			for (BinaryImage binaryImage : feedList) {
				ArrayList<Point> points = this.kohonensNet.feed(binaryImage);
				this.kohonensNet.modifyWinners(points.get(0), i + 1, loops);
				this.classified.put(points.get(0), loadedFeedMap.get(binaryImage));
				this.kohonensNet.eraseValues();
			}
			Collections.shuffle(feedList);
		}
	}
	
	public boolean isNetInitialized() {
		return this.kohonensNet != null;
	}
	
	public File classify(BinaryImage image) {
		ArrayList<Point> points = this.kohonensNet.feed(image);
		this.kohonensNet.eraseValues();
		return this.classified.get(points.get(0));
	}
	

}
