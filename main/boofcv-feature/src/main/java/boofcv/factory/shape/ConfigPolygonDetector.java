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

package boofcv.factory.shape;

import boofcv.alg.shapes.edge.EdgeIntensityPolygon;
import boofcv.alg.shapes.polygon.DetectPolygonFromContour;
import boofcv.struct.Configuration;

/**
 * Configuration for {@link DetectPolygonFromContour} for use in {@link FactoryShapeDetector}.
 *
 * @author Peter Abeles
 */
public class ConfigPolygonDetector implements Configuration {

	/**
	 * Configuration for detecting polygons from their contour
	 */
	public ConfigPolygonFromContour detector = new ConfigPolygonFromContour(3,10000);

	/**
	 * <p>
	 * The minimum allowed edge intensity for a shape after refinement. Used to remove false positives
	 * generated by noise, which is especially common when using a local threshold during binarization.
	 * </p>
	 *
	 * <p>Set to zero to disable.</p>
	 *
	 * @see EdgeIntensityPolygon
	 */
	public double minimumRefineEdgeIntensity = 6.0;

	/**
	 * If true then a contour based refinement will be run to improve the polygon estimate. 
	 */
	public boolean refineContour = false;

	/**
	 * Because of how a binary image is created the contour is biases along some sided. This algorithm
	 * will adjust the polygon computed directly from a contour to remove that bias.
	 */
	public boolean adjustForThresholdBias = true;

	/**
	 * Configuration for sub-pixel refinement of line. If null then this step is skipped.
	 */
	public ConfigRefinePolygonLineToImage refineGray = new ConfigRefinePolygonLineToImage();

	/**
	 * Specifies the number of sides in the polygon and uses default settings for everything else
	 */
	public ConfigPolygonDetector(int minimumSides, int maximumSides) {
		detector = new ConfigPolygonFromContour(minimumSides, maximumSides);
	}

	public ConfigPolygonDetector(boolean clockwise, int minimumSides, int maximumSides) {
		detector = new ConfigPolygonFromContour(clockwise, minimumSides, maximumSides);
	}

	public ConfigPolygonDetector() {
	}

	public void setTo( ConfigPolygonDetector src ) {
		this.detector.setTo(src.detector);
		this.minimumRefineEdgeIntensity = src.minimumRefineEdgeIntensity;
		this.refineContour = src.refineContour;
		this.adjustForThresholdBias = src.adjustForThresholdBias;
		this.refineGray.setTo(src.refineGray);
	}

	@Override
	public void checkValidity() {

	}

	@Override
	public String toString() {
		return "ConfigPolygonDetector{" +
				"detector=" + detector +
				", minimumEdgeIntensity=" + minimumRefineEdgeIntensity +
				", refineContour=" + refineContour +
				", refineGray=" + refineGray +
				'}';
	}
}
