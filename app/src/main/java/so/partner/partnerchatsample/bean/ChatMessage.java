package so.partner.partnerchatsample.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatMessage implements Serializable {

	public boolean isMine;
	@SerializedName("nickname")
	public String nickname;
	@SerializedName("text")
	public String text;
	public long date;
}
