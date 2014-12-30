package com.github.ojil.core;

public class FactoryService {
    private OjilFactories factoryServiceSpi;
    private static FactoryService factoryService;
    
    private FactoryService() {
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
    
    public static synchronized ImageFactory getImageFactory() {
        if (null == FactoryService.factoryService) {
            FactoryService.factoryService = new FactoryService();
        }
        return FactoryService.factoryService.factoryServiceSpi.getImageFactory();
    }
    
    public static synchronized IoFactory getIoFactory() {
        if (null == FactoryService.factoryService) {
            FactoryService.factoryService = new FactoryService();
        }
        return FactoryService.factoryService.factoryServiceSpi.getIoFactory();
    }
}