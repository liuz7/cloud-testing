package com.vmware.vchs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

//--r templates[0].vms.nics[0].name  --f /Users/liuda/Downloads/release.yml
//--w templates[0].vms.nics[0].name=new  --f /Users/liuda/Downloads/release.yml
//--a services[name=gateway-housekeeper].properties.env.BACKUP_GUARD_DEBUG_MODE_ENABLED true   --f /Users/liuda/Downloads/release.yml
//--d templates[0].vms.nics[0].goal  --f /Users/liuda/Downloads/release.yml
//--w services[name=gateway-housekeeper]/properties/env[COMPACT_SNAPSHOT_PERIOD=%{gateway-snapshot-compact-period}]/COMPACT_SNAPSHOT_PERIOD=10  --f /Users/liuda/Downloads/release.yml
public class YmlHelper extends OptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(YmlHelper.class);

    public static void main(String[] args) throws Exception {
//        YmlHelper helper = new YmlHelper();
//        OperationHolder operationHolder = helper.parse(args);
//        YmlParser parser = new YmlParser(operationHolder.getFile());
//        Map<String, Map<String, String>> map = parser.loadYml();
//        YmlHandler ymlHandler = new YmlHandler();
//        ymlHandler.handle(map, operationHolder);

//        args=new String[]{"--r","services[*]/properties/env[RDBMS_*=%{mysql-*}]","--f", "/Users/liuda/Downloads/release.yml"};

//        args=new String[]{"--r","services[name=gateway-housekeeper]/properties/env[RDBMS_*=%{mysql-*}]","--f", "/Users/liuda/Downloads/release.yml"};
//        YmlHelper helper = new YmlHelper();
//        helper.parse(args);
//        YmlParser parser = new YmlParser(helper.file);
//        Map<String, Map<String, String>> map = parser.loadYml();
        YmlHandler ymlHandler = new YmlHandler();
//        System.out.println(ymlHandler.get(helper.queryList, helper.file));
//        Map file=ymlHandler.put(helper.queryList, helper.file, "aaaaaa");
//        Map file=ymlHandler.delete(helper.queryList, helper.file);
//        Utils.writeToYml(helper.file, file);
//        System.out.println(ymlHandler.get(helper.queryList, helper.file));



//        args=new String[]{"--w","variables/deploy-env=PROD","--f", "/Users/liuda/Downloads/config.yml"};
//        args=new String[]{"--w","services[name=gateway-housekeeper]/properties/env[COMPACT_SNAPSHOT_PERIOD=*gateway*]||COMPACT_SNAPSHOT_PERIOD=10","--f", "/Users/liuda/Downloads/release.yml"};
//        args=new String[]{"--w","targets[*]/storages[*]/properties/storage-profile||aaaaab","--f", "/Users/liuda/Downloads/config.yml"};
//        args=new String[]{"--w","targets[*]/storages[*]||aaaaab","--f", "/Users/liuda/Downloads/config.yml"};
//        args=new String[]{"--w","services[name=gateway-housekeeper]/*/env[COMPACT_SNAPSHOT_PERIOD=*gateway*]||COMPACT_SNAPSHOT_PERIOD=10","--f", "/Users/liuda/Downloads/release.yml"};
//        args=new String[]{"--w","services[name=gateway-housekeeper]/*/env[COMPACT_SNAPSHOT_*=*gateway*]||COMPACT_SNAPSHOT_PERIOD=10","--f", "/Users/liuda/Downloads/release.yml"};
//                args=new String[]{"--w","targets[*]/storages[*]||aaaaab","--f", "/Users/liuda/Downloads/config.yml"};
//        args=new String[]{"--w","services[*=gateway-housekeeper]/properties/env[COMPACT_SNAPSHOT_PERIOD=%{*]||COMPACT_SNAPSHOT_PERIOD=10","--f", "/Users/liuda/Downloads/release.yml"};

        YmlHelper helper = new YmlHelper();
        helper.parse(args);
        switch (helper.operation){
            case PUT:
                Map file=ymlHandler.put(helper.queryList, helper.file, helper.value);
                Utils.writeToYml(helper.file, file);
                break;
            case DELETE:
                file=ymlHandler.delete(helper.queryList, helper.file);
                Utils.writeToYml(helper.file, file);
                break;
            case GET:
                System.out.println(ymlHandler.get(helper.queryList, helper.file));
                break;
        }

//
//        args=new String[]{"--w","services[name=billingservice]/properties/env[DBAAS_SERVICE_TYPE=]/DBAAS_SERVICE_TYPE","com.vmware.vchs.dbaas","--f", "/Users/liuda/Downloads/dbaas_release.yml"};

//                args=new String[]{"--w","services[name=gateway-housekeeper]/properties/env[17]/COMPACT_SNAPSHOT_PERIOD","11","--f", "/Users/liuda/Downloads/dbaas_release.yml"};
//        YmlHelper helper = new YmlHelper();
//        OperationHolder operationHolder = helper.parse(args);
//        YmlParser parser = new YmlParser(operationHolder.getFile());
//        Map<String, Map<String, String>> map = parser.loadYml();
//        YmlHandler ymlHandler = new YmlHandler();
//        ymlHandler.handle(map, operationHolder);
//        args=new String[]{"--r","services[name=gateway-housekeeper]/properties/env[COMPACT_SNAPSHOT_PERIOD=%{gateway-snapshot-compact-period}]/COMPACT_SNAPSHOT_PERIOD","--f", "/Users/liuda/Downloads/dbaas_release.yml"};
//        helper = new YmlHelper();
//        operationHolder = helper.parse(args);
//        parser = new YmlParser(operationHolder.getFile());
//        map = parser.loadYml();
//        ymlHandler = new YmlHandler();
//        ymlHandler.handle(map, operationHolder);
//        System.out.println(operationHolder.getValue());

//        args=new String[]{"--r","services[name=gateway-housekeeper]/properties/env[COMPACT_SNAPSHOT_PERIOD=%{gateway-snapshot-compact-period}]/COMPACT_SNAPSHOT_PERIOD","--f", "/Users/liuda/Downloads/dbaas_release.yml"};
//        args=new String[]{"--r","services[name=gateway-housekeeper]/properties/env[17]/COMPACT_SNAPSHOT_PERIOD","--f", "/Users/liuda/Downloads/dbaas_release.yml"};
//        YmlHelper helper = new YmlHelper();
//        OperationHolder operationHolder = helper.parse(args);
//        YmlParser parser = new YmlParser(operationHolder.getFile());
//        Map<String, Map<String, String>> map = parser.loadYml();
//        YmlHandler ymlHandler = new YmlHandler();
//        ymlHandler.handle(map, operationHolder);
//        System.out.println(operationHolder.getValue());



    }
}
