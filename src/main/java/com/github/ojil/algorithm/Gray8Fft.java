/*
 * Gray8Fft.java
 *
 * Created on October 31, 2007, 4:02 PM
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
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Takes the fast Fourier transform of the input Gray8Image. The output image is
 * a Complex32Image. The image size must be a power of 2.
 * 
 * @author webb
 */
public class Gray8Fft extends PipelineStage {
    /**
     * Defines the scale factor applied to the image as a power of two, for
     * accuracy.
     */
    public static int SCALE = 8;
    
    private Fft1d fft = null;
    
    /**
     * Creates a new instance of Gray8Fft.
     */
    public Gray8Fft() {
    }
    
    /**
     * Performs the fast Fourier transform on an image. The input image is a
     * Gray8Image, and the output is a Complex32Image. The input is scaled by
     * shifting left SCALE bits before the transformation, for accuracy.
     * 
     * @param im
     *            Input image. Must be a Gray8Image.
     * @throws ImageError
     *             if the input is not a Gray8Image or is not a power of two in
     *             width and height.
     */
    @Override
    public void push(final Image<?, ?> im) throws ImageError {
        if (!(im instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, im.toString(), null, null);
        }
        final int nWidth = im.getWidth();
        final int nHeight = im.getHeight();
        if ((nWidth & (nWidth - 1)) != 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.FFT_SIZE_NOT_POWER_OF_2, im.toString(), null, null);
        }
        if ((nHeight & (nHeight - 1)) != 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.FFT_SIZE_NOT_POWER_OF_2, im.toString(), null, null);
        }
        // initialize FFT
        if (fft == null) {
            fft = new Fft1d(Math.max(nWidth, nHeight));
        } else {
            fft.setMaxWidth(Math.max(nWidth, nHeight));
        }
        
        final Gray8Image<?> gray = (Gray8Image<?>) im;
        final Byte data[] = gray.getData();
        // create output
        final Complex32Image<?> cxmResult = new Complex32Image<>(nWidth, nHeight);
        // take FFT of each row
        int nIndex = 0;
        final Complex cxRow[] = new Complex[nWidth];
        for (int i = 0; i < nHeight; i++) {
            for (int j = 0; j < nWidth; j++) {
                // convert each byte to a complex number. Imaginary component is
                // 0.
                // everything gets scaled for accuracy
                cxRow[j] = new Complex((data[nIndex++] - Byte.MIN_VALUE) << Gray8Fft.SCALE);
            }
            // compute FFT
            final Complex cxResult[] = fft.fft(cxRow);
            // save result
            System.arraycopy(cxResult, 0, cxmResult.getData(), i * nWidth, nWidth);
        }
        // take FFT of each column
        final Complex cxCol[] = new Complex[nHeight];
        for (int j = 0; j < nWidth; j++) {
            // copy column into a 1-D array
            for (int i = 0; i < nHeight; i++) {
                cxCol[i] = cxmResult.getData()[(i * nWidth) + j];
            }
            // compute FFT
            final Complex cxResult[] = fft.fft(cxCol);
            // save result back into column
            for (int i = 0; i < nHeight; i++) {
                cxmResult.getData()[(i * nWidth) + j] = cxResult[i];
            }
        }
        super.setOutput(cxmResult);
    }
}
