package com.github.ojil.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class FactoryServiceTest {
    
    @Test
    public void testGetImageFactory() {
        FactoryService service = new FactoryService();
        service.getImageFactory();
        assertNotNull(service.getImageFactory());
        assertEquals(DefaultImageFactory.class, service.getImageFactory().getClass());
    }
}