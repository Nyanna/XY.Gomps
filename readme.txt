Phase 1 target features:

-navigate to an fast rendering view via db or tiles or xml, zooming, mouse navigation, scrollwheel zooming (event manager)
-support point, way and area data
-native setting menu for configuration
-gps sirf nmea and tsr support
-on the fly style support and colloring

Phase 2 target features:
-efficient routing suport
	-with track highlighting
	-on demand recalculation
	-track snapping
	-routing modes

TODO:
-split data types and rendering, sorting
	1. render areas from largest to smallest ones
	2. streets from highest to lowest ones
	3. points from most importent to lowest
-build menu
-build config manager (config, resources)
-implement styles
-implement codebase logging
-add gps support
-add logging exception (replace old in hsql)