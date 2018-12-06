package com.k.todo.service.music

interface MPlayer {

    val isPlaying: Boolean

    val isInitialized: Boolean

    var audioSessionId: Int

    fun setDataSource(path: String)

    fun setNextDataSource(path: String?)

    fun play()

    fun pause()

    fun seekTo(to: Int)

    fun setVolume(vol: Float)

    fun duration(): Int

    fun position(): Int

    fun stop()

    fun release()

}
