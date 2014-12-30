/*
 * Created on September 9, 2006, 3:17 PM
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
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.Sequence;

/**
 * This PipelineStage warps a trapezoid in the input gray image into a
 * rectangular output image. The trapezoid is alined vertically, i.e., the
 * vertical edges are parallel
 * 
 * @author webb
 */
public class RgbHorizGaussSmooth extends PipelineStage {
    private Sequence seqR, seqG, seqB;
    int nSigma;
    
    /**
     * Creates a new instance of RgbHorizGaussSmooth. RgbVertTrapWarp smooths an
     * RgbImage using a horizontal Gaussian blur. The red, green, and blue
     * values are smoothed independently.
     * 
     * @param nSigma
     *            the sigma value of window to blur over
     * @throws ImageError
     *             if sigma out of range
     */
    public RgbHorizGaussSmooth(final int nSigma) throws ImageError {
        setSigma(nSigma);
    }
    
    /**
     * Smooth an RgbImage horizontally using a Gaussian blur.
     * 
     * @param image
     *            the input RgbImage.
     * @throws ImageError
     *             if the input image is not an RgbImage
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof RgbImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, image.toString(), null, null);
        }
        seqR.push(image);
        seqG.push(image);
        seqB.push(image);
        super.setOutput(Gray3Bands2Rgb.push((Gray8Image) seqR.getFront(), (Gray8Image) seqG.getFront(), (Gray8Image) seqB.getFront()));
    }
    
    /**
     * Sets the Gaussian blur width. Constructs 3 pipelines to use for smoothing
     * each band of the RgbImage independently.
     * 
     * @param nSigma
     *            the sigma of the window to blur over
     * @throws ImageError
     *             if sigma out of range
     */
    public void setSigma(final int nSigma) throws ImageError {
        this.nSigma = nSigma;
        seqR = new Sequence(new RgbSelectGray(RgbSelectGray.RED));
        seqR.add(new Gray8GaussHoriz(nSigma));
        seqG = new Sequence(new RgbSelectGray(RgbSelectGray.GREEN));
        seqG.add(new Gray8GaussHoriz(nSigma));
        seqB = new Sequence(new RgbSelectGray(RgbSelectGray.BLUE));
        seqB.add(new Gray8GaussHoriz(nSigma));
    }
    
    /**
     * Returns a string describing the current instance. All the constructor
     * parameters are returned in the order specified in the constructor.
     * 
     * @return The string describing the current instance
     */
    @Override
    public String toString() {
        return super.toString() + " (" + nSigma + ")"; //$NON-NLS-1$
    }
}
