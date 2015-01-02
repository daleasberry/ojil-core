package com.github.ojil.core;

public class DefaultImageFactory implements ImageFactory<Void> {
    
    @Override
    public Image<?, Void> createImage(int width, int height, ImageType type) {
        Image<?, Void> newImage = null;
        switch (type) {
            case INT_RGB:
                newImage = new RgbImage<>(width, height);
                break;
            default:
                throw new RuntimeException("Not yet implemented");
        }
        return newImage;
    }
    
    @Override
    public Image<?, Void> createImage(Void platformImage) {
        throw new RuntimeException("DefaultImageFactory cannot create a platform-specific image. Do not make calls to this factory, use the ImageFactoryService to create platform-specific images.");
    }
}
