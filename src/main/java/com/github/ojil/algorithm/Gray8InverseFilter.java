/*
 * Gray8InverseFilter.java
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
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.MathPlus;
import com.github.ojil.core.PipelineStage;

/**
 * Computes the inverse filter of the input image, given an input point spread
 * function and noise level.
 * 
 * @author webb
 */
public class Gray8InverseFilter extends PipelineStage {
    private final int nGamma;
    Gray8Fft fft;
    Complex32IFft ifft;
    Complex32Image<?> cxmPsfInv;
    
    /**
     * Creates a new instance of Gray8InverseFilter.
     * 
     * @param psf
     *            The input point spread function. This must be a power of 2 in
     *            size.
     * @param nGamma
     *            The gamma parameter from the inverse filter operation,
     *            corresponding to a noise level. Higher gamma values imply a
     *            higher noise level and keep the inverse filter from amplifying
     *            noisy components.
     * @throws ImageError
     *             If the point spread function is not square or a power of 2 in
     *             size.
     */
    public Gray8InverseFilter(final Gray8Image<?> psf, final int nGamma) throws ImageError {
        if (psf.getWidth() != psf.getHeight()) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_SQUARE, psf.toString(), null, null);
        }
        if (!(psf instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, psf.toString(), null, null);
        }
        this.nGamma = nGamma;
        fft = new Gray8Fft();
        fft.push(psf);
        cxmPsfInv = (Complex32Image<?>) fft.getFront();
        ifft = new Complex32IFft(true);
    }
    
    /**
     * Compute the inverse filter of the given image.
     * 
     * @param im
     *            the Gray8Image to compute the inverse filter on.
     * @throws ImageError
     *             If the input image is not a Gray8Image or not the same size
     *             as the point spread function.
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
        final Complex cxPsfFft[] = cxmPsfInv.getData();
        // compute inverse filter
        for (int i = 0; i < (im.getWidth() * im.getHeight()); i++) {
            final int nMag = cxPsfFft[i].magnitude();
            if ((nMag * nGamma) > MathPlus.SCALE) {
                // cxPsfFft is the FFT of the point spread function, therefore
                // multiplied by SCALE. We are dividing by it so we must
                // multiply by SCALE to maintain
                // the same range.
                cxOut[i] = cxIn[i].div(cxPsfFft[i]).times(MathPlus.SCALE);
            } else {
                // the Psf is too small -- scale by nGamma
                cxOut[i] = cxIn[i].times(nGamma * nMag).div(cxPsfFft[i]);
            }
        }
        // inverse FFT to get result
        ifft.push(cxmResult);
        super.setOutput(ifft.getFront());
    }
    
}
