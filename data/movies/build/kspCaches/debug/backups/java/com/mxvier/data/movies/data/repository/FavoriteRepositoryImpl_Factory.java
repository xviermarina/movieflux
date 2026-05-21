package com.mxvier.data.movies.data.repository;

import com.mxvier.data.movies.data.local.dao.FavoriteMovieDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class FavoriteRepositoryImpl_Factory implements Factory<FavoriteRepositoryImpl> {
  private final Provider<FavoriteMovieDao> movieDaoProvider;

  private FavoriteRepositoryImpl_Factory(Provider<FavoriteMovieDao> movieDaoProvider) {
    this.movieDaoProvider = movieDaoProvider;
  }

  @Override
  public FavoriteRepositoryImpl get() {
    return newInstance(movieDaoProvider.get());
  }

  public static FavoriteRepositoryImpl_Factory create(Provider<FavoriteMovieDao> movieDaoProvider) {
    return new FavoriteRepositoryImpl_Factory(movieDaoProvider);
  }

  public static FavoriteRepositoryImpl newInstance(FavoriteMovieDao movieDao) {
    return new FavoriteRepositoryImpl(movieDao);
  }
}
