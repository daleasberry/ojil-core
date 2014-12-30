package com.github.ojil.algorithm;

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

public class Gray8Unsigned2Signed extends PipelineStage {
    
    @Override
    public void push(final Image<?> imageInput) throws ImageError {
        if (!(imageInput instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, imageInput.toString(), null, null);
        }
        final Gray8Image gray = (Gray8Image) imageInput;
        final Byte[] rb = gray.getData();
        for (int i = 0; i < (gray.getWidth() * gray.getHeight()); i++) {
            rb[i] = (byte) ((0xff & rb[i]) + Byte.MIN_VALUE);
        }
        super.setOutput(gray);
    }
    
}
