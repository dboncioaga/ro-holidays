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
package io.github.dboncioaga.holidays.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties for Romanian holidays.
 * <p>
 * Allows overriding the default holiday calendar by adding custom holidays
 * or excluding certain dates.
 * </p>
 */
@ConfigurationProperties(prefix = "ro.holidays")
public class RomanianHolidaysProperties {

    /**
     * Dates to exclude from the holiday calendar (in ISO format: YYYY-MM-DD).
     */
    private List<LocalDate> exclude = new ArrayList<>();

    /**
     * If true, holidays falling on weekends are moved to the nearest working day.
     * Saturday holidays move to Friday, Sunday holidays move to Monday.
     */
    private boolean floatWeekendHolidays = false;

    /**
     * Custom holidays with names (date to name mapping).
     * Example: {"2026-03-15": "Company Anniversary", "2026-12-24": "Christmas Eve"}
     */
    private Map<LocalDate, String> customHolidays = new HashMap<>();

    /**
     * Names of holidays that should not be subject to floating.
     * Example: ["Crăciunul", "Ziua Națională a României"]
     */
    private List<String> nonFloatableHolidays = new ArrayList<>();

    public List<LocalDate> getExclude() {
        return exclude;
    }

    public void setExclude(List<LocalDate> exclude) {
        this.exclude = exclude;
    }

    public boolean isFloatWeekendHolidays() {
        return floatWeekendHolidays;
    }

    public void setFloatWeekendHolidays(boolean floatWeekendHolidays) {
        this.floatWeekendHolidays = floatWeekendHolidays;
    }

    public Map<LocalDate, String> getCustomHolidays() {
        return customHolidays;
    }

    public void setCustomHolidays(Map<LocalDate, String> customHolidays) {
        this.customHolidays = customHolidays;
    }

    public List<String> getNonFloatableHolidays() {
        return nonFloatableHolidays;
    }

    public void setNonFloatableHolidays(List<String> nonFloatableHolidays) {
        this.nonFloatableHolidays = nonFloatableHolidays;
    }
}
