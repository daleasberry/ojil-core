/*
 * Gray32Div.java
 *
 * Created on September 9, 2006, 10:25 AM
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
import com.github.ojil.core.Gray32Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Divides a Gray32Image by a constant.
 * 
 * @author webb
 */
public class Gray32Div extends PipelineStage {
    private int nDivisor;
    
    /**
     * Creates a new instance of Gray32Div.
     * 
     * @param nDivisor
     *            The number to divide the input image by.
     * @throws ImageError
     *             if the divisor is 0.
     */
    public Gray32Div(final int nDivisor) throws ImageError {
        setDivisor(nDivisor);
    }
    
    /**
     * Divides a Gray32Image by a constant.
     * 
     * @param image
     *            the image image (and output)
     * @throws ImageError
     *             if the image is not a gray 32-bit image.
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof Gray32Image)) {
            throw new ImageError(ImageError.PACKAGE.CORE, AlgorithmErrorCodes.IMAGE_NOT_GRAY32IMAGE, image.toString(), null, null);
        }
        final Gray32Image gray = (Gray32Image) image;
        final Integer[] data = gray.getData();
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] / nDivisor;
        }
        super.setOutput(image);
    }
    
    /**
     * Changes divisor.
     * 
     * @param nDivisor
     *            The new divisor.
     * @throws ImageError
     *             if the divisor is 0.
     */
    public void setDivisor(final int nDivisor) throws ImageError {
        if (nDivisor == 0) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.MATH_DIVISION_ZERO, null, null, null);
        }
        this.nDivisor = nDivisor;
    }
}
