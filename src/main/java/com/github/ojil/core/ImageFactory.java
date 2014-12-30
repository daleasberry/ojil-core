package com.github.ojil.core;

import com.github.ojil.core.Image;
import com.github.ojil.core.ImageType;

public interface ImageFactory {
	Image<?> createImage(int width, int height, ImageType type);
}
