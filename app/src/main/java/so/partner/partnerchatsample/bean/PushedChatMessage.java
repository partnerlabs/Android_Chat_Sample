package so.partner.partnerchatsample.bean;

import com.google.gson.annotations.SerializedName;

public class PushedChatMessage {

    @SerializedName("sId")
    public String sId;
    @SerializedName(("text"))
    public String text;
    @SerializedName("date")
    public long date;
}
