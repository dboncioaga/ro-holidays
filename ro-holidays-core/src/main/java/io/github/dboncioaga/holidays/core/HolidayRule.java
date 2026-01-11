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

import java.util.Set;

/**
 * Strategy interface for generating holidays for a given year.
 * <p>
 * Implementations of this interface define how specific holidays are calculated,
 * whether they are fixed dates (e.g., January 1st) or movable dates (e.g., Orthodox Easter).
 * </p>
 * <p>
 * Implementations should be stateless and thread-safe.
 * </p>
 */
public interface HolidayRule {

    /**
     * Computes the holidays for the specified year according to this rule.
     * <p>
     * The returned set must be immutable and may be empty if no holidays
     * apply for the given year according to this rule.
     * </p>
     *
     * @param year the year for which to compute holidays
     * @return an immutable set of holidays, never null
     * @throws IllegalArgumentException if the year is outside the supported range
     */
    Set<Holiday> holidaysFor(int year);
}
