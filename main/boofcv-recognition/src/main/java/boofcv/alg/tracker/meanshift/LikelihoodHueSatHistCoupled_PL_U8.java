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

package boofcv.alg.tracker.meanshift;

import boofcv.alg.color.ColorHsv;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.struct.shapes.RectangleLength2D_I32;

/**
 * TODO redo comments
 * Converts an RGB image into HSV image to add invariance to changes in lighting conditions. Creates independent
 * histograms for the target's Hue and Saturation, which are then normalized such that
 * their sums are equal to one. Likelihood is computed multiply the value of the histograms together.
 *
 * Colors with a very small "Value" are ignored since their hue and saturation are not reliable.
 *
 * @author Peter Abeles
 */
public class LikelihoodHueSatHistCoupled_PL_U8 implements PixelLikelihood<Planar<GrayU8>> {
	// each band in the image
	private GrayU8 imageRed;
	private GrayU8 imageGreen;
	private GrayU8 imageBlue;

	// storage for RGB to HSV conversion
	private final float[] hsv = new float[3];

	// number of bins for Hue and Saturation bands
	protected int numHistogramBins;
	// largest element index in a histogram bin. numBins-1
	protected int maxElementInBin;

	// the minimum value allowed. used to avoid pathological case in HSV color space
	protected float minimumValue;

	// Hue has a range of 0 to 2*pi and this is a discretized histogram
	// Saturation has a range of 0 to 1 and this is a discretized histogram
	protected float bins[];

	// size of the hue and saturation bins
	protected float sizeH, sizeS;

	/**
	 * Configures likelihood function
	 *
	 * @param maxPixelValue The maximum intensity value a pixel can take on.
	 * @param numHistogramBins Number of bins in the Hue and Saturation histogram.
	 */
	public LikelihoodHueSatHistCoupled_PL_U8( int maxPixelValue, int numHistogramBins ) {
		minimumValue = (maxPixelValue + 1)*0.01f;
		this.numHistogramBins = numHistogramBins;

		bins = new float[numHistogramBins*numHistogramBins];

		// divide it by a number slightly larger than the max to avoid the special case where it is equal to the max
		sizeH = (float)(2.001*Math.PI/numHistogramBins);
		sizeS = 1.001f/numHistogramBins;
	}

	@Override
	public void setImage( Planar<GrayU8> image ) {
		imageRed = image.getBand(0);
		imageGreen = image.getBand(1);
		imageBlue = image.getBand(2);
	}

	@Override
	public boolean isInBounds( int x, int y ) {
		return imageRed.isInBounds(x, y);
	}

	@Override
	public void createModel( RectangleLength2D_I32 target ) {
		float total = 0;

		for (int y = 0; y < target.height; y++) {
			int index = imageRed.startIndex + (y + target.y0)*imageRed.stride + target.x0;
			for (int x = 0; x < target.width; x++, index++) {
				int r = imageRed.data[index] & 0xFF;
				int g = imageGreen.data[index] & 0xFF;
				int b = imageBlue.data[index] & 0xFF;

				ColorHsv.rgbToHsv(r, g, b, hsv);

				if (hsv[2] < minimumValue)
					continue;

				int binH = (int)(hsv[0]/sizeH);
				int binS = (int)(hsv[1]/sizeS);

				bins[binH*numHistogramBins + binS]++;

				total++;
			}
		}

		// normalize the sum to one
		for (int i = 0; i < bins.length; i++) {
			bins[i] /= total;
		}
	}

	@Override
	public float compute( int x, int y ) {

		int index = imageRed.getIndex(x, y);

		int r = imageRed.data[index] & 0xFF;
		int g = imageGreen.data[index] & 0xFF;
		int b = imageBlue.data[index] & 0xFF;

		ColorHsv.rgbToHsv(r, g, b, hsv);

		if (hsv[2] < minimumValue)
			return 0f;

		int binH = (int)(hsv[0]/sizeH);
		int binS = (int)(hsv[1]/sizeS);

		return bins[binH*numHistogramBins + binS];
	}
}
