package com.zifo.gsk.barcodegeneration.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JSONUtilsTests {

	@Test
	void testPrivateConstructor() throws Exception {
		// Get the declared constructor from the utility class.
		Constructor<JSONUtils> constructor = JSONUtils.class.getDeclaredConstructor();

		// Make it accessible.
		constructor.setAccessible(true);

		// Expect that instantiating the utility class via its private constructor will
		// throw an exception.
		InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

		// Optionally verify that the cause is not null (or even check for a particular
		// exception type).
		assertNotNull(exception.getCause());
	}

	@ParameterizedTest
	@CsvSource(value = {
			// When table = "TestTable"
			"TestTable|{\"batch-request\":[{\"version\":\"1.0\"},[{\"api-id\":\"table.data\",\"api-version\":\"1.0\"},{\"data\":{\"queries\":[{\"table\":\"TestTable\",\"range\":\"\"}]}}]]}",
			// When table is an empty string
			"' '|{\"batch-request\":[{\"version\":\"1.0\"},[{\"api-id\":\"table.data\",\"api-version\":\"1.0\"},{\"data\":{\"queries\":[{\"table\":\" \",\"range\":\"\"}]}}]]}",
			// When table = null (Note: CSVSource passes the literal string "null")
			"null|{\"batch-request\":[{\"version\":\"1.0\"},[{\"api-id\":\"table.data\",\"api-version\":\"1.0\"},{\"data\":{\"queries\":[{\"table\":\"null\",\"range\":\"\"}]}}]]}" }, delimiter = '|')
	void testCreateJSONToReadTableData(String table, String expected) {
		// When
		String actual = JSONUtils.createJSONToReadTableData(table);

		// Then
		assertEquals(expected, actual);
	}

}
