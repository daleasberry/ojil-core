/*
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
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbVal;

/**
 * Thresholds an RgbImage based on a given RGB value and another RGB value that
 * represents the masimum RGB distance from the first value that a pixel can lie
 * in to be considered within the threshold. The computation is analogous to a
 * vector distance from a given point. It can be described as </br> (rgb -
 * rgbTarget) &middot rgbVec &lt; threshold
 * 
 * @author webb
 */
public class RgbVecThresh extends PipelineStage {
    private final int nR, nG, nB;
    private final int nRVec, nGVec, nBVec;
    private final int nThresh;
    private final boolean bWithin;
    
    /**
     * Initialize class to filter based on given target rgb value and vector
     * distance from the target value measured along a particular vector. The
     * bWithin parameter allows the threshold test to be reversed -- bWithin =
     * false causes true (Byte.MAX_VALUE) to be output for rgb values &gt; than
     * the test.
     * 
     * @param rgbTarget
     *            target RGB value
     * @param rgbVec
     *            direction and distance of test
     * @param nThresh
     *            threshold to compare with
     * @param bWithin
     *            if true result is true if &lt; test; if false, result is true
     *            if &gt; than the test.
     */
    public RgbVecThresh(final int rgbTarget, final int rgbVec, final int nThresh, final boolean bWithin) {
        nR = RgbVal.getR(rgbTarget) - Byte.MIN_VALUE;
        nG = RgbVal.getG(rgbTarget) - Byte.MIN_VALUE;
        nB = RgbVal.getB(rgbTarget) - Byte.MIN_VALUE;
        nRVec = RgbVal.getR(rgbVec);
        nGVec = RgbVal.getG(rgbVec);
        nBVec = RgbVal.getB(rgbVec);
        this.nThresh = nThresh;
        this.bWithin = bWithin;
    }
    
    /**
     * Perform thresholding operation. Output is a gray image.
     * 
     * @param imageInput
     *            input RgbImage to be thresholded.
     * @throws ImageError
     *             if input is not an RgbImage.
     */
    @Override
    public void push(final Image<?, ?> imageInput) throws ImageError {
        if (!(imageInput instanceof RgbImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, imageInput.toString(), null, null);
        }
        final RgbImage<?> rgb = (RgbImage<?>) imageInput;
        final Integer[] nData = rgb.getData();
        final Gray8Image<?> imageResult = new Gray8Image<>(rgb.getWidth(), rgb.getHeight());
        final Byte[] bData = imageResult.getData();
        for (int i = 0; i < (rgb.getWidth() * rgb.getHeight()); i++) {
            final int nRCurr = RgbVal.getR(nData[i]) - Byte.MIN_VALUE;
            final int nGCurr = RgbVal.getG(nData[i]) - Byte.MIN_VALUE;
            final int nBCurr = RgbVal.getB(nData[i]) - Byte.MIN_VALUE;
            final int nRDiff = nRCurr - nR;
            final int nGDiff = nGCurr - nG;
            final int nBDiff = nBCurr - nB;
            final int nDot = Math.abs((nRDiff * nRVec) + (nGDiff * nGVec) + (nBDiff * nBVec));
            bData[i] = ((nDot < nThresh) == bWithin) ? Byte.MAX_VALUE : Byte.MIN_VALUE;
            
        }
        super.setOutput(imageResult);
    }
    
}
