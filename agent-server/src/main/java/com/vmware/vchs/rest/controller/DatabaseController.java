package com.vmware.vchs.rest.controller;

import com.vmware.vchs.rest.domain.Database;
import com.vmware.vchs.rest.service.IDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuzhiwen on 14-7-18.
 */
@RestController
@RequestMapping("/api/v2")
public class DatabaseController {
    @Autowired
    private IDatabaseService databaseService;

    @RequestMapping("/provision")
    public
    @ResponseBody
    Database provision(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "plan", required = false) String plan) {
        Database db = databaseService.provision(name, plan);
        return db;
    }

    @RequestMapping("/unprovision")
    public
    @ResponseBody
    String unProvision(@RequestParam(value = "id", required = false) String id) {
        return databaseService.unProvision(id);
    }
}
