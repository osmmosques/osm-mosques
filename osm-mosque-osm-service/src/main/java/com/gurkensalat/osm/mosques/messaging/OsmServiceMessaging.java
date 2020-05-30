package com.gurkensalat.osm.mosques.messaging;

public class OsmServiceMessaging
{
    private OsmServiceMessaging()
    {
        // empty constructor for global static strings
    }

    public final static String KIND_SYNC = "sync";

    public final static String KIND_ASYNC = "async";

    public final static String QUEUE_NAME_IMPORT_OSM_DATA = "import-osm-data";
}
