package com.gurkensalat.osm.mosques;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericResponse
{
    @JsonProperty(required = true)
    private String response;

    public GenericResponse(String response)
    {
        this.response = response;
    }

    public String getResponse()
    {
        return response;
    }
}
