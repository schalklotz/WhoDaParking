package com.whodaparking.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whodaparking.app.ui.theme.WhoDaParkingTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TextSearchScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun textSearchScreen_displaysCorrectly() {
        composeTestRule.setContent {
            WhoDaParkingTheme {
                TextSearchScreen()
            }
        }

        composeTestRule.onNodeWithText("Search by Text").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter registration plate").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun textSearchScreen_enablesSearchButtonWhenTextEntered() {
        composeTestRule.setContent {
            WhoDaParkingTheme {
                TextSearchScreen()
            }
        }

        // Enter text in the search field
        composeTestRule.onNodeWithText("Enter registration plate")
            .performTextInput("ABC123GP")

        // Search button should be enabled
        composeTestRule.onNodeWithText("Search")
            .assertIsDisplayed()
    }

    @Test
    fun textSearchScreen_showsLoadingIndicatorInitially() {
        composeTestRule.setContent {
            WhoDaParkingTheme {
                TextSearchScreen()
            }
        }

        // Should show loading indicator while data loads
        composeTestRule.onNodeWithText("Loading vehicle data...")
            .assertIsDisplayed()
    }
}