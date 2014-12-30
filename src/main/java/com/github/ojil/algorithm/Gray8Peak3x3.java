/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.ojil.algorithm;

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Find local 3x3 peaks in the Gray8Image. A pixel is set to Byte.MIN_VALUE if
 * it is not equal to the local maximum.
 * 
 * @author webb
 */
public class Gray8Peak3x3 extends PipelineStage {
    /**
     * Scan the image and set all pixels not equal to the local 3x3 maximum to
     * Byte.MIN_VALUE.
     * 
     * @param imageInput
     *            input Gray8Image. Not modified.
     * @throws ImageError
     *             if input is not a Gray8Image.
     */
    @Override
    public void push(final Image<?> imageInput) throws ImageError {
        if (!(imageInput instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, imageInput.toString(), null, null);
        }
        final Gray8Image grayInput = (Gray8Image) imageInput;
        final Byte[] bData = grayInput.getData();
        final Gray8Image grayOutput = new Gray8Image(imageInput.getWidth(), imageInput.getHeight());
        final Byte[] bDataOut = grayOutput.getData();
        for (int i = 1; i < (grayInput.getHeight() - 1); i++) {
            for (int j = 1; j < (grayInput.getWidth() - 1); j++) {
                if (bData[(i * grayInput.getWidth()) + j] != Math.max(
                        bData[(((i - 1) * grayInput.getWidth()) + j) - 1],
                                Math.max(
                                bData[((i - 1) * grayInput.getWidth()) + j],
                                Math.max(
                                        bData[((i - 1) * grayInput.getWidth()) + j + 1],
                                        Math.max(
                                                bData[((i * grayInput.getWidth()) + j) - 1],
                                                Math.max(
                                                        bData[(i * grayInput.getWidth()) + j + 1],
                                                        Math.max(bData[(((i + 1) * grayInput.getWidth()) + j) - 1],
                                                                Math.max(bData[((i + 1) * grayInput.getWidth()) + j], bData[((i + 1) * grayInput.getWidth()) + j + 1])))))))) {
                    bDataOut[(i * grayInput.getWidth()) + j] = Byte.MIN_VALUE;
                } else {
                    bDataOut[(i * grayInput.getWidth()) + j] = bData[(i * grayInput.getWidth()) + j];
                    
                }
                
            }
        }
        super.setOutput(grayOutput);
    }
    
}
