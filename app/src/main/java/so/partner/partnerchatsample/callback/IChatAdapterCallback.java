package so.partner.partnerchatsample.callback;

public interface IChatAdapterCallback {

	public void removeTalk(String id);

	public void removeWaittingTalk(int id);

	public void reSend(int id, String message);
}
