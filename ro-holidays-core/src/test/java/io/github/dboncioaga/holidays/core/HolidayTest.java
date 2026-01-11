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

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Holiday}.
 */
class HolidayTest {

    @Test
    void testConstructorWithValidParameters() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        String name = "Anul Nou";
        HolidayType type = HolidayType.LEGAL;

        Holiday holiday = new Holiday(date, name, type);

        assertEquals(date, holiday.getDate());
        assertEquals(name, holiday.getName());
        assertEquals(type, holiday.getType());
    }

    @Test
    void testConstructorWithNullDate() {
        assertThrows(NullPointerException.class, () ->
                new Holiday(null, "Anul Nou", HolidayType.LEGAL)
        );
    }

    @Test
    void testConstructorWithNullName() {
        assertThrows(NullPointerException.class, () ->
                new Holiday(LocalDate.of(2025, 1, 1), null, HolidayType.LEGAL)
        );
    }

    @Test
    void testConstructorWithNullType() {
        assertThrows(NullPointerException.class, () ->
                new Holiday(LocalDate.of(2025, 1, 1), "Anul Nou", null)
        );
    }

    @Test
    void testConstructorWithBlankName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Holiday(LocalDate.of(2025, 1, 1), "   ", HolidayType.LEGAL)
        );
    }

    @Test
    void testConstructorWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Holiday(LocalDate.of(2025, 1, 1), "", HolidayType.LEGAL)
        );
    }

    @Test
    void testEqualityWithSameValues() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Holiday holiday1 = new Holiday(date, "Anul Nou", HolidayType.LEGAL);
        Holiday holiday2 = new Holiday(date, "Anul Nou", HolidayType.LEGAL);

        assertEquals(holiday1, holiday2);
        assertEquals(holiday1.hashCode(), holiday2.hashCode());
    }

    @Test
    void testEqualityWithSameReference() {
        Holiday holiday = new Holiday(LocalDate.of(2025, 1, 1), "Anul Nou", HolidayType.LEGAL);
        assertEquals(holiday, holiday);
    }

    @Test
    void testInequalityWithDifferentDates() {
        Holiday holiday1 = new Holiday(LocalDate.of(2025, 1, 1), "Anul Nou", HolidayType.LEGAL);
        Holiday holiday2 = new Holiday(LocalDate.of(2025, 1, 2), "Anul Nou", HolidayType.LEGAL);

        assertNotEquals(holiday1, holiday2);
    }

    @Test
    void testInequalityWithDifferentNames() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Holiday holiday1 = new Holiday(date, "Anul Nou", HolidayType.LEGAL);
        Holiday holiday2 = new Holiday(date, "Ziua Muncii", HolidayType.LEGAL);

        assertNotEquals(holiday1, holiday2);
    }

    @Test
    void testInequalityWithDifferentTypes() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Holiday holiday1 = new Holiday(date, "Anul Nou", HolidayType.LEGAL);
        Holiday holiday2 = new Holiday(date, "Anul Nou", HolidayType.RELIGIOUS);

        assertNotEquals(holiday1, holiday2);
    }

    @Test
    void testInequalityWithNull() {
        Holiday holiday = new Holiday(LocalDate.of(2025, 1, 1), "Anul Nou", HolidayType.LEGAL);
        assertNotEquals(holiday, null);
    }

    @Test
    void testInequalityWithDifferentClass() {
        Holiday holiday = new Holiday(LocalDate.of(2025, 1, 1), "Anul Nou", HolidayType.LEGAL);
        assertNotEquals(holiday, "Not a holiday");
    }

    @Test
    void testToString() {
        Holiday holiday = new Holiday(
                LocalDate.of(2025, 1, 1),
                "Anul Nou",
                HolidayType.LEGAL
        );

        String result = holiday.toString();

        assertTrue(result.contains("2025-01-01"));
        assertTrue(result.contains("Anul Nou"));
        assertTrue(result.contains("LEGAL"));
    }

    @Test
    void testImmutability() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Holiday holiday = new Holiday(date, "Anul Nou", HolidayType.LEGAL);

        // Verify getters return the same values
        assertSame(date, holiday.getDate());
        assertEquals("Anul Nou", holiday.getName());
        assertEquals(HolidayType.LEGAL, holiday.getType());

        // Modifying the original date doesn't affect the holiday
        LocalDate originalDate = holiday.getDate();
        LocalDate modifiedDate = originalDate.plusDays(1);

        assertEquals(LocalDate.of(2025, 1, 1), holiday.getDate());
        assertNotEquals(modifiedDate, holiday.getDate());
    }

    @Test
    void testAllHolidayTypes() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Holiday legal = new Holiday(date, "Legal Holiday", HolidayType.LEGAL);
        assertEquals(HolidayType.LEGAL, legal.getType());

        Holiday religious = new Holiday(date, "Religious Holiday", HolidayType.RELIGIOUS);
        assertEquals(HolidayType.RELIGIOUS, religious.getType());

        Holiday optional = new Holiday(date, "Optional Holiday", HolidayType.OPTIONAL);
        assertEquals(HolidayType.OPTIONAL, optional.getType());
    }
}
