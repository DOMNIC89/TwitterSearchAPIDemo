package com.example.pandyaparth.twitterapidemo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pandyaparth.twitterapidemo.R;
import com.example.pandyaparth.twitterapidemo.model.Search;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pandya Parth on 29-08-2015.
 */
public class TwitterSearchListViewAdapter extends BaseAdapter {

    private ArrayList<Search> searches;
    private List<User> users;
    private Context context;
    private int type;

    public TwitterSearchListViewAdapter(Context context){
        this.context = context;
    }

    public void setSearches(ArrayList<Search> searches) {
        this.searches = searches;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (searches!= null) {
            size = searches.size();
        } else if (users != null) {
            size = users.size();
        }
        return size;
    }

    @Override
    public Object getItem(int position) {
        return searches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final SearchResultViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_row, null);
            viewHolder = new SearchResultViewHolder();
            viewHolder.profileImage = (SimpleDraweeView) view.findViewById(R.id.profile_image);
            viewHolder.userName = (TextView) view.findViewById(R.id.profile_name);
            viewHolder.userHandle = (TextView) view.findViewById(R.id.profile_handle);
            viewHolder.tweetText = (TextView) view.findViewById(R.id.tweet_text);

            view.setTag(viewHolder);
        } else {
            viewHolder = (SearchResultViewHolder) view.getTag();
        }
        if (getItemViewType(position) == 0) {
            Search search = searches.get(position);
            viewHolder.profileImage.setImageURI(Uri.parse(search.getUser().getProfileImageUrl()));
            viewHolder.userName.setText(search.getUser().getName());
            viewHolder.userHandle.setText("@"+search.getUser().getScreenName());
            viewHolder.tweetText.setVisibility(View.VISIBLE);
            viewHolder.tweetText.setText(search.getText());
        } else {
            User user = users.get(position);
            viewHolder.profileImage.setImageURI(Uri.parse(user.profileImageUrl));
            viewHolder.userName.setText(user.name);
            viewHolder.userHandle.setText("@" + user.screenName);
            viewHolder.tweetText.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    private static class SearchResultViewHolder {
        SimpleDraweeView profileImage;
        TextView userName;
        TextView userHandle;
        TextView tweetText;
    }
}
