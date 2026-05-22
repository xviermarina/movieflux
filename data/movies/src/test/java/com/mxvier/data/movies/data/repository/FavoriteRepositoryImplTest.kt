package com.mxvier.data.movies.data.repository

import app.cash.turbine.test
import com.mxvier.data.movies.data.local.dao.FavoriteMovieDao
import com.mxvier.data.movies.data.local.entity.FavoriteMovieEntity
import com.mxvier.data.movies.domain.model.FavoriteMovie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoriteRepositoryImplTest {

    private lateinit var repository: FavoriteRepositoryImpl
    private val movieDao: FavoriteMovieDao = mockk()

    @Before
    fun setup() {
        repository = FavoriteRepositoryImpl(movieDao)
    }

    @Test
    fun `getFavoriteMovies should return domain list from entities`() = runTest {
        val entities = listOf(
            FavoriteMovieEntity(1, "Title", "path", 8.0, "Overview", "Action")
        )
        coEvery { movieDao.getFavoriteMovies() } returns flowOf(entities)

        repository.getFavoriteMovies().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Title", result[0].title)
            awaitComplete()
        }
    }

    @Test
    fun `saveFavorite should call dao insert`() = runTest {
        val domain = FavoriteMovie(1, "Title", "path", 8.0, "Overview", "Action")
        coEvery { movieDao.insertMovie(any()) } returns Unit

        repository.saveFavorite(domain)

        coVerify { movieDao.insertMovie(match { it.id == 1 && it.title == "Title" }) }
    }

    @Test
    fun `removeFavorite should call delete if movie exists`() = runTest {
        val entity = FavoriteMovieEntity(1, "Title", "path", 8.0, "Overview", "Action")
        coEvery { movieDao.getMovieById(1) } returns entity
        coEvery { movieDao.deleteMovie(entity) } returns Unit

        repository.removeFavorite(1)

        coVerify { movieDao.deleteMovie(entity) }
    }

    @Test
    fun `removeFavorite should do nothing if movie does not exist`() = runTest {
        coEvery { movieDao.getMovieById(1) } returns null

        repository.removeFavorite(1)

        coVerify(exactly = 0) { movieDao.deleteMovie(any()) }
    }

    @Test
    fun `isFavorite should return flow from dao`() = runTest {
        coEvery { movieDao.isFavorite(1) } returns flowOf(true)

        repository.isFavorite(1).test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }
}
