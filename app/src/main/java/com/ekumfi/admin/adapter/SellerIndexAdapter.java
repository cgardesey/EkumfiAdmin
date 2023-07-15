package com.ekumfi.admin.adapter;

/**
 * Created by Nana on 11/10/2017.
 */

import android.app.Activity;
import android.os.Build;
import android.text.Html;
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
import com.ekumfi.admin.constants.Const;
import com.ekumfi.admin.realm.RealmSeller;
import com.ekumfi.admin.realm.RealmStockCart;
import com.ekumfi.admin.util.RealmUtility;

import org.apache.commons.text.StringEscapeUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.Realm;

/**
 * Created by Belal on 6/6/2017.
 */

public class SellerIndexAdapter extends RecyclerView.Adapter<SellerIndexAdapter.ViewHolder> {

    private static final String YOUR_DIALOG_TAG = "";
    SellerIndexAdapterInterface chatIndexAdapterInterface;
    Activity mActivity;
    boolean showMenu;
    private ArrayList<RealmSeller> realmSellers;
    public static final SimpleDateFormat sfd_time = new SimpleDateFormat("h:mm a");

    public SellerIndexAdapter(SellerIndexAdapterInterface chatIndexAdapterInterface, Activity mActivity, ArrayList<RealmSeller> realmSellers, boolean showMenu) {
        this.chatIndexAdapterInterface = chatIndexAdapterInterface;
        this.mActivity = mActivity;
        this.realmSellers = realmSellers;
        this.showMenu = showMenu;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_seller_index, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RealmSeller realmSeller = realmSellers.get(position);

        holder.name.setText(realmSeller.getShop_name());
        holder.location.setText(realmSeller.getStreet_address());

        if (realmSeller.getShop_image_url() != null && !realmSeller.getShop_image_url().equals("")) {
            Glide.with(mActivity)
                    .load(realmSeller.getShop_image_url()) // image url
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.profilepic);
        } else {
            holder.profilepic.setImageDrawable(null);
        }


        holder.profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIndexAdapterInterface.onImageClick(realmSellers, position, holder);
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIndexAdapterInterface.onItemClick(realmSellers, position, holder);
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
                chatIndexAdapterInterface.onMenuClick(realmSellers, position, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return realmSellers.size();
    }

    public interface SellerIndexAdapterInterface {
        void onItemClick(ArrayList<RealmSeller> realmSellers, int position, ViewHolder holder);
        void onImageClick(ArrayList<RealmSeller> realmSellers, int position, ViewHolder holder);
        void onMenuClick(ArrayList<RealmSeller> realmSellers, int position, ViewHolder holder);
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
