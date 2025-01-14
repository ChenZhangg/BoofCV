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

import boofcv.abst.shapes.polyline.BaseConfigPolyline;
import boofcv.abst.shapes.polyline.ConfigPolylineSplitMerge;
import boofcv.alg.shapes.edge.EdgeIntensityPolygon;
import boofcv.struct.ConfigLength;
import boofcv.struct.Configuration;
import boofcv.struct.ConnectRule;

/**
 * Configuration for {@link boofcv.alg.shapes.polygon.DetectPolygonFromContour}
 *
 * @author Peter Abeles
 */
public class ConfigPolygonFromContour implements Configuration {

	/**
	 * If false then polygons which touch the image border are pruned
	 */
	public boolean canTouchBorder = false;

	/**
	 * Connect rule for contour finding in binary image.
	 */
	public ConnectRule contourRule = ConnectRule.FOUR;

	/**
	 * Configuration for fitting a polygon to the contour.
	 */
	public BaseConfigPolyline contourToPoly = new ConfigPolylineSplitMerge();

	/**
	 * <p>
	 * The minimum allowed edge intensity for a shape. Used to remove false positives generated by noise, which
	 * is especially common when using a local threshold during binarization.
	 * </p>
	 *
	 * <p>Set to zero to disable.</p>
	 *
	 * @see EdgeIntensityPolygon
	 */
	public double minimumEdgeIntensity = 6.0;

	/**
	 * Tangential distance away in pixels from the contour that the edge intensity is sampled.
	 */
	public double tangentEdgeIntensity = 2.5;

	/**
	 * Specifies the minimum allowed contour length. Relative lengths will be relative with to the image's
	 * width and height.
	 */
	public ConfigLength minimumContour = ConfigLength.relative(0.05,4);

	/**
	 * Will the found polygons be in clockwise order?
	 */
	public boolean clockwise = true;

	/**
	 * Specifies the number of sides in the polygon and uses default settings for everything else
	 */
	public ConfigPolygonFromContour(int minimumSides, int maximumSides) {
		contourToPoly.minimumSides = minimumSides;
		contourToPoly.maximumSides = maximumSides;
	}

	public ConfigPolygonFromContour(boolean clockwise, int minimumSides, int maximumSides) {
		contourToPoly.minimumSides = minimumSides;
		contourToPoly.maximumSides = maximumSides;

		this.clockwise = clockwise;
	}

	public ConfigPolygonFromContour(){}

	public void setTo( ConfigPolygonFromContour src ) {
		this.canTouchBorder = src.canTouchBorder;
		this.contourRule = src.contourRule;
		this.contourToPoly.setTo(src.contourToPoly);
		this.minimumEdgeIntensity = src.minimumEdgeIntensity;
		this.tangentEdgeIntensity = src.tangentEdgeIntensity;
		this.minimumContour.setTo(src.minimumContour);
		this.clockwise = src.clockwise;
	}

	@Override
	public void checkValidity() {
		minimumContour.checkValidity();
	}

	@Override
	public String toString() {
		return "ConfigPolygonFromContour{" +
				" contourToPoly=" + contourToPoly +
				", minimumEdgeIntensity=" + minimumEdgeIntensity +
				", tangentEdgeIntensity=" + tangentEdgeIntensity +
				", minimumContour=" + minimumContour +
				", clockwise=" + clockwise +
				'}';
	}
}
