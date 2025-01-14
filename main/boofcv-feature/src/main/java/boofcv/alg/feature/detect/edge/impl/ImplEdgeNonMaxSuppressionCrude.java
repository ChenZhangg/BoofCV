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

package boofcv.alg.feature.detect.edge.impl;

//CONCURRENT_INLINE import boofcv.concurrency.BoofConcurrency;

import boofcv.core.image.border.FactoryImageBorderAlgs;
import boofcv.struct.border.ImageBorder_F32;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayI;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayS32;


/**
 * <p>
 * Implementations of the crude version of non-maximum edge suppression. If the gradient is positive or negative
 * is used to determine the direction of suppression. This is faster since an expensive orientation calculation
 * is avoided.
 * </p>
 *
 * <p>
 * DO NOT MODIFY. Generated by GenerateImplEdgeNonMaxSuppressionCrude.
 * </p>
 *
 * @author Peter Abeles
 */
@SuppressWarnings("Duplicates")
public class ImplEdgeNonMaxSuppressionCrude {

	/**
	 * Only processes the inner image. Ignoring the border.
	 */
	static public void inner4( GrayF32 intensity , GrayF32 derivX , GrayF32 derivY, GrayF32 output )
	{
		final int w = intensity.width;
		final int h = intensity.height-1;

		//CONCURRENT_BELOW BoofConcurrency.loopFor(1,h,y->{
		for( int y = 1; y < h; y++ ) {
			int indexI = intensity.startIndex + y*intensity.stride+1;
			int indexX = derivX.startIndex + y*derivX.stride+1;
			int indexY = derivY.startIndex + y*derivY.stride+1;
			int indexO = output.startIndex + y*output.stride+1;

			int end = indexI + w - 2;
			for( ; indexI < end; indexI++ , indexX++, indexY++, indexO++ ) {
				int dx,dy;

				if( derivX.data[indexX] > 0 ) dx = 1; else dx = -1;
				if( derivY.data[indexY] > 0 ) dy = 1; else dy = -1;

				float middle = intensity.data[indexI];

				// suppress the value if either of its neighboring values are more than or equal to it
				if( intensity.data[indexI-dx-dy*intensity.stride] > middle || intensity.data[indexI+dx+dy*intensity.stride] > middle ) {
					output.data[indexO] = 0;
				} else {
					output.data[indexO] = middle;
				}
			}
		}
		//CONCURRENT_ABOVE });
	}

	/**
	 * Only processes the inner image. Ignoring the border.
	 */
	static public void inner4( GrayF32 intensity , GrayS16 derivX , GrayS16 derivY, GrayF32 output )
	{
		final int w = intensity.width;
		final int h = intensity.height-1;

		//CONCURRENT_BELOW BoofConcurrency.loopFor(1,h,y->{
		for( int y = 1; y < h; y++ ) {
			int indexI = intensity.startIndex + y*intensity.stride+1;
			int indexX = derivX.startIndex + y*derivX.stride+1;
			int indexY = derivY.startIndex + y*derivY.stride+1;
			int indexO = output.startIndex + y*output.stride+1;

			int end = indexI + w - 2;
			for( ; indexI < end; indexI++ , indexX++, indexY++, indexO++ ) {
				int dx,dy;

				if( derivX.data[indexX] > 0 ) dx = 1; else dx = -1;
				if( derivY.data[indexY] > 0 ) dy = 1; else dy = -1;

				float middle = intensity.data[indexI];

				// suppress the value if either of its neighboring values are more than or equal to it
				if( intensity.data[indexI-dx-dy*intensity.stride] > middle || intensity.data[indexI+dx+dy*intensity.stride] > middle ) {
					output.data[indexO] = 0;
				} else {
					output.data[indexO] = middle;
				}
			}
		}
		//CONCURRENT_ABOVE });
	}

	/**
	 * Only processes the inner image. Ignoring the border.
	 */
	static public void inner4( GrayF32 intensity , GrayS32 derivX , GrayS32 derivY, GrayF32 output )
	{
		final int w = intensity.width;
		final int h = intensity.height-1;

		//CONCURRENT_BELOW BoofConcurrency.loopFor(1,h,y->{
		for( int y = 1; y < h; y++ ) {
			int indexI = intensity.startIndex + y*intensity.stride+1;
			int indexX = derivX.startIndex + y*derivX.stride+1;
			int indexY = derivY.startIndex + y*derivY.stride+1;
			int indexO = output.startIndex + y*output.stride+1;

			int end = indexI + w - 2;
			for( ; indexI < end; indexI++ , indexX++, indexY++, indexO++ ) {
				int dx,dy;

				if( derivX.data[indexX] > 0 ) dx = 1; else dx = -1;
				if( derivY.data[indexY] > 0 ) dy = 1; else dy = -1;

				float middle = intensity.data[indexI];

				// suppress the value if either of its neighboring values are more than or equal to it
				if( intensity.data[indexI-dx-dy*intensity.stride] > middle || intensity.data[indexI+dx+dy*intensity.stride] > middle ) {
					output.data[indexO] = 0;
				} else {
					output.data[indexO] = middle;
				}
			}
		}
		//CONCURRENT_ABOVE });
	}

	/**
	 * Just processes the image border.
	 */
	static public void border4( GrayF32 _intensity , GrayF32 derivX , GrayF32 derivY , GrayF32 output )
	{
		int w = _intensity.width;
		int h = _intensity.height-1;

		ImageBorder_F32 intensity = FactoryImageBorderAlgs.value(_intensity, 0);

		// top border
		//CONCURRENT_BELOW BoofConcurrency.loopFor(0,w,x->{
		for( int x = 0; x < w; x++ ) {
			int dx,dy;

			if( derivX.get(x,0) > 0 ) dx = 1; else dx = -1;
			if( derivY.get(x,0) > 0 ) dy = 1; else dy = -1;

			float left = intensity.get(x-dx,-dy);
			float middle = intensity.get(x,0);
			float right = intensity.get(x+dx,dy);

			if( left > middle || right > middle ) {
				output.set(x,0,0);
			} else {
				output.set(x,0,middle);
			}
		}
		//CONCURRENT_ABOVE });

		// bottom border
		//CONCURRENT_BELOW BoofConcurrency.loopFor(0,w,x->{
		for( int x = 0; x < w; x++ ) {
			int dx,dy;

			if( derivX.get(x,h) > 0 ) dx = 1; else dx = -1;
			if( derivY.get(x,h) > 0 ) dy = 1; else dy = -1;

			float left = intensity.get(x-dx,h-dy);
			float middle = intensity.get(x,h);
			float right = intensity.get(x+dx,h+dy);

			if( left > middle || right > middle ) {
				output.set(x,h,0);
			} else {
				output.set(x,h,middle);
			}
		}
		//CONCURRENT_ABOVE });

		// left border
		//CONCURRENT_BELOW BoofConcurrency.loopFor(1,h,y->{
		for( int y = 1; y < h; y++ ) {
			int dx,dy;

			if( derivX.get(0,y) > 0 ) dx = 1; else dx = -1;
			if( derivY.get(0,y) > 0 ) dy = 1; else dy = -1;

			float left = intensity.get(-dx,y-dy);
			float middle = intensity.get(0,y);
			float right = intensity.get(dx,y+dy);

			if( left > middle || right > middle ) {
				output.set(0,y,0);
			} else {
				output.set(0,y,middle);
			}
		}
		//CONCURRENT_ABOVE });

		// right border
		int ww = w - 1;
		//CONCURRENT_BELOW BoofConcurrency.loopFor(1,h,y->{
		for( int y = 1; y < h; y++ ) {
			int dx,dy;

			if( derivX.get(ww,y) > 0 ) dx = 1; else dx = -1;
			if( derivY.get(ww,y) > 0 ) dy = 1; else dy = -1;

			float left = intensity.get(ww-dx,y-dy);
			float middle = intensity.get(ww,y);
			float right = intensity.get(ww+dx,y+dy);

			if( left > middle || right > middle ) {
				output.set(ww,y,0);
			} else {
				output.set(ww,y,middle);
			}
		}
		//CONCURRENT_ABOVE });
	}	/**
	 * Just processes the image border.
	 */
	static public void border4( GrayF32 _intensity , GrayI derivX , GrayI derivY , GrayF32 output )
	{
		int w = _intensity.width;
		int h = _intensity.height-1;

		ImageBorder_F32 intensity = FactoryImageBorderAlgs.value(_intensity, 0);

		// top border
		//CONCURRENT_BELOW BoofConcurrency.loopFor(0,w,x->{
		for( int x = 0; x < w; x++ ) {
			int dx,dy;

			if( derivX.get(x,0) > 0 ) dx = 1; else dx = -1;
			if( derivY.get(x,0) > 0 ) dy = 1; else dy = -1;

			float left = intensity.get(x-dx,-dy);
			float middle = intensity.get(x,0);
			float right = intensity.get(x+dx,dy);

			if( left > middle || right > middle ) {
				output.set(x,0,0);
			} else {
				output.set(x,0,middle);
			}
		}
		//CONCURRENT_ABOVE });

		// bottom border
		//CONCURRENT_BELOW BoofConcurrency.loopFor(0,w,x->{
		for( int x = 0; x < w; x++ ) {
			int dx,dy;

			if( derivX.get(x,h) > 0 ) dx = 1; else dx = -1;
			if( derivY.get(x,h) > 0 ) dy = 1; else dy = -1;

			float left = intensity.get(x-dx,h-dy);
			float middle = intensity.get(x,h);
			float right = intensity.get(x+dx,h+dy);

			if( left > middle || right > middle ) {
				output.set(x,h,0);
			} else {
				output.set(x,h,middle);
			}
		}
		//CONCURRENT_ABOVE });

		// left border
		//CONCURRENT_BELOW BoofConcurrency.loopFor(1,h,y->{
		for( int y = 1; y < h; y++ ) {
			int dx,dy;

			if( derivX.get(0,y) > 0 ) dx = 1; else dx = -1;
			if( derivY.get(0,y) > 0 ) dy = 1; else dy = -1;

			float left = intensity.get(-dx,y-dy);
			float middle = intensity.get(0,y);
			float right = intensity.get(dx,y+dy);

			if( left > middle || right > middle ) {
				output.set(0,y,0);
			} else {
				output.set(0,y,middle);
			}
		}
		//CONCURRENT_ABOVE });

		// right border
		int ww = w - 1;
		//CONCURRENT_BELOW BoofConcurrency.loopFor(1,h,y->{
		for( int y = 1; y < h; y++ ) {
			int dx,dy;

			if( derivX.get(ww,y) > 0 ) dx = 1; else dx = -1;
			if( derivY.get(ww,y) > 0 ) dy = 1; else dy = -1;

			float left = intensity.get(ww-dx,y-dy);
			float middle = intensity.get(ww,y);
			float right = intensity.get(ww+dx,y+dy);

			if( left > middle || right > middle ) {
				output.set(ww,y,0);
			} else {
				output.set(ww,y,middle);
			}
		}
		//CONCURRENT_ABOVE });
	}
}
