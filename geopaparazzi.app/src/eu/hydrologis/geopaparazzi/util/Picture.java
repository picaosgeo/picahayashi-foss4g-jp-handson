/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.hydrologis.geopaparazzi.util;

import java.io.File;

/**
 * Represents a picture.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class Picture {

    private final double lon;
    private final double lat;
    private final String picturePath;

    public Picture( double lon, double lat, String picturePath ) {
        this.lon = lon;
        this.lat = lat;
        this.picturePath = picturePath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    @SuppressWarnings("nls")
    public String toKmlString() {
        File img = new File(picturePath);
        String name = img.getName();

        StringBuilder sB = new StringBuilder();
        sB.append("<Placemark>\n");
        sB.append("<name>").append(name).append("</name>\n");
        sB.append("<description><![CDATA[<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        sB.append("<html><head><title></title>");
        sB.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
        sB.append("</head><body>");
        sB.append("<img src=\"" + name + "\" width=\"300\">");
        sB.append("</body></html>]]></description>\n");
        sB.append("<styleUrl>#yellow-pushpin</styleUrl>\n");
        sB.append("<Point>\n");
        sB.append("<coordinates>").append(lon).append(",").append(lat).append(",0</coordinates>\n");
        sB.append("</Point>\n");
        sB.append("</Placemark>\n");

        return sB.toString();
    }
}
