//
let map;

const markers = L.markerClusterGroup();

$(document).ready(init);

function init() {

    <!-- Map providers -->
    const defaultLayer = L.tileLayer.provider('Esri.WorldStreetMap');

    <!-- Now the map itself -->
    map = L.map('map', {
        center: [51.45, -0.13],
        zoom: 15,
        zoomControl: true,
        editInOSMControlOptions: {
            zoomThreshold: 16,
        },
        layers: [defaultLayer]
    });

    const baseLayers = {
        "OSM Mapnik": defaultLayer,
        // "Thunderforest": L.tileLayer.provider('Thunderforest.Landscape'),
        "Watercolor": L.tileLayer.provider('Stamen.Watercolor'),
        "Esri Streetmap": L.tileLayer.provider('Esri.WorldStreetMap'),
        "Esri Imagery": L.tileLayer.provider('Esri.WorldImagery')
    };

// if (!(typeof google === 'undefined')) {
//    baseLayers["Google Sattelite"] = new L.Google('SATELLITE');
//    baseLayers["Google Terrain"] = new L.Google('TERRAIN');
//    baseLayers["Google Hybrid"] = new L.Google('HYBRID');
// }

// if (1 === 1) {
// TODO find a way to hide the accessToken ...
// }

    L.circle([48.135, 11.389], 500, {
        color: 'red',
        fillColor: '#f03',
        fillOpacity: 0.1
    }).addTo(map);

    L.polyline([[-90, -180], [-90, 180]], { color: 'red' }).addTo(map);
    L.polyline([[-45, -180], [-45, 180]], { color: 'red' }).addTo(map);
    L.polyline([[  0, -180], [  0, 180]], { color: 'red' }).addTo(map);
    L.polyline([[ 45, -180], [ 45, 180]], { color: 'red' }).addTo(map);
    L.polyline([[ 90, -180], [ 90, 180]], { color: 'red' }).addTo(map);

    L.polyline([[-90, -180], [ 90,-180]], { color: 'red' }).addTo(map);
    L.polyline([[-90,  -90], [ 90, -90]], { color: 'red' }).addTo(map);
    L.polyline([[-90,    0], [ 90,   0]], { color: 'red' }).addTo(map);
    L.polyline([[-90,   90], [ 90,  90]], { color: 'red' }).addTo(map);
    L.polyline([[-90,  180], [ 90, 180]], { color: 'red' }).addTo(map);

    <!-- Now add the layer switcher to the map -->
    var layers = new L.Control.Layers(baseLayers);
    map.addControl(layers);

    map.addControl(new L.Control.Permalink({text: 'Permalink', layers: layers}));

    <!-- Location control -->
    L.control.locate().addTo(map);

    <!-- And location search -->
    L.Control.geocoder().addTo(map);

    <!-- Preferences control -->
    L.easyButton('fa-user', function () {
        alert('You just clicked on the user button');
    }, 'someday this will be user preferences...').addTo(map);

    <!-- Markers... -->

    // markers.addLayer(L.marker(getRandomLatLng(map)));

    map.addLayer(markers);

    let popup = L.popup();

    function onMapClick(e) {
        popup
            .setLatLng(e.latlng)
            .setContent("You clicked the map at " + e.latlng.toString())
            .openOn(map);
    }

    map.on('click', onMapClick);

    <!-- Map move methods -->
    map.on('load', onMapLoaded);
    map.on('moveend', onMapMoveEnd);

    <!-- Finally, kick off the moveend event after the page finished loading -->
    onMapMoveEnd();
}

<!-- Map move methods -->
function onMapLoaded() {
    console.log("onLoaded:");
    console.log("    Center: " + map.getCenter());
    console.log("    Zoom: " + map.getZoom());
    console.log("    minll:  " + map.getBounds().getSouthWest());
    console.log("    maxll:  " + map.getBounds().getNorthEast());
}

<!-- Map move methods -->
function onMapMoveEnd() {

    const sw = map.getBounds().getSouthWest();
    const ne = map.getBounds().getNorthEast();

    let osmDataUrl;

    if (map.getZoom() < 6) {
        osmDataUrl = '/rest/map/placemarkers/osm';
        // osmDataUrl = '/rest/map/statisticmarkers/osm'; //as-json';
        // osmMarkersArrivedFunction = osmStatisticsmarkerListArrived;
        markers.clearLayers();
    } else {
        osmDataUrl = '/rest/map/placemarkers/osm'; //as-json';
        // osmMarkersArrivedFunction = osmPlacemarkerListArrived;

        axios.get(osmDataUrl, {
            params: {
                'minlat': sw.lat,
                'minlon': sw.lng,
                'maxlat': ne.lat,
                'maxlon': ne.lng,
                'zoom': map.getZoom()
            }
        })
        .then((response) => {
            osmMarkersArrivedFunction(response.data);
        }, (error) => {
            console.log("ERROR: ", error);
        });
    }
}

function osmMarkersArrivedFunction(data) {
    markers.clearLayers();

    data.forEach((item) => {
        console.log(item);

        let marker = L.marker(L.latLng(item.lat, item.lon),
            {
                customAttrPlaceKey: item.key,
                title: item.name
                // icon: osmMosqueIcon
            }
        );

        marker.bindPopup(item.name);
        markers.addLayer(marker);
    });
}
