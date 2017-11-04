package utils;
import org.opencv.core.Mat;

public class BinaryImage {
	
	public final byte[] bytes = new byte[Settings.IMAGE_SIZE * Settings.IMAGE_SIZE];
	
	public BinaryImage(Mat mat) {
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				bytes[Settings.IMAGE_SIZE*i + j] = (byte) (mat.get(i, j)[0] > 0 ? 1 : 0);
			}
		}
		
		/* TEST
		Mat mats = new Mat(new Size(Settings.IMAGE_SIZE, Settings.IMAGE_SIZE), CvType.CV_8U);
		for (int i = 0; i < mats.rows(); i++) {
			for (int j = 0; j < mats.cols(); j++) {
				mats.put(i, j, new byte[] {(byte) (bytes[Settings.IMAGE_SIZE*i + j] == 1 ? 255 : 0)});
			}
		}*/
	}

}
