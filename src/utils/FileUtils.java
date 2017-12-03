package utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {
	
	public static final File createFile(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

}
