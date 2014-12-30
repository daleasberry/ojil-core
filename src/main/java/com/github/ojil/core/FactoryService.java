package com.github.ojil.core;

public class FactoryService {
    private OjilFactories factoryServiceSpi;
    private static FactoryService factoryService;
    
    @SuppressWarnings("unchecked")
    private FactoryService() {
        Class<?> factoryServiceClass = null;
        try {
            factoryServiceClass = (Class<OjilFactories>) ClassLoader.getSystemClassLoader().loadClass("com.github.ojil.platform.FactoryServiceSpi");
        } catch (ClassNotFoundException e) {
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
        if(null == factoryService) {
            factoryService = new FactoryService();
        }
        return factoryService.factoryServiceSpi.getImageFactory();
    }

    public static synchronized IoFactory getIoFactory() {
        if(null == factoryService) {
            factoryService = new FactoryService();
        }
        return factoryService.factoryServiceSpi.getIoFactory();
    }
}