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
package io.github.dboncioaga.holidays.core;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

/**
 * Calendar interface for querying Romanian public holidays and business days.
 * <p>
 * This interface provides methods to check if a date is a holiday, retrieve all
 * holidays for a given year, and determine business days.
 * </p>
 * <p>
 * Implementations must be thread-safe after initialization.
 * </p>
 */
public interface HolidayCalendar {

    /**
     * Checks if the specified date is a public holiday.
     *
     * @param date the date to check, must not be null
     * @return true if the date is a holiday, false otherwise
     * @throws NullPointerException if date is null
     */
    boolean isHoliday(LocalDate date);

    /**
     * Returns all public holidays for the specified year.
     * <p>
     * The returned set is immutable and may be empty if no holidays
     * are defined for the given year.
     * </p>
     *
     * @param year the year for which to retrieve holidays
     * @return an immutable set of holidays, never null
     * @throws IllegalArgumentException if the year is outside the supported range
     */
    Set<Holiday> getHolidays(int year);

    /**
     * Checks if the specified date is a business day.
     * <p>
     * A business day is a day that is neither a weekend (Saturday or Sunday)
     * nor a public holiday.
     * </p>
     *
     * @param date the date to check, must not be null
     * @return true if the date is a business day, false otherwise
     * @throws NullPointerException if date is null
     */
    default boolean isBusinessDay(LocalDate date) {
        return !isHoliday(date) && !isWeekend(date);
    }

    /**
     * Checks if the specified date falls on a weekend (Saturday or Sunday).
     *
     * @param date the date to check, must not be null
     * @return true if the date is a weekend, false otherwise
     * @throws NullPointerException if date is null
     */
    default boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}
