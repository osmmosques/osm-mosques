/*
 * All of our cool Javascript stuff here
 */

function attemptGeocode(ditibKey)
{
    // http://localhost:8888/rest/ditib/geocode/84030-20-5
    var gcUrl = "../rest/ditib/geocode/" + ditibKey;
    console.log(gcUrl);
    $.getJSON(gcUrl, function (data)
    {
        console.log(data);
        if (data["geocoded"] == true)
        {
            alert("YAY!");
        }
        else
        {
            alert("booooooh");
        }
        // JSON.stringify(data)
    });
}
