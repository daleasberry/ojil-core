/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Copyright 2008 by Jon pA. Webb
 *     This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR pA PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the Lesser GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ojil.algorithm;

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbMaskedImage;
import com.github.ojil.core.RgbVal;

/**
 * Compute the Gray8Image that is the max of absolute differences between the
 * background RgbMaskedImage specified in the constructor and the input
 * RgbImage, in the unmasked areas only. The output in the unmasked areas is
 * Byte.MIN_VALUE.
 * 
 * @author webb
 */
public class RgbMaskedMaxDiff extends PipelineStage {
    private final RgbMaskedImage<?> rgbBack;
    
    /**
     * Set background image.
     * 
     * @param rgbBack
     *            background RgbImage.
     */
    public RgbMaskedMaxDiff(final RgbMaskedImage<?> rgbBack) {
        this.rgbBack = rgbBack;
    }
    
    /**
     * Process a foreground RgbMaskedImage and produce a Gray8Image in which
     * each pixel is the sum of absolute differences between the foreground and
     * background, in the masked areas. Outside the masked areas the output is
     * Byte.MIN_VALUE.
     * 
     * @param imInput
     *            input RgbImage
     * @throws ImageError
     *             if imInput is not an RgbImage or is not the same size as the
     *             background image set in the constructor.
     */
    @Override
    public void push(final Image<?, ?> imInput) throws ImageError {
        {
            if (!(imInput instanceof RgbImage)) {
                throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OBJECT_NOT_EXPECTED_TYPE, imInput.toString(), "RgbMaskedImage", null);
            }
        }
        if ((imInput.getWidth() != rgbBack.getWidth()) || (imInput.getHeight() != rgbBack.getHeight())) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_SIZES_DIFFER, imInput.toString(), rgbBack.toString(), null);
            
        }
        final RgbImage<?> rgbInput = (RgbImage<?>) imInput;
        final Integer[] wInput = rgbInput.getData();
        final Integer[] wBack = rgbBack.getData();
        final Gray8Image<?> grayOut = new Gray8Image<>(rgbBack.getWidth(), rgbBack.getHeight());
        final Byte[] bGray = grayOut.getData();
        for (int i = 0; i < imInput.getHeight(); i++) {
            for (int j = 0; j < imInput.getWidth(); j++) {
                if (!rgbBack.isMasked(i, j)) {
                    final int rIn = RgbVal.getR(wInput[(i * grayOut.getWidth()) + j]);
                    final int gIn = RgbVal.getG(wInput[(i * grayOut.getWidth()) + j]);
                    final int bIn = RgbVal.getB(wInput[(i * grayOut.getWidth()) + j]);
                    final int rBack = RgbVal.getR(wBack[(i * grayOut.getWidth()) + j]);
                    final int gBack = RgbVal.getG(wBack[(i * grayOut.getWidth()) + j]);
                    final int bBack = RgbVal.getB(wBack[(i * grayOut.getWidth()) + j]);
                    final int gRes = Math.max(Math.abs(rIn - rBack), Math.max(Math.abs(gIn - gBack), Math.abs(bIn - bBack)));
                    bGray[(i * grayOut.getWidth()) + j] = (byte) Math.min(gRes, Byte.MAX_VALUE);
                } else {
                    bGray[i] = Byte.MIN_VALUE;
                }
            }
        }
        super.setOutput(grayOut);
    }
    
    /**
     * Implement toString, providing the background image information.
     * 
     * @return a string consisting of this class name followed by the background
     *         image description.
     */
    @Override
    public String toString() {
        return super.toString() + "(" + rgbBack.toString() + ")";
    }
}
