package com.zaurh.bober.data.repository

import com.zaurh.bober.screen.password.newPasswordError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ResetTokenTest {

    @Test
    fun `user id is read from between the UUID and the timestamp`() {
        // Same layout as the backend's EmailTokens.newPasswordResetToken.
        val token = "3f2b8c1e-9d4a-4b6f-8e2a-1c5d7f9a0b3e" + "66a1b2c3d4e5f60718293a4b" + "1732460000000"
        assertEquals("66a1b2c3d4e5f60718293a4b", ResetToken.userId(token))
    }

    @Test
    fun `malformed tokens have no user id`() {
        assertNull(ResetToken.userId(""))
        assertNull(ResetToken.userId("3f2b8c1e-9d4a-4b6f-8e2a-1c5d7f9a0b3e1732460000000"))
    }

    @Test
    fun `new password rules`() {
        assertNotNull(newPasswordError("abc", "abc"))
        assertNotNull(newPasswordError("abcdef", "abcdeg"))
        assertNull(newPasswordError("abcdef", "abcdef"))
        // Not complaining about a mismatch before the user has typed the confirmation.
        assertNull(newPasswordError("abcdef", ""))
    }
}
