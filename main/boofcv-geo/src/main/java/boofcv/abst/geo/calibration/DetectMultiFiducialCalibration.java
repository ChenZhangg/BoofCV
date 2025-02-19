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

package boofcv.abst.geo.calibration;

import boofcv.alg.distort.LensDistortionNarrowFOV;
import boofcv.alg.geo.calibration.CalibrationObservation;
import boofcv.struct.image.GrayF32;
import georegression.struct.point.Point2D_F64;

import java.util.List;

/**
 * Calibration targets which can detect multiple targets at once with unique IDs
 *
 * @author Peter Abeles
 */
public interface DetectMultiFiducialCalibration {

	/**
	 * Image processing for calibration target detection
	 *
	 * @param input Gray scale image containing calibration target
	 */
	void process( GrayF32 input );

	/** Returns the number of detected markers */
	int getDetectionCount();

	/** Returns which marker was seen for a particular detection */
	int getMarkerID( int detectionID );

	/** Returns the number of unique markers that it can detect */
	int getTotalUniqueMarkers();

	/**
	 * Returns the set of detected points from the most recent call to {@link #process(GrayF32)}. Each
	 * time this function is invoked a new instance of the list and points is returned. No data reuse here.
	 *
	 * @param detectionID Which detection should it return the points for
	 * @return List of detected points in row major grid order.
	 */
	CalibrationObservation getDetectedPoints( int detectionID );

	/**
	 * Returns the layout of the calibration points on the target
	 *
	 * @param markerID Which marker should it return the layout of
	 * @return List of calibration points
	 */
	List<Point2D_F64> getLayout( int markerID );

	/**
	 * Explicitly handles lens distortion when detecting image features. If used, features will be found in
	 * undistorted pixel coordinates
	 */
	void setLensDistortion(LensDistortionNarrowFOV distortion, int width, int height );
}
