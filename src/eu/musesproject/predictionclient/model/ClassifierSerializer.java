package eu.musesproject.predictionclient.model;
/*
 * #%L
 * musesclient
 * %%
 * Copyright (C) 2013 - 2014 HITEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
