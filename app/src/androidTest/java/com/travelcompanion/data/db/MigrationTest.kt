package com.travelcompanion.data.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented migration tests. Requires a connected device/emulator:
 *   ./gradlew connectedDebugAndroidTest
 *
 * Validates MIGRATION_4_5, which adds the nullable `photo_path` column to `notes`
 * so that Note.photoPath is actually persisted (previously dropped on write).
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val dbName = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    fun migrate4To5_addsPhotoPathColumn_andPreservesRows() {
        // Create v4 and insert a note WITHOUT photo_path.
        helper.createDatabase(dbName, 4).apply {
            execSQL(
                "INSERT INTO notes (trip_id, title, content, timestamp) " +
                    "VALUES (1, 'T', 'hello', 123)"
            )
            close()
        }

        // Run the migration.
        val db = helper.runMigrationsAndValidate(
            dbName, 5, true, AppDatabase.MIGRATION_4_5
        )

        // Old row survives and the new column is present and defaults to NULL.
        db.query("SELECT content, photo_path FROM notes").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("hello", c.getString(0))
            assertTrue("photo_path should default to NULL", c.isNull(1))
        }
    }
}
