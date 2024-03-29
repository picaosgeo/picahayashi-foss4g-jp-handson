package eu.geopaparazzi.library.forms;

import static eu.geopaparazzi.library.forms.FormUtilities.TAG_KEY;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_TYPE;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_VALUE;
import static eu.geopaparazzi.library.forms.FormUtilities.TYPE_BOOLEAN;
import static eu.geopaparazzi.library.forms.FormUtilities.TYPE_DATE;
import static eu.geopaparazzi.library.forms.FormUtilities.TYPE_DOUBLE;
import static eu.geopaparazzi.library.forms.FormUtilities.TYPE_LABEL;
import static eu.geopaparazzi.library.forms.FormUtilities.TYPE_LABELWITHLINE;
import static eu.geopaparazzi.library.forms.FormUtilities.TYPE_STRING;
import static eu.geopaparazzi.library.forms.FormUtilities.TYPE_STRINGCOMBO;
import static eu.geopaparazzi.library.forms.FormUtilities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import eu.geopaparazzi.library.R;
import eu.geopaparazzi.library.forms.constraints.Constraints;
import eu.geopaparazzi.library.util.FileUtilities;
import eu.geopaparazzi.library.util.LibraryConstants;
import eu.geopaparazzi.library.util.ResourcesManager;
import eu.geopaparazzi.library.util.debug.Logger;

public class FragmentDetail extends Fragment {

    private HashMap<String, View> key2WidgetMap = new HashMap<String, View>();
    private HashMap<String, Constraints> key2ConstraintsMap = new HashMap<String, Constraints>();
    private List<String> keyList = new ArrayList<String>();
    private String selectedFormName;
    private JSONObject sectionObject;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate(R.layout.details, container, false);
        LinearLayout mainView = (LinearLayout) view.findViewById(R.id.form_linear);
        try {
            FragmentActivity activity = getActivity();
            if (selectedFormName == null || sectionObject == null) {
                FragmentList listFragment = (FragmentList) getFragmentManager().findFragmentById(R.id.listFragment);
                if (listFragment != null) {
                    selectedFormName = listFragment.getSelectedItemName();
                    sectionObject = listFragment.getSectionObject();
                } else {
                    if (activity instanceof FragmentDetailActivity) {
                        // case of portrait mode
                        FragmentDetailActivity fragmentDetailActivity = (FragmentDetailActivity) activity;
                        selectedFormName = fragmentDetailActivity.getFormName();
                        sectionObject = fragmentDetailActivity.getSectionObject();
                    }
                }
            }
            if (selectedFormName != null) {
                JSONObject formObject = TagsManager.getForm4Name(selectedFormName, sectionObject);

                key2WidgetMap.clear();
                keyList.clear();
                key2ConstraintsMap.clear();

                JSONArray formItemsArray = TagsManager.getFormItems(formObject);

                int length = formItemsArray.length();
                for( int i = 0; i < length; i++ ) {
                    JSONObject jsonObject = formItemsArray.getJSONObject(i);

                    String key = "-"; //$NON-NLS-1$
                    if (jsonObject.has(TAG_KEY))
                        key = jsonObject.getString(TAG_KEY).trim();

                    String value = ""; //$NON-NLS-1$
                    if (jsonObject.has(TAG_VALUE)) {
                        value = jsonObject.getString(TAG_VALUE).trim();
                    }
                    String type = FormUtilities.TYPE_STRING;
                    if (jsonObject.has(TAG_TYPE)) {
                        type = jsonObject.getString(TAG_TYPE).trim();
                    }

                    Constraints constraints = new Constraints();
                    FormUtilities.handleConstraints(jsonObject, constraints);
                    key2ConstraintsMap.put(key, constraints);
                    String constraintDescription = constraints.getDescription();

                    View addedView = null;
                    if (type.equals(TYPE_STRING)) {
                        addedView = FormUtilities.addEditText(activity, mainView, key, value, 0, constraintDescription);
                    } else if (type.equals(TYPE_DOUBLE)) {
                        addedView = FormUtilities.addEditText(activity, mainView, key, value, 1, constraintDescription);
                    } else if (type.equals(TYPE_DATE)) {
                        addedView = FormUtilities.addDateView(FragmentDetail.this, mainView, key, value, constraintDescription);
                    } else if (type.equals(TYPE_TIME)) {
                        addedView = FormUtilities.addTimeView(FragmentDetail.this, mainView, key, value, constraintDescription);
                    } else if (type.equals(TYPE_LABEL)) {
                        String size = "20"; //$NON-NLS-1$
                        if (jsonObject.has(TAG_SIZE))
                            size = jsonObject.getString(TAG_SIZE);
                        String url = null;
                        if (jsonObject.has(TAG_URL))
                            url = jsonObject.getString(TAG_URL);
                        addedView = FormUtilities.addTextView(activity, mainView, value, size, false, url);
                    } else if (type.equals(TYPE_LABELWITHLINE)) {
                        String size = "20"; //$NON-NLS-1$
                        if (jsonObject.has(TAG_SIZE))
                            size = jsonObject.getString(TAG_SIZE);
                        String url = null;
                        if (jsonObject.has(TAG_URL))
                            url = jsonObject.getString(TAG_URL);
                        addedView = FormUtilities.addTextView(activity, mainView, value, size, true, url);
                    } else if (type.equals(TYPE_BOOLEAN)) {
                        addedView = FormUtilities.addBooleanView(activity, mainView, key, value, constraintDescription);
                    } else if (type.equals(TYPE_STRINGCOMBO)) {
                        JSONArray comboItems = TagsManager.getComboItems(jsonObject);
                        String[] itemsArray = TagsManager.comboItems2StringArray(comboItems);
                        addedView = FormUtilities.addComboView(activity, mainView, key, value, itemsArray, constraintDescription);
                    } else if (type.equals(TYPE_STRINGMULTIPLECHOICE)) {
                        JSONArray comboItems = TagsManager.getComboItems(jsonObject);
                        String[] itemsArray = TagsManager.comboItems2StringArray(comboItems);
                        addedView = FormUtilities.addMultiSelectionView(activity, mainView, key, value, itemsArray,
                                constraintDescription);
                    } else if (type.equals(TYPE_PICTURES)) {
                        addedView = FormUtilities.addPictureView(activity, mainView, key, value, constraintDescription);
                    } else if (type.equals(TYPE_MAP)) {
                        if (value == null || value.length() <= 0) {
                            File applicationDir = ResourcesManager.getInstance(activity).getApplicationDir();
                            File mediaDir = ResourcesManager.getInstance(activity).getMediaDir();
                            File tmpImage = new File(applicationDir, LibraryConstants.TMPPNGIMAGENAME);
                            if (tmpImage.exists()) {
                                Date currentDate = new Date();
                                String currentDatestring = LibraryConstants.TIMESTAMPFORMATTER.format(currentDate);
                                File newImageFile = new File(mediaDir, "IMG_" + currentDatestring + ".png"); //$NON-NLS-1$ //$NON-NLS-2$
                                FileUtilities.copyFile(tmpImage, newImageFile);
                                value = newImageFile.getParentFile().getName() + File.separator + newImageFile.getName();
                            }
                        }
                        addedView = FormUtilities.addMapView(activity, mainView, key, value, constraintDescription);
                    } else {
                        Logger.i(this, "Type non implemented yet: " + type); //$NON-NLS-1$
                    }
                    key2WidgetMap.put(key, addedView);
                    keyList.add(key);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public JSONObject getSectionObject() {
        return sectionObject;
    }

    public void setForm( String selectedItemName, JSONObject sectionObject ) {
        this.selectedFormName = selectedItemName;
        this.sectionObject = sectionObject;
    }

    /**
     * Store the form items the widgets.
     * 
     * @param doConstraintsCheck if <code>true</code>, a check on all constraints is performed.
     * @return <code>null</code>, if everything was saved properly, the key of the items
     *              that didn't pass the constraint check.
     * @throws Exception
     */
    @SuppressWarnings("nls")
    public String storeFormItems( boolean doConstraintsCheck ) throws Exception {
        if (selectedFormName == null) {
            return null;
        }
        JSONObject form4Name = TagsManager.getForm4Name(selectedFormName, sectionObject);
        JSONArray formItems = TagsManager.getFormItems(form4Name);

        // update the items
        for( String key : keyList ) {
            Constraints constraints = key2ConstraintsMap.get(key);

            View view = key2WidgetMap.get(key);
            String text = null;
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                boolean checked = checkBox.isChecked();
                text = checked ? "true" : "false";
            } else if (view instanceof Button) {
                Button button = (Button) view;
                text = button.getText().toString();
                if (text.trim().equals("...")) {
                    text = "";
                }
            } else if (view instanceof TextView) {
                TextView textView = (TextView) view;
                text = textView.getText().toString();
            } else if (view instanceof Spinner) {
                Spinner spinner = (Spinner) view;
                text = spinner.getSelectedItem().toString();
            }

            if (doConstraintsCheck && !constraints.isValid(text)) {
                return key;
            }

            try {
                if (text != null)
                    FormUtilities.update(formItems, key, text);
            } catch (JSONException e) {
                Logger.e(this, e.getLocalizedMessage(), e);
                e.printStackTrace();
            }
        }

        return null;
    }

}