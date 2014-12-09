package com.github.ojil.core;

import java.io.IOException;

public interface ImageIo {
    public void writeFile(Image image, int quality, String path) throws IOException;
    public Image readFile(String path);
}
