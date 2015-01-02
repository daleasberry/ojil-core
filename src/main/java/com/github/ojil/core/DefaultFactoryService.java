package com.github.ojil.core;

public class DefaultFactoryService implements OjilFactories {
    
    @Override
    public ImageFactory<?> getImageFactory() {
        return new DefaultImageFactory();
    }

    @Override
    public IoFactory getIoFactory() {
        return new DefaultIoFactory();
    }
}