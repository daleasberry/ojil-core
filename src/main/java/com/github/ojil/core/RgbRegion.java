/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.ojil.core;

/**
 * Class for managing regtangular regions in an RgbImage and their color mean
 * and variance values.
 * 
 * @author webb
 */
public class RgbRegion {
    /**
     * Class for holding a mean and variance color value.
     */
    public static class MeanVar {
        int nRgbMean;
        int nR, nG, nB;
        
        /**
         * Create a new MeanVar value, specifying color mean as a packed RGB
         * word and variance as int variables.
         * 
         * @param nRgbMean
         *            the packed RGB mean
         * @param nR
         *            red variance
         * @param nG
         *            green variance
         * @param nB
         *            blue variance
         */
        public MeanVar(final int nRgbMean, final int nR, final int nG, final int nB) {
            this.nRgbMean = nRgbMean;
            this.nR = nR;
            this.nG = nG;
            this.nB = nB;
        }
        
        /**
         * Return packed RGB mean color.
         * 
         * @return mean RGB color, packed in int
         */
        public int getMean() {
            return nRgbMean;
        }
        
        /**
         * Return red variance.
         * 
         * @return red variance
         */
        public int getRVar() {
            return nR;
        }
        
        /**
         * Return green variance
         * 
         * @return green variance
         */
        public int getGVar() {
            return nG;
        }
        
        /**
         * Return blue variance
         * 
         * @return blue variance
         */
        public int getB() {
            return nB;
        }
        
        @Override
        public String toString() {
            return super.toString() + "(Mean=" + RgbVal.toString(nRgbMean) + ",Var=[" + new Integer(nR).toString() + "," + new Integer(nG).toString() + "," + new Integer(nB).toString() + "])";
        }
    }
    
    private final MeanVar var;
    private final Rect r;
    
    /**
     * Construct a new RgbRegion
     * 
     * @param r
     *            Rect boundaries of the region
     * @param var
     *            MeanVar mean and variance of the region
     */
    public RgbRegion(final Rect r, final MeanVar var) {
        this.r = r;
        this.var = var;
    }
    
    /**
     * Return mean color of the region
     * 
     * @return packed RGB word giving color of region
     */
    public int getColor() {
        return var.getMean();
    }
    
    /**
     * Return boundaries of region
     * 
     * @return Rect boundary of region
     */
    public Rect getRect() {
        return r;
    }
    
    /**
     * Implement toString
     * 
     * @return the class name, rectangle, and mean/variance.
     */
    @Override
    public String toString() {
        return super.toString() + "," + r.toString() + "," + var.toString();
    }
}
