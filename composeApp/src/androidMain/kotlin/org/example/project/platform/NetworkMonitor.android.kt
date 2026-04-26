package org.example.project.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// ═══════════════════════════════════════════════════════════════════════
// NetworkMonitor.android.kt — androidMain
// actual class untuk memantau koneksi jaringan di Android
// Pertemuan 8: expect/actual Pattern + Platform APIs
// ═══════════════════════════════════════════════════════════════════════

actual class NetworkMonitor {

    actual fun isConnected(): Boolean = AndroidNetworkMonitorHelper.isConnected()

    actual fun observeConnectivity(): Flow<Boolean> =
        AndroidNetworkMonitorHelper.observeConnectivity()
}

/**
 * Singleton helper yang menyimpan Application Context.
 * Diinisialisasi dari MyApplication.onCreate() sebelum Koin start.
 */
object AndroidNetworkMonitorHelper {

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun isConnected(): Boolean {
        val ctx = appContext ?: return false
        val cm  = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val cap = cm.getNetworkCapabilities(net) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun observeConnectivity(): Flow<Boolean> = callbackFlow {
        val ctx = appContext
        if (ctx == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Kirim status koneksi awal saat Flow pertama kali dikumpulkan
        trySend(isConnected())

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                // Cek apakah ada network lain yang masih aktif
                trySend(isConnected())
            }
            override fun onCapabilitiesChanged(
                network: Network,
                caps: NetworkCapabilities
            ) {
                trySend(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
            }
        }

        cm.registerNetworkCallback(request, callback)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
