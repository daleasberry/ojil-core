/*
 * Gray32Threshold.java
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

import com.github.ojil.core.Gray32Image;
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Threshold. Output is a Gray8Image with values greater than or equal to
 * threshold set to Byte.MAX_VALUE, below threshold set to Byte.MIN_VALUE. Input
 * is a Gray32Image.
 * 
 * @author webb
 */
public class Gray32Threshold extends PipelineStage {
    int nThreshold;
    Gray8Image<?> imageOutput = null;
    
    /**
     * Creates a new instance of Gray32Threshold
     * 
     * @param nThreshold
     *            the threshold.
     */
    public Gray32Threshold(final int nThreshold) {
        this.nThreshold = nThreshold;
    }
    
    /**
     * Threshold gray image. Output is Byte.MAX_VALUE over or equal to
     * threshold, Byte.MIN_VALUE under.
     *
     * @param image
     *            the input Gray32Image
     * @throws ImageError
     *             if the image is not a Gray32Image.
     */
    @Override
    public void push(final Image<?, ?> image) throws ImageError {
        if (!(image instanceof Gray32Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, com.github.ojil.algorithm.AlgorithmErrorCodes.IMAGE_NOT_GRAY32IMAGE, image.toString(), null,
                    null);
        }
        if ((imageOutput == null) || (imageOutput.getWidth() != image.getWidth()) || (imageOutput.getHeight() != image.getHeight())) {
            imageOutput = new Gray8Image<>(image.getWidth(), image.getHeight());
        }
        final Gray32Image<?> gray = (Gray32Image<?>) image;
        final Integer[] data = gray.getData();
        final Byte[] dataOut = imageOutput.getData();
        for (int i = 0; i < data.length; i++) {
            dataOut[i] = (data[i] >= nThreshold) ? Byte.MAX_VALUE : Byte.MIN_VALUE;
        }
        super.setOutput(imageOutput);
    }
    
    /**
     * Implement toString
     * 
     * @return a String describing the class instance.
     */
    @Override
    public String toString() {
        return super.toString() + "(" + nThreshold + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
}
