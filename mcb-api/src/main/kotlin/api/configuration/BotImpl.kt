package api.configuration

import api.addon.AddonManager
import kotlinx.coroutines.Deferred

interface BotImpl<T> {
    suspend fun init(addonManager: AddonManager): Any
    suspend fun get(): Deferred<T?>

    suspend fun shutdown()
}