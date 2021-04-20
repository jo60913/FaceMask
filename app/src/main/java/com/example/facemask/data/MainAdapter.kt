package com.example.facemask.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facemask.databinding.ItemViewBinding

/**
 * 取名為Main表示會是在MainActivity中使用
 */
class MainAdapter(private val itemClickListener : IItemClickListener):RecyclerView.Adapter<MainAdapter.MyViewHolder>() {
    var pharmacyList : List<Feature> = emptyList()
    set(value){
        field = value
        notifyDataSetChanged()
    }
    class MyViewHolder(val itemViewBinding:ItemViewBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)

    /**
     * 可以依照類型來決定顯示的樣式
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemViewBinding =
            ItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemViewBinding)
    }

    /**
     * 決定甚麼元件要顯示甚麼
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemViewBinding.itemName.text = pharmacyList.get(position).properties.name
        holder.itemViewBinding.tvAdultAmount.text = pharmacyList.get(position).properties.mask_adult.toString()
        holder.itemViewBinding.tvChileAmount.text = pharmacyList.get(position).properties.mask_child.toString()
        holder.itemViewBinding.layoutItem.setOnClickListener {
            itemClickListener.OnItemClickListener(pharmacyList[position])
        }
    }

    override fun getItemCount(): Int {
        return pharmacyList.size
    }
    interface  IItemClickListener{
        fun OnItemClickListener(data : Feature)
    }
}