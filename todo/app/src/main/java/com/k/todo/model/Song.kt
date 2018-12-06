package com.k.todo.model


import android.os.Parcel
import android.os.Parcelable

class Song : Parcelable {
    val id: Int
    val title: String?
    val trackNumber: Int
    val year: Int
    val duration: Long
    val data: String?
    val dateModified: Long
    val albumId: Int
    val albumName: String?
    val artistId: Int
    val artistName: String?

    constructor(id: Int, title: String, trackNumber: Int, year: Int, duration: Long, data: String, dateModified: Long, albumId: Int, albumName: String, artistId: Int, artistName: String) {
        this.id = id
        this.title = title
        this.trackNumber = trackNumber
        this.year = year
        this.duration = duration
        this.data = data
        this.dateModified = dateModified
        this.albumId = albumId
        this.albumName = albumName
        this.artistId = artistId
        this.artistName = artistName
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val song = o as Song?

        if (id != song!!.id) {
            return false
        }
        if (trackNumber != song.trackNumber) {
            return false
        }
        if (year != song.year) {
            return false
        }
        if (duration != song.duration) {
            return false
        }
        if (dateModified != song.dateModified) {
            return false
        }
        if (albumId != song.albumId) {
            return false
        }
        if (artistId != song.artistId) {
            return false
        }
        if (if (title != null) title != song.title else song.title != null) {
            return false
        }
        if (if (data != null) data != song.data else song.data != null) {
            return false
        }
        if (if (albumName != null) albumName != song.albumName else song.albumName != null) {
            return false
        }
        return if (artistName != null) artistName == song.artistName else song.artistName == null

    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + trackNumber
        result = 31 * result + year
        result = 31 * result + (duration xor duration.ushr(32)).toInt()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (dateModified xor dateModified.ushr(32)).toInt()
        result = 31 * result + albumId
        result = 31 * result + (albumName?.hashCode() ?: 0)
        result = 31 * result + artistId
        result = 31 * result + (artistName?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", trackNumber=" + trackNumber +
                ", year=" + year +
                ", duration=" + duration +
                ", data='" + data + '\'' +
                ", dateModified=" + dateModified +
                ", albumId=" + albumId +
                ", albumName='" + albumName + '\'' +
                ", artistId=" + artistId +
                ", artistName='" + artistName + '\'' +
                '}'
    }


    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.id)
        dest.writeString(this.title)
        dest.writeInt(this.trackNumber)
        dest.writeInt(this.year)
        dest.writeLong(this.duration)
        dest.writeString(this.data)
        dest.writeLong(this.dateModified)
        dest.writeInt(this.albumId)
        dest.writeString(this.albumName)
        dest.writeInt(this.artistId)
        dest.writeString(this.artistName)
    }

    protected constructor(`in`: Parcel) {
        this.id = `in`.readInt()
        this.title = `in`.readString()
        this.trackNumber = `in`.readInt()
        this.year = `in`.readInt()
        this.duration = `in`.readLong()
        this.data = `in`.readString()
        this.dateModified = `in`.readLong()
        this.albumId = `in`.readInt()
        this.albumName = `in`.readString()
        this.artistId = `in`.readInt()
        this.artistName = `in`.readString()
    }

    companion object {
        val EMPTY_SONG = Song(-1, "", -1, -1, -1, "", -1, -1, "", -1, "")

        val CREATOR: Parcelable.Creator<Song> = object : Parcelable.Creator<Song> {
            override fun createFromParcel(source: Parcel): Song {
                return Song(source)
            }

            override fun newArray(size: Int): Array<Song?> {
                return arrayOfNulls(size)
            }
        }
    }
}
