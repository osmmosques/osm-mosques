package com.gurkensalat.osm.mosques.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
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

    @Column(name = "COUNTRY_CODE", length = 5)
    private String countryCode;

    @Column(name = "COUNTRY_NAME", length = 20)
    private String countryName;

    @Column
    private Integer osmMosqueNodes;

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getCountryName()
    {
        return countryName;
    }

    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
    }

    public Integer getOsmMosqueNodes()
    {
        return osmMosqueNodes;
    }

    public void setOsmMosqueNodes(Integer osmMosqueNodes)
    {
        this.osmMosqueNodes = osmMosqueNodes;
    }

    public String toString()
    {
        return (new ToStringBuilder(this))
                .append("countryCode", countryCode)
                .append("countryName", countryName)
                .append("osmMosqueNodes", osmMosqueNodes)
                .toString();
    }
}

