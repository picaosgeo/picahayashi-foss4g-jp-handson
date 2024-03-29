/*
 * Geopaparazzi - Digital field mapping on Android based devices
 * Copyright (C) 2010  HydroloGIS (www.hydrologis.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.geopaparazzi.library.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Some constants used in the lib.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public interface LibraryConstants {

    public final float E6 = 1000000f;
    public final DecimalFormat COORDINATE_FORMATTER = new DecimalFormat("#.00000000"); //$NON-NLS-1$
    public final DecimalFormat DECIMAL_FORMATTER_2 = new DecimalFormat("0.00"); //$NON-NLS-1$
    public final SimpleDateFormat TIMESTAMPFORMATTER = new SimpleDateFormat("yyyyMMdd_HHmmss"); //$NON-NLS-1$
    public final SimpleDateFormat DATEONLY_FORMATTER = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
    public final SimpleDateFormat TIMEONLY_FORMATTER = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
    public final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
    public final SimpleDateFormat TIME_FORMATTER_SQLITE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
    public final SimpleDateFormat TIME_FORMATTER_GPX = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$

    /**
     * Key used by the gps logger to store the lat in the prefs. 
     * 
     * <p><b>Note that this will always be *E6 and of type float, so to
     * get the actual value you will have to divide by E6.</b>
     * 
     * <p>
     * The gps logger uses this key to regularly store the
     * gps data recorded, so this should not be used to store own
     * data, which would be overwritten. 
     * </p>
     */
    public static final String PREFS_KEY_LAT = "PREFS_KEY_LAT"; //$NON-NLS-1$

    /**
     * Key used by the gps logger to store the lon in the prefs.
     *  
     * <p><b>Note that this will always be *E6 and of type float, so to
     * get the actual value you will have to divide by E6.</b>
     * 
     * <p>
     * The gps logger uses this key to regularly store the
     * gps data recorded, so this should not be used to store own
     * data, which would be overwritten. 
     * </p>
     */
    public static final String PREFS_KEY_LON = "PREFS_KEY_LON"; //$NON-NLS-1$

    /**
     * Key used by the gps logger to store the elev in the prefs. 
     * 
     * <p>
     * The gps logger uses this key to regularly store the
     * gps data recorded, so this should not be used to store own
     * data, which would be overwritten. 
     * </p>
     */
    public static final String PREFS_KEY_ELEV = "PREFS_KEY_ELEV"; //$NON-NLS-1$

    /**
     * Key used to store the mapview center latitude.
     * 
     * <p><b>Note that this will always be *E6 and of type float, so to
     * get the actual value you will have to divide by E6.</b>
     * 
     * <p>
     * This is used every time the map center changes,
     * so this should not be used to store own
     * data, which would be overwritten. 
     * </p>
     */
    public static final String PREFS_KEY_MAPCENTER_LAT = "PREFS_KEY_MAPCENTER_LAT"; //$NON-NLS-1$

    /**
     * Key used to store the mapview center longitude.
     * 
     * <p><b>Note that this will always be *E6 and of type float, so to
     * get the actual value you will have to divide by E6.</b>
     * 
     * <p>
     * This is used every time the map center changes,
     * so this should not be used to store own
     * data, which would be overwritten. 
     * </p>
     */
    public static final String PREFS_KEY_MAPCENTER_LON = "PREFS_KEY_MAPCENTER_LON"; //$NON-NLS-1$

    /**
     * Key used to store the mapview zoom level.
     * 
     */
    public static final String PREFS_KEY_MAP_ZOOM = "PREFS_KEY_MAP_ZOOM"; //$NON-NLS-1$

    /**
     * Key used to pass a name or description temporarily through bundles. 
     */
    public static final String NAME = "NAME"; //$NON-NLS-1$

    /**
     * Key used to pass a lat temporarily through bundles. 
     */
    public static final String LATITUDE = "LATITUDE"; //$NON-NLS-1$

    /**
     * Key used to pass a lon temporarily through bundles. 
     */
    public static final String LONGITUDE = "LONGITUDE"; //$NON-NLS-1$

    /**
     * Key used to pass a zoom level temporarily through bundles. 
     */
    public static final String ZOOMLEVEL = "ZOOMLEVEL"; //$NON-NLS-1$

    /**
     * Key used to pass an elevation temporarily through bundles. 
     */
    public static final String ELEVATION = "ELEVATION"; //$NON-NLS-1$

    /**
     * Key used to pass an azimuth value temporarily through bundles. 
     */
    public static final String AZIMUTH = "AZIMUTH"; //$NON-NLS-1$

    /**
     * Key used to pass a route string temporarily through bundles. 
     */
    public static final String ROUTE = "ROUTE"; //$NON-NLS-1$

    /**
     * Name for a general temporary image. 
     */
    public static final String TMPPNGIMAGENAME = "tmp.png"; //$NON-NLS-1$

    /**
     * Key used to store and retrieve the base folder of the application. 
     */
    public static final String PREFS_KEY_BASEFOLDER = "PREFS_KEY_BASEFOLDER"; //$NON-NLS-1$

    /**
     * Key used to store and retrieve the gps logging interval to use. 
     */
    public static final String PREFS_KEY_GPSLOGGINGINTERVAL = "PREFS_KEY_GPS_LOGGING_INTERVAL"; //$NON-NLS-1$

    /**
     * Key used to store and retrieve the gps logging distance to use. 
     */
    public static final String PREFS_KEY_GPSLOGGINGDISTANCE = "PREFS_KEY_GPS_LOGGING_DISTANCE"; //$NON-NLS-1$

    /**
     * Key used to store and retrieve the gps mode to use (apply on android listener or just on application base). 
     */
    public static final String PREFS_KEY_GPSDOATANDROIDLEVEL = "PREFS_KEY_GPSDOATANDROIDLEVEL"; //$NON-NLS-1$

    /**
     * Key used to store for sms catching. 
     */
    public static final String PREFS_KEY_SMSCATCHER = "PREFS_KEY_SMSCATCHER"; //$NON-NLS-1$

    /**
     * Key used to define a path that is passed through any workflow. 
     */
    public static final String PREFS_KEY_PATH = "PREFS_KEY_PATH"; //$NON-NLS-1$

    /**
     * Key used to define a note that is passed through any workflow. 
     */
    public static final String PREFS_KEY_NOTE = "PREFS_KEY_NOTE"; //$NON-NLS-1$

    /**
     * Key used to define a path into which to save a new camera generated image. 
     */
    public static final String PREFS_KEY_CAMERA_IMAGESAVEFOLDER = "PREFS_KEY_CAMERA_IMAGESAVEFOLDER"; //$NON-NLS-1$

    /**
     * Key used to define a name for new camera generated image. 
     */
    public static final String PREFS_KEY_CAMERA_IMAGENAME = "PREFS_KEY_CAMERA_IMAGENAME"; //$NON-NLS-1$

    /**
     * Key used to define if the network should be used instead of the GPS. 
     */
    public static final String PREFS_KEY_GPS_USE_NETWORK_POSITION = "PREFS_KEY_GPS_USE_NETWORK_POSITION"; //$NON-NLS-1$

    /**
     * Key used to define form data that are passed through any workflow. 
     */
    public static final String PREFS_KEY_FORM = "PREFS_KEY_FORM"; //$NON-NLS-1$

    /**
     * Key used to define a json form that is passed through any workflow. 
     */
    public static final String PREFS_KEY_FORM_JSON = "PREFS_KEY_FORM_JSON"; //$NON-NLS-1$

    /**
     * Key used to define a form name that is passed through any workflow. 
     */
    public static final String PREFS_KEY_FORM_NAME = "PREFS_KEY_FORM_NAME"; //$NON-NLS-1$

    /**
     * Key used to define a form category that is passed through any workflow. 
     */
    public static final String PREFS_KEY_FORM_CAT = "PREFS_KEY_FORM_CAT"; //$NON-NLS-1$

    /**
     * Key used to define a user name that is passed through any workflow. 
     */
    public static final String PREFS_KEY_USER = "PREFS_KEY_USER"; //$NON-NLS-1$

    /**
     * Key used to define a password that is passed through any workflow. 
     */
    public static final String PREFS_KEY_PWD = "PREFS_KEY_PWD"; //$NON-NLS-1$

    /**
     * Key used to define a url that is passed through any workflow. 
     */
    public static final String PREFS_KEY_URL = "PREFS_KEY_URL"; //$NON-NLS-1$

    /**
     * Default gps logging interval.
     */
    public static final int GPS_LOGGING_INTERVAL = 3;

    /**
     * Default gps logging distance.
     */
    public static final float GPS_LOGGING_DISTANCE = 1f;

}
