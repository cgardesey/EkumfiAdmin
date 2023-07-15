package com.ekumfi.admin.adapter;

/**
 * Created by Nana on 11/10/2017.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ekumfi.admin.R;
import com.ekumfi.admin.realm.RealmWholesaler;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Belal on 6/6/2017.
 */

public class WholesalerIndexAdapter extends RecyclerView.Adapter<WholesalerIndexAdapter.ViewHolder> {

    private static final String YOUR_DIALOG_TAG = "";
    WholesalerIndexAdapterInterface chatIndexAdapterInterface;
    Activity mActivity;
    boolean showMenu;
    private ArrayList<RealmWholesaler> realmWholesalers;
    public static final SimpleDateFormat sfd_time = new SimpleDateFormat("h:mm a");

    public WholesalerIndexAdapter(WholesalerIndexAdapterInterface chatIndexAdapterInterface, Activity mActivity, ArrayList<RealmWholesaler> realmWholesalers, boolean showMenu) {
        this.chatIndexAdapterInterface = chatIndexAdapterInterface;
        this.mActivity = mActivity;
        this.realmWholesalers = realmWholesalers;
        this.showMenu = showMenu;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_wholesaler_index, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        RealmWholesaler realmWholesaler = realmWholesalers.get(position);

        holder.name.setText(realmWholesaler.getShop_name());
        holder.location.setText(realmWholesaler.getStreet_address());

        if (realmWholesaler.getStreet_address() != null && !realmWholesaler.getShop_name().equals("")) {
            Glide.with(mActivity)
                    .load(realmWholesaler.getShop_image_url()) // image url
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.profilepic);
        } else {
            holder.profilepic.setImageDrawable(null);
        }


        holder.profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIndexAdapterInterface.onImageClick(realmWholesalers, position, holder);
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIndexAdapterInterface.onItemClick(realmWholesalers, position, holder);
            }
        });

        if (showMenu) {
            holder.menu.setVisibility(View.VISIBLE);
        }
        else {
            holder.menu.setVisibility(View.GONE);
        }

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIndexAdapterInterface.onMenuClick(realmWholesalers, position, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return realmWholesalers.size();
    }

    public interface WholesalerIndexAdapterInterface {
        void onItemClick(ArrayList<RealmWholesaler> realmWholesalers, int position, ViewHolder holder);
        void onImageClick(ArrayList<RealmWholesaler> realmWholesalers, int position, ViewHolder holder);
        void onMenuClick(ArrayList<RealmWholesaler> realmWholesalers, int position, ViewHolder holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, location;
        public ImageView profilepic, menu;
        public LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            profilepic = itemView.findViewById(R.id.profilepic);
            name = itemView.findViewById(R.id.name);
            location = itemView.findViewById(R.id.location);
            item = itemView.findViewById(R.id.item);
            menu = itemView.findViewById(R.id.menu);
        }
    }
}
