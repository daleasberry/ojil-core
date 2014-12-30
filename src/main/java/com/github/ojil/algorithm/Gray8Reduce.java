/*
 * Gray8Reduce.java
 *
 * Created on September 2, 2006, 3:59 PM
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
import com.github.ojil.core.PipelineStage;

/**
 * Pipeline stage reduces an image's size by rectangular averaging. The
 * reduction factor must evenly divide the image size. No smoothing is done.
 *
 * @author webb
 */
public class Gray8Reduce extends PipelineStage {
    private int cReduceHeight;
    private int cReduceWidth;
    
    /**
     * Creates a new instance of Gray8Reduce.
     * 
     * @param cReduceWidth
     *            amount to reduce the width by
     * @param cReduceHeight
     *            amount to reduce the height by
     * @throws ImageError
     *             if the reduce width or height is less than or equal to zero.
     */
    public Gray8Reduce(final int cReduceWidth, final int cReduceHeight) throws ImageError {
        setReductionFactor(cReduceWidth, cReduceHeight);
    }
    
    /**
     * Reduces a gray image by a factor horizontally and vertically through
     * averaging. The reduction factor must be an even multiple of the image
     * size.
     *
     * @param image
     *            the input image.
     * @throws ImageError
     *             if the input image is not gray, or the reduction factor does
     *             not evenly divide the image size.
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        if ((image.getWidth() % cReduceWidth) != 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        if ((image.getHeight() % cReduceHeight) != 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.REDUCE_INPUT_IMAGE_NOT_MULTIPLE_OF_OUTPUT_SIZE, image.toString(), toString(), null);
        }
        final Gray8Image gray = (Gray8Image) image;
        final Byte[] bIn = gray.getData();
        final int cReducedHeight = image.getHeight() / cReduceHeight;
        final int cReducedWidth = image.getWidth() / cReduceWidth;
        final Gray8Image result = new Gray8Image(cReducedWidth, cReducedHeight);
        final Byte[] bOut = result.getData();
        for (int i = 0; i < cReducedHeight; i++) {
            for (int j = 0; j < cReducedWidth; j++) {
                int sum = 0;
                for (int k = 0; k < cReduceHeight; k++) {
                    for (int l = 0; l < cReduceWidth; l++) {
                        sum += bIn[(((i * cReduceHeight) + k) * image.getWidth()) + ((j * cReduceWidth) + l)];
                    }
                }
                bOut[(i * cReducedWidth) + j] = (byte) (sum / (cReduceHeight * cReduceWidth));
            }
        }
        super.setOutput(result);
    }
    
    /**
     * Returns the height reduction factor.
     *
     * @return the height reduction factor.
     */
    public int getHeightReduction() {
        return cReduceHeight;
    }
    
    /**
     * Returns the width reduction factor.
     *
     * @return the width reduction factor.
     */
    public int getWidthReduction() {
        return cReduceWidth;
    }
    
    /**
     * Sets a new width, height reduction factor.
     *
     * @param cReduceWidth
     *            the amount by which to reduce the image width.
     * @param cReduceHeight
     *            the amount by which to reduce the image height.
     * @throws ImageError
     *             if either cReduceWidth or cReduceHeight is less than or equal
     *             to 0.
     */
    public void setReductionFactor(final int cReduceWidth, final int cReduceHeight) throws ImageError {
        if ((cReduceWidth <= 0) || (cReduceHeight <= 0)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OUTPUT_IMAGE_SIZE_NEGATIVE, new Integer(cReduceWidth).toString(), new Integer(cReduceHeight).toString(), null);
        }
        this.cReduceWidth = cReduceWidth;
        this.cReduceHeight = cReduceHeight;
    }
    
    /**
     * Return a string describing the reduction operation.
     *
     * @return the string describing the reduction operation.
     */
    @Override
    public String toString() {
        return super.toString() + " (" + cReduceWidth + "," + //$NON-NLS-1$ //$NON-NLS-2$
                cReduceHeight + ")"; //$NON-NLS-1$
    }
}
