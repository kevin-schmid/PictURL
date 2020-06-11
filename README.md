# PictURL
*PictURL* is a modern, fast and simple picture sharing app for Imgur.

## Build hints
Declare your Client-ID in the ```apikey.properties``` file (for naming, look at
```apikey.properties.template```). To build the app in simulation mode (no communication with Imgur)
edit the ```buildConfigField``` **IS_PRODUCTION** in ```build.gradle```.