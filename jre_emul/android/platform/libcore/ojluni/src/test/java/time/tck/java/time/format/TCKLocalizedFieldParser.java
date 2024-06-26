/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Copyright (c) 2010-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tck.java.time.format;

import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static org.junit.Assert.assertEquals;

import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.runner.RunWith;
import org.junit.Test;
import test.java.time.format.AbstractTestPrinterParser;

/**
 * Test TCKLocalizedFieldParser.
 */
@RunWith(DataProviderRunner.class)
public class TCKLocalizedFieldParser extends AbstractTestPrinterParser {

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_fieldPatterns() {
        return new Object[][] {
            {"e",  "6", 0, 1, 6},
            {"W",  "3", 0, 1, 3},
            {"w",  "29", 0, 2, 29},
            {"ww", "29", 0, 2, 29},
            {"Y", "2013", 0, 4, 2013},
            {"YY", "13", 0, 2, 2013},
            {"YYYY", "2013", 0, 4, 2013},
        };
    }

    @Test
    @UseDataProvider("provider_fieldPatterns")
    public void test_parse_textField(String pattern, String text, int pos, int expectedPos, long expectedValue) {
        WeekFields weekDef = WeekFields.of(locale);
        TemporalField field = null;
        switch(pattern.charAt(0)) {
            case 'e' :
                field = weekDef.dayOfWeek();
                break;
            case 'w':
                field = weekDef.weekOfWeekBasedYear();
                break;
            case 'W':
                field = weekDef.weekOfMonth();
                break;
            case 'Y':
                field = weekDef.weekBasedYear();
                break;
            default:
                throw new IllegalStateException("bad format letter from pattern");
        }
        ParsePosition ppos = new ParsePosition(pos);
        DateTimeFormatterBuilder b
                = new DateTimeFormatterBuilder().appendPattern(pattern);
        DateTimeFormatter dtf = b.toFormatter(locale);
        TemporalAccessor parsed = dtf.parseUnresolved(text, ppos);
        if (ppos.getErrorIndex() != -1) {
            assertEquals(ppos.getErrorIndex(), expectedPos);
        } else {
            assertEquals("Incorrect ending parse position", ppos.getIndex(), expectedPos);
            long value = parsed.getLong(field);
            assertEquals("Value incorrect for " + field, value, expectedValue);
        }
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_patternLocalDate() {
        return new Object[][] {
            {"e W M y",  "1 1 1 2012", 0, 10, LocalDate.of(2012, 1, 1)},
            {"e W M y",  "1 2 1 2012", 0, 10, LocalDate.of(2012, 1, 8)},
            {"e W M y",  "2 2 1 2012", 0, 10, LocalDate.of(2012, 1, 9)},
            {"e W M y",  "3 2 1 2012", 0, 10, LocalDate.of(2012, 1, 10)},
            {"e W M y",  "1 3 1 2012", 0, 10, LocalDate.of(2012, 1, 15)},
            {"e W M y",  "2 3 1 2012", 0, 10, LocalDate.of(2012, 1, 16)},
            {"e W M y",  "6 2 1 2012", 0, 10, LocalDate.of(2012, 1, 13)},
            {"e W M y",  "6 2 7 2012", 0, 10, LocalDate.of(2012, 7, 13)},
            {"'Date: 'y-MM', day-of-week: 'e', week-of-month: 'W",
                "Date: 2012-07, day-of-week: 6, week-of-month: 3", 0, 47, LocalDate.of(2012, 7, 20)},
        };
    }

   @Test
    @UseDataProvider("provider_patternLocalDate")
    public void test_parse_textLocalDate(String pattern, String text, int pos, int expectedPos, LocalDate expectedValue) {
        ParsePosition ppos = new ParsePosition(pos);
        DateTimeFormatterBuilder b = new DateTimeFormatterBuilder().appendPattern(pattern);
        DateTimeFormatter dtf = b.toFormatter(locale);
        TemporalAccessor parsed = dtf.parseUnresolved(text, ppos);
        if (ppos.getErrorIndex() != -1) {
            assertEquals(ppos.getErrorIndex(), expectedPos);
        } else {
            assertEquals("Incorrect ending parse position", ppos.getIndex(), expectedPos);
            assertEquals(parsed.isSupported(YEAR_OF_ERA), true);
            assertEquals(parsed.isSupported(WeekFields.of(locale).dayOfWeek()), true);
            assertEquals(parsed.isSupported(WeekFields.of(locale).weekOfMonth()) ||
                    parsed.isSupported(WeekFields.of(locale).weekOfYear()), true);
            // ensure combination resolves into a date
            LocalDate result = LocalDate.parse(text, dtf);
            assertEquals("LocalDate incorrect for " + pattern, result, expectedValue);
        }
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] provider_patternLocalWeekBasedYearDate() {
        return new Object[][] {
            //{"w Y",  "29 2012", 0, 7, LocalDate.of(2012, 7, 20)},  // Default lenient dayOfWeek not supported
            {"e w Y",  "6 29 2012", 0, 9, LocalDate.of(2012, 7, 20)},
            {"'Date: 'Y', day-of-week: 'e', week-of-year: 'w",
                "Date: 2012, day-of-week: 6, week-of-year: 29", 0, 44, LocalDate.of(2012, 7, 20)},
            {"Y-w-e",  "2008-01-1", 0, 9, LocalDate.of(2007, 12, 30)},
            {"Y-w-e",  "2008-52-1", 0, 9, LocalDate.of(2008, 12, 21)},
            {"Y-w-e",  "2008-52-7", 0, 9, LocalDate.of(2008, 12, 27)},
            {"Y-w-e",  "2009-01-01", 0, 10, LocalDate.of(2008, 12, 28)},
            {"Y-w-e",  "2009-01-04", 0, 10, LocalDate.of(2008, 12, 31)},
            {"Y-w-e",  "2009-01-05", 0, 10, LocalDate.of(2009, 1, 1)},
       };
    }

//     TODO(b/309715638): fix the matching error.
//     @Test
//     @UseDataProvider("provider_patternLocalWeekBasedYearDate")
//     public void test_parse_WeekBasedYear(String pattern, String text, int pos, int expectedPos, LocalDate expectedValue) {
//         ParsePosition ppos = new ParsePosition(pos);
//         DateTimeFormatterBuilder b = new DateTimeFormatterBuilder().appendPattern(pattern);
//         DateTimeFormatter dtf = b.toFormatter(locale);
//         TemporalAccessor parsed = dtf.parseUnresolved(text, ppos);
//         if (ppos.getErrorIndex() != -1) {
//             assertEquals(ppos.getErrorIndex(), expectedPos);
//         } else {
//             WeekFields weekDef = WeekFields.of(locale);
//             assertEquals("Incorrect ending parse position", ppos.getIndex(), expectedPos);
//             assertEquals(parsed.isSupported(weekDef.dayOfWeek()), pattern.indexOf('e') >= 0);
//             assertEquals(parsed.isSupported(weekDef.weekOfWeekBasedYear()), pattern.indexOf('w') >= 0);
//             assertEquals(parsed.isSupported(weekDef.weekBasedYear()), pattern.indexOf('Y') >= 0);
//             // ensure combination resolves into a date
//             LocalDate result = LocalDate.parse(text, dtf);
//             assertEquals("LocalDate incorrect for " + pattern + ", weekDef: " + weekDef, result, expectedValue);
//         }
//     }

}
