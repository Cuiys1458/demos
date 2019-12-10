package com.example.demo.Util;

import com.sun.jna.Pointer;
import main.java.com.netsdk.lib.NetSDKLib;

import javax.swing.*;

public class StaticLogin {
    public static DisConnect disConnect = new DisConnect();

    // 网络连接恢复
    public static HaveReConnect haveReConnect = new HaveReConnect();

    /////////////////面板///////////////////
    // 设备断线回调: 通过 CLIENT_Init 设置该回调函数，当设备出现断线时，SDK会调用该函数
    private static class DisConnect implements NetSDKLib.fDisConnect {

        @Override
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("Device[%s] Port[%d] DisConnect!\n", pchDVRIP, nDVRPort);
            // 断线提示
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println(main.java.com.netsdk.common.Res.string().getPTZ() + " : " + main.java.com.netsdk.common.Res.string().getDisConnectReconnecting());
                }
            });
        }
    }

    // 网络连接恢复，设备重连成功回调
    // 通过 CLIENT_SetAutoReconnect 设置该回调函数，当已断线的设备重连成功时，SDK会调用该函数
    private static class HaveReConnect implements NetSDKLib.fHaveReConnect {
        @Override
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("ReConnect Device[%s] Port[%d]\n", pchDVRIP, nDVRPort);

            // 重连提示
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println(main.java.com.netsdk.common.Res.string().getPTZ() + " : " + main.java.com.netsdk.common.Res.string().getOnline());
                }
            });
        }
    }

}
