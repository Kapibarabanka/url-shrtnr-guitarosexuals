package shortener.propertybased;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shortener.TestUtils;
import shortener.database.Database;
import shortener.database.entities.Alias;

import java.io.File;
import java.io.IOException;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;
import static org.quicktheories.generators.SourceDSL.longs;

public class DatabasePropertyBasedTest {

  private static final String TEST_DATABASE_DIRECTORY = "test-db";

  Database db = new Database(TEST_DATABASE_DIRECTORY);

  @AfterAll
  static void purgeDb() {
    TestUtils.purgeDirectory(new File(TEST_DATABASE_DIRECTORY));
  }

  @BeforeEach
  void setupDb() throws IOException {
    TestUtils.purgeDirectory(new File(TEST_DATABASE_DIRECTORY));

    Database.init(TEST_DATABASE_DIRECTORY);
  }

  @Test
  public void shouldAddUserAliases_propertyBased() {
    qt()
      .forAll(
        strings().betweenCodePoints(97, 122).ofLengthBetween(1, 10)
      ).check(userAlias ->{
        var testUrl = "https://example.com";
        var createdAlias = db.create(db.aliasTable, new Alias(userAlias, testUrl, 1L));
        var gotAlias = db.get(db.aliasTable, userAlias);
        return createdAlias.url().equals(testUrl) && createdAlias.url().equals(gotAlias.url());
    });
  }

  @Test
  public void shouldReturnSingleAlias_propertyBased() {
    qt()
      .forAll(
        longs().between(1, 1000)
      ).check(userId ->{
        var createdAlias = db.create(db.aliasTable, new Alias("test"+userId, "https://example.com", userId));
        var userAliases = db.search(db.aliasTable, alias -> alias.userId().equals(userId));
        return userAliases.size() == 1 && userAliases.contains(createdAlias);
    });
  }
}
