package com.gmc.server.container;

import com.gmc.server.info.MetaData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ClientContainer {
    public CopyOnWriteArraySet<MetaData> metaDataSet = new CopyOnWriteArraySet<>();

    public ClientContainer(){}

    public void putMetaDataSet(MetaData metaData){
        metaDataSet.add(metaData);
    }
    public CopyOnWriteArraySet<MetaData> getMetaDataSet(){return metaDataSet;}
}
