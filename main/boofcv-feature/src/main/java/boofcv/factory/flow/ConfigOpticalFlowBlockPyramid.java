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

package boofcv.factory.flow;

import boofcv.alg.flow.DenseOpticalFlowBlockPyramid;
import boofcv.struct.Configuration;

/**
 * Configuration for {@link DenseOpticalFlowBlockPyramid}
 *
 * @author Peter Abeles
 */
public class ConfigOpticalFlowBlockPyramid implements Configuration {
	/** Radius of the search area */
	public int searchRadius = 4;

	/** Radius of the square region */
	public int regionRadius = 5;

	/** Maximum error allowed per pixel. Default is 30 */
	public int maxPerPixelError = 30;

	/** Difference in scale between layers in the pyramid. A value of 1 means a single layer. */
	public double pyramidScale = 0.75;

	/** The maximum number of layers in the pyramid  */
	public int maxPyramidLayers = 20;

	public ConfigOpticalFlowBlockPyramid() {}

	public void setTo( ConfigOpticalFlowBlockPyramid src ) {
		this.searchRadius = src.searchRadius;
		this.regionRadius = src.regionRadius;
		this.maxPerPixelError = src.maxPerPixelError;
		this.pyramidScale = src.pyramidScale;
		this.maxPyramidLayers = src.maxPyramidLayers;
	}

	@Override public void checkValidity() {}
}
