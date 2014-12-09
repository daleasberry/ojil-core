package com.github.ojil.core;

public class DefaultImageFactory implements ImageFactory {
    
    @Override
    public Image createImage(int width, int height, ImageType type) {
        Image newImage = null;
        switch (type) {
            case INT_RGB:
                newImage = new RgbImage(width, height);
                break;
            default:
                throw new RuntimeException("Not yet implemented");
        }
        return newImage;
    }
}
