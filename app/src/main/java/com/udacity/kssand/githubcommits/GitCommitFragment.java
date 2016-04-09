package com.udacity.kssand.githubcommits;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kssand on 08-Apr-16.
 */
public class GitCommitFragment extends android.support.v4.app.ListFragment {

    CommitsAdapter arrayAdapter;
    List<Committer> committerList;
    TextView defaultText;

    public GitCommitFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.git_commit_fragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateGitCommits();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateGitCommits();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateGitCommits() {
        FetchGitCommitTask gitCommitTask = new FetchGitCommitTask();
        gitCommitTask.execute();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        committerList=new ArrayList<>();
        arrayAdapter =
                new CommitsAdapter(
                        getActivity(), // The current context (this activity)
                        committerList);

        View rootView = inflater.inflate(R.layout.frament_main, container, false);
        defaultText = (TextView)getActivity().findViewById(R.id.default_text);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_commits);
        listView.setAdapter(arrayAdapter);

        return rootView;
    }


    public class FetchGitCommitTask extends AsyncTask<String, Void, List<Committer>> {

        @Override
        protected List<Committer> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String commitJsonStr = null;
            List<Committer> result=null;

            try {
                Uri uri = Uri.parse("https://api.github.com/repos/rails/rails/commits").buildUpon().build();
                Log.v("query", uri.toString());
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                commitJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("GitCommitFragment", "Error ", e);
                // If the code didn't successfully get the commit's data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("GitCommitFragment", "Error closing stream", e);
                    }
                }
            }


            Log.v("jsonstr", commitJsonStr);
            try {
                result=getCommitDataFromJson(commitJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<Committer> committers) {
            if(committers!=null) {
                committerList.clear();
                committerList.addAll(committers);
                arrayAdapter.notifyDataSetChanged();
                defaultText.setVisibility(View.GONE);
            }else {
                defaultText.setVisibility(View.VISIBLE);
            }
        }

        private List<Committer> getCommitDataFromJson(String commitJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String COMMIT = "commit";
            final String COMMITTER = "committer";
            final String NAME = "name";
            final String MESSAGE = "message";
            final String DATE = "date";

            JSONArray commitsArray=new JSONArray(commitJsonStr);
            List<Committer> committerArrayObject= new ArrayList<>();
            for(int i=0;i<commitsArray.length();i++){
                JSONObject commitDetails = commitsArray.getJSONObject(i);
                JSONObject commit= commitDetails.getJSONObject(COMMIT);
                JSONObject committer= commit.getJSONObject(COMMITTER);
                committerArrayObject.add(new Committer(committer.getString(NAME),commit.getString(MESSAGE),committer.getString(DATE)));
            }
            return committerArrayObject;
        }

    }



}

