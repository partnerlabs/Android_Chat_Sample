package so.partner.partnerchatsample.bean;

import java.io.Serializable;

public class ChatMessage implements Serializable {

	public String id;
	public String roomId;
	public String userId;
	public String content;
	public long sendTime;
}
