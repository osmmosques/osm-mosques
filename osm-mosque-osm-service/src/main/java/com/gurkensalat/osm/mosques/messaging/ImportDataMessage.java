package com.gurkensalat.osm.mosques.messaging;

import com.gurkensalat.osm.entity.OsmEntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportDataMessage implements Serializable
{
    OsmEntityType kind;

    String path;
}
