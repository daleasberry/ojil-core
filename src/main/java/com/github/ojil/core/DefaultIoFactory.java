package com.github.ojil.core;

public class DefaultIoFactory implements IoFactory {
    
    @Override
    public ImageIo createIo() {
        return new DefaultIo();
    }
}