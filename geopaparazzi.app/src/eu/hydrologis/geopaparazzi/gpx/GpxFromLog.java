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
package eu.hydrologis.geopaparazzi.gpx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import eu.hydrologis.geopaparazzi.util.ApplicationManager;
import eu.hydrologis.geopaparazzi.util.Constants;
import eu.hydrologis.geopaparazzi.util.Line;
import eu.hydrologis.geopaparazzi.util.Note;

/**
 * Log to Gpx converter.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
@SuppressWarnings("nls")
public class GpxFromLog {

    public void export( List<Note> notes, HashMap<Long, Line> linesMap ) throws IOException {
        // lines
        File gpxLinesDir = ApplicationManager.getInstance().getGeoPaparazziDir();
        String date = Constants.TIME_FORMATTER.format(new Date());
        Collection<Line> lines = linesMap.values();
        for( Line line : lines ) {
            String fileName = line.getfileName();
            fileName = fileName + ".gpx";
            File newFile = new File(gpxLinesDir, fileName);

            if (newFile.exists()) {
                // write only new ones
                continue;
            }

            StringBuilder sB = new StringBuilder();
            addGpxTrkPre(sB);

            List<Double> latList = line.getLatList();
            List<Double> lonList = line.getLonList();
            List<Double> altimList = line.getAltimList();
            List<String> dateList = line.getDateList();
            double minLat = Double.POSITIVE_INFINITY;
            double maxLat = Double.NEGATIVE_INFINITY;
            double minLon = Double.POSITIVE_INFINITY;
            double maxLon = Double.NEGATIVE_INFINITY;
            for( int i = 0; i < latList.size(); i++ ) {
                double lat = latList.get(i);
                double lon = lonList.get(i);
                if (lat < minLat) {
                    minLat = lat;
                }
                if (lat > maxLat) {
                    maxLat = lat;
                }
                if (lon < minLon) {
                    minLon = lon;
                }
                if (lon > maxLon) {
                    maxLon = lon;
                }

                double altim = altimList.get(i);
                String pointDate = dateList.get(i);

                sB.append(" <trkseg>\n");
                sB.append("     <trkpt lat=\"" + lat + "\" lon=\"" + lon + "\">\n");
                sB.append("         <ele>" + altim + "</ele>\n");
                sB.append("         <time>" + pointDate + "</time>\n");
                sB.append("     </trkpt>\n");
                sB.append(" </trkseg>\n");
            }

            addGpxTrkPost(sB);

            String gpxBody = sB.toString();
            gpxBody = gpxBody.replaceFirst("MINLAT", String.valueOf(minLat));
            gpxBody = gpxBody.replaceFirst("MAXLAT", String.valueOf(maxLat));
            gpxBody = gpxBody.replaceFirst("MINLON", String.valueOf(minLon));
            gpxBody = gpxBody.replaceFirst("MAXLON", String.valueOf(maxLon));
            gpxBody = gpxBody.replaceFirst("TIME", date);

            BufferedWriter bW = null;
            try {
                bW = new BufferedWriter(new FileWriter(newFile));
                bW.write(gpxBody);
            } finally {
                bW.close();
            }

            // add to the gpx items so that they appear
            String absolutePath = newFile.getAbsolutePath();
            Log.d("GpxActivity", absolutePath);
            String name = newFile.getName();
            GpxItem newGpxItem = new GpxItem();
            newGpxItem.setFilename(name);
            newGpxItem.setFilepath(absolutePath);
            newGpxItem.setLine(true);
            newGpxItem.setWidth("2");
            newGpxItem.setVisible(false);
            newGpxItem.setColor("blue");
        }

        // points
        if (notes.size() < 1) {
            return;
        }
        File gpxPointsDir = ApplicationManager.getInstance().getGeoPaparazziDir();
        String fileName = "notes_" + Constants.TIMESTAMPFORMATTER.format(new Date()) + ".gpx";
        File newFile = new File(gpxPointsDir, fileName);

        StringBuilder sB = new StringBuilder();
        addGpxWptPre(sB);
        double minLat = Double.POSITIVE_INFINITY;
        double maxLat = Double.NEGATIVE_INFINITY;
        double minLon = Double.POSITIVE_INFINITY;
        double maxLon = Double.NEGATIVE_INFINITY;
        for( Note note : notes ) {
            double lat = note.getLat();
            double lon = note.getLon();
            if (lat < minLat) {
                minLat = lat;
            }
            if (lat > maxLat) {
                maxLat = lat;
            }
            if (lon < minLon) {
                minLon = lon;
            }
            if (lon > maxLon) {
                maxLon = lon;
            }
            sB.append("<wpt lat=\"" + lat + "\" lon=\"" + lon + "\">\n");
            sB.append(" <ele>0</ele>\n");
            sB.append(" <time>" + note.getDescription() + "</time>\n");
            sB.append(" <name>" + note.getName() + "</name>\n");
            sB.append(" <sym>Waypoint</sym>\n");
            sB.append("</wpt>\n");
        }
        addGpxWptPost(sB);
        String gpxBody = sB.toString();
        gpxBody = gpxBody.replaceFirst("MINLAT", String.valueOf(minLat));
        gpxBody = gpxBody.replaceFirst("MAXLAT", String.valueOf(maxLat));
        gpxBody = gpxBody.replaceFirst("MINLON", String.valueOf(minLon));
        gpxBody = gpxBody.replaceFirst("MAXLON", String.valueOf(maxLon));
        gpxBody = gpxBody.replaceFirst("TIME", date);

        BufferedWriter bW = null;
        try {
            bW = new BufferedWriter(new FileWriter(newFile));
            bW.write(gpxBody);
        } finally {
            if (bW != null)
                bW.close();
        }
        // add to the gpx items so that they appear
        String absolutePath = newFile.getAbsolutePath();
        Log.d("GpxActivity", absolutePath);
        String name = newFile.getName();
        GpxItem newGpxItem = new GpxItem();
        newGpxItem.setFilename(name);
        newGpxItem.setFilepath(absolutePath);
        newGpxItem.setLine(false);
        newGpxItem.setWidth("3");
        newGpxItem.setVisible(false);
        newGpxItem.setColor("blue");
    }

    private void addGpxTrkPre( StringBuilder sB ) {
        sB.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sB.append("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" version=\"1.1\" creator=\"Geopaparazzi\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.topografix.com/GPX/gpx_overlay/0/3 http://www.topografix.com/GPX/gpx_overlay/0/3/gpx_overlay.xsd http://www.topografix.com/GPX/gpx_modified/0/1 http://www.topografix.com/GPX/gpx_modified/0/1/gpx_modified.xsd\">\n");
        sB.append("<metadata>\n");
        sB.append(" <bounds minlat=\"MINLAT\" minlon=\"MINLON\" maxlat=\"MAXLAT\" maxlon=\"MAXLON\"/>\n");
        sB.append(" <extensions>\n");
        // 2008-10-28T21:33:12.596Z
        sB.append("     <time xmlns=\"http://www.topografix.com/GPX/gpx_modified/0/1\">TIME</time>\n");
        sB.append(" </extensions>\n");
        sB.append("</metadata>\n");
        sB.append("<trk>\n");
        sB.append(" <name>log TIME</name>\n");
        sB.append(" <type>Geopaparazzi gpslog</type>\n");
        // sB.append(" <extensions>\n");
        // sB.append("     <label xmlns=\"http://www.topografix.com/GPX/gpx_overlay/0/3\">\n");
        // sB.append("         <label_text>28-OCT-08</label_text>\n");
        // sB.append("     </label>\n");
        // sB.append(" </extensions>\n");
    }

    private void addGpxTrkPost( StringBuilder sB ) {
        sB.append("</trk>\n");
        // sB.append("<extensions>\n");
        // sB.append("</extensions>\n");
        sB.append("</gpx>\n");
        sB.append("\n");
    }
    private void addGpxWptPre( StringBuilder sB ) {
        sB.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sB.append("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" version=\"1.1\" creator=\"Geopaparazzi\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.topografix.com/GPX/gpx_overlay/0/3 http://www.topografix.com/GPX/gpx_overlay/0/3/gpx_overlay.xsd http://www.topografix.com/GPX/gpx_modified/0/1 http://www.topografix.com/GPX/gpx_modified/0/1/gpx_modified.xsd\">\n");
        sB.append("<metadata>\n");
        sB.append(" <bounds minlat=\"MINLAT\" minlon=\"MINLON\" maxlat=\"MAXLAT\" maxlon=\"MAXLON\"/>\n");
        sB.append(" <extensions>\n");
        // 2008-10-28T21:33:12.596Z
        sB.append("     <time xmlns=\"http://www.topografix.com/GPX/gpx_modified/0/1\">TIME</time>\n");
        sB.append(" </extensions>\n");
        sB.append("</metadata>\n");
        // sB.append(" <extensions>\n");
        // sB.append("     <label xmlns=\"http://www.topografix.com/GPX/gpx_overlay/0/3\">\n");
        // sB.append("         <label_text>28-OCT-08</label_text>\n");
        // sB.append("     </label>\n");
        // sB.append(" </extensions>\n");
    }

    private void addGpxWptPost( StringBuilder sB ) {
        // sB.append("<extensions>\n");
        // sB.append("</extensions>\n");
        sB.append("</gpx>\n");
        sB.append("\n");
    }
}
