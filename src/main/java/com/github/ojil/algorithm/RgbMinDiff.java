/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.ojil.algorithm;

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbVal;

/**
 * Compute the Gray8Image that is the minimum absolute difference in red, green,
 * or blue between the RgbImage specified in the constructor and the RgbImage
 * supplied to the pipeline stage. This is intended to be used as part of a
 * pipeline for separating out a fixed background from the new input. Since the
 * background can vary due to lighting conditions we find the maximum difference
 * in any color channel and output that, making it easier to find objects that
 * may match the background in one or two channels.
 * 
 * @author webb
 */
public class RgbMinDiff extends PipelineStage {
    private final RgbImage<?> rgbBack;
    
    /**
     * Set background image.
     * 
     * @param rgbBack
     *            background RgbImage.
     */
    public RgbMinDiff(final RgbImage<?> rgbBack) {
        this.rgbBack = rgbBack;
    }
    
    /**
     * Process a foreground RgbImage and produce a Gray8Image in which each
     * pixel is the maximum of the differences between the input image and the
     * background image in the three color channels.
     * 
     * @param imInput
     *            input RgbImage
     * @throws ImageError
     *             if imInput is not an RgbImage or is not the same size as the
     *             background image set in the constructor.
     */
    @Override
    public void push(final Image<?, ?> imInput) throws ImageError {
        {
            if (!(imInput instanceof RgbImage)) {
                throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, imInput.toString(), null, null);
            }
        }
        if ((imInput.getWidth() != rgbBack.getWidth()) || (imInput.getHeight() != rgbBack.getHeight())) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_SIZES_DIFFER, imInput.toString(), rgbBack.toString(), null);
            
        }
        
        final Integer[] wInput = ((RgbImage<?>) imInput).getData();
        final Integer[] wBack = rgbBack.getData();
        final Gray8Image<?> grayOut = new Gray8Image<>(rgbBack.getWidth(), rgbBack.getHeight());
        final Byte[] bGray = grayOut.getData();
        for (int i = 0; i < (imInput.getWidth() * imInput.getHeight()); i++) {
            final int rIn = RgbVal.getR(wInput[i]);
            final int gIn = RgbVal.getG(wInput[i]);
            final int bIn = RgbVal.getB(wInput[i]);
            final int rBack = RgbVal.getR(wBack[i]);
            final int gBack = RgbVal.getG(wBack[i]);
            final int bBack = RgbVal.getB(wBack[i]);
            final int gRes = Math.min(Math.abs(rIn - rBack), Math.min(Math.abs(gIn - gBack), Math.abs(bIn - bBack)));
            bGray[i] = (byte) Math.min(gRes, Byte.MAX_VALUE);
        }
        super.setOutput(grayOut);
    }
    
    /**
     * Implement toString, providing the background image information.
     * 
     * @return a string consisting of this class name followed by the background
     *         image description.
     */
    @Override
    public String toString() {
        return super.toString() + "(" + rgbBack.toString() + ")";
    }
}
