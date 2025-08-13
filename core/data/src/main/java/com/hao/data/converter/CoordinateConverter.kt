package com.hao.data.converter

import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateReferenceSystem
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate

object CoordinateConverter {

    private val crsFactory = CRSFactory()
    private val ctFactory = CoordinateTransformFactory()

    // NZTM2000 Projection
    private const val NZTM_PROJ4_PARAMS =
        "+proj=tmerc +lat_0=0.0 +lon_0=173.0 +k=0.9996 +x_0=1600000.0 +y_0=10000000.0 +datum=WGS84 +units=m"
    private val nztmCrs: CoordinateReferenceSystem =
        crsFactory.createFromParameters("NZTM", NZTM_PROJ4_PARAMS)

    // WGS84 (Latitude/Longitude)
    private val wgs84Crs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")

    private val nztmToWgs84Transform = ctFactory.createTransform(nztmCrs, wgs84Crs)

    /**
     * Converts a coordinate from New Zealand Transverse Mercator (NZTM) to WGS84 (Latitude/Longitude).
     *
     * @param easting The easting value of the NZTM coordinate.
     * @param northing The northing value of the NZTM coordinate.
     * @return A ProjCoordinate object containing the converted longitude (x) and latitude (y).
     */
    fun nztmToWgs84(easting: Double, northing: Double): ProjCoordinate {
        val nztmCoord = ProjCoordinate(easting, northing)
        val wgs84Coord = ProjCoordinate()
        return nztmToWgs84Transform.transform(nztmCoord, wgs84Coord)
    }
}
