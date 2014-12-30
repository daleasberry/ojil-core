/*
 * RgbShrink.java.
 *    Reduces a color image to a new size by averaging the pixels nearest each
 * target pixel's pre-image. This is done by converting each band of the image
 * into a gray image, shrinking them individually, then recombining them into
 * an RgbImage
 *
 * Created on October 13, 2007, 2:21 PM
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
 * Shrinks a color (RgbImage) to a given size. Each band is shrunk
 * independently. The pixels that each pixel maps to are averaged. There is no
 * between-target-pixel smoothing. The output image must be smaller than or
 * equal to the size of the input.
 * 
 * @author webb
 */
public class RgbShrink extends PipelineStage {
    private int cHeight;
    private int cWidth;
    private Sequence seqR, seqG, seqB;
    
    /**
     * Creates a new instance of RgbShrink.
     *
     * @param cWidth
     *            new image width
     * @param cHeight
     *            new image height
     * @throws ImageError
     *             if either is less than or equal to zero.
     */
    public RgbShrink(final int cWidth, final int cHeight) throws ImageError {
        setWidth(cWidth);
        setHeight(cHeight);
        setupPipeline();
    }
    
    /**
     * Gets current target height
     *
     * @return current height
     */
    public int getHeight() {
        return cHeight;
    }
    
    /**
     * Gets current target width
     *
     * @return current width
     */
    public int getWidth() {
        return cWidth;
    }
    
    /**
     * Process an image.
     * 
     * @param image
     *            the input RgbImage.
     * @throws ImageError
     *             if the input is not an RgbImage, or is smaller than the
     *             target image either horizontally or vertically.
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof RgbImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, image.toString(), null, null);
        }
        if ((image.getWidth() < cWidth) || (image.getHeight() < cHeight)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.SHRINK_OUTPUT_LARGER_THAN_INPUT, image.toString(), toString(), null);
        }
        /* shrink R band */
        seqR.push(image);
        /* shrink G band */
        seqG.push(image);
        /* shrink B band */
        seqB.push(image);
        super.setOutput(Gray3Bands2Rgb.push((Gray8Image) seqR.getFront(), (Gray8Image) seqG.getFront(), (Gray8Image) seqB.getFront()));
    }
    
    /**
     * Changes target height
     * 
     * @param cHeight
     *            the new target height.
     * @throws ImageError
     *             if height is not positive
     */
    private void setHeight(final int cHeight) throws ImageError {
        if (cHeight <= 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OUTPUT_IMAGE_SIZE_NEGATIVE, new Integer(cHeight).toString(), null, null);
        }
        this.cHeight = cHeight;
    }
    
    private void setupPipeline() throws ImageError {
        RgbSelectGray sel = new RgbSelectGray(RgbSelectGray.RED);
        seqR = new Sequence(sel);
        Gray8Shrink gs = new Gray8Shrink(cWidth, cHeight);
        seqR.add(gs);
        sel = new RgbSelectGray(RgbSelectGray.GREEN);
        seqG = new Sequence(sel);
        gs = new Gray8Shrink(cWidth, cHeight);
        seqG.add(gs);
        sel = new RgbSelectGray(RgbSelectGray.BLUE);
        seqB = new Sequence(sel);
        gs = new Gray8Shrink(cWidth, cHeight);
        seqB.add(gs);
    }
    
    /**
     * Changes target width
     * 
     * @param cWidth
     *            the new target width.
     * @throws ImageError
     *             if height is not positive
     */
    private void setWidth(final int cWidth) throws ImageError {
        if (cWidth <= 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OUTPUT_IMAGE_SIZE_NEGATIVE, new Integer(cWidth).toString(), null, null);
        }
        this.cWidth = cWidth;
    }
    
    /**
     * Return a string describing the shrinking operation.
     *
     * @return the string describing the shrinking operation.
     */
    @Override
    public String toString() {
        return super.toString() + " (" + cWidth + "," + cHeight + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
