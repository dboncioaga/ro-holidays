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
 * Immutable value object representing a Romanian holiday.
 * <p>
 * A holiday consists of a specific date, a name (in Romanian), and a type
 * indicating its legal or cultural status.
 * </p>
 * <p>
 * This class is thread-safe and immutable.
 * </p>
 */
public final class Holiday {

    private final LocalDate date;
    private final String name;
    private final HolidayType type;

    /**
     * Constructs a new Holiday.
     *
     * @param date the date of the holiday, must not be null
     * @param name the name of the holiday (in Romanian), must not be null or blank
     * @param type the type of the holiday, must not be null
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if name is blank
     */
    public Holiday(LocalDate date, String name, HolidayType type) {
        this.date = Objects.requireNonNull(date, "date must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    /**
     * Returns the date of this holiday.
     *
     * @return the date, never null
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the name of this holiday.
     *
     * @return the name in Romanian, never null or blank
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this holiday.
     *
     * @return the type, never null
     */
    public HolidayType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holiday holiday = (Holiday) o;
        return Objects.equals(date, holiday.date) &&
               Objects.equals(name, holiday.name) &&
               type == holiday.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, name, type);
    }

    @Override
    public String toString() {
        return "Holiday{" +
               "date=" + date +
               ", name='" + name + '\'' +
               ", type=" + type +
               '}';
    }
}
