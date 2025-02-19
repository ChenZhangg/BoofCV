/*
 * Copyright (c) 2021, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.struct.feature;

import org.ddogleg.struct.DogArray;

/**
 * Stores an array of floats on constant size. Intended for storing multi band data.
 *
 * @author Peter Abeles
 */
public class ColorQueue_F32 extends DogArray<float[]> {

	public ColorQueue_F32(int numBands) {
		super(float[].class,()->new float[numBands]);
	}
}
