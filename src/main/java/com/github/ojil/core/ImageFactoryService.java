package com.github.ojil.core;

public class ImageFactoryService {
    private OjilFactories factoryServiceSpi;
    private static ImageFactoryService factoryService;
    
    private ImageFactoryService() {
        Class<?> factoryServiceClass = null;
        try {
            factoryServiceClass = ClassLoader.getSystemClassLoader().loadClass("com.github.ojil.platform.FactoryServiceSpi");
        } catch (final ClassNotFoundException e) {
            System.out.println("No platform library available, loading DefaultFactoryService. FOR TESTING PURPOSES ONLY.");
            factoryServiceClass = DefaultFactoryService.class;
        }
        try {
            factoryServiceSpi = (OjilFactories) factoryServiceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized ImageFactory<?> getImageFactory() {
        if (null == ImageFactoryService.factoryService) {
            ImageFactoryService.factoryService = new ImageFactoryService();
        }
        return ImageFactoryService.factoryService.factoryServiceSpi.getImageFactory();
    }
    
    public static synchronized IoFactory getIoFactory() {
        if (null == ImageFactoryService.factoryService) {
            ImageFactoryService.factoryService = new ImageFactoryService();
        }
        return ImageFactoryService.factoryService.factoryServiceSpi.getIoFactory();
    }
}