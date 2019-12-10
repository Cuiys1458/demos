package com.example.demo.controller;


import com.example.demo.Util.StaticLogin;
import com.example.demo.Util.Tools;
import com.example.demo.Util.Util;
import com.sun.jna.Pointer;
import io.swagger.annotations.*;
import main.java.com.netsdk.demo.module.LoginModule;
import main.java.com.netsdk.demo.module.PtzControlModule;
import main.java.com.netsdk.lib.NetSDKLib;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Controller
@Api(value = "摄像头控制")
public class RemoteCameraRController {

    private static List d = new ArrayList();

    /**
     * @param switchs    开关  方向 open  开始  close 关闭   变倍 open 变大 close变小
     * @param iszhengshu iszhengshu
     * @param userName   登录名
     * @param password   密码
     * @param ip         ip地址
     * @param degree     degree
     * @param cameraType cameraType   dahua 大华摄像      mingjing  明景   其他 ...  待添加
     * @param port       登陆所需端口号
     * @param direction  方向
     * @param features   功能   // xuanZhuan,bianBei,tiaoJiao,guangQuan    目前只有大华摄像有   全部功能   明景只有旋转
     * @param speed      速度  目前大华摄像头有速度
     * @return 0 存在重复ip(说明方法没执行完)   -5 没有摄像机类别   -1 当前角度没有获取到 -8 没有改方向 -10 没有该功能 -12 缺少必要参数
     */
    @RequestMapping(value = "/RemoteRotation", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "摄像头轮盘控制")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "switchs", value = "开关  方向 open  开始  close 关闭   变倍 open 变大 close变小", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userName", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "密码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ip", value = "ip地址", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cameraType", value = "dahua 大华摄像      mingjing  明景   其他 ...  待添加", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "port", value = "端口号", required = false, defaultValue = "0", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "direction", value = "方向", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "features", value = "功能", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "speed", value = "速度", required = true, dataType = "Integer")
    })
    public String cameraRotation(String switchs, HttpServletResponse response,
                                 String iszhengshu,
                                 String userName, String password, String ip,
                                 String degree, String cameraType,
                                 @RequestParam(required = false, defaultValue = "0") int port,
                                 String direction,
                                 String features,
                                 int speed) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        ReentrantLock lock = new ReentrantLock();
        int deg = 0;
        String res = "";
        String ips = Tools.getValue("ips");
        String cameraTypes = Tools.getValue("cameraType");
        String directions = Tools.getValue("direction");
        String featureses = Tools.getValue("features");
        if (StringUtils.isEmpty(switchs)) {
            return "-12";
        }
        if (!StringUtils.isEmpty(features)) {
            if (!featureses.contains(features)) {
                //没有该功能
                res = "-10";
                return res;
            }
        }
        if (!StringUtils.isEmpty(features)) {
            if (!directions.contains(direction)) {
                //没有该方向
                res = "-8";
                return res;
            }
        }
        if (!cameraTypes.contains(cameraType)) {
            //没有该摄像头类别
            res = "-5";
            return res;
        }
        if (degree != null && degree != "") {
            deg = Integer.parseInt(degree);
        }
        lock.lock();
        try {
            System.out.println("开始");

            // 当前是否存在重复ip
            if (d.contains(ip)) {
                res = "0";
                return res;
            } else {
                d.add(ip);
                //不区分大小写
                if ("dahua".equalsIgnoreCase(cameraType)) {
                    if ("open".equalsIgnoreCase(switchs)) {
                        System.out.println("大华摄像机登陆");
                        //初始化登陆所需参数
                        LoginModule.init(StaticLogin.disConnect, StaticLogin.haveReConnect);
                        //登陆
                        LoginModule.login(ip, port, userName, new String(password));
                    }
                    if ("xuanZhuan".equalsIgnoreCase(features)) {
                        if ("open".equalsIgnoreCase(switchs)) {
                            if ("right".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlRightStart(0, 0, speed);
                            } else if ("left".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlLeftStart(0, 0, speed);
                            } else if ("up".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlUpStart(0, 0, speed);
                            } else if ("down".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlDownStart(0, 0, speed);
                            } else if ("topLeft".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlLeftUpStart(0, speed, speed);
                            } else if ("bottomLeft".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlLeftDownStart(0, speed, speed);
                            } else if ("upperRight".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlRightUpStart(0, speed, speed);
                            } else if ("bottomRight".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlRightDownStart(0, speed, speed);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else if ("close".equalsIgnoreCase(switchs)) {
                            if ("right".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlRightEnd(0);
                            } else if ("left".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlLeftEnd(0);
                            } else if ("up".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlUpEnd(0);
                            } else if ("down".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlDownEnd(0);
                            } else if ("topLeft".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlLeftUpEnd(0);
                            } else if ("bottomLeft".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlLeftDownEnd(0);
                            } else if ("upperRight".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlRightUpEnd(0);
                            } else if ("bottomRight".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlRightDownEnd(0);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else {
                            return "-12";
                        }

                    } else if ("tiaoJiao".equalsIgnoreCase(features)) {
                        if ("open".equalsIgnoreCase(switchs)) {
                            if ("focusAdd".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlFocusAddStart(0, speed);
                            } else if ("focusDec".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlFocusDecStart(0, speed);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else if ("close".equalsIgnoreCase(switchs)) {
                            if ("focusAdd".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlFocusAddEnd(0);
                            } else if ("focusDec".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlFocusDecEnd(0);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else {
                            return "-12";
                        }


                    } else if ("guangQuan".equalsIgnoreCase(features)) {
                        if ("open".equalsIgnoreCase(switchs)) {
                            if ("irisAdd".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlIrisAddStart(0, speed);
                            } else if ("irisDec".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlIrisDecStart(0, speed);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else if ("close".equalsIgnoreCase(switchs)) {
                            if ("irisAdd".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlIrisAddEnd(0);
                            } else if ("irisDec".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlIrisDecEnd(0);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else {
                            return "-12";
                        }
                    } else if ("bianBei".equalsIgnoreCase(features)) {
                        if ("open".equalsIgnoreCase(switchs)) {
                            if ("ZoomAdd".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlZoomAddStart(0, speed);
                            } else if ("ZoomDec".equalsIgnoreCase(direction)) {
                                System.out.println("变倍-");
                                PtzControlModule.ptzControlZoomDecStart(0, speed);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else if ("close".equalsIgnoreCase(switchs)) {
                            if ("ZoomAdd".equalsIgnoreCase(direction)) {
                                PtzControlModule.ptzControlZoomAddEnd(0);
                            } else if ("ZoomDec".equalsIgnoreCase(direction)) {
                                System.out.println("变倍-");
                                PtzControlModule.ptzControlZoomDecEnd(0);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else {
                            return "-12";
                        }
                    } else {
                        //没有该功能
                        res = "-10";
                        return res;
                    }
                } else if ("mingjing".equalsIgnoreCase(cameraType)) {
                    System.out.println("明景摄像机开始运作");
                    if ("xuanZhuan".equalsIgnoreCase(features)) {
                        if ("open".equalsIgnoreCase(switchs)) {
                            if ("right".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 5, ip, userName, password);
                            } else if ("left".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 4, ip, userName, password);
                            } else if ("up".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 2, ip, userName, password);
                            } else if ("down".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 7, ip, userName, password);
                            } else if ("topLeft".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 1, ip, userName, password);
                            } else if ("bottomLeft".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 6, ip, userName, password);
                            } else if ("upperRight".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 3, ip, userName, password);
                            } else if ("bottomRight".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 8, ip, userName, password);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else if ("close".equalsIgnoreCase(switchs)) {
                            Util.move2(ip, userName, password);
                        } else {
                            return "-12";
                        }

                    } else if ("guangQuan".equalsIgnoreCase(features)) {
                        if ("open".equalsIgnoreCase(switchs)) {
                            if ("irisAdd".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 13, ip, userName, password);
                            } else if ("irisDec".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 14, ip, userName, password);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else if ("close".equalsIgnoreCase(switchs)) {
                            Util.move2(ip, userName, password);
                        } else {
                            return "-12";
                        }

                    } else if ("bianBei".equalsIgnoreCase(features)) {
                        if ("open".equalsIgnoreCase(switchs)) {
                            if ("ZoomAdd".equalsIgnoreCase(direction)) {
                                System.out.println("变倍+");
                                Util.moveforspeed(speed, 10, ip, userName, password);
                            } else if ("ZoomDec".equalsIgnoreCase(direction)) {
                                System.out.println("变倍-");
                                Util.moveforspeed(speed, 9, ip, userName, password);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else if ("close".equalsIgnoreCase(switchs)) {
                            Util.move2(ip, userName, password);
                        } else {
                            return "-12";
                        }
                    } else if ("tiaoJiao".equalsIgnoreCase(features)) {

                        if ("open".equalsIgnoreCase(switchs)) {
                            if ("focusAdd".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 11, ip, userName, password);
                            } else if ("focusDec".equalsIgnoreCase(direction)) {
                                Util.moveforspeed(speed, 12, ip, userName, password);
                            } else {
                                //没有该方向
                                res = "-8";
                                return res;
                            }
                        } else if ("close".equalsIgnoreCase(switchs)) {
                            Util.move2(ip, userName, password);
                        } else {
                            return "-12";
                        }
                    } else {
                        //没有该功能
                        res = "-10";
                        return res;
                    }
                } else {
                    res = "-5";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            if ("dahua".equalsIgnoreCase(cameraType)) {
                if ("close".equalsIgnoreCase(switchs)) {
                    boolean b = LoginModule.logout();
                    String logoutStatus = b ? "登出成功" : "登出失败";
                    System.out.println(logoutStatus);
                }

            }
            System.out.println("结束");
            d.remove(ip);
        }
        return res;
    }

}



