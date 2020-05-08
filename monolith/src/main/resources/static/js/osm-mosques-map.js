//
// we don't have jquery around just yet
//
// $(document).ready(init);
//
init();

function init() {

    <!-- Map providers -->
    const osmMapnikMapProvider = L.tileLayer.provider('OpenStreetMap.Mapnik');

    <!-- Now the map itself -->
    map = L.map('map', {
        center: [48.135, 11.389],
        zoom: 15,
        zoomControl: true,
        layers: [osmMapnikMapProvider]
    });

    const baseLayers = {
        "OSM Mapnik": osmMapnikMapProvider,
        "Thunderforest": L.tileLayer.provider('Thunderforest.Landscape'),
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
// const borkenMapBoxMapProvider = L.tileLayer.provider('MapBox', {
//     id: 'mapbox/streets-v11',
//     accessToken: 'pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw'
// });
// }

    L.circle([48.135, 11.389], 500, {
        color: 'red',
        fillColor: '#f03',
        fillOpacity: 0.1
    }).addTo(map).bindPopup("I am a circle.");

    <!-- Now add the layer switcher to the map -->
    var layers = new L.Control.Layers(baseLayers);
    map.addControl(layers);

    <!-- Location control -->
    L.control.locate().addTo(map);

    var popup = L.popup();

    function onMapClick(e) {
        popup
            .setLatLng(e.latlng)
            .setContent("You clicked the map at " + e.latlng.toString())
            .openOn(map);
    }

    map.on('click', onMapClick);
}
