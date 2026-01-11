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

import java.time.LocalDate;
import java.util.Set;

/**
 * Holiday rule for Pentecost Monday (A doua zi de Rusalii).
 * <p>
 * Pentecost Monday falls 50 days after Orthodox Easter Sunday.
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 */
public final class PentecostMondayRule implements HolidayRule {

    @Override
    public Set<Holiday> holidaysFor(int year) {
        LocalDate easterDate = OrthodoxEasterCalculator.calculateEaster(year);
        LocalDate pentecostMonday = easterDate.plusDays(50);
        Holiday holiday = new Holiday(pentecostMonday, "A doua zi de Rusalii", HolidayType.LEGAL);
        return Set.of(holiday);
    }
}
