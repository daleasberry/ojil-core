/*
 * MaskedGray8SubImgGen.java
 *
 * Given a target image size and a horizontal and vertical offset
 * generates a series of subimages within the input image,
 * each subimage offset by an integral multiple of the
 * offset with size equal to the target size and lying
 * entirely within the original image. The offset of the
 * subimage in the input image is given in the subimage class.
 * In this masked version only subimages whose center is not a masked
 * point (mask image value = 0) will be generated.
 *
 * Created on July 1, 2007, 1:51 PM
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

import com.github.ojil.core.Gray8MaskedImage;
import com.github.ojil.core.Gray8OffsetImage;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Generates subimages from a source Gray8Image, using a mask. In a normal
 * subimage generator subimages are generated evenly spaced across the input
 * image. Here, the subimage is generated only if is center point is not masked.
 * This can increase processing speed in hierarchical detection operations since
 * features detected at coarser resolution don't have to be redetected at finer
 * resolution.
 * 
 * @author webb
 */
public class MaskedGray8SubImgGen extends PipelineStage {
    Gray8MaskedImage<?> imageInput; // input image
    boolean oSubImageReady; // true if sub image position is OK
    int nHeight; // target height
    int nHorizLimit = 0; // number of subimages generated horizontally
    int nVertLimit = 0; // number of subimages generated vertically
    int nHorizIndex = 0; // current subimiage index, horizontal
    int nVertIndex = 0; // current subimage index, vertical
    int nWidth; // target width
    int nXOffset; // x offset multiple for subimages
    int nYOffset; // y offset multiple for subimages
    
    /**
     * Creates a new instance of MaskedGray8SubImgGen.
     * 
     * @param nWidth
     *            the width of the generated subimage.
     * @param nHeight
     *            the height of the generated subimage.
     * @param nXOffset
     *            the horizontal offset between subimages.
     * @param nYOffset
     *            the vertical offset between subimages.
     */
    public MaskedGray8SubImgGen(final int nWidth, final int nHeight, final int nXOffset, final int nYOffset) {
        this.nWidth = nWidth;
        this.nHeight = nHeight;
        this.nXOffset = nXOffset;
        this.nYOffset = nYOffset;
        oSubImageReady = false;
        // create an output image. We'll reuse this
        // image, changing the contents and offset,
        // for every Gray8OffsetImage we output.
        super.imageOutput = new Gray8OffsetImage<>(this.nWidth, this.nHeight, 0, 0);
    }
    
    /**
     * advanceToNextSubImage advances to the next position for generating a
     * subimage. It returns true iff there is a non-masked position within the
     * image where a subimage can be generated.
     */
    private boolean advanceToNextSubImage() {
        if (oSubImageReady) {
            return nVertIndex <= nVertLimit;
        }
        // advance to next subimage position
        nHorizIndex++;
        if (nHorizIndex > nHorizLimit) {
            nVertIndex++;
            nHorizIndex = 0;
        }
        // nPos is the byte address that we will test in the mask image
        // to see if it is OK to generate a subimage. It is the midpoint
        // of the subimage.
        int nPos = (((nHeight / 2) + (nVertIndex * nYOffset)) * imageInput.getWidth()) + (nWidth / 2) + (nHorizIndex * nXOffset);
        // starting at the current position, search forward for a position
        // that is not masked.
        while (nVertIndex <= nVertLimit) {
            while (nHorizIndex <= nHorizLimit) {
                if (imageInput.getMaskData()[nPos] == Byte.MIN_VALUE) {
                    // found it
                    oSubImageReady = true;
                    return true;
                }
                nHorizIndex++;
                nPos += nWidth;
            }
            nHorizIndex = 0;
            nVertIndex++;
            // reset nPos for next row of subimages
            nPos = (((nHeight / 2) + (nVertIndex * nYOffset)) * imageInput.getWidth()) + (nWidth / 2);
        }
        oSubImageReady = true;
        return false;
    }
    
    // We are done producing images when the advance returns no more images
    /**
     * Returns true iff there is another image available from getFront(). Note
     * that the existence of another image from MaskedGray8SubImgGen depends on
     * the mask image so there's no way to guarantee there will be even one
     * subimage generated for a particular input. You must always call
     * isEmpty().
     * 
     * @return true iff there is another image available from getFront().
     */
    @Override
    public boolean isEmpty() {
        return !advanceToNextSubImage();
    }
    
    // Return the next subimage and increment the indices
    /**
     * Returns the next subimage generated. The subimage will have its offset
     * set to indicate where it was generated in the input image.
     * 
     * @return a MaskedGray8SubImage that is the next subimage in the input
     *         Gray8Image to be processed.
     * @throws ImageError
     *             if no subimage is available (you have to call isEmpty() to
     *             determine if a subimage is available. As few as 0 subimage
     *             can be generated for a given input if the entire image is
     *             masked.) Also throws if the output image (stored in the
     *             superclass) has been changed in type.
     */
    @Override
    public Image<?, ?> getFront() throws ImageError {
        // reuse output image
        // check to make sure nobody damaged it somehow
        if (!(super.imageOutput instanceof Gray8OffsetImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OBJECT_NOT_EXPECTED_TYPE, imageOutput.toString(), "Gray8SubImage", null);
        }
        if (!advanceToNextSubImage()) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.SUBIMAGE_NO_IMAGE_AVAILABLE, toString(), null, null);
        }
        
        final Byte[] dataIn = imageInput.getData();
        // offset of first pixel of the subimage within the
        // larget image.
        final int nHOffset = nXOffset * nHorizIndex;
        final int nVOffset = nYOffset * nVertIndex;
        final Gray8OffsetImage<?> imageResult = (Gray8OffsetImage<?>) super.imageOutput;
        imageResult.setXOffset(nHOffset);
        imageResult.setYOffset(nVOffset);
        final Byte[] dataOut = imageResult.getData();
        // don't access outside the image
        final int nLimitY = Math.min(imageInput.getHeight() - nVOffset, nHeight);
        final int nLimitX = Math.min(imageInput.getWidth() - nHOffset, nWidth);
        for (int i = 0; i < nLimitY; i++) {
            final int nVInLoc = i + nVOffset;
            System.arraycopy(dataIn, (nVInLoc * imageInput.getWidth()) + nHOffset, dataOut, i * nWidth, nLimitX);
        }
        
        oSubImageReady = false;
        return imageResult;
    }
    
    /**
     * Accepts a new MaskedGray8Image and initializes all the generator indices.
     * 
     * @param image
     *            The input MaskedGray8Image.
     * @throws ImageError
     *             if the input is not of type MaskedGray8Image or is smaller
     *             than the subimages to be generated.
     */
    @Override
    public void push(final Image<?, ?> image) throws ImageError {
        if (!(image instanceof Gray8MaskedImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OBJECT_NOT_EXPECTED_TYPE, image.toString(), "Gray8MaskedImage", null);
        }
        if ((image.getWidth() < nWidth) || (image.getHeight() < nHeight)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_TOO_SMALL, image.toString(), new Integer(nWidth).toString(), new Integer(nHeight).toString());
        }
        imageInput = (Gray8MaskedImage<?>) image;
        // we want to find the largest integer l such that
        // (l-1) * w + w <= iw
        // where l = computed limit on index
        // w = subimage width or height
        // iw = image width or height
        // or l = (iw - w) / w + 1 (truncated)
        // Java division truncates
        nHorizLimit = (image.getWidth() - nWidth) / nXOffset;
        nVertLimit = (image.getHeight() - nHeight) / nYOffset;
        nHorizIndex = -1; // first time through increments
        nVertIndex = 0;
        oSubImageReady = false;
    }
    
}
