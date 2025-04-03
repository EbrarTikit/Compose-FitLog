package com.example.fitlog.data.local.preferences

class UserPreferences(private val preferenceManager: PreferenceManager) {
    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    fun setOnboardingCompleted(isCompleted: Boolean) {
        preferenceManager.saveBoolean(KEY_ONBOARDING_COMPLETED, isCompleted)
    }

    fun isOnboardingCompleted(): Boolean {
        return preferenceManager.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
}