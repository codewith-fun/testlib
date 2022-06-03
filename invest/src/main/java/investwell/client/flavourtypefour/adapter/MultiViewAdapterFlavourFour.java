package investwell.client.flavourtypefour.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import investwell.client.activity.MainActivity;
import investwell.client.activity.SignUpActivity;
import investwell.client.activity.WebViewActivity;

import investwell.client.flavourtypetwo.activity.EnquiryItemActivity;
import investwell.client.fragment.requestservice.ServiceRequest;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

public class MultiViewAdapterFlavourFour extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public ArrayList<JSONObject> sectionArrayList;

    private AppSession mSession;
    private View itemView;
    private Intent intent;
    private String viewType = "";
    private static int TYPE_CARD = 1;
    private static int TYPE_GRID = 2;
    private static int TYPE_SLIDER = 3;
    private MainActivity mActivity;
    private String topSchemeType = "All";
    public MultiViewAdapterFlavourFour(Context context, ArrayList<JSONObject> sectionArrayList) {
        this.sectionArrayList = sectionArrayList;
        mContext = context;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_CARD) { // for call layout_gridview_type_two_a

            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_dashboard_cardviews_flavour_four, viewGroup, false);

            return new MultiViewAdapterFlavourFour.CardViewHolder(view);
        } else if (viewType == TYPE_SLIDER) { // for email layout_gridview_type_two_a

            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_dashboard_sliderview_flavour_one, viewGroup, false);

            return new MultiViewAdapterFlavourFour.SliderViewHolder(view);
        } else if (viewType == TYPE_GRID) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_dashboard_gridview_flavour_one, viewGroup, false);
            return new MultiViewAdapterFlavourFour.GridViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(viewType) && viewType.equalsIgnoreCase("Slider")) {
            return TYPE_SLIDER;

        } else if (!TextUtils.isEmpty(viewType) && viewType.equalsIgnoreCase("Grid")) {
            return TYPE_GRID;
        } else {
            return TYPE_CARD;
        }
    }

    public void updateSectionsTypeList(List<JSONObject> list, String viewsType) {

        sectionArrayList.clear();
        sectionArrayList.addAll(list);
        viewType = viewsType;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        mActivity = (MainActivity) mContext;
        if (getItemViewType(position) == TYPE_CARD) {
            ((MultiViewAdapterFlavourFour.CardViewHolder) viewHolder).setCardViews(sectionArrayList.get(position));
        } else if (getItemViewType(position) == TYPE_SLIDER) {
            ((MultiViewAdapterFlavourFour.SliderViewHolder) viewHolder).setSliderViews(sectionArrayList.get(position));
        } else if (getItemViewType(position) == TYPE_GRID) {
            ((MultiViewAdapterFlavourFour.GridViewHolder) viewHolder).setGridViews(sectionArrayList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }

    /*****************************CARD VIEWS*******************/
    class CardViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader;
        ImageView ivRouteIcon, ivHomeBg, iv_proceed;
        LinearLayout llRoutes;
        TextView tvRouteDesc;
        View v_routes_divider;

        CardViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_route);
            tvRouteDesc = itemView.findViewById(R.id.tv_dashboard_route_desc);
            ivRouteIcon = itemView.findViewById(R.id.iv_dashboard_route_icon);
            llRoutes = itemView.findViewById(R.id.ll_route_container);
            v_routes_divider = itemView.findViewById(R.id.v_divider_routes);
            ivHomeBg = itemView.findViewById(R.id.iv_home_bg);
            ivHomeBg = itemView.findViewById(R.id.iv_proceed);
        }

        /*****************Set Card Views Data************************************/
        void setCardViews(final JSONObject cardViews) {
            tvHeader.setText(!TextUtils.isEmpty(cardViews.optString("ChildTitle")) ? cardViews.optString("ChildTitle") : "");
            tvRouteDesc.setText(!TextUtils.isEmpty(cardViews.optString("ChildBrief")) ? cardViews.optString("ChildBrief") : "");

            String childType = "";
            String childCode = "";
            if (!TextUtils.isEmpty(cardViews.optString("ChildType"))) {
                childType = cardViews.optString("ChildType");
            }
            if (childType.equalsIgnoreCase("Module")) {
                if (!TextUtils.isEmpty(cardViews.optString("ChildCode"))) {
                    childCode = cardViews.optString("ChildCode");
                    if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                        if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                                tvHeader.setTextColor(ContextCompat.getColor(mContext, R.color.darkPrimaryTextColor));
                                tvRouteDesc.setTextColor(ContextCompat.getColor(mContext, R.color.darkSecondaryTextColor));
                            }else{
                                tvHeader.setTextColor(ContextCompat.getColor(mContext, R.color.lightPrimaryTextColor));
                                tvRouteDesc.setTextColor(ContextCompat.getColor(mContext, R.color.lightSecondaryTextColor));
                            }
                        } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                            tvHeader.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                            tvRouteDesc.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                        }
                    }
                    if (childCode.equalsIgnoreCase("vkyc")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_kyc);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_kyc);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_kyc);
                        }
                    }
                    if (childCode.equalsIgnoreCase("FundPicks")) {


                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                        }

                    }
                    if (childCode.equalsIgnoreCase("InvestNow")) {


                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_invest);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest);
                        }

                    }
                    if (childCode.equalsIgnoreCase("ServiceRequest")) {


                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_service_req);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_service_req);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_service_req);
                        }

                    }
                    if (childCode.equalsIgnoreCase("TopSchemes")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopSIPSchemes")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopRetirementFunds")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_retirement_funds);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_retirement_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_retirement_funds);
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopChildrenFunds")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_children_funds);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_children_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_children_funds);
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopGoldFunds")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_gold_funds);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_gold_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_gold_funds);
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopELSSSchemes")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_elss_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);

                            }
                        }else {
                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_elss_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);

                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("TopLiquidSchemes")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_liquid_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);

                            }
                        }else {
                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_liquid_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);

                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("SearchAMC")) {

                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest_inependently);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_invest_inependently);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest_inependently);
                            }
                        }else{
                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("RiskBasedBaskets")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_invest);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_simply_invest);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_invest);
                            }
                        }else {
                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("CalculatorRequired")) {


                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_financial_tool);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_financial_tool);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_financial_tool);
                        }
                    }

                    if (childCode.equalsIgnoreCase("GoalModuleV2")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_goal);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_goal);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_goal);
                        }
                    }
                    if (childCode.equalsIgnoreCase("Accelator")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_expertise);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_expertise);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_expertise);
                        }
                    }

                    if (childCode.equalsIgnoreCase("NFO")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_nfo);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_nfo);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_nfo);
                        }
                    }

                    if (childCode.equalsIgnoreCase("UploadCAS")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_track_old);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_track_old);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_track_old);
                        }
                    }
                    if (childCode.equalsIgnoreCase("FlavourOfMonth")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_flavour);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_flavour);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_flavour);

                        }
                    }
                    if (childCode.equalsIgnoreCase("TransferHolding")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_transfer_holding);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                        }
                    }
                    if (childCode.equalsIgnoreCase("SimplySave")) {

                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_save);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_simply_save);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_save);
                        }
                    }
                    if (childCode.equalsIgnoreCase("JustSaveReq")) {
                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_just_save);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_just_save);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_just_save);
                        }
                    }
                    if (childCode.equalsIgnoreCase("InvestInExistingSchemes")) {
                        if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                            if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_existing_schemes);

                            } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_existing_schemes);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_existing_schemes);
                        }
                    }
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
                        if (childCode.equalsIgnoreCase("SIPCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_1a);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("EducationCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education_1a);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_education_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education_1a);
                            }

                        }
                        if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage_1a);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement_1a);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_retirement_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("LumpsumCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);
                            }
                        }

                    } else {
                        if (childCode.equalsIgnoreCase("SIPCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip);
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                            }
                        }
                        if (childCode.equalsIgnoreCase("EducationCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_education);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education);
                            }

                        }
                        if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_marriage);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage);
                            }
                        }
                        if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_retirement);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);
                            }
                        }
                        if (childCode.equalsIgnoreCase("LumpsumCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_lumpsum);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);
                            }
                        }
                        if (childCode.equalsIgnoreCase("STPCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_stp);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_stp);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_stp);
                            }
                        }
                        if (childCode.equalsIgnoreCase("SWPCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_swp);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_swp);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);
                            }
                        }
                         if (childCode.equalsIgnoreCase("EMICalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_emi);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_emi);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_emi);
                            }
                        }if (childCode.equalsIgnoreCase("SIPTenureCalculator")) {

                            if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                                if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_tenure);

                                } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip_tenure);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_tenure);
                            }
                        }

                    }
                }
            }
            if (childType.equalsIgnoreCase("Custom")) {
                if (childType.equalsIgnoreCase("Custom")) {
                    if (!TextUtils.isEmpty(cardViews.optString("ImageFile"))) {
                        Picasso.get().load(cardViews.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(ivRouteIcon);
                        Picasso.get().load(cardViews.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(ivHomeBg);

                    }
                    if (!TextUtils.isEmpty(cardViews.optString("ImageType"))) {
                        if (cardViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);

                        } else if (cardViews.optString("ImageType").equalsIgnoreCase("Background")) {
                            ivRouteIcon.setVisibility(View.GONE);
                            ivHomeBg.setVisibility(View.VISIBLE);
                            llRoutes.setBackground(null);
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                        }
                    } else {
                        ivRouteIcon.setVisibility(View.VISIBLE);
                        ivHomeBg.setVisibility(View.GONE);
                    }
                }

            }

            setCardListener(cardViews);

        }

        /*********************Set Card View Listener***************************/
        void setCardListener(final JSONObject cardViews) {
            llRoutes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String childType = "";
                    String childTitle = "";
                    if (!TextUtils.isEmpty(cardViews.optString("ChildType"))) {
                        childType = cardViews.optString("ChildType");
                    }
                    if (!TextUtils.isEmpty(cardViews.optString("ChildTitle"))) {
                        childTitle = cardViews.optString("ChildTitle");
                    }
                    String targetUrl = "";
                    String childCode = "";
                    String enquiry = "";
                    String description = "";
                    if (!TextUtils.isEmpty(cardViews.optString("ChildDescription"))) {
                        description = cardViews.optString("ChildDescription");
                    }
                    String imageFile = "";
                    /*************************Custom Links*********************************************/

                    if (childType.equalsIgnoreCase("Custom")) {
                        if (!TextUtils.isEmpty(cardViews.optString("TargetURL"))) {
                            targetUrl = cardViews.optString("TargetURL");

                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", targetUrl);
                            mContext.startActivity(intent);
                        }


                    }
                    if (childType.equalsIgnoreCase("Custom") && !TextUtils.isEmpty(cardViews.optString("CustomType"))) {
                        enquiry = cardViews.optString("CustomType");

                        if (enquiry.equalsIgnoreCase("Enquiry")) {
                            if (!TextUtils.isEmpty(cardViews.optString("ImageFile"))) {
                                imageFile = cardViews.optString("ImageFile");
                            }
                            if (!TextUtils.isEmpty(cardViews.optString("ChildDescription"))) {
                                description = cardViews.optString("ChildDescription");
                            }
                            Intent intent2 = new Intent(mContext, EnquiryItemActivity.class);
                            intent2.putExtra("title", childTitle);
                            intent2.putExtra("description", description);
                            intent2.putExtra("image", imageFile);
                            mContext.startActivity(intent2);
                        }
                    }

                    /************************Module Links**********************************************/

                    if (childType.equalsIgnoreCase("Module")) {
                        if (!TextUtils.isEmpty(cardViews.optString("ChildCode"))) {
                            childCode = cardViews.optString("ChildCode");
                            if (childCode.equalsIgnoreCase("vkyc")) {

                                mActivity.displayViewOther(104, null);
                            }
                            if (childCode.equalsIgnoreCase("FundPicks")) {
                                mSession.setFundPicks(childTitle);
                                mSession.setFundPicksDesc(description);

                                mActivity.displayViewOther(50, null);
                            }
                               if (childCode.equalsIgnoreCase("InvestNow")) {
                                mSession.setInvestNow(childTitle);
                                mSession.setInvestNowDesc(description);
                                mActivity.displayViewOther(75, null);
                            }
                            if ((childCode.equalsIgnoreCase("TopSchemes") || childCode.equalsIgnoreCase("TopELSSSchemes") || 
                                    childCode.equalsIgnoreCase("TopLiquidSchemes")||childCode.equalsIgnoreCase("TopRetirementFunds")||
                            childCode.equalsIgnoreCase("TopChildrenFunds")||childCode.equalsIgnoreCase("TopGoldFunds"))) {
                                Bundle bundle = new Bundle();

                                if (childCode.equalsIgnoreCase("TopELSSSchemes")) {
                                    topSchemeType = "elss";
                                } else if (childCode.equalsIgnoreCase("TopLiquidSchemes")) {
                                    topSchemeType = "liquid";

                                }else if (childCode.equalsIgnoreCase("TopRetirementFunds")) {
                                    topSchemeType = "Ret";

                                }else if (childCode.equalsIgnoreCase("TopChildrenFunds")) {
                                    topSchemeType = "Child";

                                }else if (childCode.equalsIgnoreCase("TopGoldFunds")) {
                                    topSchemeType = "G";

                                } else {
                                    topSchemeType = "All";

                                }

                                mSession.setTopPerformer(childTitle);
                                mSession.setTopPerformerDesc(description);
                                bundle.putString("type",topSchemeType);
                                mActivity.displayViewOther(41, bundle);
                            }
                            if (childCode.equalsIgnoreCase("SearchAMC")) {
                                mSession.setAmc(childTitle);
                                mSession.setAmcDesc(description);

                                mActivity.displayViewOther(66, null);
                            }
                            if (childCode.equalsIgnoreCase("RiskBasedBaskets")) {

                                if (TextUtils.isEmpty(mSession.getRiskCode())||mSession.getRiskCode().equals("NA")) {
                                    Bundle  bundle = new Bundle();
                                    bundle.putString("Coming_from", "indirect");
                                    mActivity.displayViewOther(61, bundle);
                                } else {

                                    getSchemes();
                                }
                            }
                            if (childCode.equalsIgnoreCase("CalculatorRequired")) {

                                mActivity.displayViewOther(58, null);
                            }

                            if (childCode.equalsIgnoreCase("GoalModuleV2")) {
mSession.setGoal(childTitle);
mSession.setGoalDesc(description);
                                mActivity.displayViewOther(70, null);
                            }
                            if (childCode.equalsIgnoreCase("Accelator")) {
                                mSession.setAccelerator(childTitle);
                                mSession.setAcceleratorDesc(description);
                                mActivity.displayViewOther(1, null);
                            }

                            if (childCode.equalsIgnoreCase("NFO")) {
                                mSession.setNfo(childTitle);
                                mSession.setNfoDesc(description);
                                mActivity.displayViewOther(90, null);
                            }

                            if (childCode.equalsIgnoreCase("UploadCAS")) {
                                mSession.setCas(childTitle);
                                mSession.setCasDesc(description);
                                mActivity.displayViewOther(101, null);
                            }
                            if (childCode.equalsIgnoreCase("FlavourOfMonth")) {
                                mSession.setFom(childTitle);
                                mSession.setFomDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    mActivity.displayViewOther(74, null);
                                }

                            }
                            if (childCode.equalsIgnoreCase("TransferHolding")) {
                                mSession.setTHolding(childTitle);
                                mSession.setTHoldingDesc(description);

                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    if (mSession.getUCC_CODE().equalsIgnoreCase("NA") || mSession.getUCC_CODE().equalsIgnoreCase("")) {
                                        showDialog();
                                    } else {
                                        mActivity.displayViewOther(52, null);
                                    }
                                }


                            }
                            if (childCode.equalsIgnoreCase("SimplySave")) {
                                mSession.setSimplySave(childTitle);
                                mSession.setSimplySaveDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    mActivity.displayViewOther(79, null);
                                }


                            }
                            if (childCode.equalsIgnoreCase("JustSaveReq")) {
                                mSession.setJustSave(childTitle);
                                mSession.setJustSaveDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    if (mSession.getSave().isEmpty()) {
                                        mActivity.displayViewOther(96, null);
                                    } else {
                                        mActivity.displayViewOther(97, null);
                                    }
                                }

                            }
                            if (childCode.equalsIgnoreCase("InvestInExistingSchemes")) {
                                mSession.setExistingScheme(childTitle);
                                mSession.setExistingSchemeDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {

                                    mActivity.displayViewOther(105, null);

                                }

                            }
                            if (childCode.equalsIgnoreCase("ServiceRequest")) {
                                mSession.setServiceReq(childTitle);
                                mSession.setServiceReqDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    Intent i = new Intent(mActivity, ServiceRequest.class);
                                    mContext.startActivity(i);
                                }

                            }
                            if (childCode.equalsIgnoreCase("SIPCalculator")) {

                                mActivity.displayFinancialCalculators(3, null);
                            }
                            if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                                mActivity.displayFinancialCalculators(5, null);
                            }
                            if (childCode.equalsIgnoreCase("EducationCalculator")) {

                                mActivity.displayFinancialCalculators(7, null);
                            }
                            if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                                mActivity.displayFinancialCalculators(8, null);
                            }
                            if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                                mActivity.displayFinancialCalculators(6, null);

                            }
                            if (childCode.equalsIgnoreCase("LumpsumCalculator")) {
                                mActivity.displayFinancialCalculators(10, null);

                            }
                            if (childCode.equalsIgnoreCase("STPCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_stpcalculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("SWPCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_swpcalculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("SIPTenureCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_siptenure_calculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("EMICalculator")) {
                                mActivity.displayFinancialCalculators(4, null);

                            }
                        }
                    }
                }
            });
        }
    }

    private void showDialog() {
        if (mActivity != null) {
            final Dialog dialog = new Dialog(mActivity);
            dialog.setContentView(R.layout.dailog_investnow);
            investwell.utils.customView.CustomButton rdybtn = dialog.findViewById(R.id.ready_btn);
            investwell.utils.customView.CustomButton notrdybtn = dialog.findViewById(R.id.notready_btn);
            TextView notes = dialog.findViewById(R.id.notes);
            notes.setText(R.string.notes);

            rdybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mSession.getUCC_CODE().isEmpty() && !mSession.getUCC_CODE().equalsIgnoreCase("NA")) {
                        mActivity.displayViewOther(52, null);

                    } else if (mSession.getHasLoging() && mSession.getUCC_CODE().isEmpty()) {

                        mActivity.displayViewOther(36, null);
                    } else {
                        if (mSession.getUCC_CODE().equalsIgnoreCase("NA") || mSession.getUCC_CODE().equalsIgnoreCase("")) {
                            mContext.startActivity(new Intent(mContext, SignUpActivity.class));
                        }
                    }

                    dialog.dismiss();
                }
            });

            notrdybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            dialog.setCancelable(false);
            dialog.show();
        }
    }

    /**********************SLIDER VIEWS******************/
    class SliderViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader;
        ImageView ivRouteIcon, ivHomeBg;
        LinearLayout llRoutes;

        SliderViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_home_element_header);
            ivRouteIcon = itemView.findViewById(R.id.iv_dashboard_route_icon);
            llRoutes = itemView.findViewById(R.id.ll_invest);
            ivHomeBg = itemView.findViewById(R.id.iv_home_bg);
        }


        /*****************Set Slider Views Data************************************/
        void setSliderViews(final JSONObject sliderViews) {
            tvHeader.setText(!TextUtils.isEmpty(sliderViews.optString("ChildTitle")) ? sliderViews.optString("ChildTitle") : "");

            String childType = "";
            String childCode = "";
            if (!TextUtils.isEmpty(sliderViews.optString("ChildType"))) {
                childType = sliderViews.optString("ChildType");
            }
            if (childType.equalsIgnoreCase("Module")) {
                if (!TextUtils.isEmpty(sliderViews.optString("ChildCode"))) {
                    childCode = sliderViews.optString("ChildCode");
                    if (childCode.equalsIgnoreCase("vkyc")) {


                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_kyc);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));

                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_kyc);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_kyc);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }

                    }
                    if (childCode.equalsIgnoreCase("FundPicks")) {


                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));

                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }

                    }
                    if (childCode.equalsIgnoreCase("InvestNow")) {


                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));

                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_invest);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }

                    }
                    if (childCode.equalsIgnoreCase("ServiceRequest")) {


                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_service_req);

                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_service_req);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_service_req);
                        }

                    }
                    if (childCode.equalsIgnoreCase("TopSchemes")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopSIPSchemes")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopRetirementFunds")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_retirement_funds);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_retirement_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_retirement_funds);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopChildrenFunds")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_children_funds);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_children_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_children_funds);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopGoldFunds")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_gold_funds);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_gold_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_gold_funds);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("TopELSSSchemes")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_elss_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }else {
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_elss_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("TopLiquidSchemes")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_liquid_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }else {
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_liquid_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("SearchAMC")) {

                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest_inependently);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_invest_inependently);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest_inependently);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }else{
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("RiskBasedBaskets")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_invest);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_simply_invest);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_invest);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }else{
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("CalculatorRequired")) {


                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_financial_tool);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_financial_tool);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_financial_tool);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }

                    if (childCode.equalsIgnoreCase("GoalModuleV2")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_goal);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_goal);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_goal);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("Accelator")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_expertise);

                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_expertise);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_expertise);
                        }
                    }

                    if (childCode.equalsIgnoreCase("NFO")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_nfo);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_nfo);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_nfo);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }

                    if (childCode.equalsIgnoreCase("UploadCAS")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_track_old);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_track_old);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_track_old);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("FlavourOfMonth")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_flavour);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_flavour);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_flavour);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("TransferHolding")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("SimplySave")) {

                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_save);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_simply_save);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_save);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("JustSaveReq")) {
                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_just_save);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_just_save);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_just_save);
                            llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                        }
                    }
                    if (childCode.equalsIgnoreCase("InvestInExistingSchemes")) {
                        if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                            if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_existing_schemes);

                            } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_existing_schemes);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_existing_schemes);
                        }
                    }
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
                        if (childCode.equalsIgnoreCase("SIPCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_1a);

                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("EducationCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education_1a);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_education_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education_1a);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }

                        }
                        if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement_1a);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_retirement_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement_1a);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("LumpsumCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }

                    } else {
                        if (childCode.equalsIgnoreCase("SIPCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("EducationCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_education);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }

                        }
                        if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_marriage);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_retirement);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("LumpsumCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_lumpsum);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("STPCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_stp);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_stp);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_stp);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("SWPCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_swp);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_swp);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_swp);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("EMICalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_emi);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_emi);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_emi);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPTenureCalculator")) {

                            if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                                if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_tenure);
                                    llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                                } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip_tenure);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_tenure);
                                llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                            }
                        }
                    }
                }
            }

            if (childType.equalsIgnoreCase("Custom")) {
                if (!TextUtils.isEmpty(sliderViews.optString("ImageFile"))) {
                    Picasso.get().load(sliderViews.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(ivRouteIcon);
                    Picasso.get().load(sliderViews.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(ivHomeBg);

                }
                if (!TextUtils.isEmpty(sliderViews.optString("ImageType"))) {
                    if (sliderViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                        ivRouteIcon.setVisibility(View.VISIBLE);
                        ivHomeBg.setVisibility(View.GONE);
                        llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));

                    } else if (sliderViews.optString("ImageType").equalsIgnoreCase("Background")) {
                        ivRouteIcon.setVisibility(View.GONE);
                        ivHomeBg.setVisibility(View.VISIBLE);
                        llRoutes.setBackground(null);
                    } else {
                        ivRouteIcon.setVisibility(View.VISIBLE);
                        ivHomeBg.setVisibility(View.GONE);
                        llRoutes.setBackground(mContext.getResources().getDrawable(R.drawable.bg_slider_view));
                    }
                }
            }

            setSliderListener(sliderViews);

        }

        /*********************Set Slider View Listener***************************/
        void setSliderListener(final JSONObject sliderViews) {
            llRoutes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String childType = "";
                    String childTitle = "";
                    if (!TextUtils.isEmpty(sliderViews.optString("ChildType"))) {
                        childType = sliderViews.optString("ChildType");
                    }
                    if (!TextUtils.isEmpty(sliderViews.optString("ChildTitle"))) {
                        childTitle = sliderViews.optString("ChildTitle");
                    }
                    String targetUrl = "";
                    String childCode = "";
                    String enquiry = "";
                    String description = "";
                    if (!TextUtils.isEmpty(sliderViews.optString("ChildDescription"))) {
                        description = sliderViews.optString("ChildDescription");
                    }
                    String imageFile = "";
                    /*************************Custom Links*********************************************/

                    if (childType.equalsIgnoreCase("Custom")) {
                        if (!TextUtils.isEmpty(sliderViews.optString("TargetURL"))) {
                            targetUrl = sliderViews.optString("TargetURL");

                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", targetUrl);
                            mContext.startActivity(intent);
                        }


                    }

                    if (childType.equalsIgnoreCase("Custom") && !TextUtils.isEmpty(sliderViews.optString("CustomType"))) {
                        enquiry = sliderViews.optString("CustomType");

                        if (enquiry.equalsIgnoreCase("Enquiry")) {
                            if (!TextUtils.isEmpty(sliderViews.optString("ImageFile"))) {
                                imageFile = sliderViews.optString("ImageFile");
                            }
                            if (!TextUtils.isEmpty(sliderViews.optString("ChildDescription"))) {
                                description = sliderViews.optString("ChildDescription");
                            }
                            Intent intent2 = new Intent(mContext, EnquiryItemActivity.class);
                            intent2.putExtra("title", childTitle);
                            intent2.putExtra("description", description);
                            intent2.putExtra("image", imageFile);
                            mContext.startActivity(intent2);
                        }
                    }
                    /************************Module Links**********************************************/

                    if (childType.equalsIgnoreCase("Module")) {
                        if (!TextUtils.isEmpty(sliderViews.optString("ChildCode"))) {
                            childCode = sliderViews.optString("ChildCode");
                            if (childCode.equalsIgnoreCase("vkyc")) {

                                mActivity.displayViewOther(104, null);
                            }
                            if (childCode.equalsIgnoreCase("FundPicks")) {
                                mSession.setFundPicks(childTitle);
                                mSession.setFundPicksDesc(description);

                                mActivity.displayViewOther(50, null);
                            }
                               if (childCode.equalsIgnoreCase("InvestNow")) {
                                mSession.setInvestNow(childTitle);
                                mSession.setInvestNowDesc(description);
                                mActivity.displayViewOther(75, null);
                            }
                            if ((childCode.equalsIgnoreCase("TopSchemes") || childCode.equalsIgnoreCase("TopELSSSchemes") || 
                                    childCode.equalsIgnoreCase("TopLiquidSchemes")||childCode.equalsIgnoreCase("TopRetirementFunds")||
                            childCode.equalsIgnoreCase("TopChildrenFunds")||childCode.equalsIgnoreCase("TopGoldFunds"))) {
                                Bundle bundle = new Bundle();

                                if (childCode.equalsIgnoreCase("TopELSSSchemes")) {
                                    topSchemeType = "elss";
                                } else if (childCode.equalsIgnoreCase("TopLiquidSchemes")) {
                                    topSchemeType = "liquid";

                                }else if (childCode.equalsIgnoreCase("TopRetirementFunds")) {
                                    topSchemeType = "Ret";

                                }else if (childCode.equalsIgnoreCase("TopChildrenFunds")) {
                                    topSchemeType = "Child";

                                }else if (childCode.equalsIgnoreCase("TopGoldFunds")) {
                                    topSchemeType = "G";

                                } else {
                                    topSchemeType = "All";

                                }
                                mSession.setTopPerformer(childTitle);
                                mSession.setTopPerformerDesc(description);
                                bundle.putString("type",topSchemeType);
                                mActivity.displayViewOther(41, bundle);
                            }
                            if (childCode.equalsIgnoreCase("SearchAMC")) {
                                mSession.setAmc(childTitle);
                                mSession.setAmcDesc(description);

                                mActivity.displayViewOther(66, null);
                            }
                            if (childCode.equalsIgnoreCase("RiskBasedBaskets")) {

                                if (TextUtils.isEmpty(mSession.getRiskCode())||mSession.getRiskCode().equals("NA")) {
                                    Bundle  bundle = new Bundle();
                                    bundle.putString("Coming_from", "indirect");
                                    mActivity.displayViewOther(61, bundle);
                                } else {

                                    getSchemes();
                                }
                            }
                            if (childCode.equalsIgnoreCase("CalculatorRequired")) {

                                mActivity.displayViewOther(58, null);
                            }

                            if (childCode.equalsIgnoreCase("GoalModuleV2")) {
mSession.setGoal(childTitle);
mSession.setGoalDesc(description);
                                mActivity.displayViewOther(70, null);
                            }
                            if (childCode.equalsIgnoreCase("Accelator")) {
                                mSession.setAccelerator(childTitle);
                                mSession.setAcceleratorDesc(description);
                                mActivity.displayViewOther(1, null);
                            }

                            if (childCode.equalsIgnoreCase("NFO")) {
                                mSession.setNfo(childTitle);
                                mSession.setNfoDesc(description);
                                mActivity.displayViewOther(90, null);
                            }

                            if (childCode.equalsIgnoreCase("UploadCAS")) {
                                mSession.setCas(childTitle);
                                mSession.setCasDesc(description);
                                mActivity.displayViewOther(101, null);
                            }
                            if (childCode.equalsIgnoreCase("FlavourOfMonth")) {
                                mSession.setFom(childTitle);
                                mSession.setFomDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    mActivity.displayViewOther(74, null);
                                }

                            }
                            if (childCode.equalsIgnoreCase("TransferHolding")) {
                                mSession.setTHolding(childTitle);
                                mSession.setTHoldingDesc(description);

                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    if (mSession.getUCC_CODE().equalsIgnoreCase("NA") || mSession.getUCC_CODE().equalsIgnoreCase("")) {
                                        showDialog();
                                    } else {
                                        mActivity.displayViewOther(52, null);
                                    }
                                }


                            }
                            if (childCode.equalsIgnoreCase("SimplySave")) {
                                mSession.setSimplySave(childTitle);
                                mSession.setSimplySaveDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    mActivity.displayViewOther(79, null);
                                }


                            }
                            if (childCode.equalsIgnoreCase("JustSaveReq")) {
                                mSession.setJustSave(childTitle);
                                mSession.setJustSaveDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    if (mSession.getSave().isEmpty()) {
                                        mActivity.displayViewOther(96, null);
                                    } else {
                                        mActivity.displayViewOther(97, null);
                                    }
                                }

                            }
                            if (childCode.equalsIgnoreCase("InvestInExistingSchemes")) {
                                mSession.setExistingScheme(childTitle);
                                mSession.setExistingSchemeDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {

                                    mActivity.displayViewOther(105, null);

                                }

                            }
                            if (childCode.equalsIgnoreCase("ServiceRequest")) {
                                mSession.setServiceReq(childTitle);
                                mSession.setServiceReqDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    Intent i = new Intent(mActivity, ServiceRequest.class);
                                    mContext.startActivity(i);
                                }

                            }
                            if (childCode.equalsIgnoreCase("SIPCalculator")) {

                                mActivity.displayFinancialCalculators(3, null);
                            }
                            if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                                mActivity.displayFinancialCalculators(5, null);
                            }
                            if (childCode.equalsIgnoreCase("EducationCalculator")) {

                                mActivity.displayFinancialCalculators(7, null);
                            }
                            if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                                mActivity.displayFinancialCalculators(8, null);
                            }
                            if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                                mActivity.displayFinancialCalculators(6, null);

                            }
                            if (childCode.equalsIgnoreCase("LumpsumCalculator")) {
                                mActivity.displayFinancialCalculators(10, null);

                            }
                            if (childCode.equalsIgnoreCase("STPCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_stpcalculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("SWPCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_swpcalculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("SIPTenureCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_siptenure_calculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("EMICalculator")) {
                                mActivity.displayFinancialCalculators(4, null);

                            }
                        }
                    }
                }
            });
        }
    }

    /****************GRID VIEWS*********************************/
    class GridViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llRoutes;
        TextView tvHeader;
        ImageView ivRouteIcon, ivHomeBg;

        GridViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_route);
            ivRouteIcon = itemView.findViewById(R.id.iv_dashboard_route_icon);
            ivHomeBg = itemView.findViewById(R.id.iv_home_bg);
            llRoutes = itemView.findViewById(R.id.ll_route_container);
        }


        /*****************Set Grid Views Data************************************/
        void setGridViews(final JSONObject gridViews) {
            tvHeader.setText(!TextUtils.isEmpty(gridViews.optString("ChildTitle")) ? gridViews.optString("ChildTitle") : "");

            String childType = "";
            String childCode = "";
            if (!TextUtils.isEmpty(gridViews.optString("ChildType"))) {
                childType = gridViews.optString("ChildType");
            }
            if (childType.equalsIgnoreCase("Module")) {
                if (!TextUtils.isEmpty(gridViews.optString("ChildCode"))) {
                    childCode = gridViews.optString("ChildCode");
                    if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                        if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                                tvHeader.setTextColor(ContextCompat.getColor(mContext, R.color.darkPrimaryTextColor));
                            }else{
                                tvHeader.setTextColor(ContextCompat.getColor(mContext, R.color.lightPrimaryTextColor));
                            }
                        } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                            tvHeader.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                        }
                    }
                    if (childCode.equalsIgnoreCase("vkyc")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                        }
                    }
                    if (childCode.equalsIgnoreCase("FundPicks")) {


                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("InvestNow")) {


                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_invest);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("ServiceRequest")) {


                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_service_req);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_service_req);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_service_req);
                        }

                    }
                    if (childCode.equalsIgnoreCase("TopSchemes")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }if (childCode.equalsIgnoreCase("TopSIPSchemes")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    } if (childCode.equalsIgnoreCase("TopRetirementFunds")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_retirement_funds);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_retirement_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }if (childCode.equalsIgnoreCase("TopChildrenFunds")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_children_funds);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_children_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }if (childCode.equalsIgnoreCase("TopGoldFunds")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_gold_funds);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_gold_funds);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }
                    
                    if (childCode.equalsIgnoreCase("TopELSSSchemes")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_elss_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);

                            }
                        }else {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_elss_funds);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_elss_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);

                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("TopLiquidSchemes")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_liquid_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);

                            }
                        }else{
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_liquid_funds);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_liquid_funds);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);

                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("SearchAMC")) {

                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest_inependently);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_invest_inependently);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }else{
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("RiskBasedBaskets")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_invest);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_simply_invest);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }else {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("CalculatorRequired")) {


                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_financial_tool);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_financial_tool);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }

                    if (childCode.equalsIgnoreCase("GoalModuleV2")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_goal);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_goal);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (childCode.equalsIgnoreCase("Accelator")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_expertise);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_expertise);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }

                    if (childCode.equalsIgnoreCase("NFO")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_nfo);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_nfo);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }

                    if (childCode.equalsIgnoreCase("UploadCAS")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_track_old);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_track_old);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (childCode.equalsIgnoreCase("FlavourOfMonth")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_flavour);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_flavour);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (childCode.equalsIgnoreCase("TransferHolding")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_transfer_holding);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (childCode.equalsIgnoreCase("SimplySave")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_save);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_simply_save);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (childCode.equalsIgnoreCase("JustSaveReq")) {
                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_just_save);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_just_save);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (childCode.equalsIgnoreCase("InvestInExistingSchemes")) {
                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_existing_schemes);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_existing_schemes);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_existing_schemes);
                        }
                    }
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
                        if (childCode.equalsIgnoreCase("SIPCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("EducationCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_education_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }

                        }
                        if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_retirement_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("LumpsumCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }

                    } else {
                        if (childCode.equalsIgnoreCase("SIPCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("EducationCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_education);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }

                        }
                        if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_marriage);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_retirement);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                           if (childCode.equalsIgnoreCase("LumpsumCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_lumpsum);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("STPCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_stp);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_stp);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("SWPCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_swp);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_swp);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("EMICalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_emi);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_emi);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPTenureCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_tenure);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip_tenure);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            }

            if (childType.equalsIgnoreCase("Custom")) {
                if (childType.equalsIgnoreCase("Custom")) {
                    if (!TextUtils.isEmpty(gridViews.optString("ImageFile"))) {
                        Picasso.get().load(gridViews.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(ivRouteIcon);
                        Picasso.get().load(gridViews.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(ivHomeBg);

                    }
                    if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                        if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);

                        } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                            ivRouteIcon.setVisibility(View.GONE);
                            ivHomeBg.setVisibility(View.VISIBLE);
                            llRoutes.setBackground(null);
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                        }
                    }
                }

            }
            if (childType.equalsIgnoreCase("Module")) {
                if (!TextUtils.isEmpty(gridViews.optString("ChildCode"))) {
                    childCode = gridViews.optString("ChildCode");
                    if (childCode.equalsIgnoreCase("FundPicks")) {


                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                        }

                    }

                    if (childCode.equalsIgnoreCase("TopSchemes")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_top_performer);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                        }
                    }
                    if (childCode.equalsIgnoreCase("SearchAMC")) {
                        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest_inependently);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_invest_inependently);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_invest_inependently);
                            }
                        }else {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_invest_route_amc);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);
                            }
                        }

                    }
                    if (childCode.equalsIgnoreCase("CalculatorRequired")) {


                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_financial_tool);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_financial_tool);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_financial_tool);
                        }
                    }

                    if (childCode.equalsIgnoreCase("GoalModuleV2")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_goal);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_goal);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_goal);
                        }
                    }
                    if (childCode.equalsIgnoreCase("Accelator")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_expertise);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_expertise);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_expertise);
                        }
                    }

                    if (childCode.equalsIgnoreCase("NFO")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_nfo);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_nfo);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_nfo);
                        }
                    }

                    if (childCode.equalsIgnoreCase("UploadCAS")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_track_old);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_track_old);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_track_old);
                        }
                    }
                    if (childCode.equalsIgnoreCase("FlavourOfMonth")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_flavour);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_flavour);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_flavour);

                        }
                    }
                    if (childCode.equalsIgnoreCase("TransferHolding")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_transfer_holding);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                        }
                    }
                    if (childCode.equalsIgnoreCase("SimplySave")) {

                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_save);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_simply_save);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_save);
                        }
                    }
                    if (childCode.equalsIgnoreCase("JustSaveReq")) {
                        if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                            if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_just_save);

                            } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                ivRouteIcon.setVisibility(View.GONE);
                                ivHomeBg.setVisibility(View.VISIBLE);
                                ivHomeBg.setImageResource(R.mipmap.ic_invest_route_just_save);
                                llRoutes.setBackground(null);
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                            }
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                            ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_just_save);
                        }
                    }
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
                        if (childCode.equalsIgnoreCase("SIPCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("EducationCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_education_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education_1a);
                            }

                        }
                        if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_retirement_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement_1a);
                            }
                        }
                        if (childCode.equalsIgnoreCase("LumpsumCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);
                            }
                        }

                    } else {
                        if (childCode.equalsIgnoreCase("SIPCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_sip);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip);
                            }
                        }
                        if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                            }
                        }
                        if (childCode.equalsIgnoreCase("EducationCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_education);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education);
                            }

                        }
                        if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_marriage);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage);
                            }
                        }
                        if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_retirement);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);
                            }
                        }
                        if (childCode.equalsIgnoreCase("LumpsumCalculator")) {

                            if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                                if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                    ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum);

                                } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                                    ivRouteIcon.setVisibility(View.GONE);
                                    ivHomeBg.setVisibility(View.VISIBLE);
                                    ivHomeBg.setImageResource(R.mipmap.ic_calculator_lumpsum);
                                    llRoutes.setBackground(null);
                                } else {
                                    ivRouteIcon.setVisibility(View.VISIBLE);
                                    ivHomeBg.setVisibility(View.GONE);
                                }
                            } else {
                                ivRouteIcon.setVisibility(View.VISIBLE);
                                ivHomeBg.setVisibility(View.GONE);
                                ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);
                            }
                        }
                    }
                }
            }
            if (childType.equalsIgnoreCase("Custom")) {
                if (childType.equalsIgnoreCase("Custom")) {
                    if (!TextUtils.isEmpty(gridViews.optString("ImageFile"))) {
                        Picasso.get().load(gridViews.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(ivRouteIcon);
                        Picasso.get().load(gridViews.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(ivHomeBg);

                    }
                    if (!TextUtils.isEmpty(gridViews.optString("ImageType"))) {
                        if (gridViews.optString("ImageType").equalsIgnoreCase("Icon")) {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);

                        } else if (gridViews.optString("ImageType").equalsIgnoreCase("Background")) {
                            ivRouteIcon.setVisibility(View.GONE);
                            ivHomeBg.setVisibility(View.VISIBLE);
                            llRoutes.setBackground(null);
                        } else {
                            ivRouteIcon.setVisibility(View.VISIBLE);
                            ivHomeBg.setVisibility(View.GONE);
                        }
                    } else {
                        ivRouteIcon.setVisibility(View.VISIBLE);
                        ivHomeBg.setVisibility(View.GONE);
                    }
                }

            }
            setGridListener(gridViews);

        }

        /*********************Set Grid View Listener***************************/
        void setGridListener(final JSONObject gridViews) {
            llRoutes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String childType = "";
                    String childTitle = "";
                    if (!TextUtils.isEmpty(gridViews.optString("ChildType"))) {
                        childType = gridViews.optString("ChildType");
                    }
                    if (!TextUtils.isEmpty(gridViews.optString("ChildTitle"))) {
                        childTitle = gridViews.optString("ChildTitle");
                    }
                    String targetUrl = "";
                    String childCode = "";
                    String enquiry = "";
                    String description = "";
                    if (!TextUtils.isEmpty(gridViews.optString("ChildDescription"))) {
                        description = gridViews.optString("ChildDescription");
                    }
                    String imageFile = "";
                    /*************************Custom Links*********************************************/

                    if (childType.equalsIgnoreCase("Custom")) {
                        if (!TextUtils.isEmpty(gridViews.optString("TargetURL"))) {
                            targetUrl = gridViews.optString("TargetURL");

                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", targetUrl);
                            mContext.startActivity(intent);
                        }


                    }

                    if (childType.equalsIgnoreCase("Custom") && !TextUtils.isEmpty(gridViews.optString("CustomType"))) {
                        enquiry = gridViews.optString("CustomType");

                        if (enquiry.equalsIgnoreCase("Enquiry")) {
                            if (!TextUtils.isEmpty(gridViews.optString("ImageFile"))) {
                                imageFile = gridViews.optString("ImageFile");
                            }
                            if (!TextUtils.isEmpty(gridViews.optString("ChildDescription"))) {
                                description = gridViews.optString("ChildDescription");
                            }
                            Intent intent2 = new Intent(mContext, EnquiryItemActivity.class);
                            intent2.putExtra("title", childTitle);
                            intent2.putExtra("description", description);
                            intent2.putExtra("image", imageFile);
                            mContext.startActivity(intent2);
                        }
                    }
                    /************************Module Links**********************************************/

                    if (childType.equalsIgnoreCase("Module")) {
                        if (!TextUtils.isEmpty(gridViews.optString("ChildCode"))) {
                            childCode = gridViews.optString("ChildCode");
                            if (childCode.equalsIgnoreCase("vkyc")) {

                                mActivity.displayViewOther(104, null);
                            }
                            if (childCode.equalsIgnoreCase("FundPicks")) {
                                mSession.setFundPicks(childTitle);
                                mSession.setFundPicksDesc(description);

                                mActivity.displayViewOther(50, null);
                            }
                               if (childCode.equalsIgnoreCase("InvestNow")) {
                                mSession.setInvestNow(childTitle);
                                mSession.setInvestNowDesc(description);
                                mActivity.displayViewOther(75, null);
                            }
                            if ((childCode.equalsIgnoreCase("TopSchemes") || childCode.equalsIgnoreCase("TopELSSSchemes") || 
                                    childCode.equalsIgnoreCase("TopLiquidSchemes")||childCode.equalsIgnoreCase("TopRetirementFunds")||
                            childCode.equalsIgnoreCase("TopChildrenFunds")||childCode.equalsIgnoreCase("TopGoldFunds"))) {
                                Bundle bundle = new Bundle();

                                if (childCode.equalsIgnoreCase("TopELSSSchemes")) {
                                    topSchemeType = "elss";
                                } else if (childCode.equalsIgnoreCase("TopLiquidSchemes")) {
                                    topSchemeType = "liquid";

                                }else if (childCode.equalsIgnoreCase("TopRetirementFunds")) {
                                    topSchemeType = "Ret";

                                }else if (childCode.equalsIgnoreCase("TopChildrenFunds")) {
                                    topSchemeType = "Child";

                                }else if (childCode.equalsIgnoreCase("TopGoldFunds")) {
                                    topSchemeType = "G";

                                } else {
                                    topSchemeType = "All";

                                }
                                mSession.setTopPerformer(childTitle);
                                mSession.setTopPerformerDesc(description);
                                bundle.putString("type",topSchemeType);
                                mActivity.displayViewOther(41, bundle);
                            }
                            if (childCode.equalsIgnoreCase("SearchAMC")) {
                                mSession.setAmc(childTitle);
                                mSession.setAmcDesc(description);

                                mActivity.displayViewOther(66, null);
                            }
                            if (childCode.equalsIgnoreCase("RiskBasedBaskets")) {

                                if (TextUtils.isEmpty(mSession.getRiskCode())||mSession.getRiskCode().equals("NA")) {
                                    Bundle  bundle = new Bundle();
                                    bundle.putString("Coming_from", "indirect");
                                    mActivity.displayViewOther(61, bundle);
                                } else {

                                    getSchemes();
                                }
                            }
                            if (childCode.equalsIgnoreCase("CalculatorRequired")) {

                                mActivity.displayViewOther(58, null);
                            }

                            if (childCode.equalsIgnoreCase("GoalModuleV2")) {
mSession.setGoal(childTitle);
mSession.setGoalDesc(description);
                                mActivity.displayViewOther(70, null);
                            }
                            if (childCode.equalsIgnoreCase("Accelator")) {
                                mSession.setAccelerator(childTitle);
                                mSession.setAcceleratorDesc(description);
                                mActivity.displayViewOther(1, null);
                            }

                            if (childCode.equalsIgnoreCase("NFO")) {
                                mSession.setNfo(childTitle);
                                mSession.setNfoDesc(description);
                                mActivity.displayViewOther(90, null);
                            }

                            if (childCode.equalsIgnoreCase("UploadCAS")) {
                                mSession.setCas(childTitle);
                                mSession.setCasDesc(description);
                                mActivity.displayViewOther(101, null);
                            }
                            if (childCode.equalsIgnoreCase("FlavourOfMonth")) {
                                mSession.setFom(childTitle);
                                mSession.setFomDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    mActivity.displayViewOther(74, null);
                                }

                            }
                            if (childCode.equalsIgnoreCase("TransferHolding")) {
                                mSession.setTHolding(childTitle);
                                mSession.setTHoldingDesc(description);

                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    if (mSession.getUCC_CODE().equalsIgnoreCase("NA") || mSession.getUCC_CODE().equalsIgnoreCase("")) {
                                        showDialog();
                                    } else {
                                        mActivity.displayViewOther(52, null);
                                    }
                                }


                            }
                            if (childCode.equalsIgnoreCase("SimplySave")) {
                                mSession.setSimplySave(childTitle);
                                mSession.setSimplySaveDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    mActivity.displayViewOther(79, null);
                                }


                            }
                            if (childCode.equalsIgnoreCase("JustSaveReq")) {
                                mSession.setJustSave(childTitle);
                                mSession.setJustSaveDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    if (mSession.getSave().isEmpty()) {
                                        mActivity.displayViewOther(96, null);
                                    } else {
                                        mActivity.displayViewOther(97, null);
                                    }
                                }

                            }
                            if (childCode.equalsIgnoreCase("InvestInExistingSchemes")) {
                                mSession.setExistingScheme(childTitle);
                                mSession.setExistingSchemeDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {

                                    mActivity.displayViewOther(105, null);

                                }

                            }
                            if (childCode.equalsIgnoreCase("ServiceRequest")) {
                                mSession.setServiceReq(childTitle);
                                mSession.setServiceReqDesc(description);
                                if (!mSession.getHasLoging()) {
                                    mActivity.showCommonDialog(mContext, "Message", "Only Registered User can use this feature. Please Signup or Login.");
                                } else {
                                    Intent i = new Intent(mActivity, ServiceRequest.class);
                                    mContext.startActivity(i);
                                }

                            }
                            if (childCode.equalsIgnoreCase("SIPCalculator")) {

                                mActivity.displayFinancialCalculators(3, null);
                            }
                            if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                                mActivity.displayFinancialCalculators(5, null);
                            }
                            if (childCode.equalsIgnoreCase("EducationCalculator")) {

                                mActivity.displayFinancialCalculators(7, null);
                            }
                            if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                                mActivity.displayFinancialCalculators(8, null);
                            }
                            if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                                mActivity.displayFinancialCalculators(6, null);

                            }
                            if (childCode.equalsIgnoreCase("LumpsumCalculator")) {
                                mActivity.displayFinancialCalculators(10, null);

                            }
                            if (childCode.equalsIgnoreCase("STPCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_stpcalculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("SWPCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_swpcalculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("SIPTenureCalculator")) {
                                 Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("title", childTitle);
                            intent.putExtra("url", "https://m.investwell.in/parameter/calculator/par_siptenure_calculatorM.jsp?bid=10003");
                            mContext.startActivity(intent);

                            }if (childCode.equalsIgnoreCase("EMICalculator")) {
                                mActivity.displayFinancialCalculators(4, null);

                            }
                        }
                    }
                }
            });
        }
    }
    private void getSchemes() {
        final ProgressDialog mBar = ProgressDialog.show(mContext, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        String url = Config.GET_BASKET;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    mBar.dismiss();
                    try {

                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("BasketList");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);

                                if (object.optString("BasketName").equalsIgnoreCase(mSession.getRiskName())) {
                                    mSession.setSelectedBasketCode(object.optString("BasketCode"));
                                    Bundle bundle = new Bundle();
                                    bundle.putString("matchData", object.toString());
                                    mActivity.displayViewOther(2, bundle);
                                }

                            }


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mBar.dismiss();
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(mActivity, mContext.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
