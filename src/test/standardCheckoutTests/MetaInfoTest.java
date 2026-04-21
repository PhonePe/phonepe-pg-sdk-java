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
}

