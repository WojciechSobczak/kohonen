package core;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import utils.Settings;

public class NetImage implements Serializable {

	public byte[] binaryBytes = new byte[Settings.IMAGE_SIZE * Settings.IMAGE_SIZE];
	public transient Image sourceImage;
	public String fileName;
	public String canonicalPath;

	public NetImage(File file, Image sourceImage, Mat mat) {
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				binaryBytes[Settings.IMAGE_SIZE * i + j] = (byte) (mat.get(i, j)[0] > 0 ? 1 : 0);
			}
		}
		this.sourceImage = sourceImage;
		this.fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
		try {
			this.canonicalPath = file.getCanonicalPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		binaryBytes = (byte[]) s.readObject();
		fileName = (String) s.readObject();
		canonicalPath = (String) s.readObject();
		sourceImage = SwingFXUtils.toFXImage(ImageIO.read(s), null);
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.writeObject(binaryBytes);
		s.writeObject(fileName);
		s.writeObject(canonicalPath);
		ImageIO.write(SwingFXUtils.fromFXImage(sourceImage, null), "png", s);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.canonicalPath.equals(((NetImage) obj).canonicalPath);
	}

}
