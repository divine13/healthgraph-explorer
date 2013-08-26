package com.thoughtworks.healthgraphexplorer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.thoughtworks.healthgraphexplorer.service.listener.WeightSetFeedListener;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSet;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSetFeed;
import com.thoughtworks.healthgraphexplorer.service.request.DeleteWeightSetRequest;
import com.thoughtworks.healthgraphexplorer.service.request.GetWeightSetFeedRequest;

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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                final WeightSet weightSet = (WeightSet) parent.getItemAtPosition(position);

                String weightSetUri = ((ViewHolder) view.getTag()).weightSetUri;
                String weightSetId = weightSetUri.replace("/weight/", "");

                RetrofitSpiceRequest request = new DeleteWeightSetRequest(weightSetId);

                RequestListener<Void> listener = new RequestListener<Void>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Toast.makeText(WeightListActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRequestSuccess(Void _) {
                        final WeightListAdapter adapter = (WeightListAdapter) ((ListView) parent).getAdapter();

                        Toast.makeText(WeightListActivity.this, "Delete successful", Toast.LENGTH_SHORT).show();
                        view.animate().setDuration(500).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.getWeightSets().remove(weightSet);
                                        adapter.notifyDataSetChanged();
                                        view.setAlpha(1);
                                    }
                                });
                    }
                };

                WeightListActivity.this.getSpiceManager().execute(request, listener);
                return false;
            }
        });
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

        this.getSpiceManager().execute(new GetWeightSetFeedRequest(), listener);
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
        private final List<WeightSet> weightSets;

        private WeightListAdapter(Activity context, int textViewResourceId, List<WeightSet> weightSets) {
            super(context, textViewResourceId, weightSets);
            this.context = context;
            this.textViewResourceId = textViewResourceId;
            this.weightSets = weightSets;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WeightSet weightSet = weightSets.get(position);

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
            holder.weightSetUri = weightSet.getUri();

            String dateStr = DateFormat.getDateInstance().format(weightSet.getTimestamp());
            holder.date.setText(dateStr);

            Double weight = weightSet.getWeight();
            if (weight == null) {
                holder.weight.setVisibility(View.GONE);
                holder.weight.setText(null);
            } else {
                holder.weight.setVisibility(View.VISIBLE);
                holder.weight.setText(String.format("%.1f kg", weight));
            }

            Double fatPercent = weightSet.getFatPercent();
            if (fatPercent == null) {
                holder.fatPercent.setVisibility(View.GONE);
                holder.fatPercent.setText(null);
            } else {
                holder.fatPercent.setVisibility(View.VISIBLE);
                holder.fatPercent.setText(String.format("%.1f %%", fatPercent));
            }

            return rowView;
        }

        public List<WeightSet> getWeightSets() {
            return weightSets;
        }
    }

    private class ViewHolder {
        public TextView date;
        public TextView weight;
        public TextView fatPercent;
        public String weightSetUri;
    }
}