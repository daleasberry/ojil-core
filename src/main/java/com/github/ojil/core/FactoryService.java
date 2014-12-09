package com.github.ojil.core;

public class FactoryService implements OjilFactories {
    private OjilFactories factoryServiceSpi;
    
    @SuppressWarnings("unchecked")
    public FactoryService() {
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
    
    @Override
    public ImageFactory getImageFactory() {
        return factoryServiceSpi.getImageFactory();
    }
}