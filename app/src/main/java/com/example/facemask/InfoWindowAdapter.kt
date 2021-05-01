package com.example.facemask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.example.facemask.databinding.GoogleMapItemBinding

class InfoWindowAdapter(_context:Context) : GoogleMap.InfoWindowAdapter {
    private val context = _context
    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val windowinfoBinding = GoogleMapItemBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        render(marker,windowinfoBinding)
        return windowinfoBinding.root
    }

    private fun render (marker: Marker,itemBinding: GoogleMapItemBinding){
        val mask = marker.snippet.split(",")
        itemBinding.tvName.text = marker.title.toString()
        itemBinding.tvAdultAmount.text = mask[0]
        itemBinding.tvChildAmount.text = mask[1]
    }

}