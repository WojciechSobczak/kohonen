package utils;


import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ImageUtils {
	
	public static void showImage(Mat src) {
		BufferedImage bufImage = null;
		try {
			MatOfByte matOfByte = new MatOfByte();
			Imgcodecs.imencode(".jpg", src, matOfByte);
			byte[] byteArray = matOfByte.toArray();
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);

			JFrame frame = new JFrame("Image");
			frame.getContentPane().setLayout(new FlowLayout());
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.setLocation(450, 500);
			frame.pack();
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Mat convertImageToMat(Image image) {
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
		int[] data = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
		byte[] bytes = new byte[data.length * 3];
		for (int i = 0; i < data.length; i++) {
			bytes[(i*3)/3] = (byte) (data[i] & 0x000000FF);
			bytes[(i*3)/3] = (byte) ((data[i] >> 8) & 0x000000FF);
			bytes[(i*3)/3] = (byte) ((data[i] >> 16) & 0x000000FF);
		}
		mat.put(0, 0, bytes);
		return mat;
	}
	
	public static Image load(File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			return SwingFXUtils.toFXImage(image, null);
		} catch (IOException e) {
			return null;
		}
	}
	
}
