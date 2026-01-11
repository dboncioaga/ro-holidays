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

import io.github.dboncioaga.holidays.core.loader.YamlHolidayLoader;
import io.github.dboncioaga.holidays.core.rule.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link HolidayCalendar} for Romanian public holidays.
 * <p>
 * This calendar includes:
 * </p>
 * <ul>
 *   <li>Fixed holidays loaded from YAML configuration</li>
 *   <li>Orthodox Easter and related movable holidays</li>
 * </ul>
 * <p>
 * This class is thread-safe and caches computed holidays per year for performance.
 * The cached sets are immutable.
 * </p>
 */
public final class RomanianHolidayCalendar implements HolidayCalendar {

    private static final int MIN_SUPPORTED_YEAR = 1900;
    private static final int MAX_SUPPORTED_YEAR = 2099;

    private final List<HolidayRule> rules;
    private final Map<Integer, Set<Holiday>> cache;
    private final boolean floatWeekendHolidays;
    private final Map<LocalDate, String> customHolidays;
    private final Set<String> nonFloatableHolidayNames;

    /**
     * Constructs a Romanian holiday calendar with the specified rules.
     *
     * @param rules the holiday rules to use, must not be null
     * @param floatWeekendHolidays if true, holidays falling on weekends are moved to nearest working day
     * @param customHolidays map of custom holidays (date to name), may be null or empty
     * @param nonFloatableHolidayNames set of holiday names that should not be subject to floating, may be null or empty
     * @throws NullPointerException if rules is null
     */
    public RomanianHolidayCalendar(List<HolidayRule> rules, boolean floatWeekendHolidays, 
                                   Map<LocalDate, String> customHolidays, Set<String> nonFloatableHolidayNames) {
        this.rules = new ArrayList<>(Objects.requireNonNull(rules, "rules must not be null"));
        this.cache = new ConcurrentHashMap<>();
        this.floatWeekendHolidays = floatWeekendHolidays;
        this.customHolidays = customHolidays != null && !customHolidays.isEmpty() 
                ? Map.copyOf(customHolidays) 
                : Collections.emptyMap();
        this.nonFloatableHolidayNames = nonFloatableHolidayNames != null && !nonFloatableHolidayNames.isEmpty()
                ? Set.copyOf(nonFloatableHolidayNames)
                : Collections.emptySet();
        
        // Add custom holidays rule if provided
        if (!this.customHolidays.isEmpty()) {
            this.rules.add(new io.github.dboncioaga.holidays.core.rule.CustomHolidayRule(this.customHolidays));
        }
    }
    
    /**
     * Constructs a Romanian holiday calendar with the specified rules.
     *
     * @param rules the holiday rules to use, must not be null
     * @param floatWeekendHolidays if true, holidays falling on weekends are moved to nearest working day
     * @param customHolidays map of custom holidays (date to name), may be null or empty
     * @throws NullPointerException if rules is null
     */
    public RomanianHolidayCalendar(List<HolidayRule> rules, boolean floatWeekendHolidays, Map<LocalDate, String> customHolidays) {
        this(rules, floatWeekendHolidays, customHolidays, null);
    }

    /**
     * Constructs a Romanian holiday calendar with the specified rules.
     *
     * @param rules the holiday rules to use, must not be null
     * @param floatWeekendHolidays if true, holidays falling on weekends are moved to nearest working day
     * @throws NullPointerException if rules is null
     */
    public RomanianHolidayCalendar(List<HolidayRule> rules, boolean floatWeekendHolidays) {
        this(rules, floatWeekendHolidays, null);
    }

    /**
     * Constructs a Romanian holiday calendar with the specified rules.
     * Weekend holidays are not floated by default.
     *
     * @param rules the holiday rules to use, must not be null
     * @throws NullPointerException if rules is null
     */
    public RomanianHolidayCalendar(List<HolidayRule> rules) {
        this(rules, false);
    }

    /**
     * Creates a Romanian holiday calendar with default rules.
     * <p>
     * Default rules include:
     * </p>
     * <ul>
     *   <li>Fixed holidays from ro-holidays.yaml</li>
     *   <li>Orthodox Easter and related movable holidays</li>
     * </ul>
     *
     * @param floatWeekendHolidays if true, holidays falling on weekends are moved to nearest working day
     * @return a new Romanian holiday calendar instance, never null
     */
    public static RomanianHolidayCalendar loadDefault(boolean floatWeekendHolidays) {
        List<HolidayRule> rules = new ArrayList<>();

        // Load fixed holidays from YAML
        rules.addAll(YamlHolidayLoader.loadDefaultRules());

        // Add movable holidays based on Orthodox Easter
        rules.add(new GoodFridayRule());
        rules.add(new OrthodoxEasterSundayRule());
        rules.add(new EasterMondayRule());
        rules.add(new PentecostSundayRule());
        rules.add(new PentecostMondayRule());

        return new RomanianHolidayCalendar(rules, floatWeekendHolidays);
    }

    /**
     * Creates a Romanian holiday calendar with default rules.
     * Weekend holidays are not floated.
     * <p>
     * Default rules include:
     * </p>
     * <ul>
     *   <li>Fixed holidays from ro-holidays.yaml</li>
     *   <li>Orthodox Easter and related movable holidays</li>
     * </ul>
     *
     * @return a new Romanian holiday calendar instance, never null
     */
    public static RomanianHolidayCalendar loadDefault() {
        return loadDefault(false);
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        Set<Holiday> holidays = getHolidays(date.getYear());
        return holidays.stream().anyMatch(h -> h.getDate().equals(date));
    }

    @Override
    public Set<Holiday> getHolidays(int year) {
        validateYear(year);
        // Use cached value if available
        return cache.computeIfAbsent(year, this::computeHolidays);
    }

    /**
     * Validates that the year is within the supported range.
     *
     * @param year the year to validate
     * @throws IllegalArgumentException if year is outside the supported range
     */
    private void validateYear(int year) {
        if (year < MIN_SUPPORTED_YEAR || year > MAX_SUPPORTED_YEAR) {
            throw new IllegalArgumentException(
                    "Year must be between " + MIN_SUPPORTED_YEAR + " and " +
                    MAX_SUPPORTED_YEAR + ": " + year);
        }
    }

    /**
     * Computes holidays for the specified year by applying all rules.
     * If floatWeekendHolidays is enabled, moves weekend holidays to nearest working day.
     *
     * @param year the year
     * @return an immutable set of holidays
     */
    private Set<Holiday> computeHolidays(int year) {
        Set<Holiday> holidays = new HashSet<>();

        for (HolidayRule rule : rules) {
            holidays.addAll(rule.holidaysFor(year));
        }

        // Apply weekend floating if enabled
        if (floatWeekendHolidays) {
            holidays = floatWeekendHolidays(holidays);
        }

        // Return an immutable copy
        return Collections.unmodifiableSet(holidays);
    }

    /**
     * Floats weekend holidays to the nearest business day.
     * - Saturday holidays: try Friday first; if not available, try Monday or next business day forward
     * - Sunday holidays: try Monday or next business day forward
     * This means weekend holidays are kept and an additional floating day is added.
     * Holidays in nonFloatableHolidayNames are not subject to floating.
     *
     * @param holidays the original holidays
     * @return holidays with additional floated weekday dates
     */
    private Set<Holiday> floatWeekendHolidays(Set<Holiday> holidays) {
        Set<Holiday> result = new HashSet<>(holidays); // Start with all original holidays
        Set<LocalDate> holidayDates = holidays.stream()
                .map(Holiday::getDate)
                .collect(java.util.stream.Collectors.toSet());

        for (Holiday holiday : holidays) {
            // Skip holidays that should not be floated
            if (nonFloatableHolidayNames.contains(holiday.getName())) {
                continue;
            }
            
            LocalDate date = holiday.getDate();
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            if (dayOfWeek == DayOfWeek.SATURDAY) {
                // Try Friday first
                LocalDate friday = date.minusDays(1);
                if (!holidayDates.contains(friday)) {
                    // Friday is available, use it
                    result.add(new Holiday(friday, holiday.getName() + " FD", holiday.getType()));
                } else {
                    // Friday is already a holiday, find next business day forward (starting from Monday)
                    LocalDate floatingDay = date.plusDays(2); // Start with Monday
                    while (floatingDay.getDayOfWeek() == DayOfWeek.SATURDAY || 
                           floatingDay.getDayOfWeek() == DayOfWeek.SUNDAY ||
                           holidayDates.contains(floatingDay)) {
                        floatingDay = floatingDay.plusDays(1);
                    }
                    result.add(new Holiday(floatingDay, holiday.getName() + " FD", holiday.getType()));
                }
                
            } else if (dayOfWeek == DayOfWeek.SUNDAY) {
                // Find the next business day after Sunday
                LocalDate floatingDay = date.plusDays(1); // Start with Monday
                while (floatingDay.getDayOfWeek() == DayOfWeek.SATURDAY || 
                       floatingDay.getDayOfWeek() == DayOfWeek.SUNDAY ||
                       holidayDates.contains(floatingDay)) {
                    floatingDay = floatingDay.plusDays(1);
                }
                result.add(new Holiday(floatingDay, holiday.getName() + " FD", holiday.getType()));
            }
        }

        return result;
    }

    /**
     * Returns the number of rules configured in this calendar.
     *
     * @return the number of rules
     */
    public int getRuleCount() {
        return rules.size();
    }

    /**
     * Clears the internal cache of computed holidays.
     * <p>
     * This method is primarily useful for testing or when memory usage
     * is a concern with very long-running applications.
     * </p>
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Creates a builder for constructing a customized Romanian holiday calendar.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating customized Romanian holiday calendars.
     */
    public static final class Builder {
        private boolean floatWeekendHolidays = false;
        private final Map<LocalDate, String> customHolidays = new HashMap<>();
        private final Set<String> nonFloatableHolidayNames = new HashSet<>();

        private Builder() {
        }

        /**
         * Enables or disables floating of weekend holidays.
         *
         * @param floatWeekendHolidays if true, weekend holidays move to nearest working day
         * @return this builder for chaining
         */
        public Builder floatWeekendHolidays(boolean floatWeekendHolidays) {
            this.floatWeekendHolidays = floatWeekendHolidays;
            return this;
        }

        /**
         * Adds a custom holiday with the specified name.
         *
         * @param date the date of the holiday
         * @param name the name/description of the holiday
         * @return this builder for chaining
         * @throws NullPointerException if date or name is null
         */
        public Builder addCustomHoliday(LocalDate date, String name) {
            this.customHolidays.put(
                    Objects.requireNonNull(date, "date must not be null"),
                    Objects.requireNonNull(name, "name must not be null")
            );
            return this;
        }

        /**
         * Adds multiple custom holidays.
         *
         * @param holidays map of dates to holiday names
         * @return this builder for chaining
         * @throws NullPointerException if holidays is null
         */
        public Builder addCustomHolidays(Map<LocalDate, String> holidays) {
            this.customHolidays.putAll(Objects.requireNonNull(holidays, "holidays must not be null"));
            return this;
        }

        /**
         * Adds a holiday name that should not be subject to floating.
         * When floating is enabled, holidays with this name will not have floating days added.
         *
         * @param holidayName the name of the holiday that should not be floated
         * @return this builder for chaining
         * @throws NullPointerException if holidayName is null
         */
        public Builder addNonFloatableHoliday(String holidayName) {
            this.nonFloatableHolidayNames.add(Objects.requireNonNull(holidayName, "holidayName must not be null"));
            return this;
        }

        /**
         * Adds multiple holiday names that should not be subject to floating.
         *
         * @param holidayNames set of holiday names that should not be floated
         * @return this builder for chaining
         * @throws NullPointerException if holidayNames is null
         */
        public Builder addNonFloatableHolidays(Set<String> holidayNames) {
            this.nonFloatableHolidayNames.addAll(Objects.requireNonNull(holidayNames, "holidayNames must not be null"));
            return this;
        }

        /**
         * Builds the Romanian holiday calendar with the configured options.
         *
         * @return a new calendar instance
         */
        public RomanianHolidayCalendar build() {
            List<HolidayRule> rules = new ArrayList<>();

            // Load fixed holidays from YAML
            rules.addAll(YamlHolidayLoader.loadDefaultRules());

            // Add movable holidays based on Orthodox Easter
            rules.add(new GoodFridayRule());
            rules.add(new OrthodoxEasterSundayRule());
            rules.add(new EasterMondayRule());
            rules.add(new PentecostSundayRule());
            rules.add(new PentecostMondayRule());

            return new RomanianHolidayCalendar(rules, floatWeekendHolidays, customHolidays, nonFloatableHolidayNames);
        }
    }
}
