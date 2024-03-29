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
package eu.geopaparazzi.library.forms;

import static eu.geopaparazzi.library.forms.FormUtilities.TAG_KEY;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_VALUE;

import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import eu.geopaparazzi.library.R;
import eu.geopaparazzi.library.forms.constraints.Constraints;
import eu.geopaparazzi.library.util.LibraryConstants;
import eu.geopaparazzi.library.util.Utilities;

/**
 * The form activity.
 * 
 * <p>This returns an array of {@link String} data that can be retrieved
 * through: {@link LibraryConstants#PREFS_KEY_FORM} and contain:</p>
 * <ul>
 *   <li>longitude</li>
 *   <li>latitude</li>
 *   <li>elevation (or -1.0)</li>
 *   <li>timestamp</li>
 *   <li>a name for the form</li>
 *   <li>the filled form data json</li>
 * </ul>
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class FormActivity extends FragmentActivity {

    private static final int MENU_SAVE = Menu.FIRST;
    private static final int MENU_CANCEL = 2;

    private double latitude = -9999.0;
    private double longitude = -9999.0;
    private double elevation = -9999.0;
    private String sectionName;
    private JSONObject sectionObject;
    private List<String> formNames4Section = new ArrayList<String>();
    private String sectionObjectString;

    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        // make sure the orientation can't be changed once this activity started
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sectionName = extras.getString(LibraryConstants.PREFS_KEY_FORM_NAME);
            sectionObjectString = extras.getString(LibraryConstants.PREFS_KEY_FORM_JSON);
            latitude = extras.getDouble(LibraryConstants.LATITUDE);
            longitude = extras.getDouble(LibraryConstants.LONGITUDE);
            elevation = extras.getDouble(LibraryConstants.ELEVATION);
        }

        try {
            if (sectionObjectString == null) {
                sectionObject = TagsManager.getInstance(this).getSectionByName(sectionName);
                // copy the section object, which will be kept around along the activity
                sectionObjectString = sectionObject.toString();
            }

            sectionObject = new JSONObject(sectionObjectString);
            formNames4Section = TagsManager.getFormNames4Section(sectionObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.form);
    }

    public boolean onCreateOptionsMenu( Menu menu ) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_SAVE, 1, "Save").setIcon(android.R.drawable.ic_menu_save);
        menu.add(Menu.NONE, MENU_CANCEL, 2, "Cancel").setIcon(android.R.drawable.ic_menu_close_clear_cancel);

        return true;
    }

    public boolean onMenuItemSelected( int featureId, MenuItem item ) {
        switch( item.getItemId() ) {
        case MENU_SAVE:
            try {
                saveAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        case MENU_CANCEL:
            finish();
            return true;
        default: {
        }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public List<String> getFragmentTitles() {
        return formNames4Section;
    }

    public String getSectionName() {
        return sectionName;
    }

    public JSONObject getSectionObject() {
        return sectionObject;
    }

    public void setSectionObject( JSONObject sectionObject ) {
        this.sectionObject = sectionObject;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void saveAction() throws Exception {
        // if i landscape mode store last inserted info, since that fragment has not been stored
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentDetail detailFragment = (FragmentDetail) getSupportFragmentManager().findFragmentById(R.id.detailFragment);
            if (detailFragment != null) {
                detailFragment.storeFormItems(false);
            }
        }

        // extract and check constraints
        List<String> availableFormNames = TagsManager.getFormNames4Section(sectionObject);
        for( String formName : availableFormNames ) {
            JSONObject formObject = TagsManager.getForm4Name(formName, sectionObject);

            JSONArray formItemsArray = TagsManager.getFormItems(formObject);

            int length = formItemsArray.length();
            String value = null;
            for( int i = 0; i < length; i++ ) {
                JSONObject jsonObject = formItemsArray.getJSONObject(i);

                String key = "-";
                if (jsonObject.has(TAG_KEY))
                    key = jsonObject.getString(TAG_KEY).trim();

                if (jsonObject.has(TAG_VALUE)) {
                    value = jsonObject.getString(TAG_VALUE).trim();
                }

                // inject latitude
                if (key.equals(LibraryConstants.LATITUDE)) {
                    String latitudeString = String.valueOf(latitude);
                    value = latitudeString;
                    jsonObject.put(TAG_VALUE, latitudeString);
                }
                // inject longitude
                if (key.equals(LibraryConstants.LONGITUDE)) {
                    String longitudeString = String.valueOf(longitude);
                    value = longitudeString;
                    jsonObject.put(TAG_VALUE, longitudeString);
                }

                Constraints constraints = FormUtilities.handleConstraints(jsonObject, null);
                if (value == null || !constraints.isValid(value)) {
                    String constraintDescription = constraints.getDescription();
                    String validfieldMsg = getString(R.string.form_field_check);
                    String msg = MessageFormat.format(validfieldMsg, key, formName, constraintDescription);
                    Utilities.messageDialog(this, msg, null);
                    return;
                }
            }
        }

        // finally store data
        String sectionObjectString = sectionObject.toString();
        Date sqlDate = new Date(System.currentTimeMillis());
        String timestamp = LibraryConstants.TIME_FORMATTER_SQLITE.format(sqlDate);

        String[] formDataArray = {//
        String.valueOf(longitude), //
                String.valueOf(latitude), //
                String.valueOf(elevation), //
                timestamp, //
                sectionName, //
                "POI", //
                sectionObjectString};
        Intent intent = getIntent();
        intent.putExtra(LibraryConstants.PREFS_KEY_FORM, formDataArray);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public boolean onKeyDown( int keyCode, KeyEvent event ) {
        // force to exit through the exit button, in order to avoid losing info
        switch( keyCode ) {
        case KeyEvent.KEYCODE_BACK:
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
