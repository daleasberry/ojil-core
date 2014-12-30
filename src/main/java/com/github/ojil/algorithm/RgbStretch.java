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
import com.github.ojil.core.ImageError;
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.Sequence;
/**
 * Stretches a color (RgbImage) to a given size. Each band is shrunk independently.
 * The output image must be greater than or equal to the size of the 
 * input.
 * @author webb
 */
public class RgbStretch extends PipelineStage {
    private int cHeight;
    private int cWidth;
    private Sequence seqR, seqG, seqB;
    /** Creates a new instance of RgbStretch. 
     *
     * @param cWidth new image width
     * @param cHeight new image height
     * @throws com.github.ojil.core.ImageError if either is less than or equal to zero.
     */
    public RgbStretch(int cWidth, int cHeight) 
        throws com.github.ojil.core.ImageError {
        this.cWidth = cWidth;
        this.cHeight = cHeight;
        setupPipeline();
    }
         
    /** Gets current target height 
     *
     * @return current height
     */
    public int getHeight() {
        return this.cHeight;
    }
    
    /** Gets current target width
     *
     * @return current width
     */
    public int getWidth() {
        return this.cWidth;
    }
    
    /**
     * Process an image.
     * @param image the input RgbImage.
     * @throws com.github.ojil.core.ImageError if the input is not an RgbImage, or is smaller than the target image either
     * horizontally or vertically.
     */
    public void push(Image image) throws com.github.ojil.core.ImageError {
        if (!(image instanceof RgbImage)) {
            throw new ImageError(
        			ImageError.PACKAGE.ALGORITHM,
        			AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE,
        			image.toString(),
        			null,
        			null);
        }
        if (image.getWidth() > this.cWidth || image.getHeight() > this.cHeight) {
            throw new ImageError(
            			ImageError.PACKAGE.ALGORITHM,
            			AlgorithmErrorCodes.STRETCH_OUTPUT_SMALLER_THAN_INPUT,
            			image.toString(),
            			this.toString(),
            			null);
        }
        /* stretch R band */
        this.seqR.push(image);
        /* shrink G band */
        this.seqG.push(image);
        /* stretch B band */
        this.seqB.push(image);
        /* recombine bands */
        super.setOutput(Gray3Bands2Rgb.push(
                (Gray8Image)this.seqR.getFront(), 
                (Gray8Image)this.seqG.getFront(), 
                (Gray8Image)this.seqB.getFront()));
    }
        
    /** Changes target height
     * 
     * @param cHeight the new target height.
     * @throws com.github.ojil.core.ImageError if height is not positive
     */
    public void setHeight(int cHeight) throws com.github.ojil.core.ImageError {
        if (cHeight <= 0) {
            throw new ImageError(
                			ImageError.PACKAGE.ALGORITHM,
                			AlgorithmErrorCodes.OUTPUT_IMAGE_SIZE_NEGATIVE,
                			new Integer(cHeight).toString(),
                			null,
                			null);
        }
        this.cHeight = cHeight;
        setupPipeline();
    }
    
    private void setupPipeline() throws com.github.ojil.core.ImageError
    {
        RgbSelectGray sel = new RgbSelectGray(RgbSelectGray.RED);
        this.seqR = new Sequence(sel);
        Gray8RectStretch gs = new Gray8RectStretch(cWidth, cHeight);
        this.seqR.add(gs);
        sel = new RgbSelectGray(RgbSelectGray.GREEN);
        this.seqG = new Sequence(sel);
        gs = new Gray8RectStretch(cWidth, cHeight);
        this.seqG.add(gs);
        sel = new RgbSelectGray(RgbSelectGray.BLUE);
        this.seqB = new Sequence(sel);
        gs = new Gray8RectStretch(cWidth, cHeight);
        this.seqB.add(gs);
    }
    
    /** Changes target width
     * 
     * @param cWidth the new target width.
     * @throws com.github.ojil.core.ImageError if height is not positive
     */
    public void setWidth(int cWidth) throws com.github.ojil.core.ImageError {
        if (cWidth <= 0) {
            throw new ImageError(
                			ImageError.PACKAGE.ALGORITHM,
                			AlgorithmErrorCodes.OUTPUT_IMAGE_SIZE_NEGATIVE,
                			new Integer(cWidth).toString(),
                			null,
                			null);
        }
        this.cWidth = cWidth;
        setupPipeline();
    }
    
    
        
    /** Return a string describing the stretching operation.
     *
     * @return the string describing the stretching operation.
     */
    public String toString() {
        return super.toString() + " (" + this.cWidth + "," + this.cHeight + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
