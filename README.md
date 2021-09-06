# MyWeatherApp
Pick a location from Map and find out the weather 

# How to use the app

## View or ADD City

This App have Home Screen where you can find list of some default Cities and a option to add your own.
So, clicking on `Add City FloatingActionButton` it will ask for Permission and also it may ask to `turn on gps` then it will open a Fragment with Google Map, initially map will show your current location.
Now, `Click anywhere on Map`, it will take that place's Lat & Lon. Save it by clicking `SELECT button` on bottom.
This newly selected city will be showing in `Home Screen -> City Lits`.

## DELETE City

You can completly remove a city by `SWIPE LEFT` from either Home Screen or Bookmarks Screen

## ADD or REMOVE Bookmarks

You can Bookmark these cities by `SWIPE RIGHT`, And later can be removed by doing same `SWIPE RIGHT` in Bookmarks Screen.

## Help

Click on `Help button` it will open a website where you can find how to use this App. It uses `WebView` to load that website.

## View Weather

App will get `5 days of forecast` of the selected City by using it's Lat, Lon values. `Tap` on City from the list of cities and it will open a `Fragment` with 5 days forecast.

Here,
