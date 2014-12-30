/*
 * Gray8HorizVar.java
 *
 * Created on February 3, 2008, 4:32, PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * Copyright 2008 by Jon A. Webb
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
 * Computes the variance of pixels horizontally distributed around the current
 * pixel.
 * 
 * @author webb
 */
public class Gray8HorizVar extends PipelineStage {
    /**
     * The window size -- pixels within nWindow of the current pixel are
     * included in the window.
     */
    int nWindow;
    /**
     * The output image.
     */
    Gray16Image g16 = null;
    
    /**
     * Creates a new instance of Gray8HorizVar
     * 
     * @param nWindow
     *            window size to compute horizontal variance over.
     */
    public Gray8HorizVar(final int nWindow) {
        this.nWindow = nWindow;
    }
    
    /**
     * Compute the horizontal variance of pixels within nWindow of the current
     * pixel.
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
        if ((g16 == null) || (g16.getWidth() != image.getWidth()) || (g16.getHeight() != image.getHeight())) {
            g16 = new Gray16Image(image.getWidth(), image.getHeight());
        }
        final Gray8Image input = (Gray8Image) image;
        final Byte[] bIn = input.getData();
        final int cWidth = input.getWidth();
        final Short[] sOut = g16.getData();
        for (int i = 0; i < input.getHeight(); i++) {
            int nSum = 0;
            int nSumSq = 0;
            int nCount = 0;
            // initialize sums and count for first pixel in row
            for (int j = 0; j < nWindow; j++) {
                nSum += bIn[(i * cWidth) + j];
                nSumSq += bIn[(i * cWidth) + j] * bIn[(i * cWidth) + j];
                nCount++;
            }
            // increment across the row
            for (int j = 0; j < cWidth; j++) {
                // if window doesn't extend past right side of
                // row add new pixel
                if ((j + nWindow) < cWidth) {
                    nSum += bIn[(i * cWidth) + j + nWindow];
                    nSumSq += bIn[(i * cWidth) + j + nWindow] * bIn[(i * cWidth) + j + nWindow];
                    nCount++;
                }
                // if window doesn't extend past left side of
                // row subtract old pixel
                if (j >= nWindow) {
                    nSum -= bIn[((i * cWidth) + j) - nWindow];
                    nSumSq -= bIn[((i * cWidth) + j) - nWindow] * bIn[((i * cWidth) + j) - nWindow];
                    nCount--;
                }
                final short nVar = (short) Math.min(Short.MAX_VALUE, (nSumSq - ((nSum * nSum) / nCount)) / (nCount - 1));
                sOut[(i * cWidth) + j] = nVar;
            }
        }
        super.setOutput(g16);
    }
    
}
