package com.gurkensalat.osm.mosques.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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

    @Column(name = "CREATION_TIME", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @CreatedDate
    private DateTime creationTime;

    @Column(name = "MODIFICATION_TIME", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @LastModifiedDate
    private DateTime modificationTime;

    @Column(name = "COUNTRY_CODE", length = 5)
    private String countryCode;

    @Column(name = "COUNTRY_NAME", length = 20)
    private String countryName;

    private transient String countryFlagImgUrl;

    @Column(name = "OSM_MOSQUE_NODES")
    private Integer osmMosqueNodes;

    @Column(name = "DITIB_MOSQUE_NODES")
    private Integer ditibMosqueNodes;

    @Column(name = "MIN_LAT")
    private double minLat;

    @Column(name = "MIN_LON")
    private double minLon;

    @Column(name = "MAX_LAT")
    private double maxLat;

    @Column(name = "MAX_LON")
    private double maxLon;

    @Column(name = "CENTROID_LAT")
    private double centroidLat;

    @Column(name = "CENTROID_LON")
    private double centroidLon;

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
        this.setModificationTime(DateTime.now());
    }

    public DateTime getCreationTime()
    {
        return creationTime;
    }

    public void setCreationTime(DateTime creationTime)
    {
        this.creationTime = creationTime;
    }

    public DateTime getModificationTime()
    {
        return modificationTime;
    }

    public void setModificationTime(DateTime modificationTime)
    {
        this.modificationTime = modificationTime;
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

    public String getCountryFlagImgUrl()
    {
        return countryFlagImgUrl;
    }

    public void setCountryFlagImgUrl(String countryFlagImgUrl)
    {
        this.countryFlagImgUrl = countryFlagImgUrl;
    }

    public Integer getOsmMosqueNodes()
    {
        return osmMosqueNodes;
    }

    public void setOsmMosqueNodes(Integer osmMosqueNodes)
    {
        this.osmMosqueNodes = osmMosqueNodes;
    }

    public Integer getDitibMosqueNodes()
    {
        return ditibMosqueNodes;
    }

    public void setDitibMosqueNodes(Integer ditibMosqueNodes)
    {
        this.ditibMosqueNodes = ditibMosqueNodes;
    }

    public double getMinLat()
    {
        return minLat;
    }

    public void setMinLat(double minLat)
    {
        this.minLat = minLat;
    }

    public double getMinLon()
    {
        return minLon;
    }

    public void setMinLon(double minLon)
    {
        this.minLon = minLon;
    }

    public double getMaxLat()
    {
        return maxLat;
    }

    public void setMaxLat(double maxLat)
    {
        this.maxLat = maxLat;
    }

    public double getMaxLon()
    {
        return maxLon;
    }

    public void setMaxLon(double maxLon)
    {
        this.maxLon = maxLon;
    }

    public double getCentroidLat()
    {
        return centroidLat;
    }

    public void setCentroidLat(double centroidLat)
    {
        this.centroidLat = centroidLat;
    }

    public double getCentroidLon()
    {
        return centroidLon;
    }

    public void setCentroidLon(double centroidLon)
    {
        this.centroidLon = centroidLon;
    }

    public String toString()
    {
        return (new ToStringBuilder(this))
                .append("countryCode", countryCode)
                .append("countryName", countryName)
                .append("osmMosqueNodes", osmMosqueNodes)
                .append("ditibMosqueNodes", ditibMosqueNodes)
                .toString();
    }
}

