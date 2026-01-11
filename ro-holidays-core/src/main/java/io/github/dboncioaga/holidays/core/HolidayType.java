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

/**
 * Enumeration of holiday types in Romania.
 * <p>
 * This enum categorizes holidays into different types based on their legal status
 * and observance requirements.
 * </p>
 */
public enum HolidayType {
    /**
     * Legal public holidays - days off mandated by law.
     * Work is generally prohibited, and employees are entitled to time off with pay.
     */
    LEGAL,

    /**
     * Religious holidays recognized by Romanian law.
     * These may or may not be legal public holidays depending on specific circumstances.
     */
    RELIGIOUS,

    /**
     * Optional holidays that may be observed by certain groups or organizations.
     * Not mandatory days off under Romanian labor law.
     */
    OPTIONAL
}
