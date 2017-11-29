package core.geometry;
import org.opencv.core.Mat;

import javafx.scene.image.Image;
import utils.Settings;

public class NetImage {
	
	public final byte[] binaryBytes = new byte[Settings.IMAGE_SIZE * Settings.IMAGE_SIZE];
	public final Image sourceImage;
	public NetImage(Image sourceImage, Mat mat) {
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				binaryBytes[Settings.IMAGE_SIZE*i + j] = (byte) (mat.get(i, j)[0] > 0 ? 1 : 0);
			}
		}
		this.sourceImage = sourceImage;
	}

}
