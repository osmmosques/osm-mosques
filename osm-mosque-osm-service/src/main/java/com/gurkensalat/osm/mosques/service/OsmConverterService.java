package com.gurkensalat.osm.mosques.service;

public interface OsmConverterService
{
    void importNodes(String path);

    void importWays(String path);
}