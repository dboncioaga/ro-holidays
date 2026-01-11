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
import io.github.dboncioaga.holidays.core.HolidayType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CustomHolidayRule}.
 */
class CustomHolidayRuleTest {

    @Test
    void testCustomHolidaysForYear() {
        Map<LocalDate, String> customs = new HashMap<>();
        customs.put(LocalDate.of(2026, 3, 15), "Company Anniversary");
        customs.put(LocalDate.of(2026, 12, 24), "Christmas Eve");
        customs.put(LocalDate.of(2027, 7, 4), "Company Summer Party");

        CustomHolidayRule rule = new CustomHolidayRule(customs);

        // Get 2026 holidays
        Set<Holiday> holidays2026 = rule.holidaysFor(2026);
        assertEquals(2, holidays2026.size(), "Should have 2 custom holidays in 2026");

        assertTrue(holidays2026.stream().anyMatch(h -> 
            h.getDate().equals(LocalDate.of(2026, 3, 15)) &&
            h.getName().equals("Company Anniversary")));
        assertTrue(holidays2026.stream().anyMatch(h -> 
            h.getDate().equals(LocalDate.of(2026, 12, 24)) &&
            h.getName().equals("Christmas Eve")));

        // Get 2027 holidays
        Set<Holiday> holidays2027 = rule.holidaysFor(2027);
        assertEquals(1, holidays2027.size(), "Should have 1 custom holiday in 2027");
        assertTrue(holidays2027.stream().anyMatch(h -> 
            h.getDate().equals(LocalDate.of(2027, 7, 4))));
    }

    @Test
    void testNoCustomHolidaysForYear() {
        Map<LocalDate, String> customs = new HashMap<>();
        customs.put(LocalDate.of(2026, 3, 15), "Company Anniversary");

        CustomHolidayRule rule = new CustomHolidayRule(customs);

        Set<Holiday> holidays2025 = rule.holidaysFor(2025);
        assertTrue(holidays2025.isEmpty(), "Should have no holidays for 2025");
    }

    @Test
    void testCustomHolidayType() {
        Map<LocalDate, String> customs = new HashMap<>();
        customs.put(LocalDate.of(2026, 3, 15), "Company Anniversary");

        CustomHolidayRule rule = new CustomHolidayRule(customs, HolidayType.OPTIONAL);

        Set<Holiday> holidays = rule.holidaysFor(2026);
        Holiday holiday = holidays.iterator().next();
        assertEquals(HolidayType.OPTIONAL, holiday.getType());
    }

    @Test
    void testDefaultTypeIsOptional() {
        Map<LocalDate, String> customs = new HashMap<>();
        customs.put(LocalDate.of(2026, 3, 15), "Company Anniversary");

        CustomHolidayRule rule = new CustomHolidayRule(customs);

        Set<Holiday> holidays = rule.holidaysFor(2026);
        Holiday holiday = holidays.iterator().next();
        assertEquals(HolidayType.OPTIONAL, holiday.getType());
    }

    @Test
    void testEmptyCustomHolidays() {
        CustomHolidayRule rule = new CustomHolidayRule(Map.of());

        Set<Holiday> holidays = rule.holidaysFor(2026);
        assertTrue(holidays.isEmpty());
    }

    @Test
    void testImmutability() {
        Map<LocalDate, String> customs = new HashMap<>();
        customs.put(LocalDate.of(2026, 3, 15), "Company Anniversary");

        CustomHolidayRule rule = new CustomHolidayRule(customs);

        // Modify original map
        customs.put(LocalDate.of(2026, 12, 24), "Christmas Eve");

        // Rule should not be affected
        Set<Holiday> holidays = rule.holidaysFor(2026);
        assertEquals(1, holidays.size(), "Original modification should not affect rule");
    }

    @Test
    void testGetCustomHolidays() {
        Map<LocalDate, String> customs = new HashMap<>();
        customs.put(LocalDate.of(2026, 3, 15), "Company Anniversary");

        CustomHolidayRule rule = new CustomHolidayRule(customs);

        Map<LocalDate, String> retrieved = rule.getCustomHolidays();
        assertEquals(customs, retrieved);
        
        // Should be immutable
        assertThrows(UnsupportedOperationException.class, () -> 
            retrieved.put(LocalDate.of(2026, 1, 1), "Test"));
    }

    @Test
    void testNullCustomHolidaysThrows() {
        assertThrows(NullPointerException.class, () -> 
            new CustomHolidayRule(null));
    }

    @Test
    void testNullTypeThrows() {
        assertThrows(NullPointerException.class, () -> 
            new CustomHolidayRule(Map.of(), null));
    }
}
