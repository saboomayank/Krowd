package com.kruelkotlinkiller.krowd

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TeacherNameCommunicator : ViewModel(){
    val message = MutableLiveData<Any>()
    val id = MutableLiveData<Any>()
    fun setMsgCommunicator(msg:String){
        message.value = msg
    }
    fun setIdCommunicator(id1 : Double){
        id.value = id1
    }
}