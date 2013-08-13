package com.thoughtworks.healthgraphexplorer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.thoughtworks.healthgraphexplorer.service.listener.WeightSetFeedListener;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSet;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSetFeed;
import com.thoughtworks.healthgraphexplorer.service.request.WeightSetFeedRequest;

import java.text.DateFormat;
import java.util.List;

import roboguice.inject.InjectView;

public class WeightListActivity extends BaseActivity {

    @InjectView(R.id.weightList_listView)
    private ListView listView;

    @InjectView(R.id.weightList_progressBar)
    private ProgressBar progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weightlist);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchList() {
        updateUiFetching(true);

        WeightSetFeedListener listener = new WeightSetFeedListener() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d("xxx", "request failed: " + spiceException);
                updateUiFetching(false);
            }

            @Override
            public void onRequestSuccess(WeightSetFeed result) {
                Log.d("xxx", "request successful: " + result);
                ListAdapter adapter = new WeightListAdapter(WeightListActivity.this,
                        R.layout.layout_weightlist_listitem, result.getItems());
                listView.setAdapter(adapter);
                updateUiFetching(false);
            }
        };

        this.getSpiceManager().execute(new WeightSetFeedRequest(), listener);
    }

    private void updateUiFetching(boolean isFetching) {
        if (isFetching) {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private class WeightListAdapter extends ArrayAdapter<WeightSet> {
        private final Activity context;
        private final int textViewResourceId;
        private final List<WeightSet> objects;

        private class ViewHolder {
            public TextView date;
            public TextView weight;
            public TextView fatPercent;
        }

        private WeightListAdapter(Activity context, int textViewResourceId, List<WeightSet> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.textViewResourceId = textViewResourceId;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(textViewResourceId, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.date = (TextView) rowView.findViewById(R.id.weightList_dateTextView);
                viewHolder.weight = (TextView) rowView.findViewById(R.id.weightList_weightTextView);
                viewHolder.fatPercent =
                        (TextView) rowView.findViewById(R.id.weightList_fatPercentTextView);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();

            String dateStr = DateFormat.getDateInstance().format(objects.get(position).getTimestamp());
            holder.date.setText(dateStr);

            Double weight = objects.get(position).getWeight();
            if (weight == null) {
                holder.weight.setVisibility(View.GONE);
                holder.weight.setText(null);
            } else {
                holder.weight.setVisibility(View.VISIBLE);
                holder.weight.setText(String.format("%.1f kg", weight));
            }

            Double fatPercent = objects.get(position).getFatPercent();
            if (fatPercent == null) {
                holder.fatPercent.setVisibility(View.GONE);
                holder.fatPercent.setText(null);
            } else {
                holder.fatPercent.setVisibility(View.VISIBLE);
                holder.fatPercent.setText(String.format("%.1f %%", fatPercent));
            }

            return rowView;
        }
    }
}