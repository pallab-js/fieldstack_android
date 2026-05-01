package com.fieldstack.android.ui.reports

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.fieldstack.android.data.repository.FakeFieldStackRepository
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.usecase.SaveReportUseCase
import com.fieldstack.android.util.ImageCompressor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportBuilderViewModelTest {

    private lateinit var viewModel: ReportBuilderViewModel
    private val dispatcher = StandardTestDispatcher()
    private val compressor = mockk<ImageCompressor>()
    private val context = mockk<Context>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        // No-op compressor: return the same URI that was passed in
        coEvery { compressor.compress(any(), any(), any(), any()) } answers {
            secondArg<Uri>()
        }
        viewModel = ReportBuilderViewModel(
            savedState = SavedStateHandle(mapOf("taskId" to "t1")),
            saveReport = SaveReportUseCase(FakeFieldStackRepository()),
            imageCompressor = compressor,
            context = context,
        )
    }

    @After
    fun teardown() = Dispatchers.resetMain()

    @Test
    fun `initial step is 1`() = runTest {
        assertEquals(1, viewModel.step.value)
    }

    @Test
    fun `step 1 invalid when title blank`() = runTest {
        assertFalse(viewModel.isCurrentStepValid())
    }

    @Test
    fun `step 1 valid after title set`() = runTest {
        viewModel.setTitle("Inspection Report")
        assertTrue(viewModel.isCurrentStepValid())
    }

    @Test
    fun `nextStep advances step`() = runTest {
        viewModel.setTitle("Test")
        viewModel.nextStep()
        assertEquals(2, viewModel.step.value)
    }

    @Test
    fun `prevStep goes back`() = runTest {
        viewModel.setTitle("Test")
        viewModel.nextStep()
        viewModel.prevStep()
        assertEquals(1, viewModel.step.value)
    }

    @Test
    fun `category change reflects in draft`() = runTest {
        viewModel.setCategory(ReportCategory.Maintenance)
        assertEquals(ReportCategory.Maintenance, viewModel.draft.value.category)
    }

    @Test
    fun `saveDraft transitions to Saved`() = runTest {
        viewModel.setTitle("Test")
        viewModel.setDetails("Some details")
        viewModel.saveDraft()
        advanceUntilIdle()
        assertEquals(SaveState.Saved, viewModel.saveState.value)
    }

    @Test
    fun `addPhoto compresses and appends to draft`() = runTest {
        viewModel.addPhoto("file://photo1.jpg")
        viewModel.addPhoto("file://photo2.jpg")
        advanceUntilIdle()
        assertEquals(2, viewModel.draft.value.photoUris.size)
    }

    @Test
    fun `removePhoto removes from draft`() = runTest {
        viewModel.addPhoto("file://photo1.jpg")
        advanceUntilIdle()
        val stored = viewModel.draft.value.photoUris.first()
        viewModel.removePhoto(stored)
        assertEquals(0, viewModel.draft.value.photoUris.size)
    }

    @Test
    fun `setLocation stores coordinates`() = runTest {
        viewModel.setLocation(40.7128, -74.0060)
        assertEquals(40.7128, viewModel.draft.value.latitude)
        assertEquals(-74.0060, viewModel.draft.value.longitude)
    }

    @Test
    fun `setSignature stores uri`() = runTest {
        viewModel.setSignature("/data/sig.png")
        assertEquals("/data/sig.png", viewModel.draft.value.signatureUri)
    }

    @Test
    fun `goToStep 6 shows review`() = runTest {
        viewModel.goToStep(6)
        assertEquals(6, viewModel.step.value)
    }

    @Test
    fun `submit transitions to Saved`() = runTest {
        viewModel.setTitle("Test")
        viewModel.setDetails("Details")
        viewModel.submit()
        advanceUntilIdle()
        assertEquals(SaveState.Saved, viewModel.saveState.value)
    }
}
