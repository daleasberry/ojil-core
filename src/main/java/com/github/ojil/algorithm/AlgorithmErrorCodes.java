package com.github.ojil.algorithm;

/**
 * Provides a list of error codes for use in creating Error objects.
 * <p>
 * J2ME's version of Java unfortunately doesn't support enums. So we simulate
 * them using an old programming trick...
 */
public class AlgorithmErrorCodes {
    /**
     * null pointer in Label.compareTo.
     */
    public static final int CONN_COMP_LABEL_COMPARETO_NULL = 0;
    /**
     * label # outside bounds of array.
     */
    public static final int CONN_COMP_LABEL_OUT_OF_BOUNDS = AlgorithmErrorCodes.CONN_COMP_LABEL_COMPARETO_NULL + 1;
    /**
     * FFT exceeds maximum size.
     */
    public static final int FFT_SIZE_LARGER_THAN_MAX = AlgorithmErrorCodes.CONN_COMP_LABEL_OUT_OF_BOUNDS + 1;
    /**
     * FFT only works for powers of 2.
     */
    public static final int FFT_SIZE_NOT_POWER_OF_2 = AlgorithmErrorCodes.FFT_SIZE_LARGER_THAN_MAX + 1;
    /**
     * Heap is empty in findMin.
     */
    public static final int HEAP_EMPTY = AlgorithmErrorCodes.FFT_SIZE_NOT_POWER_OF_2 + 1;
    /**
     * Histogram array must be 256 in length.
     */
    public static final int HISTOGRAM_LENGTH_NOT_256 = AlgorithmErrorCodes.HEAP_EMPTY + 1;
    /**
     * Color not red, green, or blue.
     */
    public static final int ILLEGAL_COLOR_CHOICE = AlgorithmErrorCodes.HISTOGRAM_LENGTH_NOT_256 + 1;
    /**
     * Input image has to be a Complex32Image.
     */
    public static final int IMAGE_NOT_COMPLEX32IMAGE = AlgorithmErrorCodes.ILLEGAL_COLOR_CHOICE + 1;
    /**
     * Input image has to be a Gray16Image.
     */
    public static final int IMAGE_NOT_GRAY16IMAGE = AlgorithmErrorCodes.IMAGE_NOT_COMPLEX32IMAGE + 1;
    /**
     * Input image has to be a Gray16Image.
     */
    public static final int IMAGE_NOT_GRAY32IMAGE = AlgorithmErrorCodes.IMAGE_NOT_GRAY16IMAGE + 1;
    /**
     * Input image has to be a Gray16Image.
     */
    public static final int IMAGE_NOT_GRAY8IMAGE = AlgorithmErrorCodes.IMAGE_NOT_GRAY32IMAGE + 1;
    /**
     * Input image has to be an RgbImage.
     */
    public static final int IMAGE_NOT_RGBIMAGE = AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE + 1;
    /**
     * Input image has to be square.
     */
    public static final int IMAGE_NOT_SQUARE = AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE + 1;
    /**
     * Input image sizes differ, should be same.
     */
    public static final int IMAGE_SIZES_DIFFER = AlgorithmErrorCodes.IMAGE_NOT_SQUARE + 1;
    /**
     * input image smaller than minimum.
     */
    public static final int IMAGE_TOO_SMALL = AlgorithmErrorCodes.IMAGE_SIZES_DIFFER + 1;
    /**
     * bounds set input image size to be negative (or zero).
     */
    public static final int INPUT_IMAGE_SIZE_NEGATIVE = AlgorithmErrorCodes.IMAGE_TOO_SMALL + 1;
    /**
     * the input terminated before expected (parse error).
     */
    public static final int INPUT_TERMINATED_EARLY = AlgorithmErrorCodes.INPUT_IMAGE_SIZE_NEGATIVE + 1;
    /**
     * unspecified IO Exception.
     */
    public static final int IO_EXCEPTION = AlgorithmErrorCodes.INPUT_TERMINATED_EARLY + 1;
    /**
     * Grayvalue lookup table must be 256 in length.
     */
    public static final int LOOKUP_TABLE_LENGTH_NOT_256 = AlgorithmErrorCodes.IO_EXCEPTION + 1;
    /**
     * object in collection not expected type.
     */
    public static final int OBJECT_NOT_EXPECTED_TYPE = AlgorithmErrorCodes.LOOKUP_TABLE_LENGTH_NOT_256 + 1;
    /**
     * output image size is negative (or zero).
     */
    public static final int OUTPUT_IMAGE_SIZE_NEGATIVE = AlgorithmErrorCodes.OBJECT_NOT_EXPECTED_TYPE + 1;
    /**
     * Parameter value out of range.
     */
    public static final int PARAMETER_OUT_OF_RANGE = AlgorithmErrorCodes.OUTPUT_IMAGE_SIZE_NEGATIVE + 1;
    /**
     * Two parameters select a null or contradictory range.
     */
    public static final int PARAMETER_RANGE_NULL_OR_NEGATIVE = AlgorithmErrorCodes.PARAMETER_OUT_OF_RANGE + 1;
    /**
     * Two parameters should be the same size but aren't.
     */
    public static final int PARAMETER_SIZES_DIFFER = AlgorithmErrorCodes.PARAMETER_RANGE_NULL_OR_NEGATIVE + 1;
    /**
     * A parameter is the wrong size.
     */
    public static final int PARAMETER_WRONG_SIZE = AlgorithmErrorCodes.PARAMETER_RANGE_NULL_OR_NEGATIVE + 1;
    /**
     * parse error while reading input.
     */
    public static final int PARSE_ERROR = AlgorithmErrorCodes.PARAMETER_WRONG_SIZE + 1;
    /**
     * In reduce, input image is not an integer multiple of the output size.
     */
    public static final int REDUCE_INPUT_IMAGE_NOT_MULTIPLE_OF_OUTPUT_SIZE = AlgorithmErrorCodes.PARSE_ERROR + 1;
    /**
     * In rectangular shrink output is larger than input.
     */
    public static final int SHRINK_OUTPUT_LARGER_THAN_INPUT = AlgorithmErrorCodes.REDUCE_INPUT_IMAGE_NOT_MULTIPLE_OF_OUTPUT_SIZE + 1;
    /**
     * statistics computation gave a variance < 0.
     */
    public static final int STATISTICS_VARIANCE_LESS_THAN_ZERO = AlgorithmErrorCodes.SHRINK_OUTPUT_LARGER_THAN_INPUT + 1;
    /**
     * In stretch operation output is smaller than input.
     */
    public static final int STRETCH_OUTPUT_SMALLER_THAN_INPUT = AlgorithmErrorCodes.STATISTICS_VARIANCE_LESS_THAN_ZERO + 1;
    /**
     * Subimage requested but no more are available.
     */
    public static final int SUBIMAGE_NO_IMAGE_AVAILABLE = AlgorithmErrorCodes.STRETCH_OUTPUT_SMALLER_THAN_INPUT + 1;
    /**
     * The threshold cannot be negative.
     */
    public static final int THRESHOLD_NEGATIVE = AlgorithmErrorCodes.SUBIMAGE_NO_IMAGE_AVAILABLE + 1;
    /**
     * Warp: end left column is >= end right column.
     */
    public static final int WARP_END_LEFT_COL_GE_END_RIGHT_COL = AlgorithmErrorCodes.THRESHOLD_NEGATIVE + 1;
    /**
     * Warp: start left column is >= start right column.
     */
    public static final int WARP_START_LEFT_COL_GE_START_RIGHT_COL = AlgorithmErrorCodes.WARP_END_LEFT_COL_GE_END_RIGHT_COL + 1;
    /**
     * Warp: start row is >= end row.
     */
    public static final int WARP_START_ROW_GE_END_ROW = AlgorithmErrorCodes.WARP_START_LEFT_COL_GE_START_RIGHT_COL + 1;
    
    /**
     * Marks end of list.
     */
    public static final int COUNT = AlgorithmErrorCodes.WARP_START_ROW_GE_END_ROW + 1;
}
