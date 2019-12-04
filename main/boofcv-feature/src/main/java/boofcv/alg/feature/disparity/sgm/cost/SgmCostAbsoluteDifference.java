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

package boofcv.alg.feature.disparity.sgm.cost;

import boofcv.alg.feature.disparity.sgm.SgmDisparityCost;
import boofcv.struct.image.GrayU16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageBase;

/**
 * Computes the cost as the absolute value between two pixels, i.e. cost = |left-right|.
 *
 * @author Peter Abeles
 */
public abstract class SgmCostAbsoluteDifference<T extends ImageBase<T>> implements SgmCostBase.ComputeErrors<T> {
	SgmCostBase<T> owner;

	@Override
	public void setOwner(SgmCostBase<T> owner) {
		this.owner = owner;
	}

	public static class U8 extends SgmCostAbsoluteDifference<GrayU8> {
		@Override
		public void process(int idxLeft, int idxRight, int idxOut, int disparityMin, int disparityMax, GrayU16 _costXD) {
			final int valLeft = owner.left.data[idxLeft] & 0xFF;
			final byte[] rightData = owner.right.data;
			final short[] costXD = _costXD.data;
			for (int d = disparityMin; d <= disparityMax; d++) {
				int valRight = rightData[idxRight--] & 0xFF;
				costXD[idxOut+d] = (short)(SgmDisparityCost.MAX_COST*Math.abs(valRight-valLeft)/255);
			}
		}
	}
}
