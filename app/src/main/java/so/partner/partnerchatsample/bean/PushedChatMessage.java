package so.partner.partnerchatsample.bean;

public class PushedChatMessage {

	public Text text;
	public String date;

	public class Text {
		public String talkId;
		public String roomId;
		public String who;
		public String text;
	}
}
