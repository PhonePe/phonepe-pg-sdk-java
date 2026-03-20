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
import java.lang.reflect.RecordComponent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MetaInfoTest {

	// ── Builder / record construction ─────────────────────────────────────

	@Test
	void testBuilderCreatesRecordWithAllUdfs() {
		MetaInfo metaInfo = MetaInfo.builder()
				.udf1("val1")
				.udf2("val2")
				.udf3("val3")
				.udf4("val4")
				.udf5("val5")
				.udf6("val6")
				.udf7("val7")
				.udf8("val8")
				.udf9("val9")
				.udf10("val10")
				.udf11("val11")
				.udf12("val12")
				.udf13("val13")
				.udf14("val14")
				.udf15("val15")
				.build();

		Assertions.assertEquals("val1", metaInfo.udf1());
		Assertions.assertEquals("val10", metaInfo.udf10());
		Assertions.assertEquals("val15", metaInfo.udf15());
	}

	@Test
	void testBuilderAllowsNullUdfs() {
		MetaInfo metaInfo = MetaInfo.builder().udf1("only-one").build();
		Assertions.assertEquals("only-one", metaInfo.udf1());
		Assertions.assertNull(metaInfo.udf2());
		Assertions.assertNull(metaInfo.udf11());
	}

	// ── @Size constraints via reflection ──────────────────────────────────

	@Test
	void testUdf1To10HaveMaxSize256() throws NoSuchMethodException {
		String[] freeTextFields = {"udf1", "udf2", "udf3", "udf4", "udf5", "udf6", "udf7", "udf8",
				"udf9", "udf10"};
		for (String fieldName : freeTextFields) {
			RecordComponent component = findComponent(fieldName);
			Assertions.assertNotNull(component,
					"Record component not found: " + fieldName);
			Size size = component.getAccessor().getAnnotation(Size.class);
			Assertions.assertNotNull(size,
					"@Size missing on " + fieldName);
			Assertions.assertEquals(256, size.max(),
					"@Size max should be 256 for " + fieldName);
		}
	}

	@Test
	void testUdf11To15HaveMaxSize50() {
		String[] restrictedFields = {"udf11", "udf12", "udf13", "udf14", "udf15"};
		for (String fieldName : restrictedFields) {
			RecordComponent component = findComponent(fieldName);
			Assertions.assertNotNull(component);
			Size size = component.getAccessor().getAnnotation(Size.class);
			Assertions.assertNotNull(size, "@Size missing on " + fieldName);
			Assertions.assertEquals(50, size.max(),
					"@Size max should be 50 for " + fieldName);
		}
	}

	// ── @Pattern constraints via reflection ───────────────────────────────

	@Test
	void testUdf11To15HavePatternAnnotation() {
		String[] restrictedFields = {"udf11", "udf12", "udf13", "udf14", "udf15"};
		String expectedRegexp = "^[a-zA-Z0-9_\\- @.+]+$";
		for (String fieldName : restrictedFields) {
			RecordComponent component = findComponent(fieldName);
			Assertions.assertNotNull(component);
			Pattern pattern = component.getAccessor().getAnnotation(Pattern.class);
			Assertions.assertNotNull(pattern, "@Pattern missing on " + fieldName);
			Assertions.assertEquals(expectedRegexp, pattern.regexp(),
					"@Pattern regexp mismatch on " + fieldName);
		}
	}

	@Test
	void testUdf1To10DoNotHavePatternAnnotation() {
		String[] freeTextFields = {"udf1", "udf2", "udf3", "udf4", "udf5", "udf6", "udf7", "udf8",
				"udf9", "udf10"};
		for (String fieldName : freeTextFields) {
			RecordComponent component = findComponent(fieldName);
			Assertions.assertNotNull(component);
			Assertions.assertNull(component.getAccessor().getAnnotation(Pattern.class),
					"udf1-10 should not have @Pattern: " + fieldName);
		}
	}

	// ── Expanded fields (udf6-15 are new) ────────────────────────────────

	@Test
	void testMetaInfoHas15UdfFields() {
		RecordComponent[] components = MetaInfo.class.getRecordComponents();
		Assertions.assertEquals(15, components.length,
				"MetaInfo should have exactly 15 udf fields");
	}

	// ── Helpers ───────────────────────────────────────────────────────────

	private RecordComponent findComponent(String name) {
		for (RecordComponent c : MetaInfo.class.getRecordComponents()) {
			if (c.getName().equals(name)) return c;
		}
		return null;
	}
}
