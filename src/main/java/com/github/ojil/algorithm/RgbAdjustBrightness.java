/*
 * RgbAdjustBrightness.java
 *
 * Created on August 27, 2006, 2:38 PM
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

import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbVal;

/**
 * Pipeline stage adjusts brightness of red, green, and blue bands independently
 * by a multiplicative factor.
 * 
 * @author webb
 */
public class RgbAdjustBrightness extends PipelineStage {
    int nRedFac, nGreenFac, nBlueFac;
    
    /**
     * Creates a new instance of RgbAdjustBrightness. Multiplicative factors are
     * specified here.
     *
     * @param nRed
     *            red factor (scaled by 256)
     * @param nGreen
     *            green factor (scaled by 256)
     * @param nBlue
     *            blue factor (scaled by 256)
     */
    public RgbAdjustBrightness(final int nRed, final int nGreen, final int nBlue) {
        nRedFac = nRed;
        nGreenFac = nGreen;
        nBlueFac = nBlue;
    }
    
    /**
     * Adjust brightness of RGB image. This is an in-place modification; input
     * is modified.
     *
     * @param image
     *            the input image.
     */
    @Override
    public void push(final Image<?, ?> image) throws ImageError {
        if (!(image instanceof RgbImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, image.toString(), null, null);
        }
        final RgbImage<?> imageInput = (RgbImage<?>) image;
        final Integer[] rgb = imageInput.getData();
        for (int i = 0; i < (imageInput.getHeight() * imageInput.getWidth()); i++) {
            // the scaling has to be done on unsigned values.
            int nRed = RgbVal.getR(rgb[i]) - Byte.MIN_VALUE;
            int nGreen = RgbVal.getG(rgb[i]) - Byte.MIN_VALUE;
            int nBlue = RgbVal.getB(rgb[i]) - Byte.MIN_VALUE;
            // scale and convert back to signed byte values
            nRed = Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, ((nRed * nRedFac) / 256) + Byte.MIN_VALUE));
            nGreen = Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, ((nGreen * nGreenFac) / 256) + Byte.MIN_VALUE));
            nBlue = Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, ((nBlue * nBlueFac) / 256) + Byte.MIN_VALUE));
            rgb[i] = RgbVal.toRgb((byte) nRed, (byte) nGreen, (byte) nBlue);
        }
        super.setOutput(imageInput);
    }
    
    /**
     * Return a string describing the cropping operation.
     *
     * @return the string describing the cropping operation.
     */
    @Override
    public String toString() {
        return super.toString() + " (" + nRedFac + "," + nGreenFac + "," + nBlueFac + ")"; //$NON-NLS-1$
    }
}
