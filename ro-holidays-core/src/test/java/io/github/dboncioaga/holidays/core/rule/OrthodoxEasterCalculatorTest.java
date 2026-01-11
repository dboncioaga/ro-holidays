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

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OrthodoxEasterCalculator}.
 * <p>
 * Tests verify Orthodox Easter dates against known correct historical dates
 * from official sources.
 * </p>
 */
class OrthodoxEasterCalculatorTest {

    @Test
    void testKnownEasterDates2000to2030() {
        // Known Orthodox Easter dates (verified from official sources)
        assertEquals(LocalDate.of(2000, 4, 30), OrthodoxEasterCalculator.calculateEaster(2000));
        assertEquals(LocalDate.of(2001, 4, 15), OrthodoxEasterCalculator.calculateEaster(2001));
        assertEquals(LocalDate.of(2002, 5, 5), OrthodoxEasterCalculator.calculateEaster(2002));
        assertEquals(LocalDate.of(2003, 4, 27), OrthodoxEasterCalculator.calculateEaster(2003));
        assertEquals(LocalDate.of(2004, 4, 11), OrthodoxEasterCalculator.calculateEaster(2004));
        assertEquals(LocalDate.of(2005, 5, 1), OrthodoxEasterCalculator.calculateEaster(2005));
        assertEquals(LocalDate.of(2006, 4, 23), OrthodoxEasterCalculator.calculateEaster(2006));
        assertEquals(LocalDate.of(2007, 4, 8), OrthodoxEasterCalculator.calculateEaster(2007));
        assertEquals(LocalDate.of(2008, 4, 27), OrthodoxEasterCalculator.calculateEaster(2008));
        assertEquals(LocalDate.of(2009, 4, 19), OrthodoxEasterCalculator.calculateEaster(2009));
        assertEquals(LocalDate.of(2010, 4, 4), OrthodoxEasterCalculator.calculateEaster(2010));
        assertEquals(LocalDate.of(2011, 4, 24), OrthodoxEasterCalculator.calculateEaster(2011));
        assertEquals(LocalDate.of(2012, 4, 15), OrthodoxEasterCalculator.calculateEaster(2012));
        assertEquals(LocalDate.of(2013, 5, 5), OrthodoxEasterCalculator.calculateEaster(2013));
        assertEquals(LocalDate.of(2014, 4, 20), OrthodoxEasterCalculator.calculateEaster(2014));
        assertEquals(LocalDate.of(2015, 4, 12), OrthodoxEasterCalculator.calculateEaster(2015));
        assertEquals(LocalDate.of(2016, 5, 1), OrthodoxEasterCalculator.calculateEaster(2016));
        assertEquals(LocalDate.of(2017, 4, 16), OrthodoxEasterCalculator.calculateEaster(2017));
        assertEquals(LocalDate.of(2018, 4, 8), OrthodoxEasterCalculator.calculateEaster(2018));
        assertEquals(LocalDate.of(2019, 4, 28), OrthodoxEasterCalculator.calculateEaster(2019));
        assertEquals(LocalDate.of(2020, 4, 19), OrthodoxEasterCalculator.calculateEaster(2020));
        assertEquals(LocalDate.of(2021, 5, 2), OrthodoxEasterCalculator.calculateEaster(2021));
        assertEquals(LocalDate.of(2022, 4, 24), OrthodoxEasterCalculator.calculateEaster(2022));
        assertEquals(LocalDate.of(2023, 4, 16), OrthodoxEasterCalculator.calculateEaster(2023));
        assertEquals(LocalDate.of(2024, 5, 5), OrthodoxEasterCalculator.calculateEaster(2024));
        assertEquals(LocalDate.of(2025, 4, 20), OrthodoxEasterCalculator.calculateEaster(2025));
        assertEquals(LocalDate.of(2026, 4, 12), OrthodoxEasterCalculator.calculateEaster(2026));
        assertEquals(LocalDate.of(2027, 5, 2), OrthodoxEasterCalculator.calculateEaster(2027));
        assertEquals(LocalDate.of(2028, 4, 16), OrthodoxEasterCalculator.calculateEaster(2028));
        assertEquals(LocalDate.of(2029, 4, 8), OrthodoxEasterCalculator.calculateEaster(2029));
        assertEquals(LocalDate.of(2030, 4, 28), OrthodoxEasterCalculator.calculateEaster(2030));
    }

    @Test
    void testEasterAlwaysFallsInAprilOrMay() {
        for (int year = 2000; year <= 2030; year++) {
            LocalDate easter = OrthodoxEasterCalculator.calculateEaster(year);
            int month = easter.getMonthValue();
            assertTrue(month == 4 || month == 5,
                    "Easter " + year + " should be in April or May, was: " + easter);
        }
    }

    @Test
    void testYearBelowMinimum() {
        assertThrows(IllegalArgumentException.class, () ->
                OrthodoxEasterCalculator.calculateEaster(1899)
        );
    }

    @Test
    void testYearAboveMaximum() {
        assertThrows(IllegalArgumentException.class, () ->
                OrthodoxEasterCalculator.calculateEaster(2100)
        );
    }

    @Test
    void testMinimumYear() {
        // Should not throw
        LocalDate easter = OrthodoxEasterCalculator.calculateEaster(1900);
        assertNotNull(easter);
        assertEquals(1900, easter.getYear());
    }

    @Test
    void testMaximumYear() {
        // Should not throw
        LocalDate easter = OrthodoxEasterCalculator.calculateEaster(2099);
        assertNotNull(easter);
        assertEquals(2099, easter.getYear());
    }

    @Test
    void testGetMinYear() {
        assertEquals(1900, OrthodoxEasterCalculator.getMinYear());
    }

    @Test
    void testGetMaxYear() {
        assertEquals(2099, OrthodoxEasterCalculator.getMaxYear());
    }
}
