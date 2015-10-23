package com.vmware.vchs.rest.service;

import com.vmware.vchs.rest.domain.Database;
import org.springframework.stereotype.Service;

/**
 * Created by liuzhiwen on 14-7-18.
 */
@Service
public class DatabaseService implements IDatabaseService {
    @Override
    public Database provision(String name, String plan){
        Database db=new Database();
        db.setId("111");
        db.setName(name);
        db.setPlan(plan);
        db.setConnectionString("jdbc:mysql://<server>/<database>?user=<username>&password=<password>");
        return db;
    }
    @Override
    public String unProvision(String id){
        return "ok";
    }
}
