package com.mxvier.`data`.movies.`data`.local.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.mxvier.`data`.movies.`data`.local.dao.FavoriteMovieDao
import com.mxvier.`data`.movies.`data`.local.dao.FavoriteMovieDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FavoritesDatabase_Impl : FavoritesDatabase() {
  private val _favoriteMovieDao: Lazy<FavoriteMovieDao> = lazy {
    FavoriteMovieDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "29b4654b120b8df2b8b96e13b55c604e", "4ba10b955cc8896b393c70db9f93cf94") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `favorite_movies` (`id` INTEGER NOT NULL, `title` TEXT NOT NULL, `posterPath` TEXT, `voteAverage` REAL NOT NULL, `overview` TEXT NOT NULL, `genres` TEXT, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '29b4654b120b8df2b8b96e13b55c604e')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `favorite_movies`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsFavoriteMovies: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFavoriteMovies.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteMovies.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteMovies.put("posterPath", TableInfo.Column("posterPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteMovies.put("voteAverage", TableInfo.Column("voteAverage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteMovies.put("overview", TableInfo.Column("overview", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteMovies.put("genres", TableInfo.Column("genres", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFavoriteMovies: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFavoriteMovies: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFavoriteMovies: TableInfo = TableInfo("favorite_movies", _columnsFavoriteMovies, _foreignKeysFavoriteMovies, _indicesFavoriteMovies)
        val _existingFavoriteMovies: TableInfo = read(connection, "favorite_movies")
        if (!_infoFavoriteMovies.equals(_existingFavoriteMovies)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |favorite_movies(com.mxvier.data.movies.data.local.entity.FavoriteMovieEntity).
              | Expected:
              |""".trimMargin() + _infoFavoriteMovies + """
              |
              | Found:
              |""".trimMargin() + _existingFavoriteMovies)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "favorite_movies")
  }

  public override fun clearAllTables() {
    super.performClear(false, "favorite_movies")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(FavoriteMovieDao::class, FavoriteMovieDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun favoriteMovieDao(): FavoriteMovieDao = _favoriteMovieDao.value
}
