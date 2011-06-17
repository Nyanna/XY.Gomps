Phase 1 target features:

-navigate to an fast rendering view via db or tiles or xml, zooming, mouse navigation, scrollwheel zooming (event manager)
-support point, way and area data
-native setting menu for configuration
-gps sirf nmea and tsr support

Phase 2 target features:
-efficient routing suport
	-with track highlighting
	-on demand recalculation
	-track snapping
	-routing modes
	-osm online api support
	-direct xml support

TODO:
-add gps support
GPS:
-JSR 82: BlueCove for win bluetooth gps, JSR 82: JavaTM APIs for Bluetooth
-also javax.microedition.location JSR179 for handset intern gps

-Java comm as alternative
-samsapi, sieapi and mokia api for pnones function

REACHED:
-on the fly style support and colloring
-memory footprint of less than 8m (absolute min 4m), basic OOM protection, streamlined scaling design

THOUGTS:
Collision iface for layers, submits in range collisions with obj id
on click pop with all targets