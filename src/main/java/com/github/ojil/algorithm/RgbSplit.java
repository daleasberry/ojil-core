/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.ojil.algorithm;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import com.github.ojil.core.ErrorCodes;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.Rect;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbRegion;
import com.github.ojil.core.RgbRegion.MeanVar;
import com.github.ojil.core.RgbVal;

/**
 * Uses region splitting to break the input image down into rectangular areas of
 * similar color. The region granularity can be controlled by setting a maximum
 * standard deviation in each color channel.
 * 
 * @author webb
 */
public class RgbSplit extends PipelineStage {
    int nRVar, nGVar, nBVar;
    Random random = new Random();
    RgbImage rgbInput;
    Vector<RgbRegion> vecROk = null; // vector of RgbRegions
    
    /**
     * Construct the class, setting the maximum standard deviation in each color
     * channel.
     * 
     * @param rStdDev
     *            maximum red standard deviation
     * @param gStdDev
     *            maximum green standard deviation
     * @param bStdDev
     *            maximum blue standard deviation.
     */
    public RgbSplit(final int rStdDev, final int gStdDev, final int bStdDev) {
        nRVar = rStdDev;
        nGVar = gStdDev;
        nBVar = bStdDev;
        nRVar *= nRVar;
        nGVar *= nGVar;
        nBVar *= nBVar;
    }
    
    /**
     * Compute variance of a rectangle in the input image.
     * 
     * @param r
     *            the Rect outlining the region to compute
     * @return a MeanVar object containing the mean and variance of the region.
     */
    private MeanVar computeVariance(final Rect r) {
        final Integer[] nData = rgbInput.getData();
        int nSumR = 0, nSumG = 0, nSumB = 0;
        int nSumRSq = 0, nSumGSq = 0, nSumBSq = 0;
        for (int i = r.getTop(); i < r.getBottom(); i++) {
            for (int j = r.getLeft(); j < r.getRight(); j++) {
                final int nRgbVal = nData[(i * rgbInput.getWidth()) + j];
                final int nR = RgbVal.getR(nRgbVal);
                final int nG = RgbVal.getG(nRgbVal);
                final int nB = RgbVal.getB(nRgbVal);
                nSumR += nR;
                nSumG += nG;
                nSumB += nB;
                nSumRSq += nR * nR;
                nSumGSq += nG * nG;
                nSumBSq += nB * nB;
            }
        }
        // compute average
        final int nAvgR = nSumR / r.getArea();
        final int nAvgG = nSumG / r.getArea();
        final int nAvgB = nSumB / r.getArea();
        final int nVarR = (nSumRSq / r.getArea()) - (nAvgR * nAvgR);
        final int nVarG = (nSumGSq / r.getArea()) - (nAvgG * nAvgG);
        final int nVarB = (nSumBSq / r.getArea()) - (nAvgB * nAvgB);
        return new MeanVar(RgbVal.toRgb((byte) nAvgR, (byte) nAvgG, (byte) nAvgB), nVarR, nVarG, nVarB);
    }
    
    /**
     * Split an input RgbImage into rectangular regions of standard deviation
     * less than or equal to the thresholds specified in the constructor. The
     * minimum region size is 2x2. Regions are split horizontally and vertically
     * in each pass so as to converge to roughly square blocks.
     * 
     * @param rgbImage
     *            the input RgbImage
     */
    public void split(final RgbImage rgbImage) {
        rgbInput = rgbImage;
        final Vector<Rect> vecRNotOk = new Vector<>();
        vecROk = new Vector<>();
        vecRNotOk.addElement(new Rect(0, 0, rgbInput.getWidth(), rgbInput.getHeight()));
        while (!vecRNotOk.isEmpty()) {
            final Rect r = vecRNotOk.elementAt(0);
            vecRNotOk.removeElementAt(0);
            if ((r.getHeight() >= 2) && (r.getWidth() >= 2)) {
                final MeanVar nVar = computeVariance(r);
                if ((nVar.getRVar() > nRVar) || (nVar.getGVar() > nGVar) || (nVar.getB() > nBVar)) {
                    // split horizontally or vertically, whichever
                    // is longer
                    if (r.getWidth() >= r.getHeight()) {
                        // split horizontally
                        final int nHalfWidth = r.getWidth() / 2;
                        Rect rNew = new Rect(r.getLeft(), r.getTop(), nHalfWidth, r.getHeight());
                        vecRNotOk.addElement(rNew);
                        rNew = new Rect(r.getLeft() + nHalfWidth, r.getTop(), r.getWidth() - nHalfWidth, r.getHeight());
                        vecRNotOk.addElement(rNew);
                    } else {
                        // split vertically
                        final int nHalfHeight = r.getHeight() / 2;
                        Rect rNew = new Rect(r.getLeft(), r.getTop(), r.getWidth(), nHalfHeight);
                        vecRNotOk.addElement(rNew);
                        rNew = new Rect(r.getLeft(), r.getTop() + nHalfHeight, r.getWidth(), r.getHeight() - nHalfHeight);
                        vecRNotOk.addElement(rNew);
                    }
                } else {
                    final RgbRegion reg = new RgbRegion(r, nVar);
                    vecROk.addElement(reg);
                }
            } else {
                // region too small, stop splitting
                final MeanVar nVar = computeVariance(r);
                final RgbRegion reg = new RgbRegion(r, nVar);
                vecROk.addElement(reg);
            }
        }
    }
    
    /**
     * Returns a color image with colors randomly assigned to regions. This is
     * used during debugging to see how the image has been split so that the
     * threshold can be adjusted.
     * 
     * @return RgbImage with colors randomly assigned to regions.
     * @throws ImageError
     *             if push() hasn't been called yet
     */
    public RgbImage getRandomizedRgbImage() throws ImageError {
        if (vecROk == null) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.NO_RESULT_AVAILABLE, null, null, null);
        }
        RgbImage rgbImage = new RgbImage(rgbInput.getWidth(), rgbInput.getHeight());
        for (final RgbRegion rgbRegion : vecROk) {
            final RgbRegion r = rgbRegion;
            final int nRgb = RgbVal
                    .toRgb((byte) ((random.nextInt() & 0xff) + Byte.MIN_VALUE), (byte) ((random.nextInt() & 0xff) + Byte.MIN_VALUE), (byte) ((random.nextInt() & 0xff) + Byte.MIN_VALUE));
            final Rect rect = r.getRect();
            rgbImage = rgbImage.fill(rect, nRgb);
        }
        return rgbImage;
        
    }
    
    /**
     * Return the region list.
     * 
     * @return an Enumeration on RgbRegion objects.
     * @throws ImageError
     *             if push() hasn't been called yet
     */
    public Enumeration<RgbRegion> getRegions() throws ImageError {
        if (vecROk == null) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.NO_RESULT_AVAILABLE, null, null, null);
        }
        return vecROk.elements();
    }
    
    /**
     * Return an RgbImage with the mean color of each region assigned. This
     * should be an approximation of the original input image, except more
     * blocky, depending on the thresholds set.
     * 
     * @return RgbImage with the mean color of each region assigned to the
     *         region.
     * @throws ImageError
     *             if push() hasn't been called yet
     */
    public RgbImage getRgbImage() throws ImageError {
        if (vecROk == null) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.NO_RESULT_AVAILABLE, null, null, null);
        }
        RgbImage rgbImage = new RgbImage(rgbInput.getWidth(), rgbInput.getHeight());
        for (final RgbRegion rgbRegion : vecROk) {
            final RgbRegion r = rgbRegion;
            final int nRgb = r.getColor();
            final Rect rect = r.getRect();
            rgbImage = rgbImage.fill(rect, nRgb);
        }
        return rgbImage;
    }
    
    /**
     * Implement toString
     * 
     * @return a string giving the name of this class, the parameters, and the
     *         vector result if push has been called.
     */
    @Override
    public String toString() {
        String szResult = super.toString() + "(" + new Integer(nRVar).toString() + "," + new Integer(nGVar).toString() + "," + new Integer(nBVar).toString();
        if (vecROk != null) {
            szResult += "," + vecROk.toString();
        }
        return szResult + ")";
    }
    
    @Override
    public void push(final Image<?> imageInput) throws ImageError {
        if (!(imageInput instanceof RgbImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, imageInput.toString(), null, null);
        }
        split((RgbImage) imageInput);
        super.setOutput(getRgbImage());
    }
}
