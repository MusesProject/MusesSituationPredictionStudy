package eu.musesproject.client.preferences;
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




public class ModelCountPreference extends AbstractIntPreference {

	private static final String KEY = "model_count_preference";
	private static ModelCountPreference mInstance;
	
	public static ModelCountPreference getInstance(){
		if(mInstance == null){
			mInstance = new ModelCountPreference();
		}
		return mInstance;
	}
	
	@Override
	protected String getKey() {
		return KEY;
	}

//	public void increment(Context context){
//		int currentVale = get(context);
//		if(currentVale == DefaultValues.INT){
//			currentVale = 0;
//		}
//		++currentVale;
//		set(context, currentVale);
//	}
//	
//	public void decrement(Context context){
//		int currentVale = get(context);
//		if(currentVale == DefaultValues.INT){
//			currentVale = 1;
//		}
//		--currentVale;
//		set(context, currentVale);
//	}
}
