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
 * Thresholds an RgbImage against a number of input colors.
 * 
 * @author webb
 */
public class RgbMultiVecThresh extends PipelineStage {
    /**
     * Simplify lookup of color values in array.
     */
    private final int R = 0;
    private final int G = 1;
    private final int B = 2;
    
    private final Integer[][] nRgbVals;
    /**
     * Input color values, unpacked.
     */
    private final Integer[][] nRgbVecs;
    /**
     * Input threshold value.
     */
    private final int nThreshold;
    
    /**
     * Thresholds an RgbImage against a number of input colors. Each color is
     * described using a target value (rgbVal) and a vector (rgbVec). The idea
     * is that a pixel matches a target value if its difference from the target
     * value, projected on the vector, is less than the threshold. One threshold
     * is used for all colors and the minimum absolute value of all color
     * distances is compared with the threshold. Since the vectors can be
     * unnormalized the relative importance of each target color value can be
     * adjusted. </br> One way to use this is to set the target color to the
     * mean Rgb value of a region and the target vector to the standard
     * deviation. The threshold would then be the standard deviation squared.
     * Pixels further away than one standard deviation would be rejected.
     * 
     * @param rgbVals
     *            packed arry of target Rgb values
     * @param rgbVecs
     *            packed array of target Rgb vectors
     * @param nThreshold
     *            threshold value
     * @throws ImageError
     *             if the input Rgb vectors are not the same length.
     */
    public RgbMultiVecThresh(final Integer[] rgbVals, final Integer[] rgbVecs, final int nThreshold) throws ImageError {
        if (rgbVals.length != rgbVecs.length) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.PARAMETER_SIZES_DIFFER, rgbVals.toString(), rgbVals.toString(), null);
        }
        nRgbVecs = new Integer[rgbVecs.length][3];
        nRgbVals = new Integer[rgbVecs.length][3];
        for (int i = 0; i < rgbVecs.length; i++) {
            nRgbVals[i][R] = RgbVal.getR(rgbVals[i]).intValue();
            nRgbVals[i][G] = RgbVal.getG(rgbVals[i]).intValue();
            nRgbVals[i][B] = RgbVal.getB(rgbVals[i]).intValue();
            
            nRgbVecs[i][R] = RgbVal.getR(rgbVecs[i]).intValue();
            nRgbVecs[i][G] = RgbVal.getG(rgbVecs[i]).intValue();
            nRgbVecs[i][B] = RgbVal.getB(rgbVecs[i]).intValue();
        }
        this.nThreshold = nThreshold;
    }
    
    /**
     * Compares input RgbImage with the color values set in the constructor and
     * outputs Byte.MAX_VALUE for any pixels within the threshold value of any
     * of the input colors. Other pixels get Byte.MIN_VALUE.
     * 
     * @param imageInput
     *            input RgbImage
     * @throws ImageError
     *             if input is not an RgbImage.
     */
    @Override
    public void push(final Image<?> imageInput) throws ImageError {
        if (!(imageInput instanceof RgbImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, imageInput.toString(), null, null);
        }
        final RgbImage rgbInput = (RgbImage) imageInput;
        final Integer[] rgbData = rgbInput.getData();
        final Gray8Image grayOutput = new Gray8Image(rgbInput.getWidth(), rgbInput.getHeight());
        final Byte[] grayData = grayOutput.getData();
        for (int i = 0; i < (rgbInput.getHeight() * rgbInput.getWidth()); i++) {
            final int nR = RgbVal.getR(rgbData[i]);
            final int nG = RgbVal.getG(rgbData[i]);
            final int nB = RgbVal.getB(rgbData[i]);
            int nMinVal = Integer.MAX_VALUE;
            for (int j = 0; j < nRgbVecs.length; j++) {
                final int nVal = ((nR - nRgbVals[j][R]) * nRgbVecs[j][R]) + ((nG - nRgbVals[j][G]) * nRgbVecs[j][G]) + ((nB - nRgbVals[j][B]) * nRgbVecs[j][B]);
                nMinVal = Math.min(nMinVal, Math.abs(nVal));
            }
            if (nMinVal < nThreshold) {
                grayData[i] = Byte.MAX_VALUE;
            } else {
                grayData[i] = Byte.MIN_VALUE;
            }
        }
        super.setOutput(grayOutput);
    }
    
    /**
     * Implements toString
     * 
     * @return a string including all the input color values and the input
     *         threshold value.
     */
    @Override
    public String toString() {
        String szParams = "{";
        for (final Integer[] nRgbVec : nRgbVecs) {
            szParams += "[R=" + Integer.toString(nRgbVec[R]) + "," + "G=" + Integer.toString(nRgbVec[G]) + "," + "B=" + Integer.toString(nRgbVec[B]) + "],";
        }
        szParams += "Threshold=" + Integer.toString(nThreshold) + ")";
        return super.toString() + szParams;
    }
}
