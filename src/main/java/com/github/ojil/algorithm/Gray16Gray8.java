/*
 * Gray16Gray8.java
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

import com.github.ojil.core.Gray16Image;
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Gray16Gray8 converts an 16-bit gray image to an 8-bit gray image. The most
 * significant 8 bits of each pixel are dropped.
 *
 * @author webb
 */
public class Gray16Gray8 extends PipelineStage {
    
    /** Creates a new instance of Gray16Gray8 */
    public Gray16Gray8() {
    }
    
    /**
     * Converts an 16-bit gray image into an 8-bit image by and'ing off the top
     * 8 bits of every pixel.
     *
     * @param image
     *            the input image.
     * @throws com.github.ojil.core.ImageError
     *             if the input is not a Gray8Image
     */
    @Override
    public void push(final Image<?> image) throws com.github.ojil.core.ImageError {
        if (!(image instanceof Gray16Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY16IMAGE, image.toString(), null, null);
        }
        final Gray16Image gray = (Gray16Image) image;
        final Gray8Image gray8 = new Gray8Image(image.getWidth(), image.getHeight());
        final Short[] grayData = gray.getData();
        final Byte[] gray8Data = gray8.getData();
        for (int i = 0; i < (gray.getWidth() * gray.getHeight()); i++) {
            /*
             * Convert from 16-bit value to 8-bit value, discarding most
             * significant bits.
             */
            gray8Data[i] = (byte) (grayData[i] & 0xff);
        }
        super.setOutput(gray8);
    }
}
