/*
 * Copyright 2026 dboncioaga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.dboncioaga.holidays.core.loader;

import org.yaml.snakeyaml.Yaml;
import io.github.dboncioaga.holidays.core.HolidayRule;
import io.github.dboncioaga.holidays.core.HolidayType;
import io.github.dboncioaga.holidays.core.rule.FixedDateHolidayRule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads fixed-date holiday rules from YAML configuration.
 * <p>
 * This class reads a YAML file containing fixed holiday definitions and
 * converts them into {@link HolidayRule} instances.
 * </p>
 * <p>
 * Expected YAML format:
 * </p>
 * <pre>
 * fixed:
 *   - month: 1
 *     day: 1
 *     name: Holiday Name
 *     type: LEGAL
 *     from: 2000  # optional
 *     to: 2050    # optional
 * </pre>
 */
public final class YamlHolidayLoader {

    private YamlHolidayLoader() {
        // Utility class
    }

    /**
     * Loads fixed holiday rules from the default YAML resource.
     *
     * @return a list of holiday rules, never null
     * @throws HolidayLoadException if the resource cannot be loaded or parsed
     */
    public static List<HolidayRule> loadDefaultRules() {
        return loadRules("/ro-holidays.yaml");
    }

    /**
     * Loads fixed holiday rules from the specified YAML resource.
     *
     * @param resourcePath the classpath resource path
     * @return a list of holiday rules, never null
     * @throws HolidayLoadException if the resource cannot be loaded or parsed
     */
    public static List<HolidayRule> loadRules(String resourcePath) {
        try (InputStream input = YamlHolidayLoader.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new HolidayLoadException("Resource not found: " + resourcePath);
            }
            return loadRules(input);
        } catch (IOException e) {
            throw new HolidayLoadException("Failed to read resource: " + resourcePath, e);
        }
    }

    /**
     * Loads fixed holiday rules from the specified input stream.
     *
     * @param input the input stream containing YAML data
     * @return a list of holiday rules, never null
     * @throws HolidayLoadException if the YAML cannot be parsed or is invalid
     */
    @SuppressWarnings("unchecked")
    public static List<HolidayRule> loadRules(InputStream input) {
        Yaml yaml = new Yaml();
        Map<String, Object> data;

        try {
            data = yaml.load(input);
        } catch (Exception e) {
            throw new HolidayLoadException("Failed to parse YAML", e);
        }

        if (data == null || !data.containsKey("fixed")) {
            throw new HolidayLoadException("YAML does not contain 'fixed' key");
        }

        Object fixedObj = data.get("fixed");
        if (!(fixedObj instanceof List)) {
            throw new HolidayLoadException("'fixed' must be a list");
        }

        List<Map<String, Object>> fixedList = (List<Map<String, Object>>) fixedObj;
        List<HolidayRule> rules = new ArrayList<>();

        for (int i = 0; i < fixedList.size(); i++) {
            Map<String, Object> entry = fixedList.get(i);
            try {
                HolidayRule rule = parseFixedHolidayRule(entry);
                rules.add(rule);
            } catch (Exception e) {
                throw new HolidayLoadException("Failed to parse entry at index " + i, e);
            }
        }

        return rules;
    }

    private static HolidayRule parseFixedHolidayRule(Map<String, Object> entry) {
        Integer month = getRequiredInt(entry, "month");
        Integer day = getRequiredInt(entry, "day");
        String name = getRequiredString(entry, "name");
        String typeStr = getRequiredString(entry, "type");

        HolidayType type;
        try {
            type = HolidayType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new HolidayLoadException("Invalid holiday type: " + typeStr, e);
        }

        Integer fromYear = getOptionalInt(entry, "from");
        Integer toYear = getOptionalInt(entry, "to");

        return new FixedDateHolidayRule(month, day, name, type, fromYear, toYear);
    }

    private static Integer getRequiredInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new HolidayLoadException("Missing required field: " + key);
        }
        if (!(value instanceof Number)) {
            throw new HolidayLoadException("Field '" + key + "' must be a number: " + value);
        }
        return ((Number) value).intValue();
    }

    private static String getRequiredString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new HolidayLoadException("Missing required field: " + key);
        }
        if (!(value instanceof String)) {
            throw new HolidayLoadException("Field '" + key + "' must be a string: " + value);
        }
        return (String) value;
    }

    private static Integer getOptionalInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (!(value instanceof Number)) {
            throw new HolidayLoadException("Field '" + key + "' must be a number: " + value);
        }
        return ((Number) value).intValue();
    }
}
