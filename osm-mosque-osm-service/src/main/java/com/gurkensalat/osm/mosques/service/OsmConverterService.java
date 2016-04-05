package com.gurkensalat.osm.mosques.service;

public interface OsmConverterService
{
    OsmConverterResult importNodes(String path);

    OsmConverterResult importWays(String path);

    OsmConverterResult fetchAndImportNode(String id);

    OsmConverterResult fetchAndImportWay(String id);
}