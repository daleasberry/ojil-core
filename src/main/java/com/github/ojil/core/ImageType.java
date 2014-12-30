package com.github.ojil.core;

public enum ImageType {
    CUSTOM(0), INT_RGB(1), INT_ARGB(2), INT_ARGB_PRE(3), INT_BGR(4), BYTE_BGR(5), BYTE_ABGR(6), BYTE_ABGR_PRE(7), USHORT_565_RGB(8), USHORT_555_RGB(9), BYTE_GRAY(10), USHORT_GRAY(11), BYTE_BINARY(12), BYTE_INDEXED(
            13);
    
    @SuppressWarnings("unused")
    private int value;
    
    private ImageType(final int value) {
        this.value = value;
    }
}
