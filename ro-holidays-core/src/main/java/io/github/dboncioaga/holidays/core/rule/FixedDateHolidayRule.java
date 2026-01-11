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
import org.jspecify.annotations.Nullable;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

/**
 * Holiday rule for fixed-date holidays.
 * <p>
 * This rule generates a holiday on the same day and month each year,
 * optionally restricted to a specific year range.
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 */
public final class FixedDateHolidayRule implements HolidayRule {

    private final int month;
    private final int day;
    private final String name;
    private final HolidayType type;
    private final @Nullable Integer fromYear;
    private final @Nullable Integer toYear;

    /**
     * Constructs a fixed-date holiday rule.
     *
     * @param month the month (1-12)
     * @param day the day of month (1-31)
     * @param name the holiday name
     * @param type the holiday type
     * @param fromYear the first year this holiday applies (inclusive), or null for no lower bound
     * @param toYear the last year this holiday applies (inclusive), or null for no upper bound
     * @throws IllegalArgumentException if month or day are invalid, if name is blank,
     *         or if fromYear is greater than toYear
     * @throws NullPointerException if name or type is null
     */
    public FixedDateHolidayRule(int month, int day, String name, HolidayType type,
                                 @Nullable Integer fromYear, @Nullable Integer toYear) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month must be between 1 and 12: " + month);
        }
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("day must be between 1 and 31: " + day);
        }

        this.month = month;
        this.day = day;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.fromYear = fromYear;
        this.toYear = toYear;

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        if (fromYear != null && toYear != null && fromYear > toYear) {
            throw new IllegalArgumentException(
                    "fromYear must not be greater than toYear: " + fromYear + " > " + toYear);
        }

        // Validate that the date is valid for at least one year (try a leap year)
        try {
            LocalDate.of(2024, month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid date: month=" + month + ", day=" + day, e);
        }
    }

    @Override
    public Set<Holiday> holidaysFor(int year) {
        // Check if year is within the valid range
        if (fromYear != null && year < fromYear) {
            return Set.of();
        }
        if (toYear != null && year > toYear) {
            return Set.of();
        }

        // Try to create the date for this year
        try {
            LocalDate date = LocalDate.of(year, month, day);
            Holiday holiday = new Holiday(date, name, type);
            return Set.of(holiday);
        } catch (DateTimeException e) {
            // Date doesn't exist in this year (e.g., Feb 29 in non-leap year)
            return Set.of();
        }
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getName() {
        return name;
    }

    public HolidayType getType() {
        return type;
    }

    public @Nullable Integer getFromYear() {
        return fromYear;
    }

    public @Nullable Integer getToYear() {
        return toYear;
    }

    @Override
    public String toString() {
        return "FixedDateHolidayRule{" +
               "month=" + month +
               ", day=" + day +
               ", name='" + name + '\'' +
               ", type=" + type +
               ", fromYear=" + fromYear +
               ", toYear=" + toYear +
               '}';
    }
}
