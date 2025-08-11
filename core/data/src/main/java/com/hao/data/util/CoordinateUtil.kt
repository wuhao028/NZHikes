package com.hao.data.util

import org.locationtech.proj4j.*

object CoordinateUtil {

    private val crsFactory = CRSFactory()
    private val nztmProjection: CoordinateReferenceSystem
    private val wgs84Projection: CoordinateReferenceSystem

    init {
        // NZTM2000
        nztmProjection = crsFactory.createFromParameters(
            "EPSG:2193",
            "+proj=tmerc +lat_0=0 +lon_0=173 +k=0.9996 +x_0=1600000 +y_0=10000000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
        )

        // WGS84
        wgs84Projection = crsFactory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs")
    }

    fun nztmToWgs84(easting: Double, northing: Double): ProjCoordinate {
        val transform = CoordinateTransformFactory().createTransform(nztmProjection, wgs84Projection)
        val result = ProjCoordinate()
        return transform.transform(ProjCoordinate(easting, northing), result)
    }
}
