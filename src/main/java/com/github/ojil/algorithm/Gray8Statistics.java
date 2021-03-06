/*
 * Gray8Statistics.java
 *
 * Created on November 11, 2006, 2:17 PM
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

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.MathPlus;

/**
 * Gray8Statistics is used to measure the mean and variance of a gray image.
 * 
 * 
 * @author webb
 */
public class Gray8Statistics {
    private int nMean; // mean image value, times 256
    private int nVariance; // image variance, times 256
    
    /**
     * Creates a new instance of Gray8Statistics
     */
    public Gray8Statistics() {
    }
    
    /**
     * Estimate the mean and variance of an input gray image.
     *
     * @param image
     *            the input image.
     * @throws ImageError
     *             if the input image is not gray.
     */
    public void push(final Image<?, ?> image) throws ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        final Gray8Image<?> gray = (Gray8Image<?>) image;
        int nSum = 0, nSumSq = 0;
        final Byte[] data = gray.getData();
        for (int i = 0; i < gray.getHeight(); i++) {
            for (int j = 0; j < gray.getWidth(); j++) {
                final int pixel = (data[(i * image.getWidth()) + j]) - Byte.MIN_VALUE;
                nSum += pixel;
                nSumSq += pixel * pixel;
            }
        }
        /**
         * Compute mean and variance. Both are scaled by 256 for accuracy.
         */
        final int nCount = image.getHeight() * image.getWidth();
        nMean = (256 * nSum) / nCount;
        // expanded form of variance computation
        // note order of multiplications and divisions. we're trying to
        // avoid overflow here.
        nVariance = ((nSumSq / (nCount - 1)) - (((nSum / nCount) * nSum) / (nCount - 1))) << 8;
    }
    
    /**
     * Return computed mean, times 256.
     *
     * @return the mean value, times 256.
     */
    public int getMean() {
        return nMean;
    }
    
    /**
     * Return standard deviation, times 256 using Newton's iteration.
     * 
     * @return the standard deviation, times 256.
     * @throws ImageError
     *             if the variance computed in push() is less than zero.
     */
    public int getStdDev() throws ImageError {
        // n = variance * 256 * 256 (for accuracy)
        final int n = getVariance() << 8; // getVariance() already is * 256
        if (n < 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.STATISTICS_VARIANCE_LESS_THAN_ZERO, new Integer(n).toString(), null, null);
        }
        // return standard deviation * 256 = sqrt(variance * 256 * 256)
        return MathPlus.sqrt(n);
    }
    
    /**
     * Return computed variance, times 256.
     *
     * @return the computed variance value.
     */
    public int getVariance() {
        return nVariance;
    }
}
