package com.github.ojil.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class FactoryServiceTest {
    
    @Test
    public void testGetImageFactory() {
        assertNotNull(FactoryService.getImageFactory());
        assertEquals(DefaultImageFactory.class, FactoryService.getImageFactory().getClass());
    }
}