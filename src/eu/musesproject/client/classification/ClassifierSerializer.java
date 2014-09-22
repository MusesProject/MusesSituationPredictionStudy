package eu.musesproject.client.classification;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import weka.classifiers.Classifier;
import weka.core.SerializationHelper;
import android.os.Environment;

public class ClassifierSerializer {

	public static final String NB_MODEL_NAME = "NBModel.model";
	
	private static String mSerializiationPath;

	public ClassifierSerializer() {

	}

	public static void serializeClassifier(Classifier classifier, String name) throws Exception {

		File dir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).getPath()
				+ File.separatorChar + "Muses");

		if (!dir.exists()) {
			dir.mkdir();
		}

		mSerializiationPath = dir.getPath() + File.separatorChar
				+ name;

		SerializationHelper.write(mSerializiationPath, classifier);

	}

	public static Classifier deserializeClassifier() {
		ObjectInputStream ois;
		Classifier cls = null;
		try {
			ois = new ObjectInputStream(
					new FileInputStream(mSerializiationPath));
			cls = (Classifier) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cls;
	}

}
