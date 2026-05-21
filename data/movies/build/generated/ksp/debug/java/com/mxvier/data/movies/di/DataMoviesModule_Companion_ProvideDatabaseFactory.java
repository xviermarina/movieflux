package com.mxvier.data.movies.di;

import android.content.Context;
import com.mxvier.data.movies.data.local.db.FavoritesDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DataMoviesModule_Companion_ProvideDatabaseFactory implements Factory<FavoritesDatabase> {
  private final Provider<Context> contextProvider;

  private DataMoviesModule_Companion_ProvideDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FavoritesDatabase get() {
    return provideDatabase(contextProvider.get());
  }

  public static DataMoviesModule_Companion_ProvideDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DataMoviesModule_Companion_ProvideDatabaseFactory(contextProvider);
  }

  public static FavoritesDatabase provideDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DataMoviesModule.Companion.provideDatabase(context));
  }
}
