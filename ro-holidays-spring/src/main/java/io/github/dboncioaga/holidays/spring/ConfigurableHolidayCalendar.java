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

import io.github.dboncioaga.holidays.core.Holiday;
import io.github.dboncioaga.holidays.core.HolidayCalendar;
import io.github.dboncioaga.holidays.core.HolidayType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Decorator for {@link HolidayCalendar} that applies configuration-based overrides.
 * <p>
 * This allows adding additional holidays or excluding certain dates from the
 * underlying calendar at runtime based on Spring configuration.
 * </p>
 * <p>
 * This class is immutable and thread-safe after construction.
 * </p>
 */
public final class ConfigurableHolidayCalendar implements HolidayCalendar {

    private final HolidayCalendar delegate;
    private final Set<LocalDate> extraDates;
    private final Set<LocalDate> excludedDates;

    /**
     * Constructs a configurable holiday calendar.
     *
     * @param delegate the underlying calendar, must not be null
     * @param extraDates additional dates to treat as holidays, must not be null
     * @param excludedDates dates to exclude from holidays, must not be null
     * @throws NullPointerException if any parameter is null
     */
    public ConfigurableHolidayCalendar(HolidayCalendar delegate,
                                        Set<LocalDate> extraDates,
                                        Set<LocalDate> excludedDates) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.extraDates = new HashSet<>(Objects.requireNonNull(extraDates, "extraDates must not be null"));
        this.excludedDates = new HashSet<>(Objects.requireNonNull(excludedDates, "excludedDates must not be null"));
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        if (excludedDates.contains(date)) {
            return false;
        }

        if (extraDates.contains(date)) {
            return true;
        }

        return delegate.isHoliday(date);
    }

    @Override
    public Set<Holiday> getHolidays(int year) {
        Set<Holiday> holidays = new HashSet<>(delegate.getHolidays(year));

        // Remove excluded holidays
        holidays.removeIf(h -> excludedDates.contains(h.getDate()));

        // Add additional holidays for this year
        for (LocalDate date : extraDates) {
            if (date.getYear() == year) {
                holidays.add(new Holiday(date, "Additional Holiday", HolidayType.LEGAL));
            }
        }

        return Set.copyOf(holidays);
    }
}
