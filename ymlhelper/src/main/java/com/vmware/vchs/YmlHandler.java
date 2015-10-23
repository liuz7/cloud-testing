package com.vmware.vchs;

import com.google.common.collect.Lists;
import com.vmware.vchs.Querable;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by liuda on 7/30/15.
 */
public class YmlHandler {

    public YmlHandler() {
    }

    public Map<String, Object> loadYml(String file) {
        Map<String, Object> values = null;
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(file)) {
            values = (Map<String, Object>) yaml.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }

    private List getExceptLastItem(List fileList,List<Querable> queryList, String file){
        Map<String, Object> map = loadYml(file);
        fileList.add(map);
        int querySize = queryList.size();
        for (int index = 0; index < querySize - 1; index++) {
            fileList = queryList.get(index).get(fileList);
        }
        return fileList;
    }

    public List get(List<Querable> queryList, String file) throws Exception {
        List fileList = Lists.newArrayList();
        List tmp =getExceptLastItem(fileList,queryList,file);
        return queryList.get(queryList.size() - 1).get(tmp);
    }

    public Map put(List<Querable> queryList, String file, String value) throws Exception {
        List fileList = Lists.newArrayList();
        List tmp =getExceptLastItem(fileList,queryList,file);
        queryList.get(queryList.size() - 1).set(tmp, value);
        return (Map)fileList.get(0);
    }

    public Map delete(List<Querable> queryList, String file) throws Exception {
        Map<String, Object> map = loadYml(file);
        List tmp = Lists.newArrayList();
        List fileCopy=tmp;
        tmp.add(map);
        int querySize = queryList.size();
        for (int index = 0; index < querySize - 1; index++) {
            tmp = queryList.get(index).get(tmp);
        }
        queryList.get(queryList.size() - 1).delete(tmp);
        return (Map)fileCopy.get(0);
    }
}
