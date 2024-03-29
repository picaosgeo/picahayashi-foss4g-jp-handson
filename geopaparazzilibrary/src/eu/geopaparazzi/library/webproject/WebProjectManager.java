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
package eu.geopaparazzi.library.webproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import eu.geopaparazzi.library.network.NetworkUtilities;
import eu.geopaparazzi.library.util.CompressionUtilities;
import eu.geopaparazzi.library.util.ResourcesManager;
import eu.geopaparazzi.library.util.debug.Debug;
import eu.geopaparazzi.library.util.debug.Logger;

/**
 * Singleton to handle cloud up- and download.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public enum WebProjectManager {
    INSTANCE;

    /**
     * The relative path appended to the server url to compose the upload url.
     */
    public static String UPLOADPATH = "upload";

    /**
     * The relative path appended to the server url to compose the download projects list url.
     */
    public static String DOWNLOADPATH = "download";

    /**
     * The id parameter name to use in the server url. 
     */
    public static String ID = "id";

    /**
     * Uploads a project folder as zip to the given server via POST.
     * 
     * @param context the {@link Context} to use.
     * @param addMedia defines if also the images in media should be included.
     * @param server the server to which to upload.
     * @param user the username for authentication.
     * @param passwd the password for authentication.
     * @return the return code.
     */
    public ReturnCodes uploadProject( Context context, boolean addMedia, String server, String user, String passwd ) {
        try {
            ResourcesManager resourcesManager = ResourcesManager.getInstance(context);
            File appFolder = resourcesManager.getApplicationDir();
            String mediaFodlerName = resourcesManager.getMediaDir().getName();

            File zipFile = new File(appFolder.getParentFile(), resourcesManager.getApplicationName() + ".zip");
            if (zipFile.exists()) {
                if (!zipFile.delete()) {
                    throw new IOException();
                }
            }
            if (addMedia) {
                CompressionUtilities.zipFolder(appFolder.getAbsolutePath(), zipFile.getAbsolutePath(), true);
            } else {
                CompressionUtilities.zipFolder(appFolder.getAbsolutePath(), zipFile.getAbsolutePath(), true, mediaFodlerName);
            }

            server = server + "/" + UPLOADPATH;
            String result = NetworkUtilities.sendFilePost(server, zipFile, user, passwd);
            if (Debug.D) {
                Logger.i(this, result);
            }
            result = result.trim();
            if (result.toLowerCase().equals("ok")) {
                return ReturnCodes.OK;
            } else {
                return ReturnCodes.ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnCodes.ERROR;
        }
    }

    /**
     * Downloads a project from the given server via GET.
     * 
     * @param context the {@link Context} to use.
     * @param server the server from which to download.
     * @param user the username for authentication.
     * @param passwd the password for authentication.
     * @param webproject the project to download. 
     * @return the return code.
     */
    public String downloadProject( Context context, String server, String user, String passwd, Webproject webproject ) {
        try {
            ResourcesManager resourcesManager = ResourcesManager.getInstance(context);
            File appFolder = resourcesManager.getApplicationDir();

            File zipFile = new File(appFolder.getParentFile(), resourcesManager.getApplicationName() + ".zip");
            if (zipFile.exists()) {
                if (!zipFile.delete()) {
                    throw new IOException();
                }
            }

            server = server + "/" + DOWNLOADPATH + "/" + webproject.id;
            NetworkUtilities.sendGetRequest4File(server, zipFile, null, user, passwd);

            // now remove the zip file
            CompressionUtilities.unzipFolder(zipFile.getAbsolutePath(), appFolder.getParentFile().getAbsolutePath(), true);
            /*
             * remove the zip file
             */
            if (zipFile.exists()) {
                if (!zipFile.delete()) {
                    throw new IOException();
                }
            }

            return ReturnCodes.OK.getMsgString();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.startsWith(ReturnCodes.FILEEXISTS.getMsgString())) {
                return message;
            }

            e.printStackTrace();
            return ReturnCodes.ERROR.getMsgString();
        }
    }

    /**
     * Downloads the project list from the given server via GET.
     * 
     * @param context the {@link Context} to use.
     * @param server the server from which to download.
     * @param user the username for authentication.
     * @param passwd the password for authentication.
     * @return the project list.
     * @throws Exception 
     */
    public List<Webproject> downloadProjectList( Context context, String server, String user, String passwd ) throws Exception {
        String jsonString = "[]";
        if (server.equals("test")) {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("tags/cloudtest.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while( (line = br.readLine()) != null ) {
                sb.append(line).append("\n");
            }
            jsonString = sb.toString();
        } else {
            server = server + "/" + DOWNLOADPATH;
            jsonString = NetworkUtilities.sendGetRequest(server, null, user, passwd);
        }
        List<Webproject> webprojectsList = json2WebprojectsList(jsonString);
        return webprojectsList;
    }

    /**
     * Transform a json string to a list of webprojects.
     * 
     * @param json the json string.
     * @return the list of {@link Webproject}.
     * @throws JSONException 
     */
    public static List<Webproject> json2WebprojectsList( String json ) throws Exception {
        List<Webproject> wpList = new ArrayList<Webproject>();

        JSONObject jsonObject = new JSONObject(json);

        // JSONArray tagArrayObj = new JSONArray(json);
        // int tagsNum = tagArrayObj.length();
        // if (tagsNum != 2) {
        // throw new IOException("Two tags expected");
        // }
        // JSONObject errorObject = tagArrayObj.getJSONObject(0);
        // JSONObject projectsObject = tagArrayObj.getJSONObject(1);
        // JSONArray projectsArray = projectsObject.getJSONArray("projects");
        JSONArray projectsArray = jsonObject.getJSONArray("projects");
        int projectNum = projectsArray.length();
        for( int i = 0; i < projectNum; i++ ) {
            JSONObject projectObject = projectsArray.getJSONObject(i);
            String id = projectObject.getString("id");
            String title = projectObject.getString("title");
            String date = projectObject.getString("date");
            String author = projectObject.getString("author");
            String name = projectObject.getString("name");
            String size = projectObject.getString("size");

            Webproject wp = new Webproject();
            wp.author = author;
            wp.date = date;
            wp.name = name;
            wp.title = title;
            wp.id = Long.parseLong(id);
            try {
                wp.size = Long.parseLong(size);
            } catch (Exception e) {
                // unused for now
            }
            wpList.add(wp);
        }
        return wpList;
    }

}
