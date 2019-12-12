package com.kruelkotlinkiller.krowd

import java.util.*
import kotlin.collections.ArrayList

class Record
    (
    val courseId : String,
    val date : String,
    val students : ArrayList<String>
){
    constructor():this("","",ArrayList())
}