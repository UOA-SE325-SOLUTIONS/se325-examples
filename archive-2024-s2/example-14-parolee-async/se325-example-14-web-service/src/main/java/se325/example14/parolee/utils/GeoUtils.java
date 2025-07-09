package se325.example14.parolee.utils;

import se325.example14.parolee.domain.GeoPosition;
import se325.example14.parolee.dto.GeoPositionDTO;

/**
 * Contains methods which can calculate the distance, in meters, between two points on the globe (given their latitude
 * and longitude).
 */
public class GeoUtils {

    private static final double RADIUS_OF_EARTH = 6371e3;

    public static double calculateDistanceInMeters(GeoPosition point1, GeoPosition point2) {
        return haversine(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
    }

    public static double calculateDistanceInMeters(GeoPositionDTO point1, GeoPositionDTO point2) {
        return haversine(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
    }

    /**
     * Calculates the distance, in meters, between two points on the globe, given their latitude and longitude.
     * <p>
     * Formula from: https://movable-type.co.uk/scripts/latlong.html
     *
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @return the distance, in meters, between the two points
     */
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {

        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δφ = Math.toRadians(lat2 - lat1);
        double Δλ = Math.toRadians(lon2 - lon1);

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS_OF_EARTH * c;
    }


}
