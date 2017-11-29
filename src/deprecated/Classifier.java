/*package deprecated;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import core.geometry.Point;
import utils.NetImage;
import utils.FeedProvider;

public class Classifier {
	
	public KohonensNet kohonensNet;
	public HashMap<Point, File> classified;
	
	public Classifier() {
		this.classified = new HashMap<>();
	}
	
	public void learn(int loops) throws IOException {
		HashMap<NetImage, File> loadedFeedMap = FeedProvider.loadFeed();
		ArrayList<NetImage> feedList = new ArrayList<>(loadedFeedMap.keySet());
		this.kohonensNet = new KohonensNet(feedList.size());
		this.classified = new HashMap<>();
		HashSet<Point> winners = new HashSet<>();
		for (int i = 0; i < loops; i++) {
			//System.out.println((i + 1) + " learing loop of " + loops);
			for (NetImage binaryImage : feedList) {
				ArrayList<Point> points = this.kohonensNet.feed(binaryImage);
				Point winner = null;
				for (Point point : points) {
					if (!winners.contains(point)) {
						winner = point;
						winners.add(winner);
						break;
					}
				}
				this.kohonensNet.modifyWithWinner(winner, binaryImage.binaryBytes, i + 1, loops);
				if (i == loops - 2) {
					this.classified.put(winner, loadedFeedMap.get(binaryImage));
				}
				this.kohonensNet.eraseValues();
				winners.clear();
			}
			Collections.shuffle(feedList);
		}
		
	}
	
	public boolean isNetInitialized() {
		return this.kohonensNet != null;
	}
	
	public File classify(NetImage image) {
		ArrayList<Point> points = this.kohonensNet.feed(image);
		this.kohonensNet.eraseValues();
		return this.classified.get(points.get(0));
	}
	

}
*/