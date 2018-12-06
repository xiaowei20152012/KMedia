package com.k.todo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.k.todo.R
import com.k.todo.base.MusicServiceEventListener
import com.k.todo.service.MusicPlayerRemote


class MusicCardFragment : Fragment(), View.OnClickListener, MusicServiceEventListener {

    private var playView: View? = null
    private var preView: View? = null
    private var nextView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_music_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playView = view.findViewById(R.id.music_pause_iv)
        preView = view.findViewById(R.id.music_pre_iv)
        nextView = view.findViewById(R.id.music_next_iv)
        playView!!.setOnClickListener(this)
        preView!!.setOnClickListener(this)
        nextView!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.music_pause_iv -> if (MusicPlayerRemote.isPlaying) {
                MusicPlayerRemote.pauseSong()
            } else {
                MusicPlayerRemote.resumePlaying()
            }
            R.id.music_pre_iv -> MusicPlayerRemote.back()
            R.id.music_next_iv -> MusicPlayerRemote.playNextSong()
        }
    }

    override fun onServiceConnected() {

    }

    override fun onServiceDisconnected() {

    }

    override fun onQueueChanged() {

    }

    override fun onPlayingMetaChanged() {

    }

    override fun onPlayStateChanged() {

    }

    override fun onRepeatModeChanged() {

    }

    override fun onShuffleModeChanged() {

    }

    override fun onMediaStoreChanged() {

    }

    companion object {

        fun instance(): MusicCardFragment {
            return MusicCardFragment()
        }
    }
}
