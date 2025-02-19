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

package boofcv.alg.disparity.block.score;

import boofcv.alg.InputSanityCheck;
import boofcv.alg.disparity.DisparityBlockMatchBestFive;
import boofcv.alg.disparity.block.BlockRowScore;
import boofcv.alg.disparity.block.DisparitySelect;
import boofcv.concurrency.BoofConcurrency;
import boofcv.misc.Compare_S32;
import boofcv.struct.border.ImageBorder;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageGray;
import boofcv.struct.image.ImageType;
import pabeles.concurrency.GrowArray;
import pabeles.concurrency.IntRangeObjectConsumer;

/**
 * <p>
 * Implementation of {@link boofcv.alg.feature.disparity.DisparityBlockMatchBestFive} for processing
 * images of type {@link GrayU8}.
 * </p>
 *
 * @author Peter Abeles
 */
public class DisparityScoreBMBestFive_S32<T extends ImageBase<T>, DI extends ImageGray<DI>>
		extends DisparityBlockMatchBestFive<T, DI> {
	// Computes disparity from scores
	DisparitySelect<int[], DI> disparitySelect0;

	BlockRowScore<T, int[], Object> scoreRows;

	// number of blocks that are computed
	int sampleRadiusX, sampleRadiusY;
	int sampleWidthX, sampleWidthY;

	// reference to input images;
	T left, right;
	DI disparity;

	GrowArray<WorkSpace> workspace = new GrowArray<>(WorkSpace::new);
	ComputeBlock computeBlock = new ComputeBlock();

	public DisparityScoreBMBestFive_S32( int regionRadiusX, int regionRadiusY,
										 BlockRowScore<T, int[], Object> scoreRows,
										 DisparitySelect<int[], DI> computeDisparity,
										 ImageType<T> imageType ) {
		super(regionRadiusX, regionRadiusY, imageType);
		this.scoreRows = scoreRows;
		this.disparitySelect0 = computeDisparity;
		workspace.grow();

		this.sampleRadiusX = regionRadiusX*2;
		this.sampleRadiusY = regionRadiusY*2;
		this.sampleWidthX = sampleRadiusX*2 + 1;
		this.sampleWidthY = sampleRadiusY*2 + 1;

		if (!(computeDisparity instanceof Compare_S32))
			throw new IllegalArgumentException("computeDisparity must also implement Compare_S32");
	}

	@Override
	public void setBorder( ImageBorder<T> border ) {
		super.setBorder(border);
		this.scoreRows.setBorder(border);
	}

	@Override
	public void _process( T left, T right, DI disparity ) {
		InputSanityCheck.checkSameShape(left, right);
		this.left = left;
		this.right = right;
		this.growBorderL.setImage(left);
		this.growBorderR.setImage(right);
		this.disparity = disparity;
		scoreRows.setInput(left, right);

		if (BoofConcurrency.USE_CONCURRENT) {
			BoofConcurrency.loopBlocks(0, left.height, regionHeight, workspace, computeBlock);
		} else {
			computeBlock.accept((WorkSpace)workspace.get(0), 0, left.height);
		}
	}

	class WorkSpace {
		// stores the local scores for the width of the region
		int[] elementScore;
		// scores along horizontal axis for current block
		int[][] horizontalScore;
		// summed scores along vertical axis
		// Save the last regionHeight scores in a rolling window
		int[][] verticalScore;
		int[][] verticalScoreNorm;
		// In the rolling verticalScore window, which one is the active one
		int activeVerticalScore;
		// Where the final score it stored that has been computed from five regions
		int[] fiveScore;
		// Used to store a copy of the image's row, plus outside border pixels
		Object leftRow, rightRow;

		DisparitySelect<int[], DI> computeDisparity;

		public void checkSize() {
			if (horizontalScore == null || verticalScore.length < widthDisparityBlock) {
				horizontalScore = new int[regionHeight][widthDisparityBlock];
				verticalScore = new int[regionHeight][widthDisparityBlock];
				if (scoreRows.isRequireNormalize())
					verticalScoreNorm = new int[regionHeight][widthDisparityBlock];
				elementScore = new int[left.width + 2*radiusX];
				fiveScore = new int[widthDisparityBlock];
				leftRow = left.getImageType().getDataType().newArray(elementScore.length);
				rightRow = right.getImageType().getDataType().newArray(elementScore.length);
			}
			if (computeDisparity == null) {
				computeDisparity = disparitySelect0.concurrentCopy();
			}
			computeDisparity.configure(disparity, disparityMin, disparityMax, radiusX*2);
		}
	}

	private class ComputeBlock implements IntRangeObjectConsumer<WorkSpace> {
		@Override
		public void accept( WorkSpace workspace, int minInclusive, int maxExclusive ) {
			// NOTE: for out of image pixels maybe the approach used in horizontal direction should be adapted?
			workspace.checkSize();
//			int row0 = Math.max(0,minInclusive-2*radiusY);
//			int row1 = Math.min(left.height,maxExclusive+2*radiusY);
			int row0 = minInclusive - 2*radiusY;
			int row1 = maxExclusive + 2*radiusY;

			// initialize computation
			computeFirstRow(row0, workspace);

			// efficiently compute rest of the rows using previous results to avoid repeat computations
			computeRemainingRows(row0, row1, workspace);
		}
	}

	/**
	 * Initializes disparity calculation by finding the scores for the initial block of horizontal
	 * rows.
	 */
	private void computeFirstRow( final int row0, final WorkSpace ws ) {
		int disparityMax = Math.min(left.width, this.disparityMax);

		ws.activeVerticalScore = 1;

		// compute horizontal scores for first row block
		for (int row = 0; row < regionHeight; row++) {
			growBorderL.growRow(row0 + row, radiusX, radiusX, ws.leftRow, 0);
			growBorderR.growRow(row0 + row, radiusX, radiusX, ws.rightRow, 0);
			int[] scores = ws.horizontalScore[row];
			scoreRows.scoreRow(row0 + row, ws.leftRow, ws.rightRow, scores, disparityMin, disparityMax, regionWidth, ws.elementScore);
		}

		// compute score for the top possible row
		final int[] firstRow = ws.verticalScore[0];
		for (int i = 0; i < widthDisparityBlock; i++) {
			int sum = 0;
			for (int row = 0; row < regionHeight; row++) {
				sum += ws.horizontalScore[row][i];
			}
			firstRow[i] = sum;
		}

		if (scoreRows.isRequireNormalize() && row0 + radiusY >= 0) {
			scoreRows.normalizeRegionScores(row0 + radiusY,
					firstRow, disparityMin, disparityMax, regionWidth, regionHeight, ws.verticalScoreNorm[0]);
		}
	}

	/**
	 * Using previously computed results it efficiently finds the disparity in the remaining rows.
	 * When a new block is processes the last row/column is subtracted and the new row/column is
	 * added.
	 */
	private void computeRemainingRows( final int row0, final int row1, final WorkSpace ws ) {
		int disparityMax = Math.min(left.width, this.disparityMax);

		for (int row = row0 + regionHeight; row < row1; row++, ws.activeVerticalScore++) {
			int activeIndex = ws.activeVerticalScore%regionHeight;
			int oldRow = (row - row0)%regionHeight;
			int[] previous = ws.verticalScore[(ws.activeVerticalScore - 1)%regionHeight];
			int[] active = ws.verticalScore[activeIndex];

			// subtract first row from vertical score
			int[] scores = ws.horizontalScore[oldRow];
			for (int i = 0; i < widthDisparityBlock; i++) {
				active[i] = previous[i] - scores[i];
			}

			growBorderL.growRow(row, radiusX, radiusX, ws.leftRow, 0);
			growBorderR.growRow(row, radiusX, radiusX, ws.rightRow, 0);
			scoreRows.scoreRow(row, ws.leftRow, ws.rightRow, scores, disparityMin, disparityMax, regionWidth, ws.elementScore);

			// add the new score
			for (int i = 0; i < widthDisparityBlock; i++) {
				active[i] += scores[i];
			}

			if (scoreRows.isRequireNormalize() && row >= radiusY && row < left.height + radiusY) {
				scoreRows.normalizeRegionScores(row - radiusY,
						active, disparityMin, disparityMax, regionWidth, regionHeight, ws.verticalScoreNorm[activeIndex]);
			}

			if (ws.activeVerticalScore >= 2*radiusY) {
				// The y-axis in the output disparity image
				int disparityY = row - 2*radiusY;
				// always compute the score using a row that's inside the image
				// This greatly simplifies normalizeRegionScores() code
				int off0 = -2*radiusY;
				int off1 = -radiusY;
				int off2 = 0;

				if (disparityY - radiusY < 0) {
					off0 = off0 - (disparityY - radiusY);
				}
				if (disparityY + radiusY >= left.height) {
					off2 = off2 - (disparityY + radiusY - left.height) - 1;
				}

				// The five-regions have different rows seperated by -radiusY. Use either normalized
				// or unnormalized scores
				int[] top, middle, bottom;
				if (scoreRows.isRequireNormalize()) {
					top = ws.verticalScoreNorm[(ws.activeVerticalScore + off0)%regionHeight];
					middle = ws.verticalScoreNorm[(ws.activeVerticalScore + off1)%regionHeight];
					bottom = ws.verticalScoreNorm[(ws.activeVerticalScore + off2)%regionHeight];
				} else {
					top = ws.verticalScore[(ws.activeVerticalScore + off0)%regionHeight];
					middle = ws.verticalScore[(ws.activeVerticalScore + off1)%regionHeight];
					bottom = ws.verticalScore[(ws.activeVerticalScore + off2)%regionHeight];
				}

				computeScoreFive(top, middle, bottom, ws.fiveScore, left.width, (Compare_S32)ws.computeDisparity);
				ws.computeDisparity.process(disparityY, ws.fiveScore);
			}
		}
	}

	/**
	 * Compute the final score by sampling the 5 regions. Four regions are sampled around the center
	 * region. Out of those four only the two with the smallest score are used.
	 */
	protected void computeScoreFive( int[] top, int[] middle, int[] bottom, int[] score, int width,
									 Compare_S32 compare ) {
		int disparityMax = Math.min(left.width, this.disparityMax);
		final int WORST_SCORE = Integer.MAX_VALUE*compare.compare(0, 1);

		// disparity as the outer loop to maximize common elements in inner loops, reducing redundant calculations
		for (int d = disparityMin; d <= disparityMax; d++) {
			// take in account the different in image border between the sub-regions and the effective region
			int indexSrc = (d - disparityMin)*width + (d - disparityMin);
			int indexDst = (d - disparityMin)*width + (d - disparityMin);

			for (int i = 0; i < width - d; i++, indexSrc++) {
				int val0 = WORST_SCORE;
				int val1 = WORST_SCORE;
				int val2 = WORST_SCORE;
				int val3 = WORST_SCORE;

				if (i + d + radiusX < width) { // is the sample in the left image inside
					val1 = top[indexSrc + radiusX];
					val3 = bottom[indexSrc + radiusX];
				}
				if (i - radiusX >= 0) { // is the sample in the right image inside
					val0 = top[indexSrc - radiusX];
					val2 = bottom[indexSrc - radiusX];
				}
				// select the two best scores from outer for regions
				if (compare.compare(val0, val1) < 0) {
					int temp = val0;
					val0 = val1;
					val1 = temp;
				}

				if (compare.compare(val2, val3) < 0) {
					int temp = val2;
					val2 = val3;
					val3 = temp;
				}

				int s;
				if (compare.compare(val0, val3) < 0) {
					s = val2 + val3;
				} else if (compare.compare(val1, val2) < 0) {
					s = val2 + val0;
				} else {
					s = val0 + val1;
				}

				score[indexDst++] = s + middle[indexSrc];
			}
		}
	}

	@Override
	public ImageType<T> getInputType() {
		return scoreRows.getImageType();
	}

	@Override
	public Class<DI> getDisparityType() {
		return disparitySelect0.getDisparityType();
	}

	@Override
	public int getMaxRegionError() {
		return 3*regionWidth*regionHeight*getMaxPerPixelError();
	}

	@Override
	protected int getMaxPerPixelError() {
		return scoreRows.getMaxPerPixelError();
	}
}
