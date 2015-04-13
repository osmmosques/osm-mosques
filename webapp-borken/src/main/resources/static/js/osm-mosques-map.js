<!-- Markers from here on -->
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

var ditibPlaces = L.markerClusterGroup({ chunkedLoading: true });

var osmPlaces = L.markerClusterGroup({ chunkedLoading: true });

for (var i = 0; i < ditibAddressPoints.length; i++) {
    var a = ditibAddressPoints[i];
    var title = a[2];
    var marker = L.marker(L.latLng(a[0], a[1]), { title: title, icon: ditibMosqueIcon });
    marker.bindPopup(title);
    ditibPlaces.addLayer(marker);
}

for (var i = 0; i < osmAddressPoints.length; i++) {
    var a = osmAddressPoints[i];
    var title = a[2];
    var marker = L.marker(L.latLng(a[0], a[1]), { title: title, icon: osmMosqueIcon });
    marker.bindPopup(title);
    osmPlaces.addLayer(marker);
}

var overlays =
{
    "OSM Places": osmPlaces,
    "DITIB Places": ditibPlaces
};

<!-- Map providers -->
var osmLink = '<a href="http://openstreetmap.org">OpenStreetMap</a>';
var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
var osmAttrib = '&copy; ' + osmLink + ' Contributors';
var osmMap = L.tileLayer(osmUrl, {attribution: osmAttrib});

var thunLink = '<a href="http://thunderforest.com/">Thunderforest</a>';
var thunUrl = 'http://{s}.tile.thunderforest.com/landscape/{z}/{x}/{y}.png';
var thunAttrib = '&copy; ' + osmLink + ' Contributors &and;' + thunLink;
var thunMap = L.tileLayer(thunUrl, {attribution: thunAttrib});

mapLink = '<a href="http://openstreetmap.org">OpenStreetMap</a>';

var googleSatMap = new L.Google('SATELLITE');
var googleTerrainMap = new L.Google('TERRAIN');
var googleHybridMap = new L.Google('HYBRID');

<!-- Now the map itself -->
var map = L.map('map', {
    center: [48.12955, 11.34873],
    zoom: 11,
    layers: [osmMap, osmPlaces, ditibPlaces]
});

var baseLayers =
{
    "OSM Mapnik": osmMap,
    "Thunderforest": thunMap,
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
