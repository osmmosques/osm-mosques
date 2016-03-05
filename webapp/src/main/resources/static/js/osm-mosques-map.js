$(document).ready(init);

var map;

var osmPlaces = L.markerClusterGroup({chunkedLoading: true});

var osmMosqueIcon = L.MakiMarkers.icon({
    icon: "religious-muslim",
    color: "#6699ff",
    size: "m"
});

var ditibMosqueIcon = L.MakiMarkers.icon({
    icon: "religious-muslim",
    color: "#00cc33",
    size: "m"
});

var ditibPlaces = L.markerClusterGroup({chunkedLoading: true});

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
    console.log("    minll:  " + map.getBounds().getSouthWest());
    console.log("    maxll:  " + map.getBounds().getNorthEast());
}

function onMapMoveEnd() {
    // console.log("onMoveEnd:");
    // console.log("    Center: " + map.getCenter());
    // console.log("    minll:  " + map.getBounds().getSouthWest());
    // console.log("    maxll:  " + map.getBounds().getNorthEast());

    var sw = map.getBounds().getSouthWest();
    var ne = map.getBounds().getNorthEast();

    // if (osm places enabled...)
    if (true) {
        var request = ajaxQueryCache['osmPlacemarkerList'];
        if (request != null) {
            request.abort();
            console.log("Cancelled Ajax Query");
        }

        var url = '/rest/map/placemarkers/osm/as-json';
        var request = $.getJSON({
            url: url,
            data: {
                'minlat': sw.lat,
                'minlon': sw.lng,
                'maxlat': ne.lat,
                'maxlon': ne.lng
            },
            success: osmPlacemarkerListArrived
        })

        ajaxQueryCache['osmPlacemarkerList'] = request;
    }

    // if (ditib places enabled...)
    if (true) {
        var request = ajaxQueryCache['ditibPlacemarkerList'];
        if (request != null) {
            request.abort();
            console.log("Cancelled Ajax Query");
        }

        var url = '/rest/map/placemarkers/ditib/as-json';
        var request = $.getJSON({
            url: url,
            data: {
                'minlat': sw.lat,
                'minlon': sw.lng,
                'maxlat': ne.lat,
                'maxlon': ne.lng
            },
            success: ditibPlacemarkerListArrived
        })

        ajaxQueryCache['ditibPlacemarkerList'] = request;
    }
}

function ajaxDataArrived() {
    console.log("Ajax Data Arrived");
    ajaxQueryCache['myquery'] = null;
}

function osmPlacemarkerListArrived(data) {
    console.log("osmPlacemarkerListArrived");
    ajaxQueryCache['osmPlacemarkerList'] = null;

    console.log("    Clearing osmPlaces layers");
    osmPlaces.clearLayers();

    for (var i = 0; i < data.length; i++) {
        var node = data[i];
        var title = "OSM / " + node['name'];
        var lat = node['lat'];
        var lon = node['lon'];
        var marker = L.marker(L.latLng(lat, lon), {
                title: title, icon: osmMosqueIcon
            }
        );
        marker.bindPopup(title);
        osmPlaces.addLayer(marker);
    }

    console.log("    Created fresh osmPlace Markers");

    console.log("osmPlacemarkerListArrived finished")
}

function ditibPlacemarkerListArrived(data) {
    console.log("ditibPlacemarkerListArrived");
    ajaxQueryCache['ditibPlacemarkerList'] = null;

    console.log("    Clearing ditibPlaces layers");
    ditibPlaces.clearLayers();

    for (var i = 0; i < data.length; i++) {
        var node = data[i];
        var title = "DITIB / " + node['name'];
        var lat = node['lat'];
        var lon = node['lon'];
        var marker = L.marker(L.latLng(lat, lon), {
                title: title, icon: ditibMosqueIcon
            }
        );
        marker.bindPopup(title);
        ditibPlaces.addLayer(marker);
    }

    console.log("    Created fresh ditibPlace Markers");

    console.log("ditibPlacemarkerListArrived finished")
}

<!-- Markers from here on -->
function init() {

    var overlays =
    {
        "OSM Places": osmPlaces,
        "DITIB Places": ditibPlaces
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
        layers: [osmMapnikMap, osmPlaces, ditibPlaces]
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

    map.addLayer(osmPlaces);
    map.addLayer(ditibPlaces);

    <!-- Now add the layer switcher to the map -->
    var layers = new L.Control.Layers(baseLayers, overlays);
    map.addControl(layers);
    map.addControl(new L.Control.Permalink({text: 'Permalink', layers: layers}));

    <!-- Location control -->
    L.control.locate().addTo(map);

    <!-- Sidebar -->
    var sidebar = L.control.sidebar('sidebar', {position: 'left'});
    sidebar.addTo(map);

    <!-- Map move methods -->
    map.on('load', onMapLoaded);
    map.on('moveend', onMapMoveEnd);

    <!-- Finally, kick off the moveend event after the page finished loading -->
    onMapMoveEnd();
}
