/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class ResourceBackedDealershipTest {
    private static final Vehicle DUMMY_VEHICLE = new Vehicle(6, 7, "M", "D", "T", "C", 8, 9);
    private static final String SERIALIZED_VEHICLE = System.lineSeparator() + "6|7|M|D|T|C|8|9.00";
    private Dealership dealership;
    private TestDealershipImpl wrapped;
    private StringWriter writer;
    private int readCount, writeCount, appendCount;

    @BeforeEach
    void createWriter() {
        writer = new StringWriter();
    }

    @Nested
    class EmptyFile {
        private static final String FILE_HEADER = "TEST_DISPLAY_NAME|TEST_ADDRESS|TEST_PHONE";

        @BeforeEach
        void createDealership() {
            wrapped = new TestDealershipImpl();
            dealership = new ResourceBackedDealership(
                wrapped,
                () -> {
                    readCount++;
                    return new StringReader("");
                },
                append -> {
                    writeCount++;
                    if (append) appendCount++;
                    else createWriter();
                    return writer;
                }
            );
        }

        @Test
        void test_reads() {
            assertEquals(1, readCount, "Should read from supplied resource");
        }

        @Test
        void test_doesNotModify() {
            assertAll(
                () -> assertEquals(0, wrapped.countClear, "An empty file should not clear the backing implementation"),
                () -> assertEquals(0, wrapped.countAdd, "Should not add to wrapped"),
                () -> assertEquals(0, wrapped.countAddAll, "Should not addAll to wrapped")
            );
        }

        @Test
        void test_updatesFile() {
            assertEquals(FILE_HEADER, writer.toString(), "Class should have written provided metadata");
            assertEquals(1, writeCount, "Should only write once");
            assertEquals(0, appendCount, "Should overwrite");
        }

        @Test
        void test_getPhone() {
            assertEquals(new TestDealershipImpl().getPhone(), dealership.getPhone(), "getPhone() should return the wrapped phone number.");
        }

        @Test
        void test_getAddress() {
            assertEquals(new TestDealershipImpl().getAddress(), dealership.getAddress(), "getAddress() should return the wrapped address.");
        }

        @Test
        void test_getDisplayName() {
            assertEquals(new TestDealershipImpl().getDisplayName(), dealership.getDisplayName(), "getDisplayName() should return the wrapped display name.");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4})
        void test_getAllVehicles(int count) {
            wrapped.getAllResult = Collections.nCopies(count, DUMMY_VEHICLE);
            wrapped.countGetAll = 0;

            var all = dealership.getAllVehicles();

            assertArrayEquals(wrapped.getAllResult.toArray(), all.toArray(), "Should delegate getAllVehicles() to wrapped class");
            assertEquals(1, wrapped.countGetAll, "Should only delegate once");
        }

        @Test
        void test_remove_fails() {
            wrapped.removeSuccess = false;
            wrapped.countRemove = 0;
            writeCount = 0;

            var result = dealership.remove(DUMMY_VEHICLE);

            assertAll(
                "remove()",
                () -> assertFalse(result, "Removing vehicle should fail when delegated removal fails"),
                () -> assertEquals(1, wrapped.countRemove, "Removal should delegate to wrapped class once"),
                () -> assertEquals(0, writeCount, "Should not write on failure")
            );
        }

        @Test
        void test_remove_succeeds() {
            wrapped.removeSuccess = true;
            wrapped.countRemove = 0;
            writeCount = 0;
            appendCount = 0;

            var result = dealership.remove(DUMMY_VEHICLE);

            assertAll(
                "remove()",
                () -> assertTrue(result, "Removing vehicle should succeed when delegated removal succeeds"),
                () -> assertEquals(1, wrapped.countRemove, "Removal should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(0, appendCount, "Should overwrite on success"),
                () -> assertEquals(FILE_HEADER, writer.toString(), "Class should have written provided metadata")
            );
        }

        @Test
        void test_clear() {
            wrapped.countClear = 0;
            writeCount = 0;
            appendCount = 0;

            assertDoesNotThrow(dealership::clear, "Clearing should not fail");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countClear, "Clearing should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(0, appendCount, "Should overwrite on success"),
                () -> assertEquals(FILE_HEADER, writer.toString(), "Class should have written provided metadata")
            );
        }

        @Test
        void test_add() {
            wrapped.countAdd = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.add(DUMMY_VEHICLE), "Adding a vehicle should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAdd, "Adding should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicle correctly"),
                () -> assertEquals(DUMMY_VEHICLE, wrapped.addArgument, "Should add vehicle unchanged")
            );
        }

        @Test
        void test_addAll_singleItem() {
            wrapped.countAddAll = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE)), "Adding one vehicle should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAddAll, "AddAll should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicle correctly"),
                () -> assertArrayEquals(new Vehicle[]{DUMMY_VEHICLE}, wrapped.addAllArgument.toArray(), "Should add vehicles unchanged")
            );
        }

        @Test
        void test_addAll_multipleItems() {
            wrapped.countAddAll = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE, DUMMY_VEHICLE, DUMMY_VEHICLE)), "Adding three vehicles should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAddAll, "AddAll should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE + SERIALIZED_VEHICLE + SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicles correctly"),
                () -> assertArrayEquals(new Vehicle[]{DUMMY_VEHICLE, DUMMY_VEHICLE, DUMMY_VEHICLE}, wrapped.addAllArgument.toArray(), "Should add vehicles unchanged")
            );
        }
    }

    @Nested
    class HeaderFile {
        private static final String FILE_DISPLAY_NAME = "FILE_DISPLAY_NAME";
        private static final String FILE_ADDRESS = "FILE_ADDRESS";
        private static final String FILE_PHONE = "FILE_PHONE";
        private static final String FILE_HEADER = "$FILE_DISPLAY_NAME|$FILE_ADDRESS|$FILE_PHONE";

        @BeforeEach
        void createDealership() {
            wrapped = new TestDealershipImpl();
            dealership = new ResourceBackedDealership(
                wrapped,
                () -> {
                    readCount++;
                    return new StringReader(FILE_HEADER);
                },
                append -> {
                    writeCount++;
                    if (append) appendCount++;
                    else createWriter();
                    return writer;
                }
            );
        }

        @Test
        void test_reads() {
            assertEquals(1, readCount, "Should read from supplied resource");
        }

        @Test
        void test_modifies() {
            assertAll(
                () -> assertEquals(1, wrapped.countClear, "An header-only file should clear the backing implementation"),
                () -> assertEquals(0, wrapped.countAdd, "Should not add to wrapped"),
                () -> assertEquals(0, wrapped.countAddAll, "Should not addAll to wrapped")
            );
        }

        @Test
        void test_updatesFile() {
            assertEquals(0, writeCount, "Header file should not write");
        }

        @Test
        void test_getPhone() {
            assumeFalse(FILE_PHONE.equals(new TestDealershipImpl().getPhone()), "Phones should be different to test");

            assertEquals(FILE_PHONE, dealership.getPhone(), "getPhone() should return the wrapped phone number.");
        }

        @Test
        void test_getAddress() {
            assumeFalse(FILE_ADDRESS.equals(new TestDealershipImpl().getAddress()), "Addresses should be different to test");

            assertEquals(FILE_ADDRESS, dealership.getAddress(), "getAddress() should return the wrapped address.");
        }

        @Test
        void test_getDisplayName() {
            assumeFalse(FILE_DISPLAY_NAME.equals(new TestDealershipImpl().getDisplayName()), "Names should be different to test");

            assertEquals(FILE_DISPLAY_NAME, dealership.getDisplayName(), "getDisplayName() should return the wrapped display name.");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4})
        void test_getAllVehicles(int count) {
            wrapped.getAllResult = Collections.nCopies(count, DUMMY_VEHICLE);
            wrapped.countGetAll = 0;

            var all = dealership.getAllVehicles();

            assertArrayEquals(wrapped.getAllResult.toArray(), all.toArray(), "Should delegate getAllVehicles() to wrapped class");
            assertEquals(1, wrapped.countGetAll, "Should only delegate once");
        }

        @Test
        void test_remove_fails() {
            wrapped.removeSuccess = false;
            wrapped.countRemove = 0;
            writeCount = 0;

            var result = dealership.remove(DUMMY_VEHICLE);

            assertAll(
                "remove()",
                () -> assertFalse(result, "Removing vehicle should fail when delegated removal fails"),
                () -> assertEquals(1, wrapped.countRemove, "Removal should delegate to wrapped class once"),
                () -> assertEquals(0, writeCount, "Should not write on failure")
            );
        }

        @Test
        void test_remove_succeeds() {
            wrapped.removeSuccess = true;
            wrapped.countRemove = 0;
            writeCount = 0;
            appendCount = 0;

            var result = dealership.remove(DUMMY_VEHICLE);

            assertAll(
                "remove()",
                () -> assertTrue(result, "Removing vehicle should succeed when delegated removal succeeds"),
                () -> assertEquals(1, wrapped.countRemove, "Removal should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(0, appendCount, "Should overwrite on success"),
                () -> assertEquals(FILE_HEADER, writer.toString(), "Class should have written provided metadata")
            );
        }

        @Test
        void test_clear() {
            wrapped.countClear = 0;
            writeCount = 0;
            appendCount = 0;

            assertDoesNotThrow(dealership::clear, "Clearing should not fail");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countClear, "Clearing should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(0, appendCount, "Should overwrite on success"),
                () -> assertEquals(FILE_HEADER, writer.toString(), "Class should have written provided metadata")
            );
        }

        @Test
        void test_add() {
            wrapped.countAdd = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.add(DUMMY_VEHICLE), "Adding a vehicle should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAdd, "Adding should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicle correctly"),
                () -> assertEquals(DUMMY_VEHICLE, wrapped.addArgument, "Should add vehicle unchanged")
            );
        }

        @Test
        void test_addAll_singleItem() {
            wrapped.countAddAll = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE)), "Adding one vehicle should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAddAll, "AddAll should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicle correctly"),
                () -> assertArrayEquals(new Vehicle[]{DUMMY_VEHICLE}, wrapped.addAllArgument.toArray(), "Should add vehicles unchanged")
            );
        }

        @Test
        void test_addAll_multipleItems() {
            wrapped.countAddAll = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE, DUMMY_VEHICLE, DUMMY_VEHICLE)), "Adding three vehicles should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAddAll, "AddAll should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE + SERIALIZED_VEHICLE + SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicles correctly"),
                () -> assertArrayEquals(new Vehicle[]{DUMMY_VEHICLE, DUMMY_VEHICLE, DUMMY_VEHICLE}, wrapped.addAllArgument.toArray(), "Should add vehicles unchanged")
            );
        }
    }

    @Nested
    class BodyFile {
        private static final String FILE_DISPLAY_NAME = "FILE_DISPLAY_NAME";
        private static final String FILE_ADDRESS = "FILE_ADDRESS";
        private static final String FILE_PHONE = "FILE_PHONE";
        private static final String FILE_HEADER = "$FILE_DISPLAY_NAME|$FILE_ADDRESS|$FILE_PHONE";
        private static final String FILE_DATA = "$FILE_HEADER$SERIALIZED_VEHICLE";

        @BeforeEach
        void createDealership() {
            wrapped = new TestDealershipImpl();
            dealership = new ResourceBackedDealership(
                wrapped,
                () -> {
                    readCount++;
                    return new StringReader(FILE_DATA);
                },
                append -> {
                    writeCount++;
                    if (append) appendCount++;
                    else createWriter();
                    return writer;
                }
            );
        }

        @Test
        void test_reads() {
            assertEquals(1, readCount, "Should read from supplied resource");
        }

        @Test
        void test_modifies() {
            assertAll(
                () -> assertEquals(1, wrapped.countClear, "An given file should clear the backing implementation"),
                () -> assertEquals(0, wrapped.countAdd, "Should not add to wrapped"),
                () -> assertEquals(1, wrapped.countAddAll, "Should addAll once to wrapped"),
                () -> assertArrayEquals(new Vehicle[]{DUMMY_VEHICLE}, wrapped.addAllArgument.toArray(), "Should add serialized vehicle")
            );
        }

        @Test
        void test_updatesFile() {
            assertEquals(0, writeCount, "Given file should not write");
        }

        @Test
        void test_getPhone() {
            assumeFalse(FILE_PHONE.equals(new TestDealershipImpl().getPhone()), "Phones should be different to test");

            assertEquals(FILE_PHONE, dealership.getPhone(), "getPhone() should return the file phone number.");
        }

        @Test
        void test_getAddress() {
            assumeFalse(FILE_ADDRESS.equals(new TestDealershipImpl().getAddress()), "Addresses should be different to test");

            assertEquals(FILE_ADDRESS, dealership.getAddress(), "getAddress() should return the file address.");
        }

        @Test
        void test_getDisplayName() {
            assumeFalse(FILE_DISPLAY_NAME.equals(new TestDealershipImpl().getDisplayName()), "Names should be different to test");

            assertEquals(FILE_DISPLAY_NAME, dealership.getDisplayName(), "getDisplayName() should return the file display name.");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4})
        void test_getAllVehicles(int count) {
            wrapped.getAllResult = Collections.nCopies(count, DUMMY_VEHICLE);
            wrapped.countGetAll = 0;

            var all = dealership.getAllVehicles();

            assertArrayEquals(wrapped.getAllResult.toArray(), all.toArray(), "Should delegate getAllVehicles() to wrapped class");
            assertEquals(1, wrapped.countGetAll, "Should only delegate once");
        }

        @Test
        void test_remove_fails() {
            wrapped.removeSuccess = false;
            wrapped.countRemove = 0;
            writeCount = 0;

            var result = dealership.remove(DUMMY_VEHICLE);

            assertAll(
                "remove()",
                () -> assertFalse(result, "Removing vehicle should fail when delegated removal fails"),
                () -> assertEquals(1, wrapped.countRemove, "Removal should delegate to wrapped class once"),
                () -> assertEquals(0, writeCount, "Should not write on failure")
            );
        }

        @Test
        void test_remove_succeeds() {
            wrapped.removeSuccess = true;
            wrapped.countRemove = 0;
            writeCount = 0;
            appendCount = 0;

            var result = dealership.remove(DUMMY_VEHICLE);

            assertAll(
                "remove()",
                () -> assertTrue(result, "Removing vehicle should succeed when delegated removal succeeds"),
                () -> assertEquals(1, wrapped.countRemove, "Removal should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(0, appendCount, "Should overwrite on success"),
                () -> assertEquals(FILE_HEADER, writer.toString(), "Class should have written provided metadata")
            );
        }

        @Test
        void test_clear() {
            wrapped.countClear = 0;
            writeCount = 0;
            appendCount = 0;

            assertDoesNotThrow(dealership::clear, "Clearing should not fail");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countClear, "Clearing should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(0, appendCount, "Should overwrite on success"),
                () -> assertEquals(FILE_HEADER, writer.toString(), "Class should have written provided metadata")
            );
        }

        @Test
        void test_add() {
            wrapped.countAdd = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.add(DUMMY_VEHICLE), "Adding a vehicle should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAdd, "Adding should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicle correctly"),
                () -> assertEquals(DUMMY_VEHICLE, wrapped.addArgument, "Should add vehicle unchanged")
            );
        }

        @Test
        void test_addAll_singleItem() {
            wrapped.countAddAll = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE)), "Adding one vehicle should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAddAll, "AddAll should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicle correctly"),
                () -> assertArrayEquals(new Vehicle[]{DUMMY_VEHICLE}, wrapped.addAllArgument.toArray(), "Should add vehicles unchanged")
            );
        }

        @Test
        void test_addAll_multipleItems() {
            wrapped.countAddAll = 0;
            writeCount = 0;
            appendCount = 0;
            createWriter();

            assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE, DUMMY_VEHICLE, DUMMY_VEHICLE)), "Adding three vehicles should succeed");

            assertAll(
                "remove()",
                () -> assertEquals(1, wrapped.countAddAll, "AddAll should delegate to wrapped class once"),
                () -> assertEquals(1, writeCount, "Should write to file on success"),
                () -> assertEquals(1, appendCount, "Should append on success"),
                () -> assertEquals(SERIALIZED_VEHICLE + SERIALIZED_VEHICLE + SERIALIZED_VEHICLE, writer.toString(), "Class should have written vehicles correctly"),
                () -> assertArrayEquals(new Vehicle[]{DUMMY_VEHICLE, DUMMY_VEHICLE, DUMMY_VEHICLE}, wrapped.addAllArgument.toArray(), "Should add vehicles unchanged")
            );
        }
    }

    @Nested
    class Writing {
        private static final String FILE_HEADER = "TEST_DISPLAY_NAME|TEST_ADDRESS|TEST_PHONE";

        @Test
        void test_emptyFileIsWritten() {
            wrapped = new TestDealershipImpl();
            wrapped.getAllResult = List.of(DUMMY_VEHICLE);

            dealership = new ResourceBackedDealership(
                wrapped,
                () -> {
                    readCount++;
                    return new StringReader("");
                },
                append -> {
                    writeCount++;
                    if (append) appendCount++;
                    else createWriter();
                    return writer;
                }
            );

            assertAll(
                () -> assertEquals(1, writeCount, "Should write to file"),
                () -> assertEquals(0, appendCount, "Should overwrite file"),
                () -> assertEquals(FILE_HEADER + SERIALIZED_VEHICLE, writer.toString(), "Should write correct data to file")
            );
        }
    }


    @Nested
    class EdgeCases {
        private static final String FILE_HEADER = "TEST_DISPLAY_NAME|TEST_ADDRESS|TEST_PHONE";
        private static final String FILE_DATA = """
            $FILE_HEADER
            A|B|C|D|E|F|G|H
            0|B|C|D|E|F|G|H
            0|0|C|D|E|F|G|H
            0|0|C|D|E|F|0|H
            0|0|0
                        \s
            0|0|0|0|0|0|0|0|0|0
            """;

        @Test
        void test_badFileHeader() {
            wrapped = new TestDealershipImpl();
            wrapped.emptyData = true;

            assertThrows(IOException.class,
                () -> new ResourceBackedDealership(
                    wrapped,
                    () -> {
                        readCount++;
                        return new StringReader("|");
                    },
                    append -> {
                        writeCount++;
                        if (append) appendCount++;
                        else createWriter();
                        return writer;
                    }
                ), "Should not merge bad data");
        }

        @Test
        void test_badData() {
            wrapped = new TestDealershipImpl();

            assertDoesNotThrow(
                () -> new ResourceBackedDealership(
                    wrapped,
                    () -> {
                        readCount++;
                        return new StringReader(FILE_DATA);
                    },
                    append -> {
                        writeCount++;
                        if (append) appendCount++;
                        else createWriter();
                        return writer;
                    }
                ), "Should merge bad data");

            assertAll(
                () -> assertEquals(0, wrapped.countAddAll, "Should not add invalid vehicles"),
                () -> assertEquals(1, writeCount, "Should rewrite file"),
                () -> assertEquals(FILE_HEADER, writer.toString(), "Should rewrite file correctly")
            );
        }
    }
}