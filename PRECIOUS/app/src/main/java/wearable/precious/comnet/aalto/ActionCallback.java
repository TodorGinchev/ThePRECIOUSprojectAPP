package wearable.precious.comnet.aalto;

public interface ActionCallback {
    public void onSuccess(Object data);

    public void onFail(int errorCode, String msg);
}
