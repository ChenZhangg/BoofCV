/*
 * Copyright (c) 2011-2019, Peter Abeles. All Rights Reserved.
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

package boofcv.alg.feature.disparity;

import boofcv.alg.InputSanityCheck;
import boofcv.alg.feature.disparity.block.DisparitySelect;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageGray;
import boofcv.struct.image.ImageType;

/**
 * <p>
 * Base class for all dense stereo disparity score algorithms whose score's can be processed by
 * {@link DisparitySelect}. The scores for all possible disparities at each pixel is computed for
 * an entire row at once.  Then {@link DisparitySelect} is called to process this score.
 * </p>
 *
 * <p>
 * Score Format:  The index of the score for column i &ge; radiusX + minDisparity at disparity d is: <br>
 * index = imgWidth*(d-minDisparity-radiusX) + i - minDisparity-radiusX<br>
 * Format Comment:<br>
 * This ordering is a bit unnatural when searching for the best disparity, but reduces cache misses
 * when writing.  Performance boost is about 20%-30% depending on max disparity and image size.
 * </p>
 *
 * @author Peter Abeles
 */
public abstract class DisparityBlockMatchRowFormat
		<Input extends ImageBase<Input>, Disparity extends ImageGray<Disparity>>
{
	// the minimum disparity value (inclusive)
	protected int minDisparity;
	// maximum allowed image disparity (exclusive)
	protected int maxDisparity;
	// difference between max and min
	protected int rangeDisparity;

	// number of score elements: image_width*rangeDisparity
	protected int lengthHorizontal;

	// radius of the region along x and y axis
	protected int radiusX,radiusY;
	// size of the region: radius*2 + 1
	protected int regionWidth,regionHeight;

	/**
	 * Configures disparity calculation.
	 *
	 * @param minDisparity Minimum disparity that it will check. Must be &ge; 0 and < maxDisparity
	 * @param maxDisparity Maximum disparity that it will calculate. Must be &gt; 0
	 * @param regionRadiusX Radius of the rectangular region along x-axis.
	 * @param regionRadiusY Radius of the rectangular region along y-axis.
	 */
	public DisparityBlockMatchRowFormat(int minDisparity, int maxDisparity,
										int regionRadiusX, int regionRadiusY ) {
		if( maxDisparity <= 0 )
			throw new IllegalArgumentException("Max disparity must be greater than zero. max="+maxDisparity);
		if( minDisparity < 0 || minDisparity >= maxDisparity )
			throw new IllegalArgumentException("Min disparity must be >= 0 and < maxDisparity. min="+minDisparity+" max="+maxDisparity);

		this.minDisparity = minDisparity;
		this.maxDisparity = maxDisparity;
		this.radiusX = regionRadiusX;
		this.radiusY = regionRadiusY;

		this.rangeDisparity = maxDisparity-minDisparity+1;

		this.regionWidth = regionRadiusX*2+1;
		this.regionHeight = regionRadiusY*2+1;
	}

	/**
	 * Computes disparity between two stereo images
	 *
	 * @param left Left rectified stereo image. Input
	 * @param right Right rectified stereo image. Input
	 * @param disparity Disparity between the two images. Output
	 */
	public void process( Input left , Input right , Disparity disparity ) {
		// initialize data structures
		InputSanityCheck.checkSameShape(left, right);
		disparity.reshape(left);

		if( maxDisparity >=  left.width )
			throw new RuntimeException(
					"The maximum disparity is too large for this image size: max size "+(left.width-1));

		lengthHorizontal = left.width*rangeDisparity;

		_process(left,right,disparity);
	}

	/**
	 * Inner function that computes the disparity.
	 */
	public abstract void _process( Input left , Input right , Disparity disparity );

	public abstract ImageType<Input> getInputType();

	public abstract Class<Disparity> getDisparityType();

	public int getMinDisparity() {
		return minDisparity;
	}

	public int getMaxDisparity() {
		return maxDisparity;
	}

	public int getBorderX() {
		return 0;
	}

	public int getBorderY() {
		return 0;
	}
}
