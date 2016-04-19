package com.jimbo.myapplication.view;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 16:44
 */
public interface IConnectToNetView {

    void success();
    void trying();
    void failed();
    void isConnectedNet(boolean is);
    void isCheckingNet();

    void update(String versionName, String versionDescription);
}
