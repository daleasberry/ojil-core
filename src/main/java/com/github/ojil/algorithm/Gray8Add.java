/*
 * Gray8Add.java
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

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.Ladder;

/**
 * Adds two gray images. To be used as a join operation in a Ladder operation.
 * 
 * @author webb
 */
public class Gray8Add implements Ladder.Join {
    
    /** Creates a new instance of Gray8Add */
    public Gray8Add() {
    }
    
    /**
     * Adds two Gray8Image's. Result is clamped between Byte.MIN_VALUE and
     * Byte.MAX_VALUE. The first input image is replaced by the result.
     *
     * @param imageFirst
     *            the first image (and output)
     * @param imageSecond
     *            the second image
     * @return the sum of the two byte images, replacing the first
     * @throws ImageError
     *             if either image is not a gray 8-bit image, or they are of
     *             different sizes.
     */
    @Override
    public Image<?> doJoin(final Image<?> imageFirst, final Image<?> imageSecond) throws ImageError {
        if (!(imageFirst instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, imageFirst.toString(), null, null);
        }
        if (!(imageSecond instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, imageSecond.toString(), null, null);
        }
        if ((imageFirst.getWidth() != imageSecond.getWidth()) || (imageSecond.getHeight() != imageSecond.getHeight())) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_SIZES_DIFFER, imageFirst.toString(), imageSecond.toString(), null);
            
        }
        final Gray8Image gray1 = (Gray8Image) imageFirst;
        final Gray8Image gray2 = (Gray8Image) imageSecond;
        final Byte[] data1 = gray1.getData();
        final Byte[] data2 = gray2.getData();
        for (int i = 0; i < data1.length; i++) {
            data1[i] = (byte) Math.min(Byte.MAX_VALUE, Math.max(Byte.MIN_VALUE, (data1[i] + data2[i])));
        }
        return gray1;
    }
    
}
