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

package boofcv.struct.kmeans;

import boofcv.struct.feature.TupleDesc_U8;
import org.ddogleg.struct.DogArray_I8;

/**
 * Stores a set of tuples in a single continuous array. This is intended to make storage of a large number of tuples
 * more memory efficient by removing all the packaging that Java adds to a class. The memory is also continuous,
 * opening the possibility of further optimizations.
 *
 * @author Peter Abeles
 */
public class PackedTupleArray_U8 implements PackedArray<TupleDesc_U8> {
	// degree-of-freedom, number of elements in the tuple
	public final int dof;
	// Stores tuple in a single continuous array
	public final DogArray_I8 array;
	// tuple that the result is temporarily written to
	public final TupleDesc_U8 temp;

	// Number of tuples stored in the array
	protected int numElements;

	public PackedTupleArray_U8( int dof ) {
		this.dof = dof;
		this.temp = new TupleDesc_U8(dof);
		array = new DogArray_I8(dof*20);
		array.resize(0);
	}

	@Override public void reset() {
		numElements = 0;
		array.reset();
	}

	@Override public void reserve( int numTuples ) {
		array.reserve(numTuples*dof);
	}

	@Override public void addCopy( TupleDesc_U8 element ) {
		array.addAll(element.value, 0, dof);
		numElements++;
	}

	@Override public TupleDesc_U8 getTemp( int index ) {
		System.arraycopy(array.data, index*dof, temp.value, 0, dof);
		return temp;
	}

	@Override public void getCopy( int index, TupleDesc_U8 dst ) {
		System.arraycopy(array.data, index*dof, dst.value, 0, dof);
	}

	@Override public void copy( TupleDesc_U8 src, TupleDesc_U8 dst ) {
		System.arraycopy(src.value, 0, dst.value, 0, dof);
	}

	@Override public int size() {
		return numElements;
	}

	@Override public Class<TupleDesc_U8> getElementType() {
		return TupleDesc_U8.class;
	}
}
