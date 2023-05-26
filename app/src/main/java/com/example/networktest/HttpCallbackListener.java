package com.example.networktest;

/**
 * 另外需要注意的是，onFinish()方法和 onError()方法最终还是在子线程中运行的，
 * 因此 我们不可以在这里执行任何的 UI 操作，如果需要根据返回的结果来更新 UI，则仍然要使用runOnUiThread。
 */
public interface HttpCallbackListener {

    /**
     * 请求响应成功回调信息
     *
     * @param response
     */
    void onFinish(String response);

    /**
     * 请求响应失败回调信息
     *
     * @param e
     */
    void onError(Exception e);

}
