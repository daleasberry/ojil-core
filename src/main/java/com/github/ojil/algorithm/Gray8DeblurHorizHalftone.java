/*
 * Gray8DeblurHorizHalftone.java
 *
 * Created on November 3, 2007, 3:07 PM
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

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Uses halftoning to remove horizontal blur from a Gray8Image. The idea is to
 * threshold the image at 0 at each pixel and add the error this introduces to
 * adjacent pixels, weighting by parameters specified in the constructor.
 * Depending on the weights, various kinds of blur can be removed.
 * 
 * @author webb
 */
public class Gray8DeblurHorizHalftone extends PipelineStage {
    int n0, n1, n2, n3, n4;
    
    /**
     * Creates a new instance of Gray8DeblurHorizHalftone. The weights specified
     * are scaled so that the image average brightness should not change after
     * the halftoning.
     * 
     * @param n0
     *            the weight for pixel (r,c+1)
     * @param n1
     *            the weight for pixel (r,c+2)
     * @param n2
     *            the weight for pixel (r,c+3)
     * @param n3
     *            the weight for pixel (r,c+4)
     * @param n4
     *            the weight for pixel (r,c+5)
     */
    public Gray8DeblurHorizHalftone(final int n0, final int n1, final int n2, final int n3, final int n4) {
        // scale the weights so the sum is 8
        final int sum = n0 + n1 + n2 + n3 + n4;
        this.n0 = (8 * n0) / sum;
        this.n1 = (8 * n1) / sum;
        this.n2 = (8 * n2) / sum;
        this.n3 = (8 * n3) / sum;
        this.n4 = (8 * n4) / sum;
        // integer division may make the sum not quite 8. correct this
        // in the weight for pixel (r,c+1)
        this.n0 = 8 - (this.n0 + this.n1 + this.n2 + this.n3 + this.n4);
    }
    
    /**
     * Deblurs an input Gray8Image by halftoning, spreading the error introduced
     * horizontally.
     * 
     * @param im
     *            Input Gray8Image.
     * @throws ImageError
     *             if the input is not of type Gray8Image.
     */
    @Override
    public void push(final Image<?> im) throws ImageError {
        if (!(im instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, im.toString(), null, null);
        }
        final Byte[] bData = ((Gray8Image) im).getData();
        for (int i = 0; i < im.getHeight(); i++) {
            final int nRow = i * im.getWidth();
            for (int j = 0; j < im.getWidth(); j++) {
                final byte bVal = bData[nRow + j];
                byte bNewVal;
                if (bVal >= 0) {
                    bNewVal = Byte.MAX_VALUE;
                } else {
                    bNewVal = Byte.MIN_VALUE;
                }
                final int nDiff = bVal - bNewVal;
                if (j < (im.getWidth() - 1)) {
                    bData[nRow + j + 1] = (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, bData[nRow + j + 1] + ((n0 * nDiff) / 8)));
                }
                if (j < (im.getWidth() - 2)) {
                    bData[nRow + j + 2] = (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, bData[nRow + j + 2] + ((n1 * nDiff) / 8)));
                    
                }
                if (j < (im.getWidth() - 3)) {
                    bData[nRow + j + 3] = (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, bData[nRow + j + 3] + ((n2 * nDiff) / 8)));
                    
                }
                if (j < (im.getWidth() - 4)) {
                    bData[nRow + j + 4] = (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, bData[nRow + j + 4] + ((n3 * nDiff) / 8)));
                    
                }
                if (j < (im.getWidth() - 5)) {
                    bData[nRow + j + 5] = (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, bData[nRow + j + 5] + ((n4 * nDiff) / 8)));
                    
                }
            }
        }
        super.setOutput(im);
    }
    
}
