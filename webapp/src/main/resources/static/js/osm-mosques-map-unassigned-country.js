$(document).ready(init);

var map;

var popup;

var userPreferencesPopup;

var lowZoomMode = true;

var osmUnassignedDataUrl;
var osmUnassignedMarkersArrivedFunction;

var osmKnownDataUrl;
var osmKnownMarkersArrivedFunction;

var osmPopupDetailsUrl = "/osm-details-unassigned-country-for-popup";

var osmUnassignedPlaces = L.markerClusterGroup({chunkedLoading: true});

var osmKnownPlaces = L.markerClusterGroup({chunkedLoading: true});


var osmUnassignedMosqueIcon = L.MakiMarkers.icon({
    icon: "religious-muslim",
    color: "#00cc00",
    size: "m"
});

var osmKnownMosqueIcon = L.MakiMarkers.icon({
    icon: "religious-muslim",
    color: "#ff3300",
    size: "m"
});

var osmStatisticsMosqueIcon = L.MakiMarkers.icon({
    icon: "religious-muslim",
    color: "#00cc33",
    size: "m"
});

var ajaxQueryCache = {};

<!-- For contextmenu -->
function showCoordinates(e) {
    alert(e.latlng);
}

function centerMap(e) {
    map.panTo(e.latlng);
}

<!-- Map move methods -->
function onMapLoaded() {
    console.log("onLoaded:");
    console.log("    Center: " + map.getCenter());
    console.log("    Zoom: " + map.getZoom());
    console.log("    minll:  " + map.getBounds().getSouthWest());
    console.log("    maxll:  " + map.getBounds().getNorthEast());
}

function onMapMoveEnd() {
    var sw = map.getBounds().getSouthWest();
    var ne = map.getBounds().getNorthEast();

    if (map.getZoom() < 6) {
        osmUnassignedDataUrl = '/rest/map/statisticmarkers/osm/as-json';
        osmUnassignedMarkersArrivedFunction = osmStatisticsmarkerListArrived;
        osmKnownDataUrl = '/rest/map/statisticmarkers/osm/as-json';
        osmKnownMarkersArrivedFunction = osmStatisticsmarkerListArrived;
        if (lowZoomMode == true) {
            lowZoomMode = false;
            osmKnownPlaces.singleMarkerMode = false;
            osmKnownPlaces.disableClusteringAtZoom = false;
            osmUnassignedPlaces.singleMarkerMode = false;
            osmUnassignedPlaces.disableClusteringAtZoom = false;
        }
    } else {
        osmUnassignedDataUrl = '/rest/map/placemarkers/osm-unassigned.json';
        osmUnassignedMarkersArrivedFunction = osmUnassignedPlacemarkerListArrived;
        osmKnownDataUrl = '/rest/map/placemarkers/osm-known.json';
        osmKnownMarkersArrivedFunction = osmKnownPlacemarkerListArrived;
        if (lowZoomMode == false) {
            lowZoomMode = true;
            osmKnownPlaces.singleMarkerMode = true;
            osmKnownPlaces.disableClusteringAtZoom = 1;
            osmUnassignedPlaces.singleMarkerMode = true;
            osmUnassignedPlaces.disableClusteringAtZoom = 1;
        }
    }
    // if (osm places enabled...)

    if (true) {
        var request = ajaxQueryCache['osmUnassignedPlacemarkerList'];
        if (request != null) {
            request.abort();
            console.log("Cancelled Ajax Query");
        }

        var request = $.getJSON({
            url: osmUnassignedDataUrl,
            data: {
                'minlat': sw.lat,
                'minlon': sw.lng,
                'maxlat': ne.lat,
                'maxlon': ne.lng
            },
            success: osmUnassignedMarkersArrivedFunction
        })

        ajaxQueryCache['osmUnassignedPlacemarkerList'] = request;
    }

    if (true) {
        var request = ajaxQueryCache['osmKnownPlacemarkerList'];
        if (request != null) {
            request.abort();
            console.log("Cancelled Ajax Query");
        }

        var request = $.getJSON({
            url: osmKnownDataUrl,
            data: {
                'minlat': sw.lat,
                'minlon': sw.lng,
                'maxlat': ne.lat,
                'maxlon': ne.lng
            },
            success: osmKnownMarkersArrivedFunction
        })

        ajaxQueryCache['osmKnownPlacemarkerList'] = request;
    }

}

function osmUnassignedPlacemarkerListArrived(data) {
    ajaxQueryCache['osmUnassignedPlacemarkerList'] = null;

    osmUnassignedPlaces.clearLayers();

    for (var i = 0; i < data.length; i++) {
        var node = data[i];
        var key = node['key'];
        var title = "OSM / " + key + ' / ' + node['name'];
        var lat = node['lat'];
        var lon = node['lon'];
        var marker = L.marker(L.latLng(lat, lon), {
                customAttrPlaceKey: key,
                title: title,
                icon: osmUnassignedMosqueIcon
            }
        );
        marker.bindPopup(title);
        marker.on('click', onClickOsmMarker);
        osmUnassignedPlaces.addLayer(marker);
    }
}

function osmKnownPlacemarkerListArrived(data) {
    ajaxQueryCache['osmKnownPlacemarkerList'] = null;

    // console.log("    Clearing osmPlaces layers");
    osmKnownPlaces.clearLayers();

    for (var i = 0; i < data.length; i++) {
        var node = data[i];
        var key = node['key'];
        var title = "OSM / " + key + ' / ' + node['name'];
        var lat = node['lat'];
        var lon = node['lon'];
        var marker = L.marker(L.latLng(lat, lon), {
                customAttrPlaceKey: key,
                title: title,
                icon: osmKnownMosqueIcon
            }
        );
        marker.bindPopup(title);
        marker.on('click', onClickOsmMarker);
        osmKnownPlaces.addLayer(marker);
    }
}

function osmStatisticsmarkerListArrived(data) {
    console.log("osmStatisticsmarkerListArrived");
    ajaxQueryCache['osmPlacemarkerList'] = null;

    console.log("    Clearing osmPlaces layers");
    osmUnassignedPlaces.clearLayers();

    for (var i = 0; i < data.length; i++) {
        var node = data[i];
        var title = (node['countryName'] == "" ? node['countryCode'] : node['countryName']) + " : " + node['osmMosqueTotal'] + " Places";
        var lat = node['lat'];
        var lon = node['lon'];
        var marker = L.marker(L.latLng(lat, lon), {
                title: title, icon: osmStatisticsMosqueIcon
            }
        );
        marker.bindPopup(title);
        marker.on('click', function(){
            marker._popup.setContent('something better content for statistics nodes...')
        });
        osmUnassignedPlaces.addLayer(marker);
    }

    console.log("    Created fresh osmPlace Markers");

    console.log("osmStatisticsmarkerListArrived finished")
}

function init() {

    var overlays =
    {
        "WITHOUT addr:country": osmUnassignedPlaces,
        "with addr:country": osmKnownPlaces
    };

    <!-- Map providers -->
    var osmMapnikMap = L.tileLayer.provider('OpenStreetMap.Mapnik');

    <!-- Now the map itself -->
    map = L.map('map', {
        center: [48.12955, 11.34873],
        contextmenu: true,
        contextmenuWidth: 140,
        contextmenuItems: [{
            text: 'Show coordinates', callback: showCoordinates
        }, {
            text: 'Center map here', callback: centerMap
        }],
        zoom: 11,
        zoomControl: true,
        layers: [osmMapnikMap, osmUnassignedPlaces, osmKnownPlaces]
    });

    var baseLayers =
    {
        "OSM Mapnik": osmMapnikMap,
        "Thunderforest": L.tileLayer.provider('Thunderforest.Landscape'),
        "Watercolor": L.tileLayer.provider('Stamen.Watercolor'),
        "Esri Streetmap": L.tileLayer.provider('Esri.WorldStreetMap'),
        "Esri Imagery": L.tileLayer.provider('Esri.WorldImagery'),
        "Google Satellite": new L.Google('SATELLITE'),
        "Google Terrain": new L.Google('TERRAIN'),
        "Google Hybrid": new L.Google('HYBRID')
    };

    map.addLayer(osmUnassignedPlaces);
    map.addLayer(osmKnownPlaces);

    <!-- Now add the layer switcher to the map -->
    var layers = new L.Control.Layers(baseLayers, overlays);
    map.addControl(layers);
    map.addControl(new L.Control.Permalink({text: 'Permalink', layers: layers}));

    <!-- Location control -->
    L.control.locate().addTo(map);

    <!-- Preferences control -->
    userPreferencesPopup = L.popup().setContent('<p>Hello world!<br />This is a nice popup.</p>');
    L.easyButton('fa-user', onClickUserPreferences).addTo(map);

    <!-- Map move methods -->
    map.on('load', onMapLoaded);
    map.on('moveend', onMapMoveEnd);

    <!-- Finally, kick off the moveend event after the page finished loading -->
    onMapMoveEnd();
}
