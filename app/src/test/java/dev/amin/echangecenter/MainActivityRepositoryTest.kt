package dev.amin.echangecenter

import android.content.Context
import dev.amin.echangecenter.data.repositories.MainActivityRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class MainActivityRepositoryTest {

    @Mock
    lateinit var repoMock: MainActivityRepository

    @Before
    fun setup() {

        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `is test`() {
        repoMock.startUpdates()
    }

    @Test
    fun `is value 0`() {

        val int = 0

        assert(int == 0)
    }
}