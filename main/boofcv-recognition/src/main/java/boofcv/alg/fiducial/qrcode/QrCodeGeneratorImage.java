/*
 * Copyright (c) 2011-2017, Peter Abeles. All Rights Reserved.
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

package boofcv.alg.fiducial.qrcode;

import boofcv.alg.misc.ImageMiscOps;
import boofcv.struct.image.GrayU8;

/**
 * @author Peter Abeles
 */
public class QrCodeGeneratorImage extends QrCodeGenerator {

	GrayU8 gray = new GrayU8(1,1);

	public QrCodeGeneratorImage(int version, int pixelsPerModule) {
		super(version, 1.0);

		int width = pixelsPerModule*numModules;
		gray.reshape(width,width);
	}

	@Override
	public void init() {
		ImageMiscOps.fill(gray,255);
	}

	@Override
	public void square(double x0, double y0, double width) {
		int pixelX = (int)(x0*gray.width+0.5);
		int pixelY = (int)(y0*gray.width+0.5);
		int pixelsWidth = (int)(width*gray.width+0.5);

		ImageMiscOps.fillRectangle(gray,0,pixelX,pixelY,
				pixelsWidth, pixelsWidth);
	}

	@Override
	public void square(double x0, double y0, double width0, double thickness) {

		int X0 = (int)(x0*gray.width+0.5);
		int Y0 = (int)(y0*gray.width+0.5);
		int WIDTH = (int)(width0*gray.width+0.5);
		int THICKNESS = (int)(thickness *gray.width+0.5);

		ImageMiscOps.fillRectangle(gray,0,X0,Y0,WIDTH,THICKNESS);
		ImageMiscOps.fillRectangle(gray,0,X0,Y0+WIDTH-THICKNESS,WIDTH,THICKNESS);
		ImageMiscOps.fillRectangle(gray,0,X0,Y0+THICKNESS,THICKNESS,WIDTH-THICKNESS*2);
		ImageMiscOps.fillRectangle(gray,0,X0+WIDTH-THICKNESS,Y0+THICKNESS,THICKNESS,WIDTH-THICKNESS*2);
	}

	public GrayU8 getGray() {
		return gray;
	}
}
