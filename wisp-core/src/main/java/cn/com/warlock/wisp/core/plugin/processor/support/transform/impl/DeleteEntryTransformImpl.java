package cn.com.warlock.wisp.core.plugin.processor.support.transform.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import cn.com.warlock.wisp.core.dto.MysqlColumn;
import cn.com.warlock.wisp.core.dto.MysqlEntry;
import cn.com.warlock.wisp.core.plugin.processor.support.transform.IEntryTransform;
import cn.com.warlock.wisp.core.plugin.processor.support.transform.TransformResult;

public class DeleteEntryTransformImpl implements IEntryTransform {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DeleteEntryTransformImpl.class);

    protected static Gson gson = new Gson();

    @Override
    public TransformResult entry2Json(MysqlEntry entry, String tableKey) {

        Map<String, String> map = new HashMap<>();

        String currentKeyValue = "";

        List<MysqlColumn> columns = entry.getColumns();
        for (MysqlColumn mysqlColumn : columns) {

            boolean isNull = mysqlColumn.isNull();
            String key = mysqlColumn.getName();
            String value = mysqlColumn.getValue();

            if (value == null) {
                isNull = true;
            }

            if (isNull) {
                map.put(key, "");
            } else {
                map.put(key, value);

                if (key.equals(tableKey)) {
                    currentKeyValue = value;
                }
            }
        }

        if (currentKeyValue.equals("")) {
            LOGGER.error("cannot find key {} for table {} with entry {}", tableKey, entry.getTable(), entry);
            return null;

        } else {

            String value;
            try {
                value = new ObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                LOGGER.error("json dump error, key {} for table {} with entry {}", tableKey, entry.getTable
                        (), entry);
                return null;
            }

            return new TransformResult(currentKeyValue, value);
        }
    }
}
