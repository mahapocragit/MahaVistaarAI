package in.gov.mahapocra.mahavistaarai.sma.ui.adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.gov.mahapocra.mahavistaarai.R;

public class CustomListAdapter extends BaseAdapter {

        private JSONArray jsonArray;
        private Activity activity;
        private String keyName;

        public CustomListAdapter(Activity activity, JSONArray jsonArray, String keyName) {
            this.activity = activity;
            this.jsonArray = jsonArray;
            this.keyName = keyName;
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return jsonArray.getJSONObject(position);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(activity)
                        .inflate(R.layout.item_custom_list, parent, false);
            }

            TextView tvName = convertView.findViewById(R.id.tvName);

            try {
                JSONObject obj = jsonArray.getJSONObject(position);
                tvName.setText(obj.getString(keyName));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

