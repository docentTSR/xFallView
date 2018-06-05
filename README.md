xFallView
---------

[![Download](https://api.bintray.com/packages/docenttsr/views/xFallView/images/download.svg)](https://bintray.com/docenttsr/views/xFallView/_latestVersion)
[![Api](https://img.shields.io/badge/API-19+-blue.svg)](https://github.com/docentTSR/xFallView)
[![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Apk](https://img.shields.io/badge/APK-Download-green.svg)](https://github.com/docentTSR/xFallView)

Setup
-----
```groovy
repositories {
    jcenter()
}
   
dependencies {
    implementation 'com.github.docentTSR:xFallView:0.9.0'
}
```

Usage
-----
`layout.xml`
```xml
<com.docentTSR.xFallView.views.XFallView
    android:id="@+id/x_fall_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:viewsCount="80"
    app:minSpeed="60"
    app:maxSpeed="95"
    app:minAlpha="10"
    app:maxAlpha="255"
    app:minScale="30"
    app:maxScale="60"
    app:wind="slight"
    app:rotate="on"
    app:srcArray="@array/drawable_array"
    />
```
`arrays.xml`
```xml
<array name="drawable_array">
    <item>@drawable/drawable_1</item>
    <item>@drawable/drawable_2</item>
    <item>@drawable/drawable_...</item>
    <item>@drawable/drawable_n</item>
</array>
```

### Attributes description
Attrubute | Value | Default | Description
--- | --- | --- | ---
app:viewsCount | n > 0 | 50 | views count for x-fallview
app:minSpeed | n > 0 | 50 | minimum speed for each view
app:maxSpeed | n ≥ `minSpeed` | `minSpeed` * 3 | maximum speed for each view
app:minAlpha | n > 0 | 10 | minimum transparency value for each view
app:maxAlpha | `minAlpha` ≤ n ≤ 255 | 255 | maximum transparency value for each view
app:minScale | n > 0 | 50 | minimum scale value for each view
app:maxScale | n (100 = 1:1) | 100 | maximum scale value for each view
app:wind | `slight`, `normal`, `strong` | disabled | wind option for all views
app:rotate | `on`, `off` | `off` | rotate option for all views

Optimization tips
-----------------
* use optimized views count for x-fallview;
* use optimized drawable resources. For example, use [ImageOptim](https://imageoptim.com/mac);
* use drawables for each screen density `mdpi`, `hdpi`, `xhdpi`, `xxhdpi`. 

Tasks list
----------
- [x] :eyes: random alpha for xViews;
- [x] :rocket: random speed for xViews;
- [x] :mag: random scale for xViews;
- [x] :dash: wind for xViews;
- [x] :cyclone: rotate for xViews;
- [x] :triangular_ruler: random start rotate angle for xViews;
- [x] :sparkles: replace single drawable to drawable list for xViews;
- [ ] :bicyclist: random rotate speed for xViews;
- [ ] :arrows_counterclockwise: random rotate direction for xViews.

License
-------

	Copyright 2018 Anton Savenok

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
