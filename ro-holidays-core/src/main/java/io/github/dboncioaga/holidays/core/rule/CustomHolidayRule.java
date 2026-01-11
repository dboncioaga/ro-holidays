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
package io.github.dboncioaga.holidays.core.rule;

import io.github.dboncioaga.holidays.core.Holiday;
import io.github.dboncioaga.holidays.core.HolidayRule;
import io.github.dboncioaga.holidays.core.HolidayType;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A holiday rule for custom user-defined holidays.
 * <p>
 * This rule allows users to define specific dates as holidays,
 * useful for company-specific free days or any custom holidays.
 * </p>
 */
public final class CustomHolidayRule implements HolidayRule {

    private final Map<LocalDate, String> customHolidays;
    private final HolidayType type;

    /**
     * Constructs a custom holiday rule with the specified holidays.
     *
     * @param customHolidays map of dates to holiday names, must not be null
     * @param type the type of these holidays (e.g., OPTIONAL for company free days)
     * @throws NullPointerException if customHolidays or type is null
     */
    public CustomHolidayRule(Map<LocalDate, String> customHolidays, HolidayType type) {
        this.customHolidays = Map.copyOf(customHolidays);
        this.type = java.util.Objects.requireNonNull(type, "type must not be null");
    }

    /**
     * Constructs a custom holiday rule with OPTIONAL type.
     *
     * @param customHolidays map of dates to holiday names, must not be null
     * @throws NullPointerException if customHolidays is null
     */
    public CustomHolidayRule(Map<LocalDate, String> customHolidays) {
        this(customHolidays, HolidayType.OPTIONAL);
    }

    @Override
    public Set<Holiday> holidaysFor(int year) {
        Set<Holiday> holidays = new HashSet<>();

        for (Map.Entry<LocalDate, String> entry : customHolidays.entrySet()) {
            LocalDate date = entry.getKey();
            if (date.getYear() == year) {
                holidays.add(new Holiday(date, entry.getValue(), type));
            }
        }

        return Collections.unmodifiableSet(holidays);
    }

    /**
     * Returns the custom holidays map.
     *
     * @return an immutable map of custom holidays
     */
    public Map<LocalDate, String> getCustomHolidays() {
        return customHolidays;
    }

    /**
     * Returns the holiday type.
     *
     * @return the holiday type
     */
    public HolidayType getType() {
        return type;
    }
}
