package com.tedmob.merchants.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tedmob.merchants.MainActivity;
import com.tedmob.merchants.R;
import com.tedmob.merchants.model.Merchant;

import java.util.List;


/**
 * Created by wiaam on 06-Jun-17.
 */

public class MerchantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Merchant> merchantList;
    RecyclerView recyclerView;
    MainActivity mainActivity;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.pbHeaderProgress);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView settings, imageloader, logo;

        private MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            settings = (ImageView) view.findViewById(R.id.settings);
            imageloader = (ImageView) view.findViewById(R.id.image);
            logo = (ImageView) view.findViewById(R.id.logo);

        }
    }


    public MerchantAdapter(RecyclerView recyclerView, List<Merchant> moviesList, MainActivity mainActivity) {
        this.merchantList = moviesList;
        this.recyclerView = recyclerView;
        this.mainActivity = mainActivity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }

                    isLoading = true;
                }
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.merchant_list_row, parent, false);
            return new MyViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_LOADING) {
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
                return new LoadingViewHolder(view);
            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            Merchant merchant = merchantList.get(position);
            final MyViewHolder userViewHolder = (MyViewHolder) holder;
            userViewHolder.title.setText(merchant.getTitle());
            userViewHolder.title.setText(merchant.getTitle());

            if (merchant.getImage_paths() != null)
                Picasso.with(mainActivity).load(Uri.parse(merchant.getImage_paths().get(0).toString())).into(userViewHolder.imageloader);
            Picasso.with(mainActivity).load(Uri.parse(merchant.getLogo_path().toString())).into(userViewHolder.logo);
            ;


            userViewHolder.settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu popup = new PopupMenu(mainActivity, userViewHolder.settings);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_settings, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            Gson gson = new Gson();
                            String json;
                            Intent intent;
                            switch (id) {
                                case R.id.call:

                                    MainActivity.instance.MakeCall(position);

                                    return true;
                                case R.id.email:

                                    MainActivity.instance.SendMail(position);
                                    return true;

                            }
                            return true;
                        }
                    });
                    popup.show(); //showing popup menu
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemCount() {
        return merchantList == null ? 0 : merchantList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return merchantList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }
}
