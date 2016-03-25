package com.gurkensalat.osm.mosques.service;

public interface OsmConverterService
{
    void importNodes(String path);

    void importWays(String path);

    void fetchAndImportNode(String id);

    void fetchAndImportWay(String id);
}