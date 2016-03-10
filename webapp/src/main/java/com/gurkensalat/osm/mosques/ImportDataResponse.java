package com.gurkensalat.osm.mosques;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportDataResponse
{
    @JsonProperty(required = true)
    private String response;

    @JsonProperty(required = false)
    private Long loaded;

    public ImportDataResponse(String response)
    {
        this.response = response;
        this.loaded = null;
    }

    public ImportDataResponse(String response, Long loaded)
    {
        this.response = response;
        this.loaded = loaded;
    }

    public String getResponse()
    {
        return response;
    }

    public Long getLoaded()
    {
        return loaded;
    }
}
