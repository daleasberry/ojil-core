package com.github.ojil.algorithm;

import com.github.ojil.core.Error;
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.PipelineStage;

public class Gray8Unsigned2Signed extends PipelineStage {

	public void push(Image imageInput) throws Error {
		if (!(imageInput instanceof Gray8Image)) {
			throw new Error(Error.PACKAGE.ALGORITHM,
					ErrorCodes.IMAGE_NOT_GRAY8IMAGE, imageInput.toString(),
					null, null);
		}
		Gray8Image gray = (Gray8Image) imageInput;
		Byte[] rb = gray.getData();
		for (int i=0; i<gray.getWidth()*gray.getHeight(); i++) {
			rb[i] = (byte) ((0xff&rb[i]) + Byte.MIN_VALUE);
		}
		super.setOutput(gray);
	}

}
