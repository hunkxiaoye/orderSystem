package com.controller;

import com.common.CookieUtils;
import com.db.model.User;
import com.db.service.inf.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class UserController {
    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/login")
    public String Loginview() {
        return "login";
    }
    //登录验证
    @RequestMapping(value = "/loginVerify")
    public String loginverify(HttpServletResponse response,
                              Model model, String username, String password, String returnurl,
                              @RequestParam(required = false)String auto) throws Exception
    {
        if ((username==null||username.isEmpty())||(password==null||password.isEmpty()))
        {
            model.addAttribute("msg","用户名密码不能为空！");
        }
        else
        {
            User user = userService.checklogin(username, password);
            if (user==null)
            {
                model.addAttribute("msg","用户名或密码错误");
            }else{
                if (auto!=null)
                {
                    CookieUtils.Login(response,3000,user.getUserName(),user.getId()+"",new Date());
                }else {
                    CookieUtils.Login(response,300,user.getUserName(),user.getId()+"",new Date());
                }

                if (returnurl==null||returnurl.isEmpty())
                    response.sendRedirect("welcome");
                else
                    response.sendRedirect(returnurl);

            }
        }
        return "login";
    }

}
