package com.gurkensalat.osm.mosques.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OsmConverterResult
{
    String what;

    String path;

    LocalDateTime start;

    LocalDateTime end;

    int places;

    int nodes;

    int ways;
}
