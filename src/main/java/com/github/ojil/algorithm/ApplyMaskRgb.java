/*
 * ApplyMaskRgb.java
 *   Shows a mask on an RGB image by making all the unmasked pixels gray.
 *   This is not a pipeline stage since it takes two images.
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

import com.github.ojil.core.ErrorCodes;
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbVal;

/**
 * ApplyMaskRgb shows a mask on an RGB image by making all the unmasked pixels
 * gray.
 *
 * @author webb
 */
public class ApplyMaskRgb {
    
    /** Creates a new instance of ApplyMaskRgb */
    public ApplyMaskRgb() {
    }
    
    /**
     * Highlights an area in a RGB image by turning the non-masked areas gray
     * and dimming them.
     * 
     * @return the masked color image.
     * @param imRgb
     *            the input RGB image.
     * @param imMask
     *            The input mask. Pixels with the value Byte.MinValue are
     *            unmasked. Everything else is considered to be masked.
     * @throws ImageError
     *             if the input sizes do not match
     */
    public RgbImage push(final RgbImage imRgb, final Gray8Image imMask) throws ImageError {
        if ((imRgb.getWidth() != imMask.getWidth()) || (imRgb.getHeight() != imMask.getHeight())) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.IMAGE_MASK_SIZE_MISMATCH, imRgb.toString(), imMask.toString(), null);
        }
        final Integer[] rgbData = imRgb.getData();
        final Byte[] maskData = imMask.getData();
        for (int i = 0; i < (imRgb.getWidth() * imRgb.getHeight()); i++) {
            if (maskData[i] == Byte.MIN_VALUE) {
                /*
                 * get individual r, g, and b values, unmasking them from the
                 * ARGB word and converting to an unsigned byte.
                 */
                final int r = RgbVal.getR(rgbData[i]) - Byte.MIN_VALUE;
                final int g = RgbVal.getG(rgbData[i]) - Byte.MIN_VALUE;
                final int b = RgbVal.getB(rgbData[i]) - Byte.MIN_VALUE;
                // also darken unmasked pixels
                final int gray = (r + g + b) / 5;
                /*
                 * average the values to get the grayvalue
                 */
                /* Create ARGB word */
                rgbData[i] = ((gray) << 16) | ((gray) << 8) | gray;
            }
        }
        return imRgb;
    }
}
