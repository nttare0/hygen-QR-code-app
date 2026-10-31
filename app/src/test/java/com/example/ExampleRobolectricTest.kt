package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.NeuAccent
import com.example.ui.theme.NeuAccentSecondary
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.viewmodel.PaymentViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("QuickPay QR", appName)
    }

    @Test
    fun `design tokens match recommended palette`() {
        assertEquals(Color(0xFF5B5CE2), NeuAccent)
        assertEquals(Color(0xFFF3F7FC), NeuBackground)
        assertEquals(Color(0xFFFFFFFF), NeuSurface)
        assertEquals(Color(0xFF18A999), NeuAccentSecondary)
        assertEquals(Color(0xFF17243B), NeuTextPrimary)
    }

    @Test
    fun `link bank card, select default card, and enforce transaction limits`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = PaymentViewModel(app)

        // Link a new card and set as default
        val linked = vm.linkBankCard(
            bankName = "Al Rajhi Digital",
            cardType = "Mastercard",
            last4 = "9912",
            initialBalance = 4200.0,
            perTransactionLimit = 300.0,
            expiryDate = "06/30",
            makeDefault = true
        )
        assertEquals(4, vm.bankAccounts.value.size)
        assertEquals(linked.id, vm.defaultAccountId.value)
        assertTrue(vm.bankAccounts.value.first { it.id == linked.id }.isPrimary)

        // Switch default payment card back to acc_2
        vm.setDefaultAccount("acc_2")
        assertEquals("acc_2", vm.defaultAccountId.value)
        assertTrue(vm.bankAccounts.value.first { it.id == "acc_2" }.isPrimary)
        assertFalse(vm.bankAccounts.value.first { it.id == linked.id }.isPrimary)

        // Update per-card limit and verify guardrail
        vm.updateCardTransactionLimit(linked.id, 200.0)
        val updatedLinked = vm.bankAccounts.value.first { it.id == linked.id }
        assertEquals(200.0, updatedLinked.perTransactionLimit, 0.01)
        assertNull(vm.evaluatePaymentGuardrail(updatedLinked, 150.0))
        assertNotNull(vm.evaluatePaymentGuardrail(updatedLinked, 250.0))

        // Freeze card and verify guardrail blocks payment
        vm.toggleCardFreeze(linked.id, true)
        val frozenLinked = vm.bankAccounts.value.first { it.id == linked.id }
        assertTrue(frozenLinked.isFrozen)
        assertNotNull(vm.evaluatePaymentGuardrail(frozenLinked, 20.0))

        // Unlink card
        vm.removeBankCard(linked.id)
        assertEquals(3, vm.bankAccounts.value.size)
    }
}
