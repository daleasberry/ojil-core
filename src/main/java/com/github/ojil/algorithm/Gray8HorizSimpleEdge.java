/*
 * CannyHoriz.java
 *
 * Created on August 27, 2006, 4:32, PM
 *
 * To change this templatef, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * Copyright 2007 by Jon A. Webb
 *     This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the Lesser GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ojil.algorithm;

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Computes a simple horizontal edge measure. The measure is simply the
 * difference between the current pixel and the one to the left, clamped between
 * Byte.MIN_VALUE and Byte.MAX_VALUE. The output replaces the input.
 * 
 * @author webb
 */
public class Gray8HorizSimpleEdge extends PipelineStage {
    
    /**
     * Creates a new instance of Gray8HorizSimpleEdge
     */
    public Gray8HorizSimpleEdge() {
    }
    
    /**
     * Compute a simple horizontal edge measure. The measure is simply the
     * difference between the current pixel and the one to the left, clamped
     * between Byte.MIN_VALUE and Byte.MAX_VALUE. The output replaces the input.
     *
     * @param image
     *            the input Gray8Image
     * @throws ImageError
     *             if image is not a Gray8Image
     */
    @Override
    public void push(final Image<?, ?> image) throws ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        final Gray8Image<?> input = (Gray8Image<?>) image;
        final Byte[] bIn = input.getData();
        final int cWidth = input.getWidth();
        for (int i = 0; i < input.getHeight(); i++) {
            int nPrev;
            int nThis = bIn[i * cWidth];
            for (int j = 0; j < cWidth; j++) {
                nPrev = nThis;
                nThis = bIn[(i * cWidth) + j];
                final int nVal = Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, nPrev - nThis));
                bIn[(i * cWidth) + j] = (byte) nVal;
            }
        }
        super.setOutput(input);
    }
    
}
