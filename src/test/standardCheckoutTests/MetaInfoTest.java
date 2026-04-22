/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package standardCheckoutTests;

import com.phonepe.sdk.pg.common.models.MetaInfo;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MetaInfoTest {

	// ── Builder construction ──────────────────────────────────────────────

	@Test
	void testBuilderCreatesInstanceWithAllUdfs() {
		MetaInfo metaInfo = MetaInfo.builder()
				.udf1("val1").udf2("val2").udf3("val3").udf4("val4").udf5("val5")
				.udf6("val6").udf7("val7").udf8("val8").udf9("val9").udf10("val10")
				.udf11("val11").udf12("val12").udf13("val13").udf14("val14").udf15("val15")
				.build();

		Assertions.assertEquals("val1", metaInfo.getUdf1());
		Assertions.assertEquals("val10", metaInfo.getUdf10());
		Assertions.assertEquals("val15", metaInfo.getUdf15());
	}

	@Test
	void testBuilderAllowsNullUdfs() {
		MetaInfo metaInfo = MetaInfo.builder().udf1("only-one").build();
		Assertions.assertEquals("only-one", metaInfo.getUdf1());
		Assertions.assertNull(metaInfo.getUdf2());
		Assertions.assertNull(metaInfo.getUdf11());
	}

	@Test
	void testNoArgConstructorCreatesInstanceWithNullFields() {
		MetaInfo metaInfo = new MetaInfo();
		Assertions.assertNull(metaInfo.getUdf1());
		Assertions.assertNull(metaInfo.getUdf5());
		Assertions.assertNull(metaInfo.getUdf15());
	}

	@Test
	void testMetaInfoHas15UdfFields() {
		long udfFieldCount = java.util.Arrays.stream(MetaInfo.class.getDeclaredFields())
				.filter(f -> f.getName().startsWith("udf"))
				.count();
		Assertions.assertEquals(15, udfFieldCount, "MetaInfo should have exactly 15 udf fields");
	}

	@Test
	void testUdf6To15FieldsExist() throws NoSuchFieldException {
		for (int i = 6; i <= 15; i++) {
			Field field = MetaInfo.class.getDeclaredField("udf" + i);
			Assertions.assertNotNull(field);
			Assertions.assertEquals(String.class, field.getType());
		}
	}

	// ── udf1-10: @Size(max=256) validation ───────────────────────────────

	@Test
	void testUdf1To10AcceptsExactly256Chars() {
		String val256 = "a".repeat(256);
		MetaInfo metaInfo = MetaInfo.builder().udf1(val256).udf10(val256).build();
		Assertions.assertEquals(val256, metaInfo.getUdf1());
		Assertions.assertEquals(val256, metaInfo.getUdf10());
	}

	@Test
	void testUdf1To10RejectsOver256Chars() {
		String val257 = "a".repeat(257);
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
				() -> MetaInfo.builder().udf5(val257).build());
		Assertions.assertTrue(ex.getMessage().contains("udf5"));
		Assertions.assertTrue(ex.getMessage().contains("256"));
	}

	// ── udf11-15: @Size(max=50) + @Pattern validation ────────────────────

	@Test
	void testUdf11To15AcceptsValidChars() {
		MetaInfo metaInfo = MetaInfo.builder()
				.udf11("Hello World").udf12("user@email.com")
				.udf13("id_123").udf14("ref-456").udf15("val+1")
				.build();
		Assertions.assertEquals("Hello World", metaInfo.getUdf11());
		Assertions.assertEquals("user@email.com", metaInfo.getUdf12());
	}

	@Test
	void testUdf11To15RejectsOver50Chars() {
		String val51 = "a".repeat(51);
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
				() -> MetaInfo.builder().udf11(val51).build());
		Assertions.assertTrue(ex.getMessage().contains("udf11"));
		Assertions.assertTrue(ex.getMessage().contains("50"));
	}

	@Test
	void testUdf11To15RejectsInvalidChars() {
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
				() -> MetaInfo.builder().udf11("invalid!#$").build());
		Assertions.assertTrue(ex.getMessage().contains("udf11"));
	}

	@Test
	void testUdf11To15AllowsEmptyString() {
		MetaInfo metaInfo = MetaInfo.builder().udf11("").build();
		Assertions.assertEquals("", metaInfo.getUdf11());
	}

	@Test
	void testUdf11To15AllowsNull() {
		Assertions.assertDoesNotThrow(() -> MetaInfo.builder().udf11(null).build());
	}

	// ── New fields (udf6-10) specifically validated ───────────────────────

	@Test
	void testNewFieldsUdf6To10RejectOver256Chars() {
		String val257 = "a".repeat(257);
		for (String field : new String[]{"udf6", "udf7", "udf8", "udf9", "udf10"}) {
			IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
					() -> MetaInfo.builder().udf6(field.equals("udf6") ? val257 : "ok")
							.udf7(field.equals("udf7") ? val257 : "ok")
							.udf8(field.equals("udf8") ? val257 : "ok")
							.udf9(field.equals("udf9") ? val257 : "ok")
							.udf10(field.equals("udf10") ? val257 : "ok")
							.build(),
					"Expected rejection for " + field);
			Assertions.assertTrue(ex.getMessage().contains(field));
		}
	}

	@Test
	void testUdf12To15EachRejectInvalidChars() {
		String[][] cases = {
			{"udf12", "bad!char"}, {"udf13", "no#hash"}, {"udf14", "no*star"}, {"udf15", "no%pct"}
		};
		for (String[] tc : cases) {
			String fieldName = tc[0];
			String badValue = tc[1];
			IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
					() -> MetaInfo.builder().udf12(fieldName.equals("udf12") ? badValue : "ok")
							.udf13(fieldName.equals("udf13") ? badValue : "ok")
							.udf14(fieldName.equals("udf14") ? badValue : "ok")
							.udf15(fieldName.equals("udf15") ? badValue : "ok")
							.build(),
					"Expected rejection for " + fieldName);
			Assertions.assertTrue(ex.getMessage().contains(fieldName));
		}
	}

	@Test
	void testUdf1To10DoNotApplyPatternRestriction() {
		// Special chars that would fail udf11-15 pattern — should be fine for udf1-10
		String specialChars = "hello!#$%^&*()";
		Assertions.assertDoesNotThrow(() -> MetaInfo.builder()
				.udf1(specialChars).udf6(specialChars).udf10(specialChars).build());
	}

	// ── @Data generated: setters (udf6-15), equals, hashCode, toString ───

	@Test
	void testSettersUdf6To15() {
		MetaInfo metaInfo = new MetaInfo();
		metaInfo.setUdf6("v6");
		metaInfo.setUdf7("v7");
		metaInfo.setUdf8("v8");
		metaInfo.setUdf9("v9");
		metaInfo.setUdf10("v10");
		metaInfo.setUdf11("v11");
		metaInfo.setUdf12("v12");
		metaInfo.setUdf13("v13");
		metaInfo.setUdf14("v14");
		metaInfo.setUdf15("v15");

		Assertions.assertEquals("v6", metaInfo.getUdf6());
		Assertions.assertEquals("v7", metaInfo.getUdf7());
		Assertions.assertEquals("v8", metaInfo.getUdf8());
		Assertions.assertEquals("v9", metaInfo.getUdf9());
		Assertions.assertEquals("v10", metaInfo.getUdf10());
		Assertions.assertEquals("v11", metaInfo.getUdf11());
		Assertions.assertEquals("v12", metaInfo.getUdf12());
		Assertions.assertEquals("v13", metaInfo.getUdf13());
		Assertions.assertEquals("v14", metaInfo.getUdf14());
		Assertions.assertEquals("v15", metaInfo.getUdf15());
	}

	// ── equals(): header branches ─────────────────────────────────────────

	@Test
	void testEqualsSameInstance() {
		MetaInfo a = MetaInfo.builder().udf1("x").build();
		Assertions.assertEquals(a, a);
	}

	@Test
	void testEqualsNullReturnsFalse() {
		MetaInfo a = MetaInfo.builder().udf1("x").build();
		Assertions.assertNotEquals(a, null);
	}

	@Test
	void testEqualsDifferentTypeReturnsFalse() {
		MetaInfo a = MetaInfo.builder().udf1("x").build();
		Assertions.assertNotEquals(a, "not a MetaInfo");
	}

	@Test
	void testEqualsAllFieldsNullBothObjects() {
		// Covers: this.fieldN == null AND other.fieldN == null (proceed) for all 15 fields
		Assertions.assertEquals(new MetaInfo(), new MetaInfo());
	}

	@Test
	void testEqualsAllFieldsNonNullAndEqual() {
		// Covers: this.fieldN != null AND this.fieldN.equals(other.fieldN) (proceed) for all 15 fields
		MetaInfo a = MetaInfo.builder()
				.udf1("v1").udf2("v2").udf3("v3").udf4("v4").udf5("v5")
				.udf6("v6").udf7("v7").udf8("v8").udf9("v9").udf10("v10")
				.udf11("v11").udf12("v12").udf13("v13").udf14("v14").udf15("v15")
				.build();
		MetaInfo b = MetaInfo.builder()
				.udf1("v1").udf2("v2").udf3("v3").udf4("v4").udf5("v5")
				.udf6("v6").udf7("v7").udf8("v8").udf9("v9").udf10("v10")
				.udf11("v11").udf12("v12").udf13("v13").udf14("v14").udf15("v15")
				.build();
		Assertions.assertEquals(a, b);
	}

	// ── equals(): per-field "this=null, other=non-null → false" branch ───
	// All preceding fields are null in both objects so we reach field N.

	@Test
	void testEqualsNullVsNonNullPerField() {
		// udf1
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 1, "v"));
		// udf2 (udf1 matches: both null)
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 2, "v"));
		// udf3
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 3, "v"));
		// udf4
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 4, "v"));
		// udf5
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 5, "v"));
		// udf6
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 6, "v"));
		// udf7
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 7, "v"));
		// udf8
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 8, "v"));
		// udf9
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 9, "v"));
		// udf10
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 10, "v"));
		// udf11
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 11, "v"));
		// udf12
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 12, "v"));
		// udf13
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 13, "v"));
		// udf14
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 14, "v"));
		// udf15
		Assertions.assertNotEquals(new MetaInfo(), setField(new MetaInfo(), 15, "v"));
	}

	// ── equals(): per-field "this!=null, !this.equals(other) → false" branch ─
	// All preceding fields are null in both; field N differs (both non-null, different values).

	@Test
	void testEqualsNonNullDifferentValuePerField() {
		Assertions.assertNotEquals(setField(new MetaInfo(), 1, "a"), setField(new MetaInfo(), 1, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 2, "a"), setField(new MetaInfo(), 2, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 3, "a"), setField(new MetaInfo(), 3, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 4, "a"), setField(new MetaInfo(), 4, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 5, "a"), setField(new MetaInfo(), 5, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 6, "a"), setField(new MetaInfo(), 6, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 7, "a"), setField(new MetaInfo(), 7, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 8, "a"), setField(new MetaInfo(), 8, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 9, "a"), setField(new MetaInfo(), 9, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 10, "a"), setField(new MetaInfo(), 10, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 11, "a"), setField(new MetaInfo(), 11, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 12, "a"), setField(new MetaInfo(), 12, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 13, "a"), setField(new MetaInfo(), 13, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 14, "a"), setField(new MetaInfo(), 14, "b"));
		Assertions.assertNotEquals(setField(new MetaInfo(), 15, "a"), setField(new MetaInfo(), 15, "b"));
	}

	// ── hashCode(): various null/non-null combinations ───────────────────

	@Test
	void testHashCodeConsistency() {
		MetaInfo a = MetaInfo.builder().udf1("x").udf11("y").build();
		MetaInfo b = MetaInfo.builder().udf1("x").udf11("y").build();
		Assertions.assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	void testHashCodeAllNullFields() {
		MetaInfo a = new MetaInfo();
		// Just verify it doesn't throw and is stable
		Assertions.assertEquals(a.hashCode(), a.hashCode());
	}

	@Test
	void testHashCodeAllNonNullFields() {
		MetaInfo a = MetaInfo.builder()
				.udf1("v1").udf2("v2").udf3("v3").udf4("v4").udf5("v5")
				.udf6("v6").udf7("v7").udf8("v8").udf9("v9").udf10("v10")
				.udf11("v11").udf12("v12").udf13("v13").udf14("v14").udf15("v15")
				.build();
		MetaInfo b = MetaInfo.builder()
				.udf1("v1").udf2("v2").udf3("v3").udf4("v4").udf5("v5")
				.udf6("v6").udf7("v7").udf8("v8").udf9("v9").udf10("v10")
				.udf11("v11").udf12("v12").udf13("v13").udf14("v14").udf15("v15")
				.build();
		Assertions.assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	void testHashCodeDifferentWhenFieldsDiffer() {
		MetaInfo a = MetaInfo.builder().udf1("x").build();
		MetaInfo b = MetaInfo.builder().udf1("y").build();
		Assertions.assertNotEquals(a.hashCode(), b.hashCode());
	}

	// ── toString ─────────────────────────────────────────────────────────

	@Test
	void testToString() {
		MetaInfo metaInfo = MetaInfo.builder().udf1("hello").udf11("world").build();
		String str = metaInfo.toString();

		Assertions.assertNotNull(str);
		Assertions.assertTrue(str.contains("hello"));
		Assertions.assertTrue(str.contains("world"));
	}

	// ── Helper ───────────────────────────────────────────────────────────

	/** Sets udfN (1-15) on a MetaInfo instance via the appropriate setter. */
	private static MetaInfo setField(MetaInfo m, int n, String value) {
		switch (n) {
			case 1: m.setUdf1(value); break;
			case 2: m.setUdf2(value); break;
			case 3: m.setUdf3(value); break;
			case 4: m.setUdf4(value); break;
			case 5: m.setUdf5(value); break;
			case 6: m.setUdf6(value); break;
			case 7: m.setUdf7(value); break;
			case 8: m.setUdf8(value); break;
			case 9: m.setUdf9(value); break;
			case 10: m.setUdf10(value); break;
			case 11: m.setUdf11(value); break;
			case 12: m.setUdf12(value); break;
			case 13: m.setUdf13(value); break;
			case 14: m.setUdf14(value); break;
			case 15: m.setUdf15(value); break;
		}
		return m;
	}
}

