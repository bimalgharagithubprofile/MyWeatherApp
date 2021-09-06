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

Here, You will see 5 days from now and `by default today's weather details` will be shown. You can `select any day` from the list to see that day's weather forecast.

Also, Will can see the selected `City Name` and Current `Time` and which `Day of Week`

### Weather Details Shown
```
Feels Like in Celcius
Humidity
Weather (Rain -> d,n | Cloud -> d,n | Sunny -> d)
Temperature
Wind Speed
```
All the `Background & Icons` will be showing according to the weather `Rain` `Cloudy` `Sunny`

# Technical Info

## UI/UX

App is implemented with a quite simiply UX with minimal Buttons.

Fully flat hierarchy UI designinig

Apps supports all kind if `Mobile` and `Tablet` resolution(s) and for both `Orientation` -> `portrait` `landscape`

All Icons are `vetcor`, No blurry issues

Swipe LEFT | RIGHT operations

Only Light/Day theme supported as of now.

```
CoordinatorLayout used for CollapsingToolbarLayout
ConstraintLayout is been used for evey kind of designing for all the screens
Following modern Material Color config principles
Custom Drawable for Gradient-Shadow and more
Different layout files for portrait, landscape, mobile, tablet
```

## Architecture Pattern

App is having `MVVM` Architecture pattern to keep thing `testable` as this is `loosly-coupled`

## Design Pattern

App is fully reactive using `RxJava -> Observer` Class from `androidx - lifecycle`

## Language

`Kotlin` used with many of its features
```
Coroutines
suspend Funcions
Generic Functions to wrap network response, etc..
Extenstion function
Delegates Functions like lazyDeferred -> LAZY operations wrapper
object file for helper Functions
Data Class
Enum Class
Each and everywhere null check
```

## DI

App having `kodein` dependency injection jitpack

## Jitpack

App using `androidx`
```
databinding
groupie
enhancedAdapter
retrofit
```

## gradle plugins

All plugins are organised in a separate file call `dependencies.gradle`

## Database

Sharedpreferences

## Network Call

`retrofit` with FULL BODY Logger
Network Callback is werapped with generic type fuinction to handle `Success` `Network Error` `Failure` wrapped with ENUM Class returns
