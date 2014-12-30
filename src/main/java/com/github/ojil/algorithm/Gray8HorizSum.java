/*
 * 
 * Copyright 2008 by Jon A. Webb
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
 */

package com.github.ojil.algorithm;

import com.github.ojil.core.Gray32Image;
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Sum a Gray8Image in a horizontal window, with the width controllable,
 * creating a Gray32Image.
 * 
 * @author webb
 */
public class Gray8HorizSum extends PipelineStage {
    int nSumWidth;
    
    /**
     * Initialize Gray8HorizSum. The width is set here.
     * 
     * @param nWidth
     *            width of the sum.
     */
    public Gray8HorizSum(final int nWidth) {
        nSumWidth = nWidth;
    }
    
    /**
     * Sum a Gray8Image horizontally, creating a Gray32Image. The edges of the
     * image (closer than width) are set to 0. The summing is done efficiently
     * so that each pixel computation takes only 2 additions on average.
     * <p>
     * This code uses Gray8QmSum to form a cumulative sum of the whole image.
     * Since the entire image is summed to a Gray32Image by Gray8QmSum overflow
     * may occur if more thant 2**24 pixels are in the image (e.g., larger than
     * 2**12x2**12 = 4096x4096).
     * 
     * @param imageInput
     *            input Gray8Image.
     * @throws ImageError
     *             if the input is not a Gray8Image.
     */
    @Override
    public void push(final Image<?> imageInput) throws ImageError {
        if (!(imageInput instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, imageInput.toString(), null, null);
        }
        final Gray8QmSum gqs = new Gray8QmSum();
        gqs.push(imageInput);
        final Gray32Image gSum = (Gray32Image) gqs.getFront();
        final Integer[] sData = gSum.getData();
        final Gray32Image gResult = new Gray32Image(imageInput.getWidth(), imageInput.getHeight());
        final Integer[] gData = gResult.getData();
        for (int i = 1; i < imageInput.getHeight(); i++) {
            for (int j = 0; j < nSumWidth; j++) {
                gData[(i * imageInput.getWidth()) + j] = 0;
            }
            for (int j = nSumWidth; j < imageInput.getWidth(); j++) {
                gData[(i * imageInput.getWidth()) + j] = sData[(i * imageInput.getWidth()) + j] - sData[((i * imageInput.getWidth()) + j) - nSumWidth];
            }
        }
        for (int i = 1; i < imageInput.getHeight(); i++) {
            for (int j = 0; j < nSumWidth; j++) {
                gData[(i * imageInput.getWidth()) + j] = 0;
            }
            for (int j = nSumWidth; j < imageInput.getWidth(); j++) {
                gData[(i * imageInput.getWidth()) + j] = (sData[(i * imageInput.getWidth()) + j] - sData[((i * imageInput.getWidth()) + j) - nSumWidth] - sData[((i - 1) * imageInput.getWidth()) + j])
                        + sData[(((i - 1) * imageInput.getWidth()) + j) - nSumWidth];
            }
        }
        super.setOutput(gResult);
    }
    
}
