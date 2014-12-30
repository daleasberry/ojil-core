/*
 * Gray8Abs.java
 *
 * Created on August 27, 2006, 4:32, PM
 *
 * To change this templatef, choose Tools | Template Manager
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
 * Computes absolute value of a Gray8Image, replacing the original.
 * 
 * @author webb
 */
public class Gray8Abs extends PipelineStage {
    
    /**
     * Creates a new instance of Gray8Abs
     *
     */
    public Gray8Abs() {
    }
    
    /**
     * Compute absolute value of the image. Input is range Byte.MIN_VALUE to
     * Byte.MAX_VALUE; output is 0..Byte.MAX_VALUE
     * 
     * @param image
     *            the input Gray8Image
     * @throws ImageError
     *             if image is not a Gray8Image
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        final Gray8Image input = (Gray8Image) image;
        final Byte[] bIn = input.getData();
        for (int i = 0; i < bIn.length; i++) {
            bIn[i] = (byte) Math.min(Byte.MAX_VALUE, Math.abs(bIn[i]));
        }
        super.setOutput(input);
    }
    
}
