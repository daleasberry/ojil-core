/*
 * Gray8QmSum.java
 *
 * Created on August 27, 2006, 9:02 AM
 *
 * To change this template, choose Tools | Template Manager
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

import com.github.ojil.core.Gray32Image;
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Gray8QmSum forms the cumulative sum of an image. <blockquote> Output(i,j) =
 * &sum;<sub>k &le; i, l &le; j</sub> Input(k,l). </blockquote> Output is a
 * 32-bit gray image. Input is an 8-bit gray image.
 * <p>
 * Note that since the output is 32 bits the input image cannot have more than
 * 2<sup>24</sup> pixels without risking overflow. For example, an image larger
 * than 4096 &times; 4096 (=2<sup>12</sup> &times; 2<sup>12</sup>) might
 * overflow.
 * 
 * @author webb
 */
public class Gray8QmSum extends PipelineStage {
    
    /**
     * Creates a new instance of Gray8QmSum
     */
    public Gray8QmSum() {
    }
    
    /**
     * Forms the cumulative sum of an image. Output(i,j) = &sum;<sub>k &le; i, l
     * &le; j</sub> Input(k,l)). Output is 32-bit gray image. Input is 8-bit
     * gray image.
     *
     * @param image
     *            the input image.
     * @throws ImageError
     *             if the input is not a Gray8Image
     */
    @Override
    public void push(final Image<?, ?> image) throws ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        final Gray8Image<?> gray = (Gray8Image<?>) image;
        final Gray32Image<?> gray32 = new Gray32Image<>(image.getWidth(), image.getHeight());
        final Byte[] grayData = gray.getData();
        final Integer[] gray32Data = gray32.getData();
        // First row
        int nSum = 0;
        for (int j = 0; j < gray.getWidth(); j++) {
            /*
             * Convert from signed byte value to unsigned byte for storage in
             * the 32-bit image.
             */
            final int grayUnsigned = (grayData[j]) - Byte.MIN_VALUE;
            /* Assign 32-bit output */
            nSum += grayUnsigned;
            gray32Data[j] = nSum;
        }
        // Other rows
        for (int i = 1; i < gray.getHeight(); i++) {
            nSum = 0;
            for (int j = 0; j < gray.getWidth(); j++) {
                /*
                 * Convert from signed byte value to unsigned byte for storage
                 * in the 32-bit image.
                 */
                final int grayUnsigned = (grayData[(i * gray.getWidth()) + j]) - Byte.MIN_VALUE;
                nSum += grayUnsigned;
                gray32Data[(i * gray.getWidth()) + j] = gray32Data[((i - 1) * gray.getWidth()) + j] + nSum;
            }
        }
        super.setOutput(gray32);
    }
}
