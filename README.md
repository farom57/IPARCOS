<img align="left" width="100" height="100" src="docs/logo.png">

# IPARCOS
IPARCOS (Indi Protocol Android Remote COntrol Software) is an INDI client for Android. It features a generic control panel and others specialized tools to easily control your devices.
The project main site is https://github.com/farom57/IPARCOS/

## Features

+ Generic control panel compatible with every devices (BLOB not yet supported)
+ Control of the telescope motion by means of a directional pad and speed buttons
+ Languages: English, French, Italian

## Screenshots
<img align="left" width="250" src="docs/connection.png">
<img align="center" width="250" src="docs/motion.png">
<img align="right" width="250" src="docs/control-panel.png">

## Download and install
* ~~From Google Play:~~
  * Not yet available
* From the .apk file:
  * Download the latest release from the GitHub project page
  * Allow app installs from unknown sources in Android settings (Settings → Security → Unknown sources)
  * Enjoy!

## Usage
1. Prerequisite
  * Minimum Android version: 4.0.3
  * An INDI server must be running on the remote computer.
  * You must have a network access to this computer. 
    * To achieve this, the Android device and the remote computer can be placed on your home network.
    * Alternatively, you can create a wireless network with your Android device and connect the remote computer to this network (the PC address is likely to be 192.168.43.71)
2. Connection
  * Choose the server address in the list or touch "Add server" to add a new server in the list
  * Optionally, you can change the port number if you do not use the default value for the INDI protocol (7624)
  * Click on "Connect"
3. Use the generic control panel
  * Click on the gear icon in the action bar to display the generic control panel
  * Use the tabs to switch between the devices
  * The properties of the device are displayed in a list. Click on a property to edit it or show the details.
4. Control the telescope motion
  * Click on the icon with four arrows in the action bar to display the telescope motion control panel
  * The buttons will be enabled (red) or disabled (grey) depending on the devices features.
    * The arrows are enabled if one device provide the TELESCOPE_ABORT_MOTION, TELESCOPE_MOTION_NS and/or TELESCOPE_MOTION_WE properties
    * The + and - buttons are enabled if one device provide the standard property to control the speed (TELESCOPE_MOTION_RATE) or the LX200 style property (Slew Rate) or the EQMod style property (SLEWMODE)
    * If the device is not connected, the properties may not appears and the buttons will be disabled

### Developed by
* **Romain Fafet** (farom57)
* **Zerjillo**
  * INDI for Java http://www.indilib.org/develop/indiforjava.html
* **SquareBoot**