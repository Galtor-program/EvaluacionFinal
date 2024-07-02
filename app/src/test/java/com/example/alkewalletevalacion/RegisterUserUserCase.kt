package com.example.alkewalletevalacion

import com.example.alkewalletevalacion.data.network.api.AuthService
import com.example.alkewalletevalacion.data.network.response.NewUserRequest
import com.example.alkewalletevalacion.data.network.response.NewUserResponse
import com.example.alkewalletevalacion.domain.usecases.RegisterUserUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


@ExperimentalCoroutinesApi
class RegisterUserUseCaseTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var authService: AuthService

    private lateinit var registerUserUseCase: RegisterUserUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        registerUserUseCase = RegisterUserUseCase(authService)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `execute should call authService and return NewUserResponse`(): Unit = runBlocking {

        val newUserRequest = NewUserRequest(
            first_name = "Pepe",
            last_name = "Tapia",
            email = "pepetapia@example.com",
            password = "password",
            points = 100
        )
        val newUserResponse = NewUserResponse( 3000,"Pepe","Tapia","pepetapia@example.com",
                                              1, 100)





        `when`(authService.registerUser(newUserRequest)).thenReturn(newUserResponse)


        val result = registerUserUseCase.execute(newUserRequest)

        /**
         * Verifica que el metodo fue llamado una vez
         */
        assertEquals(newUserResponse, result)
        verify(authService, times(1)).registerUser(newUserRequest)
    }
}