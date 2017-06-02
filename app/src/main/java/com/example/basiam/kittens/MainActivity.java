package com.example.basiam.kittens;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Kitten>> {

    private static final String LOG_TAG = MainActivity.class.getName();
    private TextView mEmptyStateTextView;
    private KittenAdapter mAdapter;
    private static final int LOADER_ID = 1;
    private static final String REQUEST_URL =
            "http://thecatapi.com/api/images/get?format=xml&results_per_page=20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView kittenListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        kittenListView.setEmptyView(mEmptyStateTextView);



        // Create a new {@link ArrayAdapter} of kittens
        mAdapter = new KittenAdapter(this, new ArrayList<Kitten>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        kittenListView.setAdapter(mAdapter);

        kittenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Kitten currentKitten = mAdapter.getItem(position);
                Uri kittenUri = currentKitten.getUrl();
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, kittenUri);
                startActivity(websiteIntent);
            }
        });
        // Get a reference to the LoaderManager, in order to interact with loaders.

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_ID, null, this);
        }  else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }


    @Override
    public Loader<List<Kitten>> onCreateLoader(int i, Bundle bundle) {
        KittenLoader loader = new KittenLoader(this, REQUEST_URL);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Kitten>> loader, List<Kitten> Kittens) {
        // Clear the adapter of previous Kitten data
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_kittens);
        mAdapter.clear();

        // If there is a valid list of {@link Kitten}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (Kittens != null && !Kittens.isEmpty()) {
            mAdapter.addAll(Kittens);
        }


    }

    @Override
    public void onLoaderReset(Loader<List<Kitten>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}