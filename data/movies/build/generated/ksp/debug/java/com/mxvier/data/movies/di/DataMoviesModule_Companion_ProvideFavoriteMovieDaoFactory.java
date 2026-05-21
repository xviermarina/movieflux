package com.mxvier.data.movies.di;

import com.mxvier.data.movies.data.local.dao.FavoriteMovieDao;
import com.mxvier.data.movies.data.local.db.FavoritesDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DataMoviesModule_Companion_ProvideFavoriteMovieDaoFactory implements Factory<FavoriteMovieDao> {
  private final Provider<FavoritesDatabase> databaseProvider;

  private DataMoviesModule_Companion_ProvideFavoriteMovieDaoFactory(
      Provider<FavoritesDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FavoriteMovieDao get() {
    return provideFavoriteMovieDao(databaseProvider.get());
  }

  public static DataMoviesModule_Companion_ProvideFavoriteMovieDaoFactory create(
      Provider<FavoritesDatabase> databaseProvider) {
    return new DataMoviesModule_Companion_ProvideFavoriteMovieDaoFactory(databaseProvider);
  }

  public static FavoriteMovieDao provideFavoriteMovieDao(FavoritesDatabase database) {
    return Preconditions.checkNotNullFromProvides(DataMoviesModule.Companion.provideFavoriteMovieDao(database));
  }
}
