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

import java.time.LocalDate;

/**
 * Utility class for calculating Orthodox Easter dates using the
 * Meeus/Jones/Butcher algorithm adapted for the Julian calendar.
 * <p>
 * The Orthodox Easter is calculated using the Julian calendar and then
 * converted to the Gregorian calendar. This implementation is valid for
 * years 1900-2099.
 * </p>
 * <p>
 * This class is stateless and thread-safe.
 * </p>
 */
public final class OrthodoxEasterCalculator {

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2099;

    private OrthodoxEasterCalculator() {
        // Utility class
    }

    /**
     * Calculates the Orthodox Easter Sunday for the specified year.
     * <p>
     * This uses the Meeus/Jones/Butcher algorithm adapted for the
     * Julian calendar, then converts to Gregorian calendar.
     * </p>
     *
     * @param year the year (1900-2099)
     * @return the date of Orthodox Easter Sunday in the Gregorian calendar
     * @throws IllegalArgumentException if year is outside the supported range
     */
    public static LocalDate calculateEaster(int year) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException(
                    "Year must be between " + MIN_YEAR + " and " + MAX_YEAR + ": " + year);
        }

        // Meeus/Jones/Butcher algorithm for Orthodox Easter
        int a = year % 4;
        int b = year % 7;
        int c = year % 19;
        int d = (19 * c + 15) % 30;
        int e = (2 * a + 4 * b - d + 34) % 7;
        int month = (d + e + 114) / 31;
        int day = ((d + e + 114) % 31) + 1;

        // Julian calendar date
        LocalDate julianEaster = LocalDate.of(year, month, day);

        // Add 13 days to convert from Julian to Gregorian calendar (for 1900-2099)
        return julianEaster.plusDays(13);
    }

    /**
     * Returns the minimum supported year.
     *
     * @return the minimum year (1900)
     */
    public static int getMinYear() {
        return MIN_YEAR;
    }

    /**
     * Returns the maximum supported year.
     *
     * @return the maximum year (2099)
     */
    public static int getMaxYear() {
        return MAX_YEAR;
    }
}
