<!-- Markers from here on -->
function onClickOsmMarker(e) {
    // console.log("onClickOsmMarker");
    popup = this._popup;
    // console.log(popup);

    // Update the contents of the popup
    $(popup._contentNode).html('Asking backend for details...');

    // Calling _updateLayout to the popup resizes to the new content
    popup._updateLayout();

    // Calling _updatePosition so the popup is centered.
    popup._updatePosition();

    var request = ajaxQueryCache['osmPlaceDetails'];
    if (request != null) {
        request.abort();
        console.log("Cancelled Ajax Query");
    }

    var marker = popup._source;
    // console.log(marker);

    var placeKey = marker.options.customAttrPlaceKey;
    // console.log(placeKey);

    // osmPopupDetailsUrl will be set externally
    var request = $.get({
        url: osmPopupDetailsUrl,
        data: {
            'placeKey': placeKey
        },
        success: osmPlacemarkerDetailsArrived
    });

    ajaxQueryCache['osmPlaceDetails'] = request;
}

<!-- OSM Place Details callback -->
function osmPlacemarkerDetailsArrived(data) {
    // console.log("osmPlacemarkerDetailsArrived");
    // console.log(this);
    ajaxQueryCache['osmPlaceDetails'] = null;

    // $(popup._contentNode).html('The new content is much longer so the popup should update how it looks.');
    $(popup._contentNode).html(data);

    // Calling _updateLayout to the popup resizes to the new content
    popup._updateLayout();

    // Calling _updatePosition so the popup is centered.
    popup._updatePosition();
}

<!-- User preferences callback -->
function onClickUserPreferences(e) {

    var request = ajaxQueryCache['userPreferences'];
    if (request != null) {
        request.abort();
        console.log("Cancelled Ajax Query");
    }

    // osmPopupDetailsUrl will be set externally
    var request = $.get({
        url: "/user/preferences",
        data: {
            'foo': 'bar'
        },
        success: userPreferencesArrived
    });

    ajaxQueryCache['userPreferences'] = request;
}
function userPreferencesArrived(data) {
    // console.log("userPreferencesArrived");
    // console.log(e);
    // console.log(userPreferencesPopup);

    userPreferencesPopup.setContent("This here is the place where your user preferences will be shown");

    // Calling _updateLayout to the popup resizes to the new content
    // userPreferencesPopup._updateLayout();

    // Center the popup on the currently visible part of the map
    userPreferencesPopup.setLatLng(map.getCenter());

    // Calling _updatePosition so the popup is centered.
    userPreferencesPopup._updatePosition();

    map.openPopup(userPreferencesPopup);
}