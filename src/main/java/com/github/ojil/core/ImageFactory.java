package com.github.ojil.core;

import com.github.ojil.core.Image;
import com.github.ojil.core.ImageType;

public interface ImageFactory<PlatformImage extends Object> {
	Image<?, PlatformImage> createImage(int width, int height, ImageType type);
	Image<?, PlatformImage> createImage(PlatformImage platformImage);
}
