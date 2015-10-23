package com.vmware.vchs.performance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by georgeliu on 14/12/1.
 */
@Controller
@RequestMapping("/")
public class PerformanceController {

    @RequestMapping("{test}")
    public String responseTime(@PathVariable String test, Model model) {
        model.addAttribute("test", test);
        return "responseTime";
    }

}
