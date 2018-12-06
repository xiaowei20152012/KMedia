package com.k.todo.loader


import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns

import com.k.todo.model.Song

import java.util.ArrayList

object SongLoader {



    val BASE_SELECTION = AudioColumns.IS_MUSIC + "=1" + " AND " + AudioColumns.TITLE + " != ''"
    val BASE_PROJECTION = arrayOf(BaseColumns._ID, // 0
            AudioColumns.TITLE, // 1
            AudioColumns.TRACK, // 2
            AudioColumns.YEAR, // 3
            AudioColumns.DURATION, // 4
            AudioColumns.DATA, // 5
            AudioColumns.DATE_MODIFIED, // 6
            AudioColumns.ALBUM_ID, // 7
            AudioColumns.ALBUM, // 8
            AudioColumns.ARTIST_ID, // 9
            AudioColumns.ARTIST)// 10

    fun getAllSongs(context: Context): ArrayList<Song> {
        val cursor = makeSongCursor(context, null, null)
        return getSongs(cursor)
    }

    fun getSongs(context: Context, query: String): ArrayList<Song> {
        val cursor = makeSongCursor(context, AudioColumns.TITLE + " LIKE ?", arrayOf("%$query%"))
        return getSongs(cursor)
    }

    fun getSong(context: Context, queryId: Int): Song {
        val cursor = makeSongCursor(context, AudioColumns._ID + "=?", arrayOf(queryId.toString()))
        return getSong(cursor)
    }

    fun getSongs(cursor: Cursor?): ArrayList<Song> {
        val songs = ArrayList<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return songs
    }

    fun getSong(cursor: Cursor?): Song {
        val song: Song
        if (cursor != null && cursor.moveToFirst()) {
            song = getSongFromCursorImpl(cursor)
        } else {
            song = Song.EMPTY_SONG
        }
        cursor?.close()
        return song
    }

    private fun getSongFromCursorImpl(cursor: Cursor): Song {
        val id = cursor.getInt(0)
        val title = cursor.getString(1)
        val trackNumber = cursor.getInt(2)
        val year = cursor.getInt(3)
        val duration = cursor.getLong(4)
        val data = cursor.getString(5)
        val dateModified = cursor.getLong(6)
        val albumId = cursor.getInt(7)
        val albumName = cursor.getString(8)
        val artistId = cursor.getInt(9)
        val artistName = cursor.getString(10)

        return Song(id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName)
    }

    @JvmOverloads
    fun makeSongCursor(context: Context, selection: String?, selectionValues: Array<String>?, sortOrder: String = ""): Cursor? {
        var selection = selection
        var selectionValues = selectionValues
        if (selection != null && selection.trim { it <= ' ' } != "") {
            selection = BASE_SELECTION + " AND " + selection
        } else {
            selection = BASE_SELECTION
        }

        // Blacklist
        //        ArrayList<String> paths = BlacklistStore.getInstance(context).getPaths();
        val paths = ArrayList<String>()
        if (!paths.isEmpty()) {
            selection = generateBlacklistSelection(selection, paths.size)
            selectionValues = addBlacklistSelectionValues(selectionValues, paths)
        }

        try {
            return context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    BASE_PROJECTION, selection, selectionValues, sortOrder)
        } catch (e: SecurityException) {
            return null
        }

    }

    private fun generateBlacklistSelection(selection: String?, pathCount: Int): String {
        var newSelection = if (selection != null && selection.trim { it <= ' ' } != "") selection + " AND " else ""
        newSelection += AudioColumns.DATA + " NOT LIKE ?"
        for (i in 0 until pathCount - 1) {
            newSelection += " AND " + AudioColumns.DATA + " NOT LIKE ?"
        }
        return newSelection
    }

    private fun addBlacklistSelectionValues(selectionsValues: Array<String>?, paths: ArrayList<String>): Array<String>? {
//        var selectionValues
//        if (selectionsValues == null) {
//            selectionValues = arrayOfNulls<String>(0)
//        } else {
        var selectionValues =  selectionsValues
//        }
//        var newSelectionValues = arrayOfNulls<String>(selectionValues!!.size + paths.size)
//        System.arraycopy(selectionValues, 0, newSelectionValues, 0, selectionValues.size)
//        for (i in selectionValues.size until newSelectionValues.size) {
//            newSelectionValues[i] = paths[i - selectionValues.size] + "%"
//        }

        return selectionsValues
    }
}//        return makeSongCursor(context, selection, selectionValues, PreferenceUtil.getInstance(context).getSongSortOrder());
