/*
 * Gray8WienerDeconv.java
 *
 * Created on November 3, 2007, 3:07 PM
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
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.MathPlus;
import com.github.ojil.core.PipelineStage;

/**
 * Wiener deconvolution of input Gray8Image. You specify a point spread function
 * as a Gray8Image and a noise level. See PsfGray8 for point spread function
 * generating methods. The computation is done in the Fourier domain. The output
 * is of type Complex32Image.
 * 
 * @author webb
 */
public class Gray8WienerDeconv extends PipelineStage {
    private final int nNoise;
    private static final int nThreshold = 5;
    Gray8Fft fft;
    Complex32Image<?> cxmPsfInv;
    Gray32Image<?> gPsfSq;
    
    /**
     * Creates a new instance of Gray8WienerDeconv.
     * 
     * @param psf
     *            the input point spread function. This is the expected blur
     *            window, for example a disk or rectangle.
     * @param nNoise
     *            the noise level.
     * @throws ImageError
     *             if the input point spread function is not a Gray8Image or not
     *             square.
     */
    public Gray8WienerDeconv(final Gray8Image<?> psf, final int nNoise) throws ImageError {
        if (psf.getWidth() != psf.getHeight()) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_SQUARE, psf.toString(), null, null);
        }
        if (!(psf instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, psf.toString(), null, null);
        }
        this.nNoise = nNoise;
        fft = new Gray8Fft();
        fft.push(psf);
        cxmPsfInv = (Complex32Image<?>) fft.getFront();
        invertPsf();
    }
    
    /**
     * Compute the deconvolution of the input Gray8Image, producing a
     * Complex32Image.
     * 
     * @param im
     *            the input Gray8Image.
     * @throws ImageError
     *             if the input image is not a Gray8Image or not square.
     */
    @Override
    public void push(final Image<?, ?> im) throws ImageError {
        if (im.getWidth() != im.getHeight()) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_SQUARE, im.toString(), null, null);
        }
        if ((im.getWidth() != cxmPsfInv.getWidth()) || (im.getHeight() != cxmPsfInv.getHeight())) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_SIZES_DIFFER, im.toString(), cxmPsfInv.toString(), null);
        }
        if (!(im instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, im.toString(), null, null);
        }
        fft.push(im);
        final Complex32Image<?> cxmIm = (Complex32Image<?>) fft.getFront();
        final Complex cxIn[] = cxmIm.getData();
        final Complex32Image<?> cxmResult = new Complex32Image<>(im.getWidth(), im.getHeight());
        final Complex cxOut[] = cxmResult.getData();
        final Complex cxPsfInv[] = cxmPsfInv.getData();
        final Integer[] nPsfSq = gPsfSq.getData();
        // compute Wiener filter
        for (int i = 0; i < (im.getWidth() * im.getHeight()); i++) {
            final int nMag = cxIn[i].magnitude();
            final int nScale = (nPsfSq[i] * nMag) / ((nPsfSq[i] * nMag) + nNoise);
            cxOut[i] = cxIn[i].times(cxPsfInv[i]).times(nScale).rsh(MathPlus.SHIFT);
        }
        super.setOutput(cxmResult);
    }
    
    private void invertPsf() throws ImageError {
        gPsfSq = new Gray32Image<>(cxmPsfInv.getWidth(), cxmPsfInv.getHeight());
        final Complex cxPsf[] = cxmPsfInv.getData();
        final Integer[] nData = gPsfSq.getData();
        for (int i = 0; i < (cxmPsfInv.getWidth() * cxmPsfInv.getHeight()); i++) {
            if ((Math.abs(cxPsf[i].real()) > MathPlus.SCALE) || (Math.abs(cxPsf[i].imag()) > MathPlus.SCALE)) {
                cxPsf[i] = new Complex(0);
                nData[i] = 1;
            } else {
                final int nSq = cxPsf[i].square();
                nData[i] = nSq;
                if (nSq < Gray8WienerDeconv.nThreshold) {
                    // if the square value is too small we will be enhancing
                    // noise
                    // too much
                    cxPsf[i] = new Complex(MathPlus.SCALE);
                    nData[i] = 1;
                } else {
                    cxPsf[i] = new Complex(MathPlus.SCALE).div(cxPsf[i]);
                }
            }
        }
    }
}
