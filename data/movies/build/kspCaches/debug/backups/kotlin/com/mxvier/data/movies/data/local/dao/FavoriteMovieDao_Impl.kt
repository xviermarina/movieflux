package com.mxvier.`data`.movies.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.mxvier.`data`.movies.`data`.local.entity.FavoriteMovieEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FavoriteMovieDao_Impl(
  __db: RoomDatabase,
) : FavoriteMovieDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFavoriteMovieEntity: EntityInsertAdapter<FavoriteMovieEntity>

  private val __deleteAdapterOfFavoriteMovieEntity: EntityDeleteOrUpdateAdapter<FavoriteMovieEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFavoriteMovieEntity = object : EntityInsertAdapter<FavoriteMovieEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `favorite_movies` (`id`,`title`,`posterPath`,`voteAverage`,`overview`,`genres`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteMovieEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.title)
        val _tmpPosterPath: String? = entity.posterPath
        if (_tmpPosterPath == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpPosterPath)
        }
        statement.bindDouble(4, entity.voteAverage)
        statement.bindText(5, entity.overview)
        val _tmpGenres: String? = entity.genres
        if (_tmpGenres == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpGenres)
        }
      }
    }
    this.__deleteAdapterOfFavoriteMovieEntity = object : EntityDeleteOrUpdateAdapter<FavoriteMovieEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `favorite_movies` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteMovieEntity) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertMovie(movie: FavoriteMovieEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFavoriteMovieEntity.insert(_connection, movie)
  }

  public override suspend fun deleteMovie(movie: FavoriteMovieEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfFavoriteMovieEntity.handle(_connection, movie)
  }

  public override fun getFavoriteMovies(): Flow<List<FavoriteMovieEntity>> {
    val _sql: String = "SELECT * FROM favorite_movies"
    return createFlow(__db, false, arrayOf("favorite_movies")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfPosterPath: Int = getColumnIndexOrThrow(_stmt, "posterPath")
        val _columnIndexOfVoteAverage: Int = getColumnIndexOrThrow(_stmt, "voteAverage")
        val _columnIndexOfOverview: Int = getColumnIndexOrThrow(_stmt, "overview")
        val _columnIndexOfGenres: Int = getColumnIndexOrThrow(_stmt, "genres")
        val _result: MutableList<FavoriteMovieEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FavoriteMovieEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpPosterPath: String?
          if (_stmt.isNull(_columnIndexOfPosterPath)) {
            _tmpPosterPath = null
          } else {
            _tmpPosterPath = _stmt.getText(_columnIndexOfPosterPath)
          }
          val _tmpVoteAverage: Double
          _tmpVoteAverage = _stmt.getDouble(_columnIndexOfVoteAverage)
          val _tmpOverview: String
          _tmpOverview = _stmt.getText(_columnIndexOfOverview)
          val _tmpGenres: String?
          if (_stmt.isNull(_columnIndexOfGenres)) {
            _tmpGenres = null
          } else {
            _tmpGenres = _stmt.getText(_columnIndexOfGenres)
          }
          _item = FavoriteMovieEntity(_tmpId,_tmpTitle,_tmpPosterPath,_tmpVoteAverage,_tmpOverview,_tmpGenres)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun isFavorite(id: Int): Flow<Boolean> {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = ?)"
    return createFlow(__db, false, arrayOf("favorite_movies")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMovieById(id: Int): FavoriteMovieEntity? {
    val _sql: String = "SELECT * FROM favorite_movies WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfPosterPath: Int = getColumnIndexOrThrow(_stmt, "posterPath")
        val _columnIndexOfVoteAverage: Int = getColumnIndexOrThrow(_stmt, "voteAverage")
        val _columnIndexOfOverview: Int = getColumnIndexOrThrow(_stmt, "overview")
        val _columnIndexOfGenres: Int = getColumnIndexOrThrow(_stmt, "genres")
        val _result: FavoriteMovieEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpPosterPath: String?
          if (_stmt.isNull(_columnIndexOfPosterPath)) {
            _tmpPosterPath = null
          } else {
            _tmpPosterPath = _stmt.getText(_columnIndexOfPosterPath)
          }
          val _tmpVoteAverage: Double
          _tmpVoteAverage = _stmt.getDouble(_columnIndexOfVoteAverage)
          val _tmpOverview: String
          _tmpOverview = _stmt.getText(_columnIndexOfOverview)
          val _tmpGenres: String?
          if (_stmt.isNull(_columnIndexOfGenres)) {
            _tmpGenres = null
          } else {
            _tmpGenres = _stmt.getText(_columnIndexOfGenres)
          }
          _result = FavoriteMovieEntity(_tmpId,_tmpTitle,_tmpPosterPath,_tmpVoteAverage,_tmpOverview,_tmpGenres)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
