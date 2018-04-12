package com.controller;

import com.db.model.Goods;
import com.db.service.inf.igoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class goodsController {
    @Autowired
    private igoodsService service;

    @RequestMapping(value = "/index")
    public String getGoods(Model model){
     List<Goods> list = service.findGoodsAll();
     model.addAttribute("list",list);
        return "goodspage";
    }
}
