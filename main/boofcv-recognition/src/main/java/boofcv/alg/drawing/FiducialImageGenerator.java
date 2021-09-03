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

package boofcv.alg.drawing;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class for generators images of fiducials
 *
 * @author Peter Abeles
 */
public abstract class FiducialImageGenerator {
	/** size of marker in document units */
	@Getter @Setter protected double markerWidth = 0;

	/** used to draw the fiducial */
	@Getter @Setter protected FiducialRenderEngine renderer;
}
