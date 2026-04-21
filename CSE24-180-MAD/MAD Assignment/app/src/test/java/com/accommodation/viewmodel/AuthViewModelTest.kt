package com.accommodation.viewmodel

import com.accommodation.data.database.entities.User
import com.accommodation.data.repository.UserRepository
import com.accommodation.ui.auth.AuthState
import com.accommodation.ui.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun fakeRepo(
        registerResult: Result<User> = Result.success(User(1, "a@b.com", "hash", "S001", "71234567", "Student")),
        loginResult: Result<User> = Result.success(User(1, "a@b.com", "hash", "S001", "71234567", "Student"))
    ) = object : UserRepository(
        object : com.accommodation.data.database.dao.UserDao {
            override suspend fun insert(user: User) = 1L
            override suspend fun findByEmail(email: String) = null
            override suspend fun findById(id: Int) = null
            override suspend fun count() = 0
        }
    ) {
        override suspend fun register(email: String, password: String, studentId: String?, phone: String, role: String) = registerResult
        override suspend fun login(email: String, password: String) = loginResult
    }

    @Test fun `register success emits Success state`() = runTest {
        val vm = AuthViewModel(fakeRepo())
        vm.register("a@b.com", "Pass@123", "S001", "71234567", "Student")
        assertTrue(vm.state.first() is AuthState.Success)
    }

    @Test fun `register failure emits Error state`() = runTest {
        val vm = AuthViewModel(fakeRepo(registerResult = Result.failure(Exception("Email already registered"))))
        vm.register("a@b.com", "Pass@123", "S001", "71234567", "Student")
        val state = vm.state.first()
        assertTrue(state is AuthState.Error)
        assertEquals("Email already registered", (state as AuthState.Error).message)
    }

    @Test fun `login success emits Success state`() = runTest {
        val vm = AuthViewModel(fakeRepo())
        vm.login("a@b.com", "Pass@123")
        assertTrue(vm.state.first() is AuthState.Success)
    }

    @Test fun `login failure emits Error state`() = runTest {
        val vm = AuthViewModel(fakeRepo(loginResult = Result.failure(Exception("Invalid email or password"))))
        vm.login("a@b.com", "wrong")
        val state = vm.state.first()
        assertTrue(state is AuthState.Error)
    }

    @Test fun `reset returns Idle state`() = runTest {
        val vm = AuthViewModel(fakeRepo())
        vm.login("a@b.com", "Pass@123")
        vm.reset()
        assertTrue(vm.state.first() is AuthState.Idle)
    }
}
