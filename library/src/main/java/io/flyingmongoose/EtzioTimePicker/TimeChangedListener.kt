package io.flyingmongoose.EtzioTimePicker

import java.util.*

/**
 * @author Ugur Tekbas
 */
interface TimeChangedListener
{
    fun timeChanged(date: Date?)
}