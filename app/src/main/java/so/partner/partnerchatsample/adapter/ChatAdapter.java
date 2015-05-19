package so.partner.partnerchatsample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import so.partner.partnerchatsample.ChatManager;
import so.partner.partnerchatsample.R;
import so.partner.partnerchatsample.bean.ChatMessage;

public class ChatAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<ChatMessage> mTalkList;
    private final LayoutInflater mInflater;
    private final SimpleDateFormat mYearMonthDateFormat = new SimpleDateFormat("yyyy년 M월 d일");
    private final SimpleDateFormat mHourMinuteFormat = new SimpleDateFormat("a h:mm");

    public ChatAdapter(Context context, List<ChatMessage> talkList) {
        mContext = context;
        mTalkList = talkList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public boolean add(ChatMessage item) {
        return mTalkList.add(item);
    }

    @Override
    public int getCount() {
        return mTalkList.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return mTalkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.i_chat, null);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ChatMessage item = getItem(position);

        if (null != item) {
            String date = mYearMonthDateFormat.format(item.date);
            if (position == 0
                    || !date.equals(mYearMonthDateFormat.format(mTalkList.get(position - 1).date))) {
                holder.tvDateLabel.setVisibility(View.VISIBLE);
                holder.tvDateLabel.setText(date);
            } else {
                holder.tvDateLabel.setVisibility(View.GONE);
            }

            if (ChatManager.getClientId().equals(item.userId)) {
                holder.rl_other.setVisibility(View.GONE);
                holder.rl_me.setVisibility(View.VISIBLE);

                holder.tv_user_id_me.setText(item.userId);
                holder.tv_content_me.setText(item.content);
                holder.tv_time_me.setText(mHourMinuteFormat.format(item.date));
            } else {
                holder.rl_other.setVisibility(View.VISIBLE);
                holder.rl_me.setVisibility(View.GONE);

                holder.tv_user_id_other.setText(item.userId);
                holder.tv_content_other.setText(item.content);
                holder.tv_time_other.setText(mHourMinuteFormat.format(item.date));
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView tvDateLabel;

        RelativeLayout rl_other;
        TextView tv_user_id_other;
        TextView tv_content_other;
        TextView tv_time_other;

        RelativeLayout rl_me;
        TextView tv_user_id_me;
        TextView tv_content_me;
        TextView tv_time_me;

        ViewHolder(View v) {
            tvDateLabel = (TextView) v.findViewById(R.id.i_chat_tv_date_label);

            rl_other = (RelativeLayout) v.findViewById(R.id.i_chat_rl_other);
            tv_user_id_other = (TextView) v.findViewById(R.id.i_chat_tv_user_id_other);
            tv_content_other = (TextView) v.findViewById(R.id.i_chat_tv_content_other);
            tv_time_other = (TextView) v.findViewById(R.id.i_chat_tv_time_other);

            rl_me = (RelativeLayout) v.findViewById(R.id.i_chat_rl_me);
            tv_user_id_me = (TextView) v.findViewById(R.id.i_chat_tv_user_id_me);
            tv_content_me = (TextView) v.findViewById(R.id.i_chat_tv_content_me);
            tv_time_me = (TextView) v.findViewById(R.id.i_chat_tv_time_me);
        }
    }
}
