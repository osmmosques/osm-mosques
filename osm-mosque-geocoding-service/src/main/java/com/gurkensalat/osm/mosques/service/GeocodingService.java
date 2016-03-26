package com.gurkensalat.osm.mosques.service;

import com.tandogan.geostuff.opencagedata.entity.GeocodeResponse;

public interface GeocodingService
{
    GeocodeResponse reverse(String key);
}