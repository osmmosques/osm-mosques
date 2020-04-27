<!DOCTYPE html>
<html lang="en">
<head>
    <title>Spring Boot FreeMarker example</title>

    <link href="/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container">
    FOOBAR!!!!!!
</div>

<pre>
{
  "_links" : {
    "place" : {
      "href" : "/place{?page,size,sort}",
      "templated" : true
    },
    "tag" : {
      "href" : "/tag{?page,size,sort}",
      "templated" : true
    },
    "mosqueplace" : {
      "href" : "/mosqueplace{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "/profile"
    }
  }
}

</pre>

</body>
</html>

