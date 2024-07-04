package ch.zhaw.fswd.powerdate.namingstrategy;

import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.hibernate.boot.model.naming.Identifier;

class CustomPhysicalNamingStrategyTest {
    @Mock
    private JdbcEnvironment jdbcEnvironment;

    private CustomPhysicalNamingStrategy namingStrategy;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        namingStrategy = new CustomPhysicalNamingStrategy();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @ParameterizedTest
    @CsvSource({
            "myTable, T_MYTABLE",
            "myTableDBO, T_MYTABLE",
            "myTabledbo, T_MYTABLE"
    })
    void testToPhysicalTableName(String input, String expected) {
        Identifier original = Identifier.toIdentifier(input);
        Identifier transformed = namingStrategy.toPhysicalTableName(original, jdbcEnvironment);
        Assertions.assertEquals(expected, transformed.getText());
    }

    @Test
    void testToPhysicalSequenceName() {
        Identifier original = Identifier.toIdentifier("mySequence");
        Identifier transformed = namingStrategy.toPhysicalSequenceName(original, jdbcEnvironment);
        Assertions.assertEquals("SEQ_MYSEQUENCE", transformed.getText());
    }

    @Test
    void testToPhysicalColumnName() {
        Identifier original = Identifier.toIdentifier("streetNr");
        Identifier transformed = namingStrategy.toPhysicalColumnName(original, jdbcEnvironment);
        Assertions.assertEquals("street_nr", transformed.getText());
    }

    @Test
    void testToPhysicalColumnNameWithComplexCamelCase() {
        Identifier original = Identifier.toIdentifier("myColumnData");
        Identifier transformed = namingStrategy.toPhysicalColumnName(original, jdbcEnvironment);
        Assertions.assertEquals("my_column_data", transformed.getText());
    }

    @Test
    void testNullIdentifierReturnsNull() {
        Assertions.assertNull(namingStrategy.toPhysicalTableName(null, jdbcEnvironment));
        Assertions.assertNull(namingStrategy.toPhysicalSequenceName(null, jdbcEnvironment));
        Assertions.assertNull(namingStrategy.toPhysicalColumnName(null, jdbcEnvironment));
    }
}