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

import java.time.LocalDate;
import java.util.Objects;

/**
 * Utility class for business day calculations using a holiday calendar.
 * <p>
 * This class provides methods to:
 * </p>
 * <ul>
 *   <li>Find the next business day</li>
 *   <li>Find the previous business day</li>
 *   <li>Add a specified number of business days to a date</li>
 * </ul>
 * <p>
 * All methods use iterative algorithms and are deterministic.
 * This class is immutable and thread-safe.
 * </p>
 */
public final class BusinessDays {

    private final HolidayCalendar calendar;

    /**
     * Constructs a BusinessDays calculator with the specified holiday calendar.
     *
     * @param calendar the holiday calendar to use, must not be null
     * @throws NullPointerException if calendar is null
     */
    public BusinessDays(HolidayCalendar calendar) {
        this.calendar = Objects.requireNonNull(calendar, "calendar must not be null");
    }

    /**
     * Returns the next business day after the specified date.
     * <p>
     * If the specified date is already a business day, the next business day
     * is returned (not the same date).
     * </p>
     *
     * @param date the starting date, must not be null
     * @return the next business day, never null
     * @throws NullPointerException if date is null
     */
    public LocalDate nextBusinessDay(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        LocalDate current = date.plusDays(1);
        while (!calendar.isBusinessDay(current)) {
            current = current.plusDays(1);
        }
        return current;
    }

    /**
     * Returns the previous business day before the specified date.
     * <p>
     * If the specified date is already a business day, the previous business day
     * is returned (not the same date).
     * </p>
     *
     * @param date the starting date, must not be null
     * @return the previous business day, never null
     * @throws NullPointerException if date is null
     */
    public LocalDate previousBusinessDay(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        LocalDate current = date.minusDays(1);
        while (!calendar.isBusinessDay(current)) {
            current = current.minusDays(1);
        }
        return current;
    }

    /**
     * Adds the specified number of business days to the given date.
     * <p>
     * If days is positive, moves forward. If days is negative, moves backward.
     * If days is zero, returns the date unchanged.
     * </p>
     * <p>
     * Examples:
     * </p>
     * <ul>
     *   <li>addBusinessDays(Monday, 1) → Tuesday (if Tuesday is a business day)</li>
     *   <li>addBusinessDays(Friday, 1) → Monday (skipping weekend)</li>
     *   <li>addBusinessDays(Monday, -1) → Friday (previous week)</li>
     * </ul>
     *
     * @param date the starting date, must not be null
     * @param days the number of business days to add (can be negative)
     * @return the resulting date after adding business days, never null
     * @throws NullPointerException if date is null
     */
    public LocalDate addBusinessDays(LocalDate date, int days) {
        Objects.requireNonNull(date, "date must not be null");

        if (days == 0) {
            return date;
        }

        LocalDate current = date;
        int remaining = Math.abs(days);
        boolean forward = days > 0;

        while (remaining > 0) {
            current = forward ? current.plusDays(1) : current.minusDays(1);
            if (calendar.isBusinessDay(current)) {
                remaining--;
            }
        }

        return current;
    }

    /**
     * Returns the holiday calendar used by this instance.
     *
     * @return the holiday calendar, never null
     */
    public HolidayCalendar getCalendar() {
        return calendar;
    }
}
