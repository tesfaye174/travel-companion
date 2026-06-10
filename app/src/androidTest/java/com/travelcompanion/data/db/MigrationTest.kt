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
 * Test strumentato sulle migrazioni del database. Serve un device/emulatore collegato:
 *   ./gradlew connectedDebugAndroidTest
 *
 * Verifica la MIGRATION_4_5, che aggiunge la colonna nullable `photo_path` alla tabella
 * `notes`, così che Note.photoPath venga davvero salvata (prima andava persa in scrittura).
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
        // Creo la v4 e inserisco una nota SENZA photo_path.
        helper.createDatabase(dbName, 4).apply {
            execSQL(
                "INSERT INTO notes (trip_id, title, content, timestamp) " +
                    "VALUES (1, 'T', 'hello', 123)"
            )
            close()
        }

        // Eseguo la migrazione.
        val db = helper.runMigrationsAndValidate(
            dbName, 5, true, AppDatabase.MIGRATION_4_5
        )

        // La riga vecchia sopravvive e la nuova colonna esiste, con default NULL.
        db.query("SELECT content, photo_path FROM notes").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("hello", c.getString(0))
            assertTrue("photo_path should default to NULL", c.isNull(1))
        }
    }
}
