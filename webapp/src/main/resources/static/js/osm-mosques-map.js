$(document).ready(init);

var map;

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
    console.log("onMoveEnd:");
    console.log("    Center: " + map.getCenter());
    console.log("    minll:  " + map.getBounds().getSouthWest());
    console.log("    maxll:  " + map.getBounds().getNorthEast());

    // var minll=bounds.getSouthWest();
    // var maxll=bounds.getNorthEast();
    // var msg='leaflet/findbybbox.cgi?format=leaflet&bbox='+minll.lng+','+minll.lat+','+maxll.lng+','+maxll.lat;

    var request = ajaxQueryCache['myquery'];
    if (request != null)
    {
        request.cancel();
        console.log("Cancelled Ajax Query");
    }

    var url = '/rest/map/placemarkers/osm/as-json';
    var request = $.ajax({
        url: url,
        dataType: 'json',
        success: ajaxDataArrived
    })

    ajaxQueryCache['myquery'] = request;
}

function ajaxDataArrived() {
    console.log("Ajax Data Arrived");
    ajaxQueryCache['myquery'] = null;
}

<!-- Markers from here on -->
function init() {

    var ditibMosqueIcon = L.MakiMarkers.icon({
        icon: "religious-muslim",
        color: "#00cc33",
        size: "m"
    });

    var osmMosqueIcon = L.MakiMarkers.icon({
        icon: "religious-muslim",
        color: "#6699ff",
        size: "m"
    });

    var ditibPlaces = L.markerClusterGroup({chunkedLoading: true});

    var osmPlaces = L.markerClusterGroup({chunkedLoading: true});

    for (var i = 0; i < ditibAddressPoints.length; i++) {
        var a = ditibAddressPoints[i];
        var title = a[2];
        var marker = L.marker(L.latLng(a[0], a[1]), {title: title, icon: ditibMosqueIcon});
        marker.bindPopup(title);
        ditibPlaces.addLayer(marker);
    }

    for (var i = 0; i < osmAddressPoints.length; i++) {
        var a = osmAddressPoints[i];
        var title = a[2];
        var marker = L.marker(L.latLng(a[0], a[1]), {title: title, icon: osmMosqueIcon});
        marker.bindPopup(title);
        osmPlaces.addLayer(marker);
    }

    var overlays =
    {
        "OSM Places": osmPlaces,
        "DITIB Places": ditibPlaces
    };

    <!-- Map providers -->
    var osmMapnikMap = L.tileLayer.provider('OpenStreetMap.Mapnik');
    var stamenWatercolorMap = L.tileLayer.provider('Stamen.Watercolor');
    var thunderforestMap = L.tileLayer.provider('Thunderforest.Landscape');

    mapLink = '<a href="http://openstreetmap.org">OpenStreetMap</a>';

    var googleSatMap = new L.Google('SATELLITE');
    var googleTerrainMap = new L.Google('TERRAIN');
    var googleHybridMap = new L.Google('HYBRID');

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
        "Thunderforest": thunderforestMap,
        "Watercolor": stamenWatercolorMap,
        "Google Satellite": googleSatMap,
        "Google Terrain": googleTerrainMap,
        "Google Hybrid": googleHybridMap
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
