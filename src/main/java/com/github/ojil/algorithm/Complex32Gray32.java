/*
 * Complex32Gray32.java
 *
 * Created on November 5, 2007, 4:12 PM
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

import com.github.ojil.core.Complex;
import com.github.ojil.core.Complex32Image;
import com.github.ojil.core.Gray32Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Converts a Complex32Image to a Gray32Image by taking the complex magnitude of
 * each pixel.
 * 
 * @author webb
 */
public class Complex32Gray32 extends PipelineStage {
    
    /**
     * Creates a new instance of Complex32Gray32
     */
    public Complex32Gray32() {
    }
    
    /**
     * Convert an input Complex32Image to an output Gray32Image by taking the
     * complex magnitude of each pixel.
     * 
     * @param im
     *            Input image. Must be a Complex32Image.
     * @throws com.github.ojil.core.ImageError
     *             if the input is not of type Complex32Image.
     */
    @Override
    public void push(final Image<?> im) throws com.github.ojil.core.ImageError {
        if (!(im instanceof Complex32Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_COMPLEX32IMAGE, im.toString(), null, null);
        }
        final Gray32Image imResult = new Gray32Image(im.getWidth(), im.getHeight());
        final Complex cData[] = ((Complex32Image) im).getData();
        final Integer nData[] = imResult.getData();
        for (int i = 0; i < (im.getWidth() * im.getHeight()); i++) {
            nData[i] = cData[i].magnitude();
        }
        super.setOutput(imResult);
    }
}
