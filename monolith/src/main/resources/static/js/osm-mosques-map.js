//
let map;

$(document).ready(init);

function init() {

    <!-- Map providers -->
    const defaultLayer = L.tileLayer.provider('Esri.WorldStreetMap');

    <!-- Now the map itself -->
    map = L.map('map', {
        center: [48.135, 11.389],
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
    }).addTo(map).bindPopup("I am a circle.");

    <!-- Now add the layer switcher to the map -->
    var layers = new L.Control.Layers(baseLayers);
    map.addControl(layers);

    map.addControl(new L.Control.Permalink({text: 'Permalink', layers: layers}));

    <!-- Location control -->
    L.control.locate().addTo(map);

    <!-- Markers... -->
    const markers = L.markerClusterGroup();
    // markers.addLayer(L.marker(getRandomLatLng(map)));

    // var node = data[i];
    // var key = node['key'];
    // var title = "OSM / " + key + ' / ' + node['name'];
    // var lat = node['lat'];
    // var lon = node['lon'];
    // var marker = L.marker(L.latLng(lat, lon), {
    // customAttrPlaceKey: key,
    // title: title,
    // icon: osmMosqueIcon
    // }
    // );

    for (let x = -2 ; x <= 2 ; x = x + 1)
    {
        for (let y = -2 ; y <= 2 ; y = y + 1)
        {
            const key = x + " - " + y;
            const title = x + " - " + y;

            const marker = L.marker(L.latLng( 48.135 + ( y / 200), 11.389 + ( x / 100)),
                {
                    customAttrPlaceKey: key,
                    title: title
                }
            );

            marker.bindPopup(title);

            markers.addLayer(marker);
        }
    }

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
    console.log("onMapMoveEnd:");
    console.log("    Center: " + map.getCenter());
    console.log("    Zoom: " + map.getZoom());
    console.log("    minll:  " + map.getBounds().getSouthWest());
    console.log("    maxll:  " + map.getBounds().getNorthEast());
}
