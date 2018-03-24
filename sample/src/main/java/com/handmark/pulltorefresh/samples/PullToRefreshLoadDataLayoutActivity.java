/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.samples;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.samples.baoGong.loadData.LoadDataLayout;

import java.util.Arrays;
import java.util.LinkedList;

public final class PullToRefreshLoadDataLayoutActivity extends Activity {

    static final int MENU_SET_MODE = 0;

    private LinkedList<String> mListItems;
    private PullToRefreshLoadDataLayoutRecyclerView mPullToRefreshFrameLayout;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    LoadDataLayout mLoadDataLayout;

    private String[] mStrings = {"Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Kevin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_frame_layout);

        mPullToRefreshFrameLayout = (PullToRefreshLoadDataLayoutRecyclerView) findViewById(R.id.pull_refresh_webview);
        mLoadDataLayout = mPullToRefreshFrameLayout.getRefreshableView();
        mLoadDataLayout.setStatus(LoadDataLayout.SUCCESS);
        mLoadDataLayout.setOnReloadListener(new LoadDataLayout.OnReloadListener() {
            @Override
            public void onReload(View v, int status) {
                mLoadDataLayout.setStatus(LoadDataLayout.SUCCESS);
            }
        });

        mRecyclerView = mPullToRefreshFrameLayout.getRealRefreshableView();

        //		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        // Set a listener to be invoked when the list should be refreshed.

        mPullToRefreshFrameLayout.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<LoadDataLayout>() {
            public void onPullDownToRefresh(PullToRefreshBase<LoadDataLayout> refreshView) {
                Toast.makeText(PullToRefreshLoadDataLayoutActivity.this, "Pull Down!", Toast.LENGTH_SHORT).show();
                new GetDataTask().execute();
            }

            public void onPullUpToRefresh(PullToRefreshBase<LoadDataLayout> refreshView) {
                Toast.makeText(PullToRefreshLoadDataLayoutActivity.this, "Pull Up!", Toast.LENGTH_SHORT).show();
                new GetDataTask().execute();
            }
        });

        mListItems = new LinkedList<String>();
        mListItems.addAll(Arrays.asList(mStrings));
        mAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            return mStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mListItems.addFirst("Added after refresh...");
            mListItems.addAll(Arrays.asList(result));
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullToRefreshFrameLayout.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SET_MODE, 0,
                mPullToRefreshFrameLayout.getMode() == PullToRefreshBase.Mode.BOTH ? "Change to MODE_PULL_DOWN"
                        : "Change to MODE_PULL_BOTH");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem setModeItem = menu.findItem(MENU_SET_MODE);
        setModeItem.setTitle(mPullToRefreshFrameLayout.getMode() == PullToRefreshBase.Mode.BOTH ? "Change to MODE_PULL_FROM_START"
                : "Change to MODE_PULL_BOTH");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SET_MODE:
                mPullToRefreshFrameLayout.getRefreshableView().setStatus(mPullToRefreshFrameLayout.getRefreshableView().getStatus() == LoadDataLayout.SUCCESS
                        ? LoadDataLayout.EMPTY : LoadDataLayout.SUCCESS);

                mPullToRefreshFrameLayout
                        .setMode(mPullToRefreshFrameLayout.getMode() == PullToRefreshBase.Mode.BOTH ? PullToRefreshBase.Mode.PULL_FROM_START
                                : PullToRefreshBase.Mode.BOTH);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemCount() {
            return mListItems.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    PullToRefreshLoadDataLayoutActivity.this).inflate(android.R.layout.simple_list_item_1, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MyViewHolder) holder).tv.setText(mListItems.get(position));
            int bg = Color.rgb((int) Math.floor(Math.random() * 128) + 64,
                    (int) Math.floor(Math.random() * 128) + 64,
                    (int) Math.floor(Math.random() * 128) + 64);
            ((MyViewHolder) holder).tv.setBackgroundColor(bg);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(android.R.id.text1);
            }
        }
    }
}
