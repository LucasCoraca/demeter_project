# Demeter Project

The objective of this project was to develop software that allows an ArduPilotMega drone to survey an plantation and return with an NDVI map autonomously.

# Warning

All these softwares are a proof of concept. Barely tested and is not recommended to use as is on a real world application.

## Oracle Mapping

The image Processing part of the system. It scans a folder for jpg images of the survey, stitch them together to create a large map and applies an NDVI filter on the final map.

### Dependencies

OpenCV 2.4 or 3

### Compiling

For OpenCV 2.4:
```
gcc oracle_mapping.cpp -o oracle_mapping
```

For OpenCV 3:
```
gcc oracle_mapping31.cpp -o oracle_mapping
```

### Running

```
./oracle_mapping
```

## Oracle App (ALPHA)

The Android app that is used to set the area to be surveyed. Simply connect to the Raspberry Pi on the drone running the Oracle Server and set the area.

## Oracle Server (ALPHA)

Handles the communication between the ArduPilotMega micro-controller and the Android app.

### How to use

Setup a Raspberry with a wifi hotspot, connect the ArduPilotMega drone on it and run the server.

```
cd Oracle_server
python server.py
```
