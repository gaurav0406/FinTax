package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val activeNewsId: Int? = null,
    val activeTitle: String = "",
    val activeText: String = "",
    val speechRate: Float = 1.0f,
    val progressRatio: Float = 0f,
    val statusMessage: String = "Ready"
)

class AudioSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    init {
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("en", "IN")) ?: TextToSpeech.LANG_NOT_SUPPORTED
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            isInitialized = true

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        statusMessage = "Playing voice audio..."
                    )
                }

                override fun onDone(utteranceId: String?) {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        progressRatio = 1.0f,
                        statusMessage = "Finished playing"
                    )
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        statusMessage = "Audio playback error"
                    )
                }

                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    val textLen = _playbackState.value.activeText.length
                    if (textLen > 0) {
                        val ratio = (end.toFloat() / textLen.toFloat()).coerceIn(0f, 1f)
                        _playbackState.value = _playbackState.value.copy(progressRatio = ratio)
                    }
                }
            })
        } else {
            Log.e("AudioSpeechManager", "TextToSpeech Initialization failed!")
        }
    }

    fun playAudio(newsId: Int, title: String, textToSpeak: String) {
        if (_playbackState.value.activeNewsId == newsId && _playbackState.value.isPlaying) {
            pauseAudio()
            return
        }

        stopAudio()

        _playbackState.value = _playbackState.value.copy(
            activeNewsId = newsId,
            activeTitle = title,
            activeText = textToSpeak,
            progressRatio = 0f,
            isPlaying = true,
            statusMessage = "Speaking 60-word audio digest..."
        )

        if (isInitialized && tts != null) {
            tts?.setSpeechRate(_playbackState.value.speechRate)
            val params = android.os.Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "fintax_news_$newsId")
            }
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "fintax_news_$newsId")
        }
    }

    fun pauseAudio() {
        if (tts != null && isInitialized) {
            tts?.stop()
        }
        _playbackState.value = _playbackState.value.copy(isPlaying = false, statusMessage = "Paused")
    }

    fun stopAudio() {
        if (tts != null && isInitialized) {
            tts?.stop()
        }
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            activeNewsId = null,
            progressRatio = 0f,
            statusMessage = "Stopped"
        )
    }

    fun cycleSpeechRate() {
        val current = _playbackState.value.speechRate
        val nextRate = when {
            current < 1.1f -> 1.25f
            current < 1.3f -> 1.5f
            current < 1.6f -> 2.0f
            else -> 1.0f
        }
        _playbackState.value = _playbackState.value.copy(speechRate = nextRate)
        if (_playbackState.value.isPlaying) {
            tts?.setSpeechRate(nextRate)
            val currentNewsId = _playbackState.value.activeNewsId
            val currentTitle = _playbackState.value.activeTitle
            val currentText = _playbackState.value.activeText
            if (currentNewsId != null) {
                playAudio(currentNewsId, currentTitle, currentText)
            }
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
