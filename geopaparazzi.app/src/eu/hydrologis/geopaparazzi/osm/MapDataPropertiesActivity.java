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
package eu.hydrologis.geopaparazzi.osm;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import eu.hydrologis.geopaparazzi.R;
import eu.hydrologis.geopaparazzi.database.DaoMaps;
import eu.hydrologis.geopaparazzi.util.ApplicationManager;
import eu.hydrologis.geopaparazzi.util.Constants;
import eu.hydrologis.geopaparazzi.util.Line;

/**
 * Data properties activity.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class MapDataPropertiesActivity extends Activity {
    private static List<String> colorList;
    private static List<String> widthsList;
    private MapItem item;
    private EditText textView;

    public void onCreate( Bundle icicle ) {
        super.onCreate(icicle);

        setContentView(R.layout.maps_properties);
        getResourcesAndColors();

        Bundle extras = getIntent().getExtras();
        Object object = extras.get(Constants.PREFS_KEY_MAP4PROPERTIES);
        if (object instanceof MapItem) {
            item = (MapItem) object;

            textView = (EditText) findViewById(R.id.mapname);
            final Spinner colorView = (Spinner) findViewById(R.id.color_spinner);
            final Spinner widthView = (Spinner) findViewById(R.id.widthText);

            textView.setText(item.getName());
            textView.addTextChangedListener(new TextWatcher(){
                public void onTextChanged( CharSequence s, int start, int before, int count ) {
                    item.setDirty(true);
                }
                public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
                }
                public void afterTextChanged( Editable s ) {
                }
            });

            // width spinner
            ArrayAdapter< ? > widthSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_widths,
                    android.R.layout.simple_spinner_item);
            widthSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            widthView.setAdapter(widthSpinnerAdapter);
            int widthIndex = widthsList.indexOf(String.valueOf((int) item.getWidth()));
            widthView.setSelection(widthIndex);
            widthView.setOnItemSelectedListener(new OnItemSelectedListener(){
                public void onItemSelected( AdapterView< ? > arg0, View arg1, int arg2, long arg3 ) {
                    Object selectedItem = widthView.getSelectedItem();
                    float newWidth = Float.parseFloat(selectedItem.toString());
                    item.setWidth(newWidth);
                    item.setDirty(true);
                }
                public void onNothingSelected( AdapterView< ? > arg0 ) {
                }
            });

            // color box
            ArrayAdapter< ? > colorSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_colornames,
                    android.R.layout.simple_spinner_item);
            colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorView.setAdapter(colorSpinnerAdapter);
            int colorIndex = colorList.indexOf(item.getColor());
            colorView.setSelection(colorIndex);
            colorView.setOnItemSelectedListener(new OnItemSelectedListener(){
                public void onItemSelected( AdapterView< ? > arg0, View arg1, int arg2, long arg3 ) {
                    Object selectedItem = colorView.getSelectedItem();
                    String newColor = selectedItem.toString();
                    item.setColor(newColor);
                    // rowView.setBackgroundColor(Color.parseColor(item.getColor()));
                    item.setDirty(true);
                }
                public void onNothingSelected( AdapterView< ? > arg0 ) {
                }
            });

            final Button zoomButton = (Button) findViewById(R.id.map_zoom);
            zoomButton.setOnClickListener(new Button.OnClickListener(){
                public void onClick( View v ) {
                    try {
                        Line line = DaoMaps.getMapAsLine(item.getId());
                        if (line.getLonList().size() > 0) {
                            ApplicationManager.getInstance().getOsmView()
                                    .setGotoCoordinate(line.getLonList().get(0), line.getLatList().get(0));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            final Button deleteButton = (Button) findViewById(R.id.map_delete);
            deleteButton.setOnClickListener(new Button.OnClickListener(){
                public void onClick( View v ) {
                    try {
                        long id = item.getId();
                        DaoMaps.deleteMap(id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    @Override
    protected void onPause() {
        try {
            if (item != null && item.isDirty()) {
                String newText = textView.getText().toString();
                DaoMaps.updateMapProperties(item.getId(), item.getColor(), item.getWidth(), item.isVisible(), newText);
                item.setDirty(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onPause();

    }

    private void getResourcesAndColors() {
        if (colorList == null) {
            String[] colorArray = getResources().getStringArray(R.array.array_colornames);
            colorList = Arrays.asList(colorArray);
            String[] widthsArray = getResources().getStringArray(R.array.array_widths);
            widthsList = Arrays.asList(widthsArray);
        }

    }

}
