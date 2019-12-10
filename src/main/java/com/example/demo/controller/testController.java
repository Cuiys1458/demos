package com.example.demo.controller;

import com.example.demo.Util.HttpClentUtil;
import com.example.demo.Util.Tools;
import com.example.demo.Util.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class testController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public @ResponseBody String cameraRotation() {
        return "test success";
    }

}
