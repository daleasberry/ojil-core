/**
 * Error defines a common error-reporting mechanism for all JJIL classes.
 * It includes an error code and up to three string objects representing
 * objects that explain the error, for example file names or images.
 * 
 * Build-specific libraries like jjil.android or jjil.j2se will define
 * a Error.toString() class that converts the Error object into a localized
 * error message.
 * 
 * 
 */
package com.github.ojil.core;

/**
 * Error defines a common error-reporting mechanism for all JJIL classes. It
 * includes an error code and up to three string objects representing objects
 * that explain the error, for example file names or images.
 * 
 * @author webb
 *
 */
public class ImageError extends Throwable {
    private static final long serialVersionUID = 5624201930901954089L;
    
    /**
     * J2ME's Java is only 1.4 so no enums. We must simulate them...
     */
    public static class PACKAGE {
        /**
         * Error code is defined in jjil.algorithm package.
         */
        public static final int ALGORITHM = 0;
        /**
         * Error code is defined in jjil.android package.
         */
        public static final int ANDROID = PACKAGE.ALGORITHM + 1;
        /**
         * Error code is defined in jjil.core package.
         */
        public static final int CORE = PACKAGE.ANDROID + 1;
        /**
         * Error code is defined in jjil.j2me package.
         */
        public static final int J2ME = PACKAGE.CORE + 1;
        /**
         * Error code is defined in jjil.j2se package.
         */
        public static final int J2SE = PACKAGE.J2ME + 1;
        
        /**
         * Count of packages.
         */
        public static final int COUNT = PACKAGE.J2SE + 1;
    }
    
    /**
     * nCode is a general error code. Possible values are defined in the CODES
     * enumerated type (really, we use ints for compatibility with J2ME).
     */
    private final int nCode;
    
    /**
     * The package where the error code is defined.
     */
    private final int nPackage;
    
    /**
     * szParam1 is a primary parameter giving detailed error information.
     */
    private final String szParam1;
    /**
     * szParam2 is a secondary parameter giving detailed error information.
     */
    private final String szParam2;
    /**
     * szParam3 is a tertiary parameter giving detailed error information.
     */
    private final String szParam3;
    
    /**
     * Copy constructor.
     * 
     * @param e
     *            Error object to copy.
     */
    public ImageError(final ImageError e) {
        nPackage = e.getPackage();
        nCode = e.getCode();
        szParam1 = e.getParam1();
        szParam2 = e.getParam2();
        szParam3 = e.getParam3();
    }
    
    /**
     * This is how Error objects are created. The first two parameters determine
     * the specific type of error. The other parameters give information about
     * the objects causing the error.
     * 
     * @param nPackage
     *            package where error code is defined.
     * @param nCode
     *            : the error code
     * @param szParam1
     *            : a first parameter giving detailed information
     * @param szParam2
     *            : a second parameter giving detailed information
     * @param szParam3
     *            : a third parameter giving detailed information
     */
    public ImageError(final int nPackage, final int nCode, final String szParam1, final String szParam2, final String szParam3) {
        this.nPackage = nPackage;
        this.nCode = nCode;
        this.szParam1 = szParam1;
        this.szParam2 = szParam2;
        this.szParam3 = szParam3;
    }
    
    /**
     * 
     * @return the error code.
     */
    public int getCode() {
        return nCode;
    }
    
    /**
     * 
     * @return the package where the error code is defined.
     */
    public int getPackage() {
        return nPackage;
    }
    
    /**
     * 
     * @return first parameter describing error.
     */
    public String getParam1() {
        return szParam1;
    }
    
    /**
     * 
     * @return second parameter describing error.
     */
    public String getParam2() {
        return szParam2;
    }
    
    /**
     * 
     * @return third parameter describing error.
     */
    public String getParam3() {
        return szParam3;
    }
    
    /**
     * 
     * @return String including all parameters describing error.
     */
    protected String parameters() {
        String sz = "(";
        if (getParam1() != null) {
            sz += getParam1();
        }
        sz += ",";
        if (getParam2() != null) {
            sz += getParam2();
        }
        sz += ",";
        if (getParam3() != null) {
            sz += getParam3();
        }
        sz += ")";
        return sz;
    }
    
    /**
     * 
     * @return String describing this instance of Error.
     */
    @Override
    public String toString() {
        return new Integer(nPackage).toString() + " " + new Integer(nCode).toString() + parameters();
    }
}
