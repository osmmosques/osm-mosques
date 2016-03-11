package com.gurkensalat.osm.mosques.entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "STATISTICS")
public class StatisticsEntry extends AbstractPersistable<Long>
{
    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "VALID")
    private boolean valid;

}

