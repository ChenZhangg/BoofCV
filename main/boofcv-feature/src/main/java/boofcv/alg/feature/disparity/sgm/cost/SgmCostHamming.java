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

import boofcv.alg.descriptor.DescriptorDistance;
import boofcv.alg.feature.disparity.sgm.SgmDisparityCost;
import boofcv.struct.image.*;

/**
 * Computes the cost as the hamming distance between two pixels.
 *
 * @author Peter Abeles
 */
public abstract class SgmCostHamming<T extends ImageBase<T>> implements SgmCostBase.ComputeErrors<T> {
	SgmCostBase<T> owner;

	@Override
	public void setOwner(SgmCostBase<T> owner) {
		this.owner = owner;
	}

	public static class U8 extends SgmCostHamming<GrayU8> {
		@Override
		public void process(int idxLeft, int idxRight, int idxOut, int disparityMin, int disparityMax, GrayU16 _costXD) {
			final int valLeft = owner.left.data[idxLeft] & 0xFF;
			final byte[] rightData = owner.right.data;
			final short[] costXD = _costXD.data;
			for (int d = disparityMin; d <= disparityMax; d++) {
				int valRight = rightData[idxRight--] & 0xFF;
				costXD[idxOut+d] = (short) (SgmDisparityCost.MAX_COST*DescriptorDistance.hamming(valLeft^valRight)/8);
			}
		}
	}

	public static class S32 extends SgmCostHamming<GrayS32> {
		@Override
		public void process(int idxLeft, int idxRight, int idxOut, int disparityMin, int disparityMax, GrayU16 _costXD) {
			final int valLeft = owner.left.data[idxLeft];
			final int[] rightData = owner.right.data;
			final short[] costXD = _costXD.data;
			for (int d = disparityMin; d <= disparityMax; d++) {
				int valRight = rightData[idxRight--];
				costXD[idxOut+d] = (short) (SgmDisparityCost.MAX_COST*DescriptorDistance.hamming(valLeft^valRight)/32);
			}
		}
	}

	public static class S64 extends SgmCostHamming<GrayS64> {
		@Override
		public void process(int idxLeft, int idxRight, int idxOut, int disparityMin, int disparityMax, GrayU16 _costXD) {
			final long valLeft = owner.left.data[idxLeft];
			final long[] rightData = owner.right.data;
			final short[] costXD = _costXD.data;
			for (int d = disparityMin; d <= disparityMax; d++) {
				long valRight = rightData[idxRight--];
				costXD[idxOut+d] = (short) (SgmDisparityCost.MAX_COST*DescriptorDistance.hamming(valLeft^valRight)/64);
			}
		}
	}
}
