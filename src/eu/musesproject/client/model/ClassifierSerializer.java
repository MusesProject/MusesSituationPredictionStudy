package eu.musesproject.client.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import weka.classifiers.Classifier;
import weka.core.SerializationHelper;
import android.os.Environment;

public class ClassifierSerializer {

	private static final String NB_MODEL_NAME = "NBModel.model";

	public ClassifierSerializer() {

	}

	public static void serializeClassifier(Classifier classifier, String path)
			throws Exception {

		SerializationHelper.write(path, classifier);

	}

	public static Classifier deserializeClassifier(String path) {
		ObjectInputStream ois;
		Classifier cls = null;
		try {
			ois = new ObjectInputStream(
					new FileInputStream(path));
			cls = (Classifier) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cls;
	}

	public static String getNaiveBayesSerializationPath() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).getPath()
				+ File.separatorChar + "Muses");

		if (!dir.exists()) {
			dir.mkdir();
		}

		return dir.getPath() + File.separatorChar + NB_MODEL_NAME;

	}

}
