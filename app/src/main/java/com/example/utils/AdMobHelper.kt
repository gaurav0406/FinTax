package com.example.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobHelper {
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingAd = false
    private const val TAG = "AdMobHelper"

    // This is the Google test Interstitial Ad Unit ID
    // Replace with your real ID: "ca-app-pub-5254258369829746/YOUR_INTERSTITIAL_ID"
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-5254258369829746/8479398423"

    fun initialize(context: Context) {
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob Initialized: $initializationStatus")
            loadInterstitialAd(context)
        }
    }

    fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null || isLoadingAd) {
            return
        }

        isLoadingAd = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial Ad loaded successfully")
                    interstitialAd = ad
                    isLoadingAd = false
                    
                    // Set FullScreenContentCallback
                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Ad was dismissed.")
                            interstitialAd = null
                            // Preload the next ad
                            loadInterstitialAd(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Ad failed to show: ${adError.message}")
                            interstitialAd = null
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Ad showed fullscreen content.")
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Interstitial Ad failed to load: ${adError.message}")
                    interstitialAd = null
                    isLoadingAd = false
                }
            }
        )
    }

    /**
     * Shows the interstitial ad if it is ready.
     * @param activity The current activity context.
     * @param onAdDismissed Callback invoked when ad is dismissed or fails to show (useful to resume app flow).
     */
    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            // Override the dismiss callback to trigger our action
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            // Ad wasn't ready, just proceed with the normal flow
            Log.d(TAG, "Ad wasn't loaded yet.")
            onAdDismissed()
            
            // Try to load one for next time
            loadInterstitialAd(activity)
        }
    }
}
