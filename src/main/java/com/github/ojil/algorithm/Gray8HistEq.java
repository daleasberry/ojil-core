/*
 * Gray8HistEq.java
 *
 * Created on September 9, 2006, 2:05 PM
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
import com.github.ojil.core.PipelineStage;

/**
 * Equalize the histogram of a gray image.
 * <p>
 * 
 * @author webb
 */
public class Gray8HistEq extends PipelineStage {
    private int cPixels = 0;
    private final Gray8HistMatch histMatch;
    
    /**
     * Creates a new instance of Gray8HistEq
     * 
     * @throws com.github.ojil.core.ImageError
     *             if Gray8HistMatch throws jjil.core.Error due to coding error.
     */
    public Gray8HistEq() throws com.github.ojil.core.ImageError {
        final Integer[] nullHist = new Integer[256];
        histMatch = new Gray8HistMatch(nullHist);
    }
    
    /**
     * Equalize the histogram of an input gray image.
     *
     * @param image
     *            the input image.
     * @throws com.github.ojil.core.ImageError
     *             if the input image is not gray.
     */
    @Override
    public void push(final Image<?, ?> image) throws com.github.ojil.core.ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        final Gray8Image<?> gray = (Gray8Image<?>) image;
        /*
         * In order to avoid recreating histMatch every call, we recompute the
         * target histogram only when the image size (total # pixels) changes.
         */
        if ((image.getWidth() * image.getHeight()) != cPixels) {
            cPixels = image.getWidth() * image.getHeight();
            int cPixelsRemaining = cPixels;
            /*
             * (Re)compute the equalizing histogram. We want a histogram which
             * is as flat as possible. The calculation below evens out the
             * histogram values so all are within 1 of each other.
             */
            final Integer[] histogram = new Integer[256];
            for (int i = 0; i < 256; i++) {
                final int c = cPixelsRemaining / (256 - i);
                histogram[i] = c;
                cPixelsRemaining -= c;
            }
            histMatch.setHistogram(histogram);
        }
        /*
         * Apply the histogram match algorithm to equalize the histogram of the
         * input.
         */
        histMatch.push(gray);
        super.setOutput(histMatch.getFront());
    }
}
